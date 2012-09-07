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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionHead;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionTail;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCall;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCompositionConstraint;
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

	private static final int RECURSION_CALLING_LEVEL_LIMIT = 5;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.xtext.xbase.typing.XbaseTypeProvider#typeForIdentifiable(
	 * org.eclipse.xtext.common.types.JvmIdentifiableElement, boolean)
	 */
	@Override
	protected JvmTypeReference typeForIdentifiable(JvmIdentifiableElement identifiable, boolean rawType) {
		if (identifiable instanceof Variable) {
			Variable variable = (Variable) identifiable;
			JvmTypeReference typeReference = getTypeReferenceForVariable(variable);
			// FIXME remove this from commit
			// System.out.println(variable.getName() + "--" + typeReference.getSimpleName());
			return typeReference;
		}
		return super.typeForIdentifiable(identifiable, rawType);
	}

	private JvmTypeReference getTypeReferenceForVariable(Variable variable) {
		Set<JvmTypeReference> possibleResults = new HashSet<JvmTypeReference>();
		EcoreUtil2.resolveAll(variable);

		// Calculate it with just the variable only
		JvmTypeReference typeRefeference = getTypeReferenceForVariableWithType(variable.getType(), variable);
		if (typeRefeference != null) {
			possibleResults.add(typeRefeference);
		}

		// Calculate it in it's pattern context

		EObject container = variable.eContainer();
		if (container instanceof Pattern) {
			possibleResults.addAll(getTypeReferenceForVariableWithPattern((Pattern) container, variable, 0));
		} else if (container instanceof PatternBody) {
			possibleResults.addAll(getTypeReferenceForVariableWithPatternBody((PatternBody) container, variable, 0));
		}

		return getTypeReferenceFromPossibleTypesList(possibleResults, variable);
	}

	private JvmTypeReference getTypeReferenceFromPossibleTypesList(Set<JvmTypeReference> possibleTypes, Variable variable) {
		if (possibleTypes.isEmpty()) {
			return typeReferences.getTypeForName(Object.class, variable);
		} else {
			if (possibleTypes.size() == 1) {
				return (JvmTypeReference) possibleTypes.toArray()[0];
			} else {
				// FIXME do it
				return typeReferences.getTypeForName(Object.class, variable);
			}
		}
	}

	private Set<JvmTypeReference> getTypeReferenceForVariableWithPattern(Pattern pattern, Variable variable, int recursionCallingLevel) {
		Set<JvmTypeReference> resultList = new HashSet<JvmTypeReference>();
		for (PatternBody body : pattern.getBodies()) {
			resultList.addAll(getTypeReferenceForVariableWithPatternBody(body, variable, recursionCallingLevel));
		}
		return resultList;
	}

	private Set<JvmTypeReference> getTypeReferenceForVariableWithPatternBody(PatternBody patternBody, Variable variable,
			int recursionCallingLevel) {
		Set<JvmTypeReference> resultList = new HashSet<JvmTypeReference>();
		for (Constraint constraint : patternBody.getConstraints()) {
			if (constraint instanceof EClassifierConstraint) {
				EClassifierConstraint eClassifierConstraint = (EClassifierConstraint) constraint;
				if (isEqualVariables(variable, eClassifierConstraint.getVar())) {
					Type type = eClassifierConstraint.getType();
					JvmTypeReference typeReference = getTypeReferenceForVariableWithType(type, variable);
					if (typeReference != null) {
						resultList.add(typeReference);
					}
				}
			} else if (constraint instanceof PathExpressionConstraint) {
				final PathExpressionHead pathExpressionHead = ((PathExpressionConstraint) constraint).getHead();
				// src is the first parameter (example: EClass.name(E, N)), src
				// is E
				final VariableReference variableReference = pathExpressionHead.getSrc();
				final ValueReference valueReference = pathExpressionHead.getDst();
				// test if the current variable is referenced by the varRef
				if (isEqualVariables(variable, variableReference)) {
					Type type = pathExpressionHead.getType();
					JvmTypeReference typeReference = getTypeReferenceForVariableWithType(type, variable);
					if (typeReference != null) {
						resultList.add(typeReference);
					}
				}
				// first variable is not the right one, so next target is the
				// second
				if (valueReference instanceof VariableValue) {
					final VariableReference secondVariableReference = ((VariableValue) valueReference).getValue();
					if (isEqualVariables(variable, secondVariableReference)) {
						Type type = computeTypeFromPathExpressionTail(pathExpressionHead.getTail());
						JvmTypeReference typeReference = getTypeReferenceForVariableWithType(type, variable);
						if (typeReference != null) {
							resultList.add(typeReference);
						}
					}
				}
			} else if (constraint instanceof PatternCompositionConstraint && recursionCallingLevel < RECURSION_CALLING_LEVEL_LIMIT) {
				PatternCompositionConstraint patternCompositionConstraint = (PatternCompositionConstraint) constraint;
				boolean isNegative = patternCompositionConstraint.isNegative();
				if (!isNegative) {
					PatternCall patternCall = patternCompositionConstraint.getCall();
					int parameterIndex = 0;
					for (ValueReference valueReference : patternCall.getParameters()) {
						if (valueReference instanceof VariableValue) {
							VariableValue variableValue = (VariableValue) valueReference;
							VariableReference variableReference = variableValue.getValue();
							if (isEqualVariables(variable, variableReference)) {
								Pattern pattern = patternCall.getPatternRef();
								Variable variableInCalledPattern = pattern.getParameters().get(parameterIndex);
								resultList.addAll(getTypeReferenceForVariableWithPattern(pattern, variableInCalledPattern,
										recursionCallingLevel + 1));
							}
						}
						parameterIndex++;
					}
				}
			}
		}
		return resultList;
	}

	/**
	 * Computes the {@link Type} from linked {@link PathExpressionTail}-s. The
	 * last tail's type is returned.
	 * 
	 * @param pathExpressionTail
	 * @return
	 */
	private Type computeTypeFromPathExpressionTail(PathExpressionTail pathExpressionTail) {
		if (pathExpressionTail == null) {
			return null;
		}
		if (pathExpressionTail.getTail() != null) {
			return computeTypeFromPathExpressionTail(pathExpressionTail.getTail());
		}
		return pathExpressionTail.getType();
	}

	/**
	 * Resolves the variable's type from a {@link Type}. If the resolution
	 * fails, null is returned.
	 * 
	 * @param type
	 * @param variable
	 * @return
	 */
	private JvmTypeReference getTypeReferenceForVariableWithType(Type type, Variable variable) {
		JvmTypeReference result = null;
		if (type != null) {
			if (type instanceof ClassType) {
				result = getTypeReferenceForVariableWithClassType((ClassType) type, variable);
			} else if (type instanceof ReferenceType) {
				result = getTypeReferenceForVariableWithReferenceType((ReferenceType) type, variable);
			}
		}
		return result;
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
	 * @param referenceType
	 * @param variable
	 * @return
	 */
	private JvmTypeReference getTypeReferenceForVariableWithReferenceType(ReferenceType referenceType, Variable variable) {
		final EStructuralFeature feature = referenceType.getRefname();
		if (feature instanceof EAttribute) {
			EAttribute attribute = (EAttribute) feature;
			EDataType eAttributeType = attribute.getEAttributeType();
			if (eAttributeType != null) {
				return getTypeReferenceForVariableWithEClassifier(eAttributeType, variable);
			}
		} else if (feature instanceof EReference) {
			EReference reference = (EReference) feature;
			EClass eReferenceType = reference.getEReferenceType();
			if (eReferenceType != null) {
				return getTypeReferenceForVariableWithEClassifier(eReferenceType, variable);
			}
		}
		return null;
	}

	/**
	 * FIXME DO IT
	 * 
	 * @param classifier
	 * @param variable
	 * @return
	 */
	protected JvmTypeReference getTypeReferenceForVariableWithEClassifier(EClassifier classifier, Variable variable) {
		// FIXME do it!!! this is overriden in the genmodel one!!!
		if (classifier != null && classifier.getInstanceClass() != null) {
			JvmTypeReference typeReference = typeReferences.getTypeForName(classifier.getInstanceClass(), variable);
			return primitives.asWrapperTypeIfPrimitive(typeReference);
		}
		return null;
	}

	/**
	 * Returns true if the variable equals to the variable referenced by the
	 * variableReference.
	 * 
	 * @param variable
	 * @param variableReference
	 * @return
	 */
	private static boolean isEqualVariables(Variable variable, VariableReference variableReference) {
		if (variable != null && variableReference != null) {
			final Variable variableReferenceVariable = variableReference.getVariable();
			if (equal(variable, variableReferenceVariable) || equal(variable.getName(), variableReferenceVariable.getName())) {
				return true;
			}
		}
		return false;
	}

}
