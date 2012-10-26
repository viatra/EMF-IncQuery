/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.patternlanguage.types;

import static com.google.common.base.Objects.equal;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CompareConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CompareFeature;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ComputationValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.LiteralValueReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ParameterRef;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionHead;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCall;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCompositionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Type;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ValueReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableValue;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassifierConstraint;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.util.Primitives;
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.xbase.typing.XbaseTypeProvider;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * An extension of the {@link XbaseTypeProvider} for infering the correct types for the pattern variables. It handles
 * all constraints in the model which can modify the outcome of the type, but it has some practical limitations, as the
 * calculation of the proper type can be time consuming in some cases.
 */
@Singleton
@SuppressWarnings("restriction")
public class EMFPatternTypeProvider extends XbaseTypeProvider implements IEMFTypeProvider {

    @Inject
    private TypeReferences typeReferences;

    @Inject
    private Primitives primitives;

    private static final int RECURSION_CALLING_LEVEL_LIMIT = 5;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.xtext.xbase.typing.XbaseTypeProvider#typeForIdentifiable(
     * org.eclipse.xtext.common.types.JvmIdentifiableElement, boolean)
     */
    @Override
    protected JvmTypeReference typeForIdentifiable(JvmIdentifiableElement identifiable, boolean rawType) {
        if (identifiable instanceof Variable) {
            Variable variable = (Variable) identifiable;
            EClassifier classifier = getClassifierForVariable(variable);
            JvmTypeReference typeReference = null;
            if (classifier != null) {
                typeReference = getTypeReferenceForVariableWithEClassifier(classifier, variable);
            }
            if (typeReference == null) {
                typeReference = typeReferences.getTypeForName(Object.class, variable);
            }
            return typeReference;
        }
        return super.typeForIdentifiable(identifiable, rawType);
    }

    /**
     * Returns the {@link JvmTypeReference} for a given {@link EClassifier} and {@link Variable} combination.
     * 
     * @param classifier
     * @param variable
     * @return
     */
    protected JvmTypeReference getTypeReferenceForVariableWithEClassifier(EClassifier classifier, Variable variable) {
        if (classifier != null && classifier.getInstanceClass() != null) {
            JvmTypeReference typeReference = typeReferences.getTypeForName(classifier.getInstanceClass(), variable);
            return primitives.asWrapperTypeIfPrimitive(typeReference);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.viatra2.patternlanguage.types.IEMFTypeProvider# getClassifierForVariable
     * (org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable)
     */
    @Override
    public EClassifier getClassifierForVariable(Variable variable) {
        EcoreUtil2.resolveAll(variable);
        EObject container = variable.eContainer();
        if (container instanceof Pattern) {
            return getClassifierForVariableWithPattern((Pattern) container, variable, 0);
        } else if (container instanceof PatternBody) {
            return getClassifierForVariableWithPatternBody((PatternBody) container, variable, 0, null);
        }
        return null;
    }

    private Set<EClassifier> minimizeClassifiersList(Set<EClassifier> classifierList) {
        Set<EClassifier> resultList = new HashSet<EClassifier>(classifierList);
        for (EClassifier classifier : classifierList) {
            if (classifier instanceof EClass) {
                for (EClass eClass : ((EClass) classifier).getEAllSuperTypes()) {
                    if (resultList.contains(eClass)) {
                        resultList.remove(eClass);
                    }
                }
            }
        }
        return resultList;
    }

    private EClassifier getClassifierForVariableWithPattern(Pattern pattern, Variable variable,
            int recursionCallingLevel) {
        Set<EClassifier> intermediateResultList = new HashSet<EClassifier>();
        for (PatternBody body : pattern.getBodies()) {
            EClassifier classifier = getClassifierForVariableWithPatternBody(body, variable, recursionCallingLevel,
                    null);
            if (classifier != null) {
                intermediateResultList.add(classifier);
            }
        }

        if (!intermediateResultList.isEmpty()) {
            if (intermediateResultList.size() == 1) {
                return (EClassifier) intermediateResultList.toArray()[0];
            } else {
                Set<EClassifier> resultSuperTypes = null;
                for (EClassifier classifier : intermediateResultList) {
                    if (classifier instanceof EClass) {
                        if (resultSuperTypes == null) {
                            resultSuperTypes = new LinkedHashSet<EClassifier>();
                            resultSuperTypes.addAll(((EClass) classifier).getEAllSuperTypes());
                            resultSuperTypes.add(classifier);
                        } else {
                            Set<EClassifier> nextSet = new LinkedHashSet<EClassifier>();
                            nextSet.addAll(((EClass) classifier).getEAllSuperTypes());
                            nextSet.add(classifier);
                            resultSuperTypes.retainAll(nextSet);
                        }
                    } else {
                        return null;
                    }
                }
                if (!resultSuperTypes.isEmpty()) {
                    Object[] result = resultSuperTypes.toArray();
                    return (EClassifier) result[result.length - 1];
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.viatra2.patternlanguage.types.IEMFTypeProvider#getPossibleClassifiersForVariableInBody(org.eclipse
     * .viatra2.patternlanguage.core.patternLanguage.PatternBody,
     * org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable)
     */
    @Override
    public Set<EClassifier> getPossibleClassifiersForVariableInBody(PatternBody patternBody, Variable variable) {
        Set<EClassifier> possibleClassifiersList = getClassifiersForVariableWithPatternBody(patternBody, variable, 0,
                null);
        if (possibleClassifiersList.size() <= 1) {
            return possibleClassifiersList;
        } else {
            return minimizeClassifiersList(possibleClassifiersList);
        }
    }

    private EClassifier getClassifierForVariableWithPatternBody(PatternBody patternBody, Variable variable,
            int recursionCallingLevel, Variable injectiveVariablePair) {
        Set<EClassifier> possibleClassifiersList = getClassifiersForVariableWithPatternBody(patternBody, variable,
                recursionCallingLevel, injectiveVariablePair);
        if (possibleClassifiersList.isEmpty()) {
            return null;
        } else {
            if (possibleClassifiersList.size() == 1) {
                return (EClassifier) possibleClassifiersList.toArray()[0];
            } else {
                possibleClassifiersList = minimizeClassifiersList(possibleClassifiersList);
                return (EClassifier) possibleClassifiersList.toArray()[0];
            }
        }
    }

    private Set<EClassifier> getClassifiersForVariableWithPatternBody(PatternBody patternBody, Variable variable,
            int recursionCallingLevel, Variable injectiveVariablePair) {
        Set<EClassifier> possibleClassifiersList = new HashSet<EClassifier>();
        EClassifier classifier = null;

        // Calculate it with just the variable only (works only for parameters)
        if (variable instanceof ParameterRef) {
            Variable referredParameter = ((ParameterRef) variable).getReferredParam();
            // if (referredParameter != null) {
            classifier = getClassifierForType(referredParameter.getType());
            if (classifier != null) {
                possibleClassifiersList.add(classifier);
            }
            // }
        } else {
            classifier = getClassifierForType(variable.getType());
            if (classifier != null) {
                possibleClassifiersList.add(classifier);
            }
        }

        // Calculate it from the constraints
        for (Constraint constraint : patternBody.getConstraints()) {
            if (constraint instanceof EClassifierConstraint) {
                EClassifierConstraint eClassifierConstraint = (EClassifierConstraint) constraint;
                if (isEqualVariables(variable, eClassifierConstraint.getVar())) {
                    Type type = eClassifierConstraint.getType();
                    classifier = getClassifierForType(type);
                    if (classifier != null) {
                        possibleClassifiersList.add(classifier);
                    }
                }
            } else if (constraint instanceof PathExpressionConstraint) {
                final PathExpressionHead pathExpressionHead = ((PathExpressionConstraint) constraint).getHead();
                // Src is the first parameter (example: E in EClass.name(E, N))
                final VariableReference firstvariableReference = pathExpressionHead.getSrc();
                if (isEqualVariables(variable, firstvariableReference)) {
                    Type type = pathExpressionHead.getType();
                    classifier = getClassifierForType(type);
                    if (classifier != null) {
                        possibleClassifiersList.add(classifier);
                    }
                }
                final ValueReference valueReference = pathExpressionHead.getDst();
                if (valueReference instanceof VariableValue) {
                    final VariableReference secondVariableReference = ((VariableValue) valueReference).getValue();
                    if (isEqualVariables(variable, secondVariableReference)) {
                        Type type = EMFPatternTypeUtil.getTypeFromPathExpressionTail(pathExpressionHead.getTail());
                        classifier = getClassifierForType(type);
                        if (classifier != null) {
                            possibleClassifiersList.add(classifier);
                        }
                    }
                }
            } else if (constraint instanceof CompareConstraint) {
                CompareConstraint compareConstraint = (CompareConstraint) constraint;
                if (CompareFeature.EQUALITY.equals(compareConstraint.getFeature())) {
                    ValueReference leftValueReference = compareConstraint.getLeftOperand();
                    ValueReference rightValueReference = compareConstraint.getRightOperand();
                    if (leftValueReference instanceof VariableValue) {
                        VariableValue leftVariableValue = (VariableValue) leftValueReference;
                        if (isEqualVariables(variable, leftVariableValue.getValue())) {
                            classifier = getClassifierForValueReference(rightValueReference, patternBody, variable,
                                    recursionCallingLevel, injectiveVariablePair);
                            if (classifier != null) {
                                possibleClassifiersList.add(classifier);
                            }
                        }
                    }
                    if (rightValueReference instanceof VariableValue) {
                        VariableValue rightVariableValue = (VariableValue) rightValueReference;
                        if (isEqualVariables(variable, rightVariableValue.getValue())) {
                            classifier = getClassifierForValueReference(leftValueReference, patternBody, variable,
                                    recursionCallingLevel, injectiveVariablePair);
                            if (classifier != null) {
                                possibleClassifiersList.add(classifier);
                            }
                        }
                    }
                }
            } else if (constraint instanceof PatternCompositionConstraint
                    && recursionCallingLevel < RECURSION_CALLING_LEVEL_LIMIT) {
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
                                EList<Variable> parameters = pattern.getParameters();
                                // In case of incorrect number of parameters we might check for non-existing parameters
                                if (parameters.size() > parameterIndex) {
                                    Variable variableInCalledPattern = parameters.get(parameterIndex);
                                    possibleClassifiersList.add(getClassifierForVariableWithPattern(pattern,
                                            variableInCalledPattern, recursionCallingLevel + 1));
                                }
                            }
                        }
                        parameterIndex++;
                    }
                }
            }
        }

        return possibleClassifiersList;
    }

    private EClassifier getClassifierForValueReference(ValueReference valueReference, PatternBody patternBody,
            Variable variable, int recursionCallingLevel, Variable injectiveVariablePair) {
        if (valueReference instanceof LiteralValueReference || valueReference instanceof ComputationValue) {
            return EMFPatternTypeUtil.getClassifierForLiteralAndComputationValueReference(valueReference);
        } else if (valueReference instanceof VariableValue) {
            VariableValue variableValue = (VariableValue) valueReference;
            Variable newPossibleInjectPair = variableValue.getValue().getVariable();
            if (!newPossibleInjectPair.equals(injectiveVariablePair)) {
                return getClassifierForVariableWithPatternBody(patternBody, newPossibleInjectPair,
                        recursionCallingLevel, variable);
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.viatra2.patternlanguage.types.IEMFTypeProvider# getClassifierForType
     * (org.eclipse.viatra2.patternlanguage.core.patternLanguage.Type)
     */
    @Override
    public EClassifier getClassifierForType(Type type) {
        return EMFPatternTypeUtil.getClassifierForType(type);
    }

    private static boolean isEqualVariables(Variable variable, VariableReference variableReference) {
        if (variable != null && variableReference != null) {
            final Variable variableReferenceVariable = variableReference.getVariable();
            if (equal(variable, variableReferenceVariable)
                    || equal(variable.getName(), variableReferenceVariable.getName())) {
                return true;
            }
        }
        return false;
    }

}
