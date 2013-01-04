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

/**
 * @author Mark Czotter
 */
public final class IssueCodes {

    private IssueCodes() {
    }

    protected static final String ISSUE_CODE_PREFIX = "org.eclipse.incquery.patternlanguage.validation.IssueCodes.";

    public static final String DUPLICATE_PATTERN_PARAMETER_NAME = ISSUE_CODE_PREFIX
            + "duplicate_pattern_parameter_name";
    public static final String DUPLICATE_PATTERN_DEFINITION = ISSUE_CODE_PREFIX + "duplicate_pattern_definition";
    public static final String WRONG_NUMBER_PATTERNCALL_PARAMETER = ISSUE_CODE_PREFIX
            + "wrong_number_pattern_parameter";
    public static final String TRANSITIVE_PATTERNCALL_NOT_APPLICABLE = ISSUE_CODE_PREFIX
            + "transitive_patterncall_not_applicable";
    public static final String TRANSITIVE_PATTERNCALL_ARITY = ISSUE_CODE_PREFIX + "transitive_patterncall_wrong_arity";
    public static final String TRANSITIVE_PATTERNCALL_TYPE = ISSUE_CODE_PREFIX
            + "transitive_patterncall_incompatibletypes";
    public static final String PATTERN_BODY_EMPTY = ISSUE_CODE_PREFIX + "patternbody_empty";

    public static final String UNKNOWN_ANNOTATION = ISSUE_CODE_PREFIX + "unknown_annotation";
    public static final String UNKNOWN_ANNOTATION_PARAMETER = ISSUE_CODE_PREFIX + "unknown_annotation_attribute";
    public static final String MISSING_REQUIRED_ANNOTATION_PARAMETER = ISSUE_CODE_PREFIX
            + "missing_annotation_parameter";
    public static final String MISTYPED_ANNOTATION_PARAMETER = ISSUE_CODE_PREFIX + "mistyped_annotation_parameter";

    public static final String CONSTANT_COMPARE_CONSTRAINT = ISSUE_CODE_PREFIX + "constant_compare_constraint";
    public static final String SELF_COMPARE_CONSTRAINT = ISSUE_CODE_PREFIX + "self_compare_constraint";

    public static final String LOWERCASE_PATTERN_NAME = ISSUE_CODE_PREFIX + "lowercase_pattern_name";
    public static final String UNUSED_PRIVATE_PATTERN = ISSUE_CODE_PREFIX + "unused_private_pattern";
    public static final String MISSING_PATTERN_PARAMETERS = ISSUE_CODE_PREFIX + "missing_pattern_parameters";

    public static final String CHECK_MUST_BE_BOOLEAN = ISSUE_CODE_PREFIX + "check_boolean";
    public static final String CHECK_WITH_IMPURE_JAVA_CALLS = ISSUE_CODE_PREFIX + "check_with_impure_java_calls";

    public static final String SYMBOLIC_VARIABLE_NEVER_REFERENCED = ISSUE_CODE_PREFIX
            + "symbolic_variable_never_referenced";
    public static final String SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE = ISSUE_CODE_PREFIX
            + "symbolic_variable_no_positive_reference";
    public static final String LOCAL_VARIABLE_REFERENCED_ONCE = ISSUE_CODE_PREFIX + "local_variable_referenced_once";
    public static final String LOCAL_VARIABLE_READONLY = ISSUE_CODE_PREFIX + "local_variable_no_quantifying_reference";
    public static final String LOCAL_VARIABLE_QUANTIFIED_REFERENCE = ISSUE_CODE_PREFIX
            + "local_variable_quantified_reference";
    public static final String LOCAL_VARIABLE_NO_POSITIVE_REFERENCE = ISSUE_CODE_PREFIX
            + "local_variable_no_positive_reference";
    public static final String ANONYM_VARIABLE_MULTIPLE_REFERENCE = ISSUE_CODE_PREFIX
            + "anonym_variable_multiple_reference";
    public static final String DUBIUS_VARIABLE_NAME = ISSUE_CODE_PREFIX + "dubius_variable_name";

}