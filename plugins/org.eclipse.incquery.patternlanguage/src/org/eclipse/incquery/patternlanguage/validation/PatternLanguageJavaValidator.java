/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.validation;

import static org.eclipse.xtext.util.Strings.equal;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.patternlanguage.annotations.IPatternAnnotationValidator;
import org.eclipse.incquery.patternlanguage.annotations.PatternAnnotationProvider;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.AggregatedValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.AnnotationParameter;
import org.eclipse.incquery.patternlanguage.patternLanguage.BoolValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.CheckConstraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.CompareConstraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.CompareFeature;
import org.eclipse.incquery.patternlanguage.patternLanguage.Constraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.DoubleValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.IntValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.ListValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.ParameterRef;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternCall;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternCompositionConstraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternLanguagePackage;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternModel;
import org.eclipse.incquery.patternlanguage.patternLanguage.StringValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.ValueReference;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.patternlanguage.patternLanguage.VariableReference;
import org.eclipse.incquery.patternlanguage.patternLanguage.VariableValue;
import org.eclipse.incquery.patternlanguage.validation.VariableReferenceCount.ReferenceType;
import org.eclipse.incquery.patternlanguage.validation.whitelist.XBasePureCheckerUtil;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.util.Primitives;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XFeatureCall;
import org.eclipse.xtext.xbase.typing.ITypeProvider;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/**
 * Validators for Core Pattern Language.
 * <p>
 * Validators implemented:
 * </p>
 * <ul>
 * <li>Duplicate parameter in pattern declaration</li>
 * <li>Duplicate pattern definition (name duplication only, better calculation is needed)</li>
 * <li>Pattern call parameter checking (only the number of the parameters, types not supported yet)</li>
 * <li>Empty PatternBody check</li>
 * </ul>
 * 
 * @author Mark Czotter
 * 
 */
@SuppressWarnings("restriction")
public class PatternLanguageJavaValidator extends AbstractPatternLanguageJavaValidator implements IIssueCallback {

    public static final String DUPLICATE_VARIABLE_MESSAGE = "Duplicate parameter ";
    public static final String DUPLICATE_PATTERN_DEFINITION_MESSAGE = "Duplicate pattern ";
    public static final String UNKNOWN_ANNOTATION_ATTRIBUTE = "Undefined annotation attribute ";
    public static final String MISSING_ANNOTATION_ATTRIBUTE = "Required attribute missing ";
    public static final String ANNOTATION_PARAMETER_TYPE_ERROR = "Invalid parameter type %s. Expected %s";
    public static final String TRANSITIVE_CLOSURE_ARITY_IN_PATTERNCALL = "The pattern %s is not of binary arity (it has %d parameters), therefore transitive closure is not supported.";
    public static final String TRANSITIVE_CLOSURE_ONLY_IN_POSITIVE_COMPOSITION = "Transitive closure of %s is currently only allowed in simple positive pattern calls (no negation or aggregation).";
    public static final String UNUSED_PRIVATE_PATTERN_MESSAGE = "The pattern '%s' is never used locally.";

    @Inject
    private PatternAnnotationProvider annotationProvider;
    @Inject
    private ITypeProvider provider;
    @Inject
    private Primitives primitives;

    @Check
    public void checkPatternParameters(Pattern pattern) {
        if (pattern.getParameters().size() == 0) {
            warning("Parameterless patterns can only be used to check for existence of a condition.",
                    PatternLanguagePackage.Literals.PATTERN__NAME, IssueCodes.MISSING_PATTERN_PARAMETERS);
            // As no duplicate parameters are available, returning now
            return;
        }
        for (int i = 0; i < pattern.getParameters().size(); ++i) {
            String leftParameterName = pattern.getParameters().get(i).getName();
            for (int j = i + 1; j < pattern.getParameters().size(); ++j) {
                if (equal(leftParameterName, pattern.getParameters().get(j).getName())) {
                    error(DUPLICATE_VARIABLE_MESSAGE + leftParameterName,
                            PatternLanguagePackage.Literals.PATTERN__PARAMETERS, i,
                            IssueCodes.DUPLICATE_PATTERN_PARAMETER_NAME);
                    error(DUPLICATE_VARIABLE_MESSAGE + leftParameterName,
                            PatternLanguagePackage.Literals.PATTERN__PARAMETERS, j,
                            IssueCodes.DUPLICATE_PATTERN_PARAMETER_NAME);
                }
            }
        }
    }

    @Check
    public void checkPrivatePatternUsage(Pattern pattern) {
        if (CorePatternLanguageHelper.isPrivate(pattern) && !isLocallyUsed(pattern, pattern.eContainer())) {
            String message = String.format(UNUSED_PRIVATE_PATTERN_MESSAGE, pattern.getName());
            warning(message, PatternLanguagePackage.Literals.PATTERN__NAME, IssueCodes.UNUSED_PRIVATE_PATTERN);
        }
    }

    @Check
    public void checkPatternCallParameters(PatternCall call) {
        if (call.getPatternRef() != null && call.getPatternRef().getName() != null && call.getParameters() != null) {
            final int definitionParameterSize = call.getPatternRef().getParameters().size();
            final int callParameterSize = call.getParameters().size();
            if (definitionParameterSize != callParameterSize) {
                error("The pattern " + getFormattedPattern(call.getPatternRef())
                        + " is not applicable for the arguments(" + getFormattedArgumentsList(call) + ")",
                        PatternLanguagePackage.Literals.PATTERN_CALL__PATTERN_REF,
                        IssueCodes.WRONG_NUMBER_PATTERNCALL_PARAMETER);
            }
        }
    }

    @Check
    public void checkApplicabilityOfTransitiveClosureInPatternCall(PatternCall call) {
        final Pattern patternRef = call.getPatternRef();
        final EObject eContainer = call.eContainer();
        if (patternRef != null && call.isTransitive()) {
            if (patternRef.getParameters() != null) {
                final int arity = patternRef.getParameters().size();
                if (2 != arity) {
                    error(String
                            .format(TRANSITIVE_CLOSURE_ARITY_IN_PATTERNCALL, getFormattedPattern(patternRef), arity),
                            PatternLanguagePackage.Literals.PATTERN_CALL__TRANSITIVE,
                            IssueCodes.TRANSITIVE_PATTERNCALL_ARITY);
                }
            }
            if (eContainer != null
                    && (!(eContainer instanceof PatternCompositionConstraint) || ((PatternCompositionConstraint) eContainer)
                            .isNegative())) {
                error(String.format(TRANSITIVE_CLOSURE_ONLY_IN_POSITIVE_COMPOSITION, getFormattedPattern(patternRef)),
                        PatternLanguagePackage.Literals.PATTERN_CALL__TRANSITIVE,
                        IssueCodes.TRANSITIVE_PATTERNCALL_NOT_APPLICABLE);
            }

        }

    }

    @Check
    public void checkPatterns(PatternModel model) {
        if (model.getPatterns() != null && !model.getPatterns().isEmpty()) {
            // TODO: more precise calculation is needed for duplicate patterns
            // (number and type of pattern parameters)
            for (int i = 0; i < model.getPatterns().size(); ++i) {
                Pattern leftPattern = model.getPatterns().get(i);
                String leftPatternName = leftPattern.getName();
                for (int j = i + 1; j < model.getPatterns().size(); ++j) {
                    Pattern rightPattern = model.getPatterns().get(j);
                    String rightPatternName = rightPattern.getName();
                    if (equal(leftPatternName, rightPatternName)) {
                        error(DUPLICATE_PATTERN_DEFINITION_MESSAGE + leftPatternName, leftPattern,
                                PatternLanguagePackage.Literals.PATTERN__NAME, IssueCodes.DUPLICATE_PATTERN_DEFINITION);
                        error(DUPLICATE_PATTERN_DEFINITION_MESSAGE + rightPatternName, rightPattern,
                                PatternLanguagePackage.Literals.PATTERN__NAME, IssueCodes.DUPLICATE_PATTERN_DEFINITION);
                    }
                }
            }
        }
    }

    @Check
    public void checkPatternBody(PatternBody body) {
        if (body.getConstraints().isEmpty()) {
            String bodyName = getName(body);
            if (bodyName == null) {
                Pattern pattern = ((Pattern) body.eContainer());
                String patternName = pattern.getName();
                error("A patternbody of " + patternName + " is empty", body,
                        PatternLanguagePackage.Literals.PATTERN_BODY__CONSTRAINTS, IssueCodes.PATTERN_BODY_EMPTY);
            } else {
                error("The patternbody " + bodyName + " cannot be empty", body,
                        PatternLanguagePackage.Literals.PATTERN_BODY__NAME, IssueCodes.PATTERN_BODY_EMPTY);
            }
        }
    }

    @Check
    public void checkAnnotation(Annotation annotation) {
        if (annotationProvider.hasValidator(annotation.getName())) {
            IPatternAnnotationValidator validator = annotationProvider.getValidator(annotation.getName());
            // Check for unknown annotation attributes
            for (AnnotationParameter unknownParameter : validator.getUnknownAttributes(annotation)) {
                error(UNKNOWN_ANNOTATION_ATTRIBUTE + unknownParameter.getName(), unknownParameter,
                        PatternLanguagePackage.Literals.ANNOTATION_PARAMETER__NAME,
                        annotation.getParameters().indexOf(unknownParameter), IssueCodes.UNKNOWN_ANNOTATION_PARAMETER);
            }
            // Check for missing mandatory attributes
            for (String missingAttribute : validator.getMissingMandatoryAttributes(annotation)) {
                error(MISSING_ANNOTATION_ATTRIBUTE + missingAttribute, annotation,
                        PatternLanguagePackage.Literals.ANNOTATION__PARAMETERS,
                        IssueCodes.MISSING_REQUIRED_ANNOTATION_PARAMETER);
            }
            // Check for annotation parameter types
            for (AnnotationParameter parameter : annotation.getParameters()) {
                Class<? extends ValueReference> expectedParameterType = validator.getExpectedParameterType(parameter);
                if (expectedParameterType != null && parameter.getValue() != null
                        && !expectedParameterType.isAssignableFrom(parameter.getValue().getClass())) {
                    error(String.format(ANNOTATION_PARAMETER_TYPE_ERROR, getTypeName(parameter.getValue().getClass()),
                            getTypeName(expectedParameterType)), parameter,
                            PatternLanguagePackage.Literals.ANNOTATION_PARAMETER__NAME, annotation.getParameters()
                                    .indexOf(parameter), IssueCodes.MISTYPED_ANNOTATION_PARAMETER);
                }
            }
            // Execute extra validation
            if (validator.getAdditionalValidator() != null) {
                validator.getAdditionalValidator().executeAdditionalValidation(annotation, this);
            }
        } else {
            warning("Unknown annotation " + annotation.getName(), PatternLanguagePackage.Literals.ANNOTATION__NAME,
                    IssueCodes.UNKNOWN_ANNOTATION);
        }
    }

    @Check
    public void checkCompareConstraints(CompareConstraint constraint) {
        ValueReference op1 = constraint.getLeftOperand();
        ValueReference op2 = constraint.getRightOperand();
        if (op1 == null || op2 == null) {
            return;
        }

        boolean op1Constant = PatternLanguagePackage.Literals.LITERAL_VALUE_REFERENCE.isSuperTypeOf(op1.eClass());
        boolean op2Constant = PatternLanguagePackage.Literals.LITERAL_VALUE_REFERENCE.isSuperTypeOf(op2.eClass());
        boolean op1Variable = PatternLanguagePackage.Literals.VARIABLE_VALUE.isSuperTypeOf(op1.eClass());
        boolean op2Variable = PatternLanguagePackage.Literals.VARIABLE_VALUE.isSuperTypeOf(op2.eClass());

        // If both operands are constant literals, issue a warning
        if (op1Constant && op2Constant) {
            warning("Both operands are constants - constraint is always true or always false.",
                    PatternLanguagePackage.Literals.COMPARE_CONSTRAINT__LEFT_OPERAND,
                    IssueCodes.CONSTANT_COMPARE_CONSTRAINT);
            warning("Both operands are constants - constraint is always true or always false.",
                    PatternLanguagePackage.Literals.COMPARE_CONSTRAINT__RIGHT_OPERAND,
                    IssueCodes.CONSTANT_COMPARE_CONSTRAINT);
        }
        // If both operands are the same, issues a warning
        if (op1Variable && op2Variable) {
            VariableValue op1v = (VariableValue) op1;
            VariableValue op2v = (VariableValue) op2;
            if (op1v.getValue().getVar().equals(op2v.getValue().getVar())) {
                warning("Comparing a variable with itself.",
                        PatternLanguagePackage.Literals.COMPARE_CONSTRAINT__LEFT_OPERAND,
                        IssueCodes.SELF_COMPARE_CONSTRAINT);
                warning("Comparing a variable with itself.",
                        PatternLanguagePackage.Literals.COMPARE_CONSTRAINT__RIGHT_OPERAND,
                        IssueCodes.SELF_COMPARE_CONSTRAINT);
            }
        }
    }

    private String getName(PatternBody body) {
        if (body.getName() != null && !body.getName().isEmpty()) {
            return "'" + body.getName() + "'";
        }
        return null;
    }

    private String getTypeName(Class<? extends ValueReference> typeClass) {
        if (IntValue.class.isAssignableFrom(typeClass)) {
            return "Integer";
        } else if (DoubleValue.class.isAssignableFrom(typeClass)) {
            return "Double";
        } else if (BoolValue.class.isAssignableFrom(typeClass)) {
            return "Boolean";
        } else if (StringValue.class.isAssignableFrom(typeClass)) {
            return "String";
        } else if (ListValue.class.isAssignableFrom(typeClass)) {
            return "List";
        } else if (VariableValue.class.isAssignableFrom(typeClass)) {
            return "Variable";
        }
        return "UNDEFINED";
    }

    private String getConstantAsString(ValueReference ref) {
        if (ref instanceof IntValue) {
            return Integer.toString(((IntValue) ref).getValue());
        } else if (ref instanceof DoubleValue) {
            return Double.toString(((DoubleValue) ref).getValue());
        } else if (ref instanceof BoolValue) {
            return Boolean.toString(((BoolValue) ref).isValue());
        } else if (ref instanceof StringValue) {
            return "\"" + ((StringValue) ref).getValue() + "\"";
        } else if (ref instanceof ListValue) {
            StringBuilder sb = new StringBuilder();
            sb.append("{ ");
            for (Iterator<ValueReference> iter = ((ListValue) ref).getValues().iterator(); iter.hasNext();) {
                sb.append(getConstantAsString(iter.next()));
                if (iter.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append("}");
            return sb.toString();
        } else if (ref instanceof VariableValue) {
            return ((VariableValue) ref).getValue().getVar();
        }
        return "UNDEFINED";
    }

    private String getFormattedPattern(Pattern pattern) {
        StringBuilder builder = new StringBuilder();
        builder.append(pattern.getName());
        builder.append("(");
        for (Iterator<Variable> iter = pattern.getParameters().iterator(); iter.hasNext();) {
            builder.append(iter.next().getName());
            if (iter.hasNext()) {
                builder.append(", ");
            }
        }
        builder.append(")");
        return builder.toString();
    }

    protected String getFormattedArgumentsList(PatternCall call) {
        StringBuilder builder = new StringBuilder();
        for (Iterator<ValueReference> iter = call.getParameters().iterator(); iter.hasNext();) {
            ValueReference parameter = iter.next();
            builder.append(getConstantAsString(parameter));
            if (iter.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    @Check
    public void checkPackageDeclaration(PatternModel model) {
        String packageName = model.getPackageName();
        if (packageName != null && !packageName.equals(packageName.toLowerCase())) {
            error("Only lowercase package names supported",
                    PatternLanguagePackage.Literals.PATTERN_MODEL__PACKAGE_NAME, IssueCodes.LOWERCASE_PATTERN_NAME);
        }
    }

    @Check
    public void checkReturnTypeOfCheckConstraints(CheckConstraint checkConstraint) {
        XExpression xExpression = checkConstraint.getExpression();
        if (xExpression != null) {
            JvmTypeReference type = provider.getCommonReturnType(xExpression, true);
            String simpleName = primitives.asPrimitiveIfWrapperType(type).getSimpleName();
            if (!simpleName.equals("boolean")) {
                error("Check expressions must return boolean instead of " + type.getSimpleName(), checkConstraint,
                        PatternLanguagePackage.Literals.CHECK_CONSTRAINT__EXPRESSION, IssueCodes.CHECK_MUST_BE_BOOLEAN);
            }
        }
    }

    @Check
    public void checkVariableUsageCounters(PatternBody body) {
        Map<Variable, VariableReferenceCount> refCounters = calculateUsageCounts(body);
        UnionFindForVariables variableUnions = calculateEqualVariables(body);
        for (Variable var : body.getVariables()) {
            if (var instanceof ParameterRef) {
                checkParameterUsageCounter((ParameterRef) var, refCounters, variableUnions, body);
            } else {
                checkLocalVariableUsageCounter(var, refCounters, variableUnions);
            }
        }
    }

    private void checkParameterUsageCounter(ParameterRef var, Map<Variable, VariableReferenceCount> refCounters,
            UnionFindForVariables variableUnions, PatternBody body) {
        Variable parameter = var.getReferredParam();
        if (refCounters.get(var).getReferenceCount() == 0) {
            error(String.format("Parameter '%s' is never referenced in body '%s'.", parameter.getName(),
                    getPatternBodyName(body)), parameter, null, IssueCodes.SYMBOLIC_VARIABLE_NEVER_REFERENCED);
        } else if (refCounters.get(var).getReferenceCount(ReferenceType.POSITIVE) == 0
                && getReferenceCount(var, ReferenceType.POSITIVE, refCounters, variableUnions) == 0) {
            error(String.format("Parameter '%s' has no positive reference in body '%s'.", var.getName(),
                    getPatternBodyName(body)), var, null, IssueCodes.SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE);
        }
    }

    private void checkLocalVariableUsageCounter(Variable var, Map<Variable, VariableReferenceCount> refCounters,
            UnionFindForVariables variableUnions) {
        if (refCounters.get(var).getReferenceCount(ReferenceType.POSITIVE) == 1
                && refCounters.get(var).getReferenceCount() == 1 && !isNamedSingleUse(var)
                && !isUnnamedSingleUseVariable(var)) {
            warning(String.format(
                    "Local variable '%s' is referenced only once. Is it mistyped? Start its name with '_' if intentional.",
                    var.getName()), var.getReferences().get(0), null, IssueCodes.LOCAL_VARIABLE_REFERENCED_ONCE);
        } else if (refCounters.get(var).getReferenceCount() > 1 && isNamedSingleUse(var)) {
            for (VariableReference ref : var.getReferences()) {
                error(String.format("Named single-use variable %s used multiple times.", var.getName()), ref, null,
                        IssueCodes.ANONYM_VARIABLE_MULTIPLE_REFERENCE);

            }
        } else if (refCounters.get(var).getReferenceCount(ReferenceType.POSITIVE) == 0) {
            if (refCounters.get(var).getReferenceCount(ReferenceType.NEGATIVE) == 0) {
                error(String.format(
                        "Local variable '%s' appears in read-only context(s) only, thus its value cannot be determined.",
                        var.getName()), var.getReferences().get(0), null, IssueCodes.LOCAL_VARIABLE_READONLY);
            } else if (refCounters.get(var).getReferenceCount(ReferenceType.NEGATIVE) == 1
                    && refCounters.get(var).getReferenceCount() == 1 && !isNamedSingleUse(var)
                    && !isUnnamedSingleUseVariable(var)) {
                warning(String.format(
                        "Local variable '%s' will be quantified because it is used only here. Acknowledge this by prefixing its name with '_'.",
                        var.getName()), var.getReferences().get(0), null,
                        IssueCodes.LOCAL_VARIABLE_QUANTIFIED_REFERENCE);
            } else if (refCounters.get(var).getReferenceCount() > 1) {
                error(String.format(
                        "Local variable '%s' has no positive reference, thus its value cannot be determined.",
                        var.getName()), var.getReferences().get(0), null,
                        IssueCodes.LOCAL_VARIABLE_NO_POSITIVE_REFERENCE);
            }
        }
    }

    private int getReferenceCount(Variable var, Map<Variable, VariableReferenceCount> refCounters,
            UnionFindForVariables variableUnions) {
        int sum = 0;
        for (Variable unionVar : variableUnions.getPartitionOfVariable(var)) {
            sum += refCounters.get(unionVar).getReferenceCount();
        }
        return sum;
    }

    private int getReferenceCount(Variable var, ReferenceType type, Map<Variable, VariableReferenceCount> refCounters,
            UnionFindForVariables variableUnions) {
        int sum = 0;
        for (Variable unionVar : variableUnions.getPartitionOfVariable(var)) {
            sum += refCounters.get(unionVar).getReferenceCount(type);
        }
        return sum;
    }

    private Map<Variable, VariableReferenceCount> calculateUsageCounts(PatternBody body) {
        Map<Variable, VariableReferenceCount> refCounters = new Hashtable<Variable, VariableReferenceCount>();
        EList<Variable> variables = body.getVariables();
        for (Variable var : variables) {
            boolean isParameter = var instanceof ParameterRef;
            refCounters.put(var, new VariableReferenceCount(var, isParameter));
        }
        TreeIterator<EObject> it = body.eAllContents();
        while (it.hasNext()) {
            EObject obj = it.next();
            if (obj instanceof CheckConstraint) {
                CheckConstraint constraint = (CheckConstraint) obj;
                for (Variable var : CorePatternLanguageHelper.getReferencedPatternVariablesOfXExpression(constraint
                        .getExpression())) {
                    refCounters.get(var).incrementCounter(ReferenceType.READ_ONLY);
                }
                it.prune();
            }
            if (obj instanceof VariableReference) {
                refCounters.get(((VariableReference) obj).getVariable()).incrementCounter(
                        classifyReference((VariableReference) obj));
            }
        }
        return refCounters;
    }

    private UnionFindForVariables calculateEqualVariables(PatternBody body) {
        UnionFindForVariables unions = new UnionFindForVariables(body.getVariables());
        TreeIterator<EObject> it = body.eAllContents();
        while (it.hasNext()) {
            EObject obj = it.next();
            if (obj instanceof CompareConstraint) {
                CompareConstraint constraint = (CompareConstraint) obj;
                if (constraint.getFeature() == CompareFeature.EQUALITY) {
                    ValueReference left = constraint.getLeftOperand();
                    ValueReference right = constraint.getRightOperand();
                    if (left instanceof VariableValue && right instanceof VariableValue) {
                        unions.unite(ImmutableSet.of(((VariableValue) left).getValue().getVariable(),
                                ((VariableValue) right).getValue().getVariable()));
                    }
                }
                it.prune();
            } else if (obj instanceof Constraint) {
                it.prune();
            }
        }
        return unions;
    }

    private String getPatternBodyName(PatternBody patternBody) {
        return (patternBody.getName() != null) ? patternBody.getName() : String.format("#%d",
                ((Pattern) patternBody.eContainer()).getBodies().indexOf(patternBody) + 1);
    }

    private ReferenceType classifyReference(VariableReference ref) {
        EObject parent = ref;
        while (parent != null && !(parent instanceof Constraint || parent instanceof AggregatedValue)) {
            parent = parent.eContainer();
        }

        if (parent instanceof CheckConstraint) {
            return ReferenceType.READ_ONLY;
        } else if (parent instanceof CompareConstraint) {
            CompareConstraint constraint = (CompareConstraint) parent;
            if (constraint.getFeature() == CompareFeature.EQUALITY) {
                if (constraint.getLeftOperand() instanceof VariableValue
                        && !(constraint.getRightOperand() instanceof VariableValue)) {

                    if (ref.equals(((VariableValue) constraint.getLeftOperand()).getValue())) {
                        return ReferenceType.POSITIVE;
                    }
                }
                if (constraint.getRightOperand() instanceof VariableValue
                        && !(constraint.getLeftOperand() instanceof VariableValue)) {

                    if (ref.equals(((VariableValue) constraint.getRightOperand()).getValue())) {
                        return ReferenceType.POSITIVE;
                    }
                }
            }
            return ReferenceType.READ_ONLY;
        } else if (parent instanceof PatternCompositionConstraint
                && ((PatternCompositionConstraint) parent).isNegative()) {
            return ReferenceType.NEGATIVE;
        } else if (parent instanceof AggregatedValue) {
            return ReferenceType.NEGATIVE;
        }
        // Other constraints use positive references
        return ReferenceType.POSITIVE;
    }

    /**
     * @return true if the variable is single-use a named variable
     */
    public boolean isNamedSingleUse(Variable variable) {
        String name = variable.getName();
        return name != null && name.startsWith("_") && !name.contains("<");
    }

    /**
     * @return true if the variable is an unnamed single-use variable
     */
    public boolean isUnnamedSingleUseVariable(Variable variable) {
        String name = variable.getName();
        return name != null && name.startsWith("_") && name.contains("<");
    }

    @Check
    public void checkForInpureJavaCallsInCheckConstraints(CheckConstraint checkConstraint) {
        XExpression xExpression = checkConstraint.getExpression();
        Set<String> elementsWithWarnings = new HashSet<String>();
        if (xExpression != null) {
            TreeIterator<EObject> eAllContents = xExpression.eAllContents();
            while (eAllContents.hasNext()) {
                EObject nextEObject = eAllContents.next();
                if (nextEObject instanceof XFeatureCall) {
                    XFeatureCall xFeatureCall = (XFeatureCall) nextEObject;
                    JvmIdentifiableElement jvmIdentifiableElement = xFeatureCall.getFeature();
                    if (jvmIdentifiableElement instanceof JvmOperation) {
                        JvmOperation jvmOperation = (JvmOperation) jvmIdentifiableElement;
                        if (XBasePureCheckerUtil.isImpureElement(jvmOperation)) {
                            elementsWithWarnings.add(jvmOperation.getQualifiedName());
                        }
                    }
                }
            }
        }
        if (!elementsWithWarnings.isEmpty()) {
            if (elementsWithWarnings.size() > 1) {
                warning("There are potentially problematic java calls in the check expression. Custom java calls without @Pure annotations "
                        + "considered unsafe in IncQuery, for more information see. The possible errorneous calls are the following: "
                        + elementsWithWarnings + ".", checkConstraint,
                        PatternLanguagePackage.Literals.CHECK_CONSTRAINT__EXPRESSION,
                        IssueCodes.CHECK_WITH_IMPURE_JAVA_CALLS);
            } else {
                warning("There is a potentially problematic java call in the check expression. Custom java calls without @Pure annotations "
                        + "considered unsafe in IncQuery, for more information see. The possible errorneous call is the following: "
                        + elementsWithWarnings + ".", checkConstraint,
                        PatternLanguagePackage.Literals.CHECK_CONSTRAINT__EXPRESSION,
                        IssueCodes.CHECK_WITH_IMPURE_JAVA_CALLS);
            }
        }
    }

    @Override
    public void warning(String message, EObject source, EStructuralFeature feature, String code, String... issueData) {
        super.warning(message, source, feature, code, issueData);
    }

    @Override
    public void error(String message, EObject source, EStructuralFeature feature, String code, String... issueData) {
        super.error(message, source, feature, code, issueData);
    }

}
