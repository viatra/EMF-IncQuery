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
 * FIXME do it, write something meaningful here
 */
@Singleton
@SuppressWarnings("restriction")
public class EMFPatternTypeProvider extends XbaseTypeProvider {

	@Inject
	private TypeReferences typeReferences;

	@Inject
	private Primitives primitives;

	@Override
	protected JvmTypeReference typeForIdentifiable(JvmIdentifiableElement identifiable, boolean rawType) {
		if (identifiable instanceof Variable) {
			Variable variable = (Variable) identifiable;
			JvmTypeReference typeReference = getTypeReferenceForVariable(variable);
			if (typeReference == null) {
				typeReference = typeReferences.getTypeForName(Object.class, variable);
			}
			System.out.println(variable.getName() + "--" + typeReference.getSimpleName());
			return typeReference;
		}
		return super.typeForIdentifiable(identifiable, rawType);
	}

	private JvmTypeReference getTypeReferenceForVariable(Variable variable) {
		EcoreUtil2.resolveAll(variable);
		JvmTypeReference typeRefeference = getTypeReferenceForVariableWithType(variable.getType(), variable);
		if (typeRefeference == null) {
			EObject container = variable.eContainer();
			if (container instanceof Pattern) {
				Pattern pattern = (Pattern) container;
				for (PatternBody body : pattern.getBodies()) {
					typeRefeference = getTypeReferenceForVariableWithPatternBody(body, variable);
					if (typeRefeference != null) {
						break;
					}
				}
			} else if (container instanceof PatternBody) {
				typeRefeference = getTypeReferenceForVariableWithPatternBody((PatternBody) container, variable);
			}
		}
		return typeRefeference;
	}

	protected JvmTypeReference getTypeReferenceForVariableWithEClassifier(EClassifier classifier, Variable variable) {
		// FIXME do it!!! this is overriden in the genmodel one!!!
		if (classifier != null && classifier.getInstanceClass() != null) {
			JvmTypeReference typeReference = typeReferences.getTypeForName(classifier.getInstanceClass(), variable);
			return primitives.asWrapperTypeIfPrimitive(typeReference);
		}
		return null;
	}

	private JvmTypeReference getTypeReferenceForVariableWithPatternBody(PatternBody body, Variable variable) {
		Type type = searchForConstraintType(body, variable);
		if (type != null) {
			return getTypeReferenceForVariableWithType(type, variable);
		}
		return null;
	}

	/**
	 * Searches the {@link PatternBody} and tries to find one of the
	 * {@link Constraint}'s {@link Type}, which is using the variable. If none
	 * found, null is returned.
	 * 
	 * @param body
	 * @param variable
	 * @return
	 */
	private Type searchForConstraintType(PatternBody body, Variable variable) {
		for (Constraint constraint : body.getConstraints()) {
			if (constraint instanceof EClassifierConstraint) {
				if (equalVariable(variable, ((EClassifierConstraint) constraint).getVar())) {
					return ((EClassifierConstraint) constraint).getType();
				}
			}
			if (constraint instanceof PathExpressionConstraint) {
				final PathExpressionHead head = ((PathExpressionConstraint) constraint).getHead();
				// src is the first parameter (example: EClass.name(E, N)), src
				// is E
				final VariableReference varRef = head.getSrc();
				final ValueReference valueRef = head.getDst();
				// test if the current variable is referenced by the varRef
				if (equalVariable(variable, varRef)) {
					return head.getType();
				}
				// first variable is not the right one, so next target is the
				// second
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
	 * 
	 * @param tail
	 * @return
	 */
	private Type computeTypeFromTail(PathExpressionTail tail) {
		if (tail == null) {
			return null;
		}
		if (tail.getTail() != null) {
			return computeTypeFromTail(tail.getTail());
		}
		return tail.getType();
	}

	/**
	 * Resolves the variable's type from a {@link Constraint}'s {@link Type}. If
	 * the resolution fails, null is returned.
	 * 
	 * @param type
	 * @param variable
	 * @return
	 */
	private JvmTypeReference getTypeReferenceForVariableWithType(Type type, Variable variable) {
		if (type instanceof ClassType) {
			return getTypeReferenceForVariableWithClassType((ClassType) type, variable);
		} else if (type instanceof ReferenceType) {
			return getTypeReferenceForVariableWithReferenceType((ReferenceType) type, variable);
		}
		return null;
	}

	/**
	 * Resolves the variable type from a {@link ClassType}.
	 * 
	 * @param type
	 * @param variable
	 * @return
	 */
	private JvmTypeReference getTypeReferenceForVariableWithClassType(ClassType type, Variable variable) {
		final EClassifier classifier = type.getClassname();
		if (classifier != null) {
			return getTypeReferenceForVariableWithEClassifier(classifier, variable);
		}
		return null;
	}

	/**
	 * Resolves the variable type from a {@link ReferenceType}.
	 * 
	 * @param type
	 * @param variable
	 * @return
	 */
	private JvmTypeReference getTypeReferenceForVariableWithReferenceType(ReferenceType type, Variable variable) {
		final EStructuralFeature feature = type.getRefname();
		if (feature instanceof EAttribute) {
			EAttribute attribute = (EAttribute) feature;
			return getTypeReferenceForVariableWithEClassifier(attribute.getEAttributeType(), variable);
		}
		if (feature instanceof EReference) {
			EReference reference = (EReference) feature;
			return getTypeReferenceForVariableWithEClassifier(reference.getEReferenceType(), variable);
		}
		return null;
	}
	
	/**
	 * Returns true if the variable referenced by the variableReference.
	 * 
	 * @param variable
	 * @param variableReference
	 * @return
	 */
	private static boolean equalVariable(Variable variable, VariableReference variableReference) {
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
