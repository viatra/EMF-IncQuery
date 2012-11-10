/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.patternlanguage.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageScopeHelper;
import org.eclipse.viatra2.patternlanguage.ResolutionException;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.AggregatedValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CheckConstraint;
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
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ValueReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableValue;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassifierConstraint;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguagePackage;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EnumValue;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.scoping.IMetamodelProvider;
import org.eclipse.viatra2.patternlanguage.types.EMFPatternTypeUtil;
import org.eclipse.viatra2.patternlanguage.types.IEMFTypeProvider;
import org.eclipse.xtext.validation.Check;

import com.google.inject.Inject;

/**
 * Validators for EMFPattern Language.
 * <p>
 * Validators implemented:
 * </p>
 * <ul>
 * <li>Duplicate import of EPackage</li>
 * <li>Enum type validators</li>
 * </ul>
 * 
 * @author Mark Czotter, Zoltan Ujhelyi
 * 
 */
public class EMFPatternLanguageJavaValidator extends AbstractEMFPatternLanguageJavaValidator {

    public static final String DUPLICATE_IMPORT = "Duplicate import of ";

    @Inject
    private IMetamodelProvider metamodelProvider;

    @Inject
    private IEMFTypeProvider emfTypeProvider;

    @Check
    public void checkDuplicatePackageImports(PatternModel patternModel) {
        for (int i = 0; i < patternModel.getImportPackages().size(); ++i) {
            EPackage leftPackage = patternModel.getImportPackages().get(i).getEPackage();
            for (int j = i + 1; j < patternModel.getImportPackages().size(); ++j) {
                EPackage rightPackage = patternModel.getImportPackages().get(j).getEPackage();
                if (leftPackage.equals(rightPackage)) {
                    warning(DUPLICATE_IMPORT + leftPackage.getNsURI(),
                            EMFPatternLanguagePackage.Literals.PATTERN_MODEL__IMPORT_PACKAGES, i,
                            EMFIssueCodes.DUPLICATE_IMPORT);
                    warning(DUPLICATE_IMPORT + rightPackage.getNsURI(),
                            EMFPatternLanguagePackage.Literals.PATTERN_MODEL__IMPORT_PACKAGES, j,
                            EMFIssueCodes.DUPLICATE_IMPORT);
                }
            }
        }
    }

    @Check
    public void checkPackageImportGeneratedCode(PackageImport packageImport) {
        if (packageImport.getEPackage() != null
                && packageImport.getEPackage().getNsURI() != null
                && !metamodelProvider.isGeneratedCodeAvailable(packageImport.getEPackage(), packageImport.eResource()
                        .getResourceSet())) {
            warning(String.format(
                    "The generated code of the Ecore model %s cannot be found. Check the org.eclipse.emf.ecore.generated_package extension in the model project or consider setting up a generator model for the generated code to work.",
                    packageImport.getEPackage().getNsURI()),
                    EMFPatternLanguagePackage.Literals.PACKAGE_IMPORT__EPACKAGE,
                    EMFIssueCodes.IMPORT_WITH_GENERATEDCODE);
        }
    }

    private enum VariableReferenceClass {
        PositiveExistential, NegativeExistential, ReadOnly
    }

    private class ClassifiedVariableReferences {
        private final Variable referredVariable;
        private final boolean isLocalVariable;

        private final Map<VariableReferenceClass, Integer> classifiedReferenceCount;
        private final Set<Variable> equalsVariables;

        public Variable getReferredVariable() {
            return referredVariable;
        }

        public int getReferenceCount(VariableReferenceClass forClass) {
            Integer count = classifiedReferenceCount.get(forClass);
            return count == null ? 0 : count;
        }

        public int getReferenceCountSum() {
            int sum = 0;

            for (Integer val : classifiedReferenceCount.values()) {
                sum += val;
            }

            return sum;
        }

        public boolean isVariableLocal() {
            return isLocalVariable;
        }

        /**
         * @return true if the variable is single-use a named variable
         */
        public boolean isNamedSingleUse() {
            String name = referredVariable.getName();
            return name != null && name.startsWith("_") && !name.contains("<");
        }

        public Set<Variable> getEqualsVariables() {
            return equalsVariables;
        }

        /**
         * @return true if the variable is an unnamed single-use variable
         */
        public boolean isUnnamedSingleUse() {
            String name = referredVariable.getName();
            return name != null && name.startsWith("_") && name.contains("<");
        }

        public ClassifiedVariableReferences(Variable referredVariable, boolean isLocal) {
            this.referredVariable = referredVariable;
            this.isLocalVariable = isLocal;

            classifiedReferenceCount = new HashMap<VariableReferenceClass, Integer>();
            equalsVariables = new HashSet<Variable>();
        }

        public void incrementCounter(VariableReferenceClass forClass) {
            Integer count = classifiedReferenceCount.get(forClass);
            classifiedReferenceCount.put(forClass, count == null ? 1 : count + 1);
        }

        public void addEqualsVariable(Variable var) {
            equalsVariables.add(var);
        }
    }

    private void classifyVariableReference(ClassifiedVariableReferences classifiedReferences, VariableReference varRef) {
        EObject container = varRef.eContainer();

        while (container != null && !(container instanceof Constraint || container instanceof AggregatedValue)) {
            container = container.eContainer();
        }

        if (container instanceof EClassifierConstraint) {
            classifiedReferences.incrementCounter(VariableReferenceClass.PositiveExistential);
        } else if (container instanceof CheckConstraint) {
            classifiedReferences.incrementCounter(VariableReferenceClass.ReadOnly);
        } else if (container instanceof CompareConstraint) {
            CompareConstraint constraint = (CompareConstraint) container;

            if (constraint.getFeature() == CompareFeature.EQUALITY) {
                if (constraint.getLeftOperand() instanceof VariableValue
                        && constraint.getRightOperand() instanceof VariableValue) {
                    classifiedReferences.incrementCounter(VariableReferenceClass.ReadOnly);

                    VariableReference leftVarRef = ((VariableValue) constraint.getLeftOperand()).getValue();
                    VariableReference rightVarRef = ((VariableValue) constraint.getRightOperand()).getValue();
                    Variable leftVariable = leftVarRef.getVariable();
                    if (leftVariable instanceof ParameterRef) {
                        leftVariable = ((ParameterRef) leftVariable).getReferredParam();
                    }
                    Variable rightVariable = rightVarRef.getVariable();
                    if (rightVariable instanceof ParameterRef) {
                        rightVariable = ((ParameterRef) rightVariable).getReferredParam();
                    }
                    if (leftVariable != rightVariable) { // not the same
                                                         // variable
                        if (leftVarRef == varRef) {
                            classifiedReferences.addEqualsVariable(rightVariable);
                        } else if (rightVarRef == varRef) {
                            classifiedReferences.addEqualsVariable(leftVariable);
                        } else {
                            throw new UnsupportedOperationException(
                                    "The variable reference in neither the left, nor the right value of the compare constraint.");
                        }
                    }
                } else {
                    classifiedReferences.incrementCounter(VariableReferenceClass.PositiveExistential);
                }
            } else if (constraint.getFeature() == CompareFeature.INEQUALITY) {
                classifiedReferences.incrementCounter(VariableReferenceClass.ReadOnly);
            } else {
                throw new UnsupportedOperationException("Unrecognised compare feature.");
            }
        } else if (container instanceof PathExpressionConstraint) {
            // if (((PathExpressionConstraint) container).isNegative()) {
            // classifiedReferences
            // .incrementCounter(VariableReferenceClass.NegativeExistential);
            // } else {
            classifiedReferences.incrementCounter(VariableReferenceClass.PositiveExistential);
            // }
        } else if (container instanceof PatternCompositionConstraint) {
            if (((PatternCompositionConstraint) container).isNegative()) {
                classifiedReferences.incrementCounter(VariableReferenceClass.NegativeExistential);
            } else {
                classifiedReferences.incrementCounter(VariableReferenceClass.PositiveExistential);
            }
        } else if (container instanceof AggregatedValue) {
            classifiedReferences.incrementCounter(VariableReferenceClass.NegativeExistential);
        } else {
            throw new UnsupportedOperationException("Unrecognised constraint.");
        }
    }

    private Map<Variable, ClassifiedVariableReferences> processVariableReferences(PatternBody inBody) {
        Map<Variable, ClassifiedVariableReferences> classifiedVariableReferencesCollection = new HashMap<Variable, ClassifiedVariableReferences>();

        Pattern pattern = (Pattern) inBody.eContainer();

        for (Variable var : pattern.getParameters()) {
            final ClassifiedVariableReferences varRefs = new ClassifiedVariableReferences(var, false);
            classifiedVariableReferencesCollection.put(var, varRefs);
            if (var.getType() != null) {
                // type assertion on parameter
                varRefs.incrementCounter(VariableReferenceClass.PositiveExistential);
            }
        }

        TreeIterator<EObject> iter = inBody.eAllContents();
        while (iter.hasNext()) {
            EObject obj = iter.next();
            if (obj instanceof VariableReference) {
                VariableReference varRef = (VariableReference) obj;
                Variable variable = varRef.getVariable();
                boolean localVariable = true;
                // Replacing parameter references with real parameter counting
                if (variable instanceof ParameterRef) {
                    localVariable = false;
                    variable = ((ParameterRef) variable).getReferredParam();
                }
                ClassifiedVariableReferences classifiedVariableReferences = classifiedVariableReferencesCollection
                        .get(variable);
                if (classifiedVariableReferences == null) {
                    classifiedVariableReferences = new ClassifiedVariableReferences(variable, localVariable);
                    // All symbolic variables are already added.
                    classifiedVariableReferencesCollection.put(classifiedVariableReferences.getReferredVariable(),
                            classifiedVariableReferences);
                }
                classifyVariableReference(classifiedVariableReferences, varRef);
            } else if (obj instanceof CheckConstraint) {
                Set<Variable> vars = CorePatternLanguageHelper
                        .getReferencedPatternVariablesOfXExpression(((CheckConstraint) obj).getExpression());
                for (Variable var : vars) {
                    Variable variable = (var instanceof ParameterRef) ? ((ParameterRef) var).getReferredParam() : var;
                    ClassifiedVariableReferences classifiedVariableReferences = classifiedVariableReferencesCollection
                            .get(variable);
                    if (classifiedVariableReferences == null) {
                        classifiedVariableReferences = new ClassifiedVariableReferences(variable, true); // All
                        // All symbolic variables are already added.
                        classifiedVariableReferencesCollection.put(classifiedVariableReferences.getReferredVariable(),
                                classifiedVariableReferences);
                    }
                    classifiedVariableReferences.incrementCounter(VariableReferenceClass.ReadOnly);
                }
            }
        }
        return classifiedVariableReferencesCollection;
    }

    private String getPatternBodyName(PatternBody patternBody) {
        return (patternBody.getName() != null) ? patternBody.getName() : String.format("#%d",
                ((Pattern) patternBody.eContainer()).getBodies().indexOf(patternBody) + 1);
    }

    private boolean equalsVariableHasPositiveExistential(
            Map<Variable, ClassifiedVariableReferences> classifiedVariableReferencesMap, Set<Variable> equalsVariables) {
        if (!equalsVariables.isEmpty()) {
            for (Variable var : equalsVariables) {
                if (classifiedVariableReferencesMap.get(var).getReferenceCount(
                        VariableReferenceClass.PositiveExistential) != 0) {
                    return true;
                }
            }
        }

        return false;
    }

    @Check
    public void checkParametersNamed(Pattern pattern) {
        for (Variable var : pattern.getParameters()) {
            if (var.getName().startsWith("_")) {
                error("Parameter name must not start with _", var, PatternLanguagePackage.Literals.VARIABLE__NAME,
                        EMFIssueCodes.SINGLEUSE_PARAMETER);
            }
        }
    }

    @Check
    public void checkUnusedVariables(PatternBody patternBody) {
        Map<Variable, ClassifiedVariableReferences> classifiedVariableReferencesMap = processVariableReferences(patternBody);

        for (ClassifiedVariableReferences classifiedVariableReferences : classifiedVariableReferencesMap.values()) {
            Variable referredVariable = classifiedVariableReferences.getReferredVariable();
            if (referredVariable instanceof ParameterRef) {
                continue;
            }
            if (classifiedVariableReferences.isVariableLocal()) {
                if (classifiedVariableReferences.getReferenceCount(VariableReferenceClass.PositiveExistential) == 1
                        && classifiedVariableReferences.getReferenceCountSum() == 1
                        && !classifiedVariableReferences.isNamedSingleUse()
                        && !classifiedVariableReferences.isUnnamedSingleUse()) {
                    warning(String.format(
                            "Local variable '%s' is referenced only once. Is it mistyped? Start its name with '_' if intentional.",
                            referredVariable.getName()), referredVariable.getReferences().get(0), null,
                            EMFIssueCodes.LOCAL_VARIABLE_REFERENCED_ONCE);
                } else if (classifiedVariableReferences.getReferenceCountSum() > 1
                        && classifiedVariableReferences.isNamedSingleUse()) {
                    for (VariableReference ref : referredVariable.getReferences()) {
                        error(String.format("Named single-use variable %s used multiple times.",
                                referredVariable.getName()), ref, null,
                                EMFIssueCodes.ANONYM_VARIABLE_MULTIPLE_REFERENCE);

                    }
                } else if (classifiedVariableReferences.getReferenceCount(VariableReferenceClass.PositiveExistential) == 0) {
                    if (classifiedVariableReferences.getReferenceCount(VariableReferenceClass.NegativeExistential) == 0
                            && !equalsVariableHasPositiveExistential(classifiedVariableReferencesMap,
                                    classifiedVariableReferences.getEqualsVariables())) {
                        error(String.format(
                                "Local variable '%s' appears in read-only context(s) only, thus its value cannot be determined.",
                                referredVariable.getName()), referredVariable.getReferences().get(0), null,
                                EMFIssueCodes.LOCAL_VARIABLE_READONLY);
                    } else if (classifiedVariableReferences
                            .getReferenceCount(VariableReferenceClass.NegativeExistential) == 1
                            && classifiedVariableReferences.getReferenceCountSum() == 1
                            && !classifiedVariableReferences.isNamedSingleUse()
                            && !classifiedVariableReferences.isUnnamedSingleUse()) {
                        warning(String.format(
                                "Local variable '%s' will be quantified because it is used only here. Acknowledge this by prefixing its name with '_'.",
                                referredVariable.getName()), referredVariable.getReferences().get(0), null,
                                EMFIssueCodes.LOCAL_VARIABLE_QUANTIFIED_REFERENCE);
                    } else if (classifiedVariableReferences.getReferenceCountSum() > 1) {
                        error(String.format(
                                "Local variable '%s' has no positive reference, thus its value cannot be determined.",
                                referredVariable.getName()), referredVariable.getReferences().get(0), null,
                                EMFIssueCodes.LOCAL_VARIABLE_NO_POSITIVE_REFERENCE);
                    }
                }
            } else { // Symbolic variable:
                if (classifiedVariableReferences.getReferenceCountSum() == 0) {
                    error(String.format("Parameter '%s' is never referenced in body '%s'.", referredVariable.getName(),
                            getPatternBodyName(patternBody)), referredVariable, null,
                            EMFIssueCodes.SYMBOLIC_VARIABLE_NEVER_REFERENCED);
                } else if (classifiedVariableReferences.getReferenceCount(VariableReferenceClass.PositiveExistential) == 0
                        && !equalsVariableHasPositiveExistential(classifiedVariableReferencesMap,
                                classifiedVariableReferences.getEqualsVariables())) {
                    error(String.format("Parameter '%s' has no positive reference in body '%s'.",
                            referredVariable.getName(), getPatternBodyName(patternBody)), referredVariable, null,
                            EMFIssueCodes.SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE);
                }
            }
        }
    }

    @Override
    protected List<EPackage> getEPackages() {
        // PatternLanguagePackage must be added to the defaults, otherwise the core language validators not used in the
        // validation process
        List<EPackage> result = super.getEPackages();
        result.add(org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage.eINSTANCE);
        return result;
    }

    @Check
    public void checkEnumValues(EnumValue value) {
        if (value.eContainer() instanceof PathExpressionHead) {
            // If container is PathExpression check for enum type assignability
            EEnum enumType = value.getEnumeration();
            if (enumType == null && value.getLiteral() != null) {
                enumType = value.getLiteral().getEEnum();
            }
            PathExpressionHead expression = (PathExpressionHead) value.eContainer();
            try {
                EEnum expectedType = EMFPatternLanguageScopeHelper.calculateEnumerationType(expression);
                if (enumType != null && !expectedType.equals(enumType)) {
                    error(String.format("Inconsistent enumeration types: found %s but expected %s", enumType.getName(),
                            expectedType.getName()), value, EMFPatternLanguagePackage.Literals.ENUM_VALUE__ENUMERATION,
                            EMFIssueCodes.INVALID_ENUM_LITERAL);
                }
            } catch (ResolutionException e) {
                // EClassifier type = EMFPatternLanguageScopeHelper.calculateExpressionType(expression);
                error(String.format("Invalid enumeration constant %s", enumType.getName()), value,
                        EMFPatternLanguagePackage.Literals.ENUM_VALUE__ENUMERATION, EMFIssueCodes.INVALID_ENUM_LITERAL);
            }
        } // else {
          // If container is not a PathExpression, the entire enum type has to be specified
          // However, the it is checked during reference resolution
          // }
    }

    @Check
    public void checkPatternParametersType(Pattern pattern) {
        // This check used to be an error, now it is classified as a warning only
        for (Variable variable : pattern.getParameters()) {
            EClassifier classifierCorrect = emfTypeProvider.getClassifierForVariable(variable);
            EClassifier classifierDefined = emfTypeProvider.getClassifierForType(variable.getType());
            if (classifierCorrect == null || classifierDefined == null || classifierDefined.equals(classifierCorrect)) {
                // Either correct - they are the same, or other validator returns the type error
                return;
            } else {
                if (classifierCorrect instanceof EClass && classifierDefined instanceof EClass) {
                    if (((EClass) classifierDefined).getEAllSuperTypes().contains(classifierCorrect)) {
                        // Correct the defined is more specific than what the pattern needs
                        return;
                    }
                }
                // OK, issue warning now
                warning(String.format(
                        "Inconsistent parameter type definition, should be %s based on the pattern definition",
                        classifierCorrect.getName()), variable, null, EMFIssueCodes.PARAMETER_TYPE_INVALID);
            }
        }
    }

    @Check
    public void checkPatternVariablesType(PatternBody patternBody) {
        for (Variable variable : patternBody.getVariables()) {
            Set<EClassifier> possibleClassifiers = emfTypeProvider.getPossibleClassifiersForVariableInBody(patternBody,
                    variable);
            if (possibleClassifiers.size() > 1) {
                List<String> classifierNamesList = new ArrayList<String>();
                List<String> classifierPackagesList = new ArrayList<String>();
                for (Object element : possibleClassifiers.toArray()) {
                    EClassifier classifier = (EClassifier) element;
                    classifierNamesList.add(classifier.getName());
                    classifierPackagesList.add(classifier.getEPackage().getName());
                }
                Set<String> classifierNamesSet = new HashSet<String>(classifierNamesList);
                Set<String> classifierPackagesSet = new HashSet<String>(classifierPackagesList);
                if (classifierNamesSet.size() == 1 && classifierPackagesSet.size() == 1) {
                    error("Variable has a type which has multiple definitions: " + classifierNamesSet, variable
                            .getReferences().get(0), null, EMFIssueCodes.VARIABLE_TYPE_MULTIPLE_DECLARATION);
                } else {
                    EClassifier classifier = emfTypeProvider.getClassifierForPatternParameterVariable(variable);
                    if (classifier != null && possibleClassifiers.contains(classifier)) {
                        warning("Ambiguous variable type defintions: " + classifierNamesList + ", the parameter type ("
                                + classifier.getName() + ") is used now.", variable.getReferences().get(0), null,
                                EMFIssueCodes.VARIABLE_TYPE_INVALID_WARNING);
                    } else {
                        error("Inconsistent variable type defintions: " + classifierNamesList
                                + ", type cannot be selected.", variable.getReferences().get(0), null,
                                EMFIssueCodes.VARIABLE_TYPE_INVALID_ERROR);
                    }
                }
            }
        }
    }

    @Check
    public void checkForCartesianProduct(PatternBody patternBody) {
        List<Variable> variables = patternBody.getVariables();
        variables.removeAll(CorePatternLanguageHelper.getUnnamedRunningVariables(patternBody));
        UnionFindForVariables justPositiveUnionFindForVariables = new UnionFindForVariables(variables);
        UnionFindForVariables generalUnionFindForVariables = new UnionFindForVariables(variables);
        boolean isSecondRunNeeded = false;

        // First run
        // Just put together the real positive connections, and all of the general connections first
        for (Constraint constraint : patternBody.getConstraints()) {
            Set<Variable> positiveVariables = new HashSet<Variable>();
            Set<Variable> generalVariables = new HashSet<Variable>();
            if (constraint instanceof CompareConstraint) {
                // Equality and inequality (==, !=)
                CompareConstraint compareConstraint = (CompareConstraint) constraint;
                ValueReference leftValueReference = compareConstraint.getLeftOperand();
                ValueReference rightValueReference = compareConstraint.getRightOperand();
                Set<Variable> leftVariables = CorePatternLanguageHelper
                        .getVariablesFromValueReference(leftValueReference);
                Set<Variable> rightVariables = CorePatternLanguageHelper
                        .getVariablesFromValueReference(rightValueReference);
                if (CompareFeature.EQUALITY.equals(compareConstraint.getFeature())) {
                    // Equality ==
                    if (!isValueReferenceAggregated(leftValueReference)
                            && !isValueReferenceAggregated(rightValueReference)) {
                        positiveVariables.addAll(leftVariables);
                        positiveVariables.addAll(rightVariables);
                        generalVariables.addAll(leftVariables);
                        generalVariables.addAll(rightVariables);
                    } else {
                        isSecondRunNeeded = true;
                        generalVariables.addAll(leftVariables);
                        generalVariables.addAll(rightVariables);
                    }
                } else if (CompareFeature.INEQUALITY.equals(compareConstraint.getFeature())) {
                    // Inequality !=
                    generalVariables.addAll(leftVariables);
                    generalVariables.addAll(rightVariables);
                }
            } else if (constraint instanceof PatternCompositionConstraint) {
                // Find and neg-find constructs
                PatternCompositionConstraint patternCompositionConstraint = (PatternCompositionConstraint) constraint;
                if (!patternCompositionConstraint.isNegative()) {
                    // Positive composition (find)
                    for (ValueReference valueReference : patternCompositionConstraint.getCall().getParameters()) {
                        if (!isValueReferenceAggregated(valueReference)) {
                            positiveVariables.addAll(CorePatternLanguageHelper
                                    .getVariablesFromValueReference(valueReference));
                            generalVariables.addAll(CorePatternLanguageHelper
                                    .getVariablesFromValueReference(valueReference));
                        } else {
                            isSecondRunNeeded = true;
                            generalVariables.addAll(CorePatternLanguageHelper
                                    .getVariablesFromValueReference(valueReference));
                        }
                    }
                } else {
                    // Negative composition (neg find)
                    for (ValueReference valueReference : patternCompositionConstraint.getCall().getParameters()) {
                        generalVariables.addAll(CorePatternLanguageHelper
                                .getVariablesFromValueReference(valueReference));
                    }
                }
            } else if (constraint instanceof PathExpressionConstraint) {
                // Normal attribute-reference constraint
                PathExpressionConstraint pathExpressionConstraint = (PathExpressionConstraint) constraint;
                PathExpressionHead pathExpressionHead = pathExpressionConstraint.getHead();
                ValueReference valueReference = pathExpressionHead.getDst();
                if (!isValueReferenceAggregated(valueReference)) {
                    positiveVariables.addAll(CorePatternLanguageHelper.getVariablesFromValueReference(valueReference));
                    positiveVariables.add(pathExpressionHead.getSrc().getVariable());
                    generalVariables.addAll(CorePatternLanguageHelper.getVariablesFromValueReference(valueReference));
                    generalVariables.add(pathExpressionHead.getSrc().getVariable());
                } else {
                    isSecondRunNeeded = true;
                    generalVariables.addAll(CorePatternLanguageHelper.getVariablesFromValueReference(valueReference));
                    generalVariables.add(pathExpressionHead.getSrc().getVariable());
                }
            } else if (constraint instanceof CheckConstraint) {
                // Variables used together in check expression, always negative
                CheckConstraint checkConstraint = (CheckConstraint) constraint;
                generalVariables.addAll(CorePatternLanguageHelper
                        .getReferencedPatternVariablesOfXExpression(checkConstraint.getExpression()));
            }
            justPositiveUnionFindForVariables.unite(positiveVariables);
            generalUnionFindForVariables.unite(generalVariables);
        }

        // Second run
        // If variables in an aggregated formula (e.g.: count find Pattern(X,Y)) are in the same union in the positive
        // case then they are considered to be in a positive relation with the respective target as well
        // M == count find Pattern(X,Y), so M with X and Y is positive if X and Y is positive
        // If the aggregated contains unnamed/running vars it should be omitted during the positive relation checking
        if (isSecondRunNeeded) {
            for (Constraint constraint : patternBody.getConstraints()) {
                Set<Variable> positiveVariables = new HashSet<Variable>();
                if (constraint instanceof CompareConstraint) {
                    CompareConstraint compareConstraint = (CompareConstraint) constraint;
                    if (CompareFeature.EQUALITY.equals(compareConstraint.getFeature())) {
                        // Equality (==), with aggregates in it
                        ValueReference leftValueReference = compareConstraint.getLeftOperand();
                        ValueReference rightValueReference = compareConstraint.getRightOperand();
                        if (isValueReferenceAggregated(leftValueReference)
                                || isValueReferenceAggregated(rightValueReference)) {
                            Set<Variable> leftVariables = CorePatternLanguageHelper
                                    .getVariablesFromValueReference(leftValueReference);
                            Set<Variable> rightVariables = CorePatternLanguageHelper
                                    .getVariablesFromValueReference(rightValueReference);
                            if (justPositiveUnionFindForVariables.isSameUnion(leftVariables)) {
                                positiveVariables.addAll(leftVariables);
                            }
                            if (justPositiveUnionFindForVariables.isSameUnion(rightVariables)) {
                                positiveVariables.addAll(rightVariables);
                            }
                        }
                    }
                } else if (constraint instanceof PatternCompositionConstraint) {
                    PatternCompositionConstraint patternCompositionConstraint = (PatternCompositionConstraint) constraint;
                    if (!patternCompositionConstraint.isNegative()) {
                        // Positive composition (find), with aggregates in it
                        for (ValueReference valueReference : patternCompositionConstraint.getCall().getParameters()) {
                            Set<Variable> actualVariables = CorePatternLanguageHelper
                                    .getVariablesFromValueReference(valueReference);
                            if (justPositiveUnionFindForVariables.isSameUnion(actualVariables)) {
                                positiveVariables.addAll(actualVariables);
                            }
                        }
                    }
                } else if (constraint instanceof PathExpressionConstraint) {
                    // Normal attribute-reference constraint, with aggregates in it
                    PathExpressionConstraint pathExpressionConstraint = (PathExpressionConstraint) constraint;
                    PathExpressionHead pathExpressionHead = pathExpressionConstraint.getHead();
                    positiveVariables.add(pathExpressionHead.getSrc().getVariable());
                    ValueReference valueReference = pathExpressionHead.getDst();
                    Set<Variable> actualVariables = CorePatternLanguageHelper
                            .getVariablesFromValueReference(valueReference);
                    if (justPositiveUnionFindForVariables.isSameUnion(actualVariables)) {
                        positiveVariables.addAll(actualVariables);
                    }
                }
                justPositiveUnionFindForVariables.unite(positiveVariables);
            }
        }

        if (generalUnionFindForVariables.isMoreThanOneUnion()) {
            // Giving strict warning in this case
            warning("The pattern body contains isolated constraints (\"cartesian products\") that can lead to severe performance and memory footprint issues. The independent partitions are: "
                    + generalUnionFindForVariables.getCurrentPartitionsFormatted() + ".", patternBody, null,
                    EMFIssueCodes.CARTESIAN_STRICT_WARNING);
        } else if (justPositiveUnionFindForVariables.isMoreThanOneUnion()) {
            // Giving soft warning in this case
            warning("The pattern body contains constraints which are only loosely connected. This may negatively impact performance. The weakly dependent partitions are: "
                    + justPositiveUnionFindForVariables.getCurrentPartitionsFormatted(), patternBody, null,
                    EMFIssueCodes.CARTESIAN_SOFT_WARNING);
        }
    }

    private static boolean isValueReferenceAggregated(ValueReference valueReference) {
        if (valueReference != null && valueReference instanceof AggregatedValue) {
            return true;
        }
        return false;
    }

    @Check
    public void checkForWrongLiteralAndComputationValuesInCompareConstraints(CompareConstraint compareConstraint) {
        // Equality and inequality (==, !=)
        ValueReference leftValueReference = compareConstraint.getLeftOperand();
        ValueReference rightValueReference = compareConstraint.getRightOperand();
        if (leftValueReference instanceof LiteralValueReference || leftValueReference instanceof ComputationValue
                || rightValueReference instanceof LiteralValueReference
                || rightValueReference instanceof ComputationValue) {
            EClassifier leftClassifier = EMFPatternTypeUtil
                    .getClassifierForLiteralAndComputationValueReference(leftValueReference);
            EClassifier rightClassifier = EMFPatternTypeUtil
                    .getClassifierForLiteralAndComputationValueReference(rightValueReference);
            if (leftClassifier != null && rightClassifier != null
                    && !leftClassifier.getInstanceClass().equals(rightClassifier.getInstanceClass())) {
                error("The types of the literal/computational values are different: "
                        + leftClassifier.getInstanceClassName() + ", " + rightClassifier.getInstanceClassName() + ".",
                        compareConstraint, null, EMFIssueCodes.LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_COMPARE);
            }
        }
    }

    @Check
    public void checkForWrongLiteralAndComputationValuesInPathExpressionConstraints(
            PathExpressionConstraint pathExpressionConstraint) {
        // Normal attribute-reference constraint
        PathExpressionHead pathExpressionHead = pathExpressionConstraint.getHead();
        ValueReference valueReference = pathExpressionHead.getDst();
        if (valueReference instanceof LiteralValueReference || valueReference instanceof ComputationValue) {
            EClassifier inputClassifier = EMFPatternTypeUtil
                    .getClassifierForLiteralAndComputationValueReference(valueReference);
            EClassifier typeClassifier = EMFPatternTypeUtil.getClassifierForType(EMFPatternTypeUtil
                    .getTypeFromPathExpressionTail(pathExpressionHead.getTail()));
            if (inputClassifier != null && typeClassifier != null
                    && !inputClassifier.getInstanceClass().equals(typeClassifier.getInstanceClass())) {
                error("The type infered from the path expression (" + typeClassifier.getInstanceClassName()
                        + ") is different from the input literal/computational value ("
                        + inputClassifier.getInstanceClassName() + ").", pathExpressionConstraint, null,
                        EMFIssueCodes.LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATH_EXPRESSION);
            }
        }
    }

    @Check
    public void checkForWrongLiteralAndComputationValuesInPatternCalls(PatternCall patternCall) {
        // Find and neg find (including count find as well)
        for (ValueReference valueReference : patternCall.getParameters()) {
            if (valueReference instanceof LiteralValueReference || valueReference instanceof ComputationValue) {
                Pattern pattern = patternCall.getPatternRef();
                Variable variable = pattern.getParameters().get(patternCall.getParameters().indexOf(valueReference));
                EClassifier typeClassifier = emfTypeProvider.getClassifierForVariable(variable);
                EClassifier inputClassifier = EMFPatternTypeUtil
                        .getClassifierForLiteralAndComputationValueReference(valueReference);
                if (inputClassifier != null && typeClassifier != null
                        && !inputClassifier.getInstanceClass().equals(typeClassifier.getInstanceClass())) {
                    error("The type infered from the called pattern (" + typeClassifier.getInstanceClassName()
                            + ") is different from the input literal/computational value ("
                            + inputClassifier.getInstanceClassName() + ").", patternCall, null,
                            EMFIssueCodes.LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATTERN_CALL);
                }
            }
        }
    }

    @Check
    public void checkForWrongVariablesInXExpressions(CheckConstraint checkConstraint) {
        for (Variable variable : CorePatternLanguageHelper.getReferencedPatternVariablesOfXExpression(checkConstraint
                .getExpression())) {
            EClassifier classifier = emfTypeProvider.getClassifierForVariable(variable);
            if (!(classifier instanceof EDataType)) {
                error("Only simple EDataTypes are allowed in check expressions. The variable " + variable.getName()
                        + "'s type is " + classifier.getName() + " now.", checkConstraint, null,
                        EMFIssueCodes.CHECK_CONSTRAINT_SCALAR_VARIABLE_ERROR);
            }
        }
    }

}
