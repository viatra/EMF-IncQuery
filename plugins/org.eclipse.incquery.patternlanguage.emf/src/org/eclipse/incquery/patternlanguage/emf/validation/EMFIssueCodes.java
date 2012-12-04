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
package org.eclipse.incquery.patternlanguage.emf.validation;

/**
 * @author Mark Czotter
 */
public final class EMFIssueCodes {

    protected static final String ISSUE_CODE_PREFIX = "org.eclipse.incquery.patternlanguage.emf.validation.IssueCodes.";

    public static final String DUPLICATE_IMPORT = ISSUE_CODE_PREFIX + "duplicate_import";
    public static final String IMPORT_WITH_GENERATEDCODE = ISSUE_CODE_PREFIX + "missing_imported_code";
    public static final String IMPORT_DEPENDENCY_MISSING = ISSUE_CODE_PREFIX + "missing_import_dependency";
    public static final String INVALID_ENUM_LITERAL = ISSUE_CODE_PREFIX + "invalid_enum";

    public static final String SINGLEUSE_PARAMETER = ISSUE_CODE_PREFIX + "singleuse_parameter";

    public static final String PARAMETER_TYPE_INVALID = ISSUE_CODE_PREFIX + "parameter_type_invalid";
    public static final String VARIABLE_TYPE_INVALID_ERROR = ISSUE_CODE_PREFIX + "variable_type_invalid_error";
    public static final String VARIABLE_TYPE_INVALID_WARNING = ISSUE_CODE_PREFIX + "variable_type_invalid_warning";
    public static final String VARIABLE_TYPE_MULTIPLE_DECLARATION = ISSUE_CODE_PREFIX
            + "variable_type_multiple_declaration";
    public static final String LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_COMPARE = ISSUE_CODE_PREFIX
            + "literal_and_computation_type_mismatch_in_compare";
    public static final String LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATH_EXPRESSION = ISSUE_CODE_PREFIX
            + "literal_or_computation_type_mismatch_in_path_expression";
    public static final String LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATTERN_CALL = ISSUE_CODE_PREFIX
            + "literal_or_computation_type_mismatch_in_pattern_call";
    public static final String CARTESIAN_SOFT_WARNING = ISSUE_CODE_PREFIX + "cartesian_soft_warning";
    public static final String CARTESIAN_STRICT_WARNING = ISSUE_CODE_PREFIX + "cartesian_strict_warning";
    public static final String CHECK_CONSTRAINT_SCALAR_VARIABLE_ERROR = ISSUE_CODE_PREFIX
            + "check_constraint_scalar_variable_error";

    public static final String IDENTIFIER_AS_KEYWORD = ISSUE_CODE_PREFIX + "identifier_as_keyword";

}
