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

import java.util.HashMap;
import java.util.Map;

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
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.xbase.typing.XbaseTypeProvider;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class EMFPatternTypeProvider extends XbaseTypeProvider {

	private Logger logger = Logger.getLogger(getClass());
	private static Map<String, Class<?>> WRAPPERCLASSES = getWrapperClasses();
	
	@Inject
	private TypeReferences typeReferences;

	@Override
	protected JvmTypeReference typeForIdentifiable(
			JvmIdentifiableElement identifiable, boolean rawType) {
		if (identifiable instanceof Variable) {
			return _typeForIdentifiable((Variable) identifiable, rawType);
		}
		return super.typeForIdentifiable(identifiable, rawType);
	}

	protected JvmTypeReference _typeForIdentifiable(Variable variable,
			boolean rawType) {
		if (logger.isDebugEnabled()) {
			logger.debug("Calculating type for: " + variable.getName() + " type: " + variable.getType() + " eContainer: " + variable.eContainer());			
		}
		EcoreUtil2.resolveAll(variable);
		JvmTypeReference typeRef = resolveClassType(variable);
		if (typeRef == null) {
			typeRef = resolveClassType(variable.eContainer(), variable);
			if (typeRef == null) {
				typeRef = typeReferences.getTypeForName(Object.class, variable);
			}
		}
		return typeRef;
	}

	/**
	 * If declared, resolves the variable's type from its own type, otherwise returns null.
	 * @param variable
	 * @return
	 */
	private JvmTypeReference resolveClassType(Variable variable) {
		// first try to get the type through the variable's type ref
		if (variable.getType() instanceof ClassType) {
			EClassifier classifier = ((ClassType) variable.getType())
					.getClassname();
			if (classifier != null && classifier.getInstanceClass() != null) {
				return typeReferenceFromClazz(classifier.getInstanceClass(), variable);
			}
		}
		return null;
	}
	
	/**
	 * Resolves the variable from an eObject as context 
	 * (Possible context: {@link Pattern}, {@link PatternBody}).
	 * Returns a {@link JvmTypeReference} if the type successfully resolved, otherwise returns null.
	 * @param eObject
	 * @param variable
	 * @return
	 */
	private JvmTypeReference resolveClassType(EObject eObject, Variable variable) {
		if (eObject instanceof Pattern) {
			Pattern pattern = (Pattern) eObject;
			for (PatternBody body : pattern.getBodies()) {
				JvmTypeReference typeRef = resolveClassType(body, variable);
				if (typeRef != null) {
					return typeRef;
				}
			}
		}
		if (eObject instanceof PatternBody) {
			return resolveClassType((PatternBody) eObject, variable);
		}
		return null;
	}

	/**
	 * Resolves the variable's type from the context of a {@link PatternBody}.
	 * For now this method only searches for {@link EClassifierConstraint}s.
	 * 
	 * @param body
	 * @param variable
	 * @return
	 */
	private JvmTypeReference resolveClassType(PatternBody body, Variable variable) {
		for (Constraint constraint : body.getConstraints()) {
			if (constraint instanceof EClassifierConstraint) {
				JvmTypeReference typeRef = getTypeRef(
						(EClassifierConstraint) constraint, variable);
				if (typeRef != null) {
					return typeRef;
				}
			}
			if (constraint instanceof PathExpressionConstraint) {
				PathExpressionHead head = ((PathExpressionConstraint) constraint).getHead();
				// src is the first parameter (example: EClass.name(E, N)), src is E
				final VariableReference varRef = head.getSrc();
				final ValueReference valueRef = head.getDst();
				// test if the current variable is referenced by the varRef
				if (equalVariable(variable, varRef)) {
					return referenceFromType(head.getType(), variable);
				}
				// first variable is not the right one, so next target is the second
				if (valueRef instanceof VariableValue) {
					final VariableReference secondVarRef = ((VariableValue) valueRef).getValue();
					if (equalVariable(variable, secondVarRef)) {
						return referenceFromType(computeTypeFromTail(head.getTail()), variable);
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
	 * Returns the JvmTypeReference for variable if it used in EClassConstraint.
	 */
	private JvmTypeReference getTypeRef(EClassifierConstraint constraint,
			Variable variable) {
		if (equalVariable(variable, constraint.getVar())) {
			return referenceFromType(constraint.getType(), variable);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("VariableRef not referring to parameter Variable!");
			}
		}
		return null;
	}

	private JvmTypeReference referenceFromType(Type type, Variable variable) {
		Class<?> clazz = computeClazzFromType(type);
		if (clazz != null) {
			return typeReferenceFromClazz(clazz, variable);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Clazz not found for " + type);
			}
		}
		return null;
	}

	/**
	 * @param type
	 * @return
	 */
	private Class<?> computeClazzFromType(Type type) {
		if (type instanceof ClassType) {
			return ((ClassType) type).getClassname().getInstanceClass();			
		}
		if (type instanceof ReferenceType) {
			EStructuralFeature feature = ((ReferenceType) type).getRefname();
			if (feature instanceof EAttribute) {
				return ((EAttribute) feature).getEAttributeType().getInstanceClass();
			}
			if (feature instanceof EReference) {
				return ((EReference) feature).getEReferenceType().getInstanceClass();
			}
		}
		return null;
	}

	/**
	 * Returns a {@link JvmTypeReference} for the parameter clazz.
	 * If the clazz is a primitive class, the corresponding wrapper class is returned. 
	 * @param clazz
	 * @param variable
	 * @return
	 */
	private JvmTypeReference typeReferenceFromClazz(Class<?> clazz,
			Variable variable) {
		if (clazz.isPrimitive()) {
			clazz = wrapperClazzForPrimitive(clazz);
		}
		return typeReferences.getTypeForName(clazz, variable);
	}

	/**
	 * Returns the wrapper class for the input class if it is a primitive type class.
	 * @param clazz
	 * @return
	 */
	private Class<?> wrapperClazzForPrimitive(Class<?> clazz) {
		if (WRAPPERCLASSES.containsKey(clazz.getCanonicalName())) {
			return WRAPPERCLASSES.get(clazz.getCanonicalName());
		}
		return clazz;
	}
	
	/**
	 * Returns a map that contains the wrapper classes for primitive types. 
	 * Keys are the primitive type names.
	 * @return
	 */
	public static Map<String, Class<?>> getWrapperClasses() {
		Map<String, Class<?>> wrapperClasses = new HashMap<String, Class<?>>();
		wrapperClasses.put(boolean.class.getCanonicalName(), Boolean.class);
		wrapperClasses.put(byte.class.getCanonicalName(), Byte.class);
		wrapperClasses.put(char.class.getCanonicalName(), Character.class);
		wrapperClasses.put(double.class.getCanonicalName(), Double.class);
		wrapperClasses.put(float.class.getCanonicalName(), Float.class);
		wrapperClasses.put(int.class.getCanonicalName(), Integer.class);
		wrapperClasses.put(long.class.getCanonicalName(), Long.class);
		wrapperClasses.put(short.class.getCanonicalName(), Short.class);
		return wrapperClasses;
	}
	
	/**
	 * Returns true if the variable references by the variableReference.
	 * @param variable
	 * @param variableReference
	 * @return
	 */
	public static boolean equalVariable(Variable variable, VariableReference variableReference) {
		if (variable == null || variableReference == null) {
			return false;
		}
		final Variable variableReferenceVariable = variableReference.getVariable();
		final String variableName = variableReference.getVar();
		if (equal(variable, variableReferenceVariable) || equal(variableName, variable.getName())) {
			return true;
		}
		return false;
	}
	
}
