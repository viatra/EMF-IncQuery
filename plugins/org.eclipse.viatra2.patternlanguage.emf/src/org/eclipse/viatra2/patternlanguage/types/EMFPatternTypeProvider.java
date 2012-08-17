/*******************************************************************************
 * Copyright (c) 2010-2012, Ujhelyi Zoltan, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Ujhelyi Zoltan, Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.patternlanguage.types;

import static com.google.common.base.Objects.equal;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionHead;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionTail;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Type;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ValueReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableValue;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassifierConstraint;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ReferenceType;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.util.Primitives;
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.xbase.typing.XbaseTypeProvider;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * @author Mark Czotter
 *
 */
@Singleton
@SuppressWarnings("restriction")
public class EMFPatternTypeProvider extends XbaseTypeProvider {

	@Inject
	private Logger logger;
	
	@Inject
	private TypeReferences typeReferences;
	@Inject
	private Primitives primitives;

	@Override
	protected JvmTypeReference typeForIdentifiable(
			JvmIdentifiableElement identifiable, boolean rawType) {
		if (identifiable instanceof Variable) {
			return _typeForIdentifiable((Variable) identifiable, rawType);
		}
		return super.typeForIdentifiable(identifiable, rawType);
	}

	/**
	 * Returns a type for a variable. If the variable type resolution fails,
	 * default Object type is returned.
	 * 
	 * @param variable
	 * @param rawType
	 * @return
	 */
	protected JvmTypeReference _typeForIdentifiable(Variable variable,
			boolean rawType) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Calculating type for: %s, type: %s, eContainer: %s.", variable.getName(), variable.getType(), variable.eContainer()));			
		}
		JvmTypeReference typeRef = resolve(variable);
		if (typeRef == null) {
			typeRef = typeReferences.getTypeForName(Object.class, variable);
		}
		return typeRef;
	}
	
	public boolean canResolveEasily(Variable variable) {
		return true;
	}
	
	/**
	 * Resolves the variable with various methods. If the resolution fails, then
	 * null is returned.
	 * 
	 * @param variable
	 * @return
	 */
	public JvmTypeReference resolve(Variable variable) {
		EcoreUtil2.resolveAll(variable);
		JvmTypeReference typeRef = resolve(variable, variable);
		if (typeRef == null) {
			typeRef = resolve(variable.eContainer(), variable);
		}
		return typeRef;
	}	

	/**
	 * Resolves the variable from different contexts. (Possible context:
	 * {@link Variable}, {@link Pattern}, {@link PatternBody}). If the
	 * resolution fails null is returned.
	 * 
	 * @param context
	 * @param variable
	 * @return a {@link JvmTypeReference} if the type successfully resolved,
	 *         otherwise returns null.
	 */
	protected JvmTypeReference resolve(EObject context, Variable variable) {
		if (context instanceof Variable && context.equals(variable)) {
			return resolve(variable.getType(), variable);
		}
		if (context instanceof Pattern) {
			final Pattern pattern = (Pattern) context;
			for (PatternBody body : pattern.getBodies()) {
				JvmTypeReference typeRef = resolve(body, variable);
				if (typeRef != null) {
					return typeRef;
				}
			}
		}
		if (context instanceof PatternBody) {
			return resolve((PatternBody) context, variable);
		}
		return null;
	}
	
	/**
	 * Resolves the variable's type using the information available in the
	 * classifier.
	 * 
	 * @param classifier
	 * @param variable
	 * @return
	 */
	protected JvmTypeReference resolve(EClassifier classifier,
			Variable variable) {
		if (classifier.getInstanceClass() != null) {
			return typeReference(classifier.getInstanceClass(), variable);
		}
		return null;
	}
	
	public boolean canResolveEasily(PatternBody body, Variable variable) {
		return true;
	}

	/**
	 * Resolves the variable using information retrievable only from the body.
	 * If the type resolution fails, null is returned.
	 * 
	 * @param context
	 * @param variable
	 * @return
	 */
	public JvmTypeReference resolve(PatternBody body, Variable variable) {
		final Type constraintType = searchForConstraintType(body, variable);
		if (constraintType != null) {
			return resolve(constraintType, variable);			
		}
		return null;
	}

	/**
	 * Searches the {@link PatternBody} and tries to find one of the {@link Constraint}'s {@link Type},
	 * which is using the variable. If none found, null is returned.
	 * 
	 * @param body
	 * @param variable
	 * @return
	 */
	protected Type searchForConstraintType(PatternBody body, Variable variable) {
		for (Constraint constraint : body.getConstraints()) {
			if (constraint instanceof EClassifierConstraint) {
				if (equalVariable(variable, ((EClassifierConstraint) constraint).getVar())) {
					return ((EClassifierConstraint) constraint).getType();
				}
			}
			if (constraint instanceof PathExpressionConstraint) {
				final PathExpressionHead head = ((PathExpressionConstraint) constraint).getHead();
				// src is the first parameter (example: EClass.name(E, N)), src is E
				final VariableReference varRef = head.getSrc();
				final ValueReference valueRef = head.getDst();
				// test if the current variable is referenced by the varRef
				if (equalVariable(variable, varRef)) {
					return head.getType();
				}
				// first variable is not the right one, so next target is the second
				if (valueRef instanceof VariableValue) {
					final VariableReference secondVarRef = ((VariableValue) valueRef).getValue();
					if (equalVariable(variable, secondVarRef)) {
						return computeTypeFromTail(head.getTail());
					}
				}
			}
		}
		return null;
	}

	/**
	 * Computes the type from linked tails. The last tail's type is returned. 
	 * @param tail
	 * @return
	 */
	private Type computeTypeFromTail(PathExpressionTail tail) {
		if (tail == null) return null;
		if (tail.getTail() != null) {
			return computeTypeFromTail(tail.getTail());
		}
		return tail.getType();
	}

	/**
	 * Resolves the variable's type from a {@link Constraint}'s {@link Type}.
	 * If the resolution fails, null is returned.
	 * @param type
	 * @param variable
	 * @return
	 */
	protected JvmTypeReference resolve(Type type, Variable variable) {
		if (type instanceof ClassType) {
			return resolve((ClassType)type, variable);
		}
		if (type instanceof ReferenceType) {
			return resolve((ReferenceType) type, variable);				
		}
		return null;
	}
	
	/**
	 * Resolves the variable type from a {@link ClassType}.
	 * @param type
	 * @param variable
	 * @return
	 */
	protected JvmTypeReference resolve(ClassType type, Variable variable) {
		final EClassifier classifier = type.getClassname();
		if (classifier != null) {
			return resolve(classifier, variable);
		}
		return null;
	}

	/**
	 * Resolves the variable type from a {@link ReferenceType}.
	 * @param type
	 * @param variable
	 * @return
	 */
	protected JvmTypeReference resolve(ReferenceType type,
			Variable variable) {
		final EStructuralFeature feature = type.getRefname();
		if (feature instanceof EAttribute) {
			return resolve((EAttribute) feature, variable);
		}
		if (feature instanceof EReference) {
			return resolve((EReference)feature, variable);
		}
		return null;
	}

	/**
	 * Resolves the variable's type from an {@link EReference}.
	 * @param feature
	 * @param variable
	 * @return
	 */
	protected JvmTypeReference resolve(EReference feature,
			Variable variable) {
		if (feature.getEReferenceType() != null) {
			return resolve(feature.getEReferenceType(), variable);
		}
		return null;
	}

	/**
	 * Resolves the variable's type from an {@link EAttribute}.
	 * @param feature
	 * @param variable
	 * @return
	 */
	protected JvmTypeReference resolve(EAttribute feature,
			Variable variable) {
		if (feature.getEAttributeType() != null) {
			return resolve(feature.getEAttributeType(), variable);
		}
		return null;
	}

	/**
	 * Returns a {@link JvmTypeReference} for the parameter clazz. If the clazz
	 * is a primitive class, the corresponding wrapper class is returned.
	 * 
	 * @param clazz
	 * @param variable
	 * @return
	 */
	protected JvmTypeReference typeReference(Class<?> clazz,
			Variable variable) {
		JvmTypeReference typeRef = typeReferences.getTypeForName(clazz, variable);
		return primitives.asWrapperTypeIfPrimitive(typeRef);
	}
	
	/**
	 * Returns a {@link JvmTypeReference} for the typeName parameter.
	 * 
	 * @param typeName
	 * @param variable
	 * @return
	 */
	protected JvmTypeReference typeReference(
			String typeName, Variable variable) {
		JvmTypeReference typeRef = typeReferences.getTypeForName(typeName, variable);
		return primitives.asWrapperTypeIfPrimitive(typeRef);
	}
	
	/**
	 * Returns true if the variable referenced by the variableReference.
	 * @param variable
	 * @param variableReference
	 * @return
	 */
	public static boolean equalVariable(Variable variable, VariableReference variableReference) {
		if (variable == null || variableReference == null) {
			return false;
		}
		final Variable variableReferenceVariable = variableReference.getVariable();
		final String variableName = variableReference.getVariable().getName();
		if (equal(variable, variableReferenceVariable) || equal(variableName, variable.getName())) {
			return true;
		}
		return false;
	}
	
}
