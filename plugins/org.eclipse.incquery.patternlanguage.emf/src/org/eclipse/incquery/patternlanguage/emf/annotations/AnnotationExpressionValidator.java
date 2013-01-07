/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.emf.annotations;

import java.util.StringTokenizer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.incquery.patternlanguage.emf.types.IEMFTypeProvider;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternLanguagePackage;
import org.eclipse.incquery.patternlanguage.patternLanguage.ValueReference;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.patternlanguage.validation.IIssueCallback;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class AnnotationExpressionValidator {

    private static final String VALIDATOR_BASE_CODE = "org.eclipse.incquery.patternlanguage.expression.";
    public static final String GENERAL_ISSUE_CODE = VALIDATOR_BASE_CODE + "general";
    public static final String UNKNOWN_VARIABLE_CODE = VALIDATOR_BASE_CODE + "unknown_variable";
    public static final String UNKNOWN_ATTRIBUTE_CODE = VALIDATOR_BASE_CODE + "unknown_attribute";
    public static final String UNDEFINED_NAME_CODE = VALIDATOR_BASE_CODE + "undefined_name";

    @Inject
    private IEMFTypeProvider typeProvider;

    /**
     * Validates a path expression referring to a simple pattern parameter
     * 
     * @param expression
     *            the string representation of the path expression. Not inside '$' symbols.
     * @param pattern
     *            the containing pattern
     * @param ref
     *            a reference for the annotation parameter for error localization
     * @param validator
     *            the validator to report the found issues
     */
    public void validateParameterString(String expression, Pattern pattern, ValueReference ref, IIssueCallback validator) {
        if (expression.contains(".")) {
            validator.error("Expression must refer to a single parameter.", ref,
                    PatternLanguagePackage.Literals.STRING_VALUE__VALUE, GENERAL_ISSUE_CODE);
        }

        Variable parameter = CorePatternLanguageHelper.getParameterByName(pattern, expression);
        if (parameter == null) {
            validator.error(String.format("Unknown parameter name %s", expression), ref,
                    PatternLanguagePackage.Literals.STRING_VALUE__VALUE, UNKNOWN_VARIABLE_CODE);
            return;
        }
    }

    /**
     * Validates a path expression starting with a parameter of the pattern.
     * 
     * @param expression
     *            the string representation of the path expression. Not inside '$' symbols.
     * @param pattern
     *            the containing pattern
     * @param ref
     *            a reference for the annotation parameter for error localization
     * @param validator
     *            the validator to report the found issues
     */
    public void validateModelExpression(String expression, Pattern pattern, ValueReference ref, IIssueCallback validator) {
        String[] tokens = expression.split("\\.");
        if (expression.isEmpty() || tokens.length == 0) {
            validator.error("Expression must not be empty.", ref, PatternLanguagePackage.Literals.STRING_VALUE__VALUE,
                    GENERAL_ISSUE_CODE);
            return;
        }

        Variable parameter = CorePatternLanguageHelper.getParameterByName(pattern, tokens[0]);
        if (parameter == null) {
            validator.error(String.format("Unknown parameter name %s", tokens[0]), ref,
                    PatternLanguagePackage.Literals.STRING_VALUE__VALUE, UNKNOWN_VARIABLE_CODE);
            return;
        }
        EClassifier classifier = typeProvider.getClassifierForVariable(parameter);

        if (tokens.length == 1) {
            checkClassifierFeature(classifier, "name", ref, validator, false);
        } else if (tokens.length == 2) {
            String featureName = tokens[1];
            checkClassifierFeature(classifier, featureName, ref, validator, true);
        } else {
            validator.error("Only direct feature references are supported.", ref,
                    PatternLanguagePackage.Literals.STRING_VALUE__VALUE, GENERAL_ISSUE_CODE);
        }
    }

    /**
     * Checks whether an {@link EClassifier} defines a feature with the selected name; if not, reports an error for the
     * selected reference.
     * 
     * @param classifier
     * @param featureName
     * @param ref
     * @param validator
     * @param userSpecified TODO
     */
    private void checkClassifierFeature(EClassifier classifier, String featureName, ValueReference ref,
            IIssueCallback validator, boolean userSpecified) {
        if (classifier instanceof EClass) {
            EClass classDef = (EClass) classifier;
            if (classDef.getEStructuralFeature(featureName) == null) {
                if (userSpecified) {
                validator.error(
                        String.format("Invalid feature type %s in EClass %s", featureName, classifier.getName()),
                        ref, PatternLanguagePackage.Literals.STRING_VALUE__VALUE, UNKNOWN_ATTRIBUTE_CODE);
                } else {
                    validator.warning(String.format(
                                            "EClass %s does not define a name attribute, so the string representation might be inconvinient to use. Perhaps a feature qualifier is missing?",
                            classifier.getName()), ref, PatternLanguagePackage.Literals.STRING_VALUE__VALUE,
                            UNKNOWN_ATTRIBUTE_CODE);
                }
            }
        } else if (classifier == null) {
            return;
        }
    }


    /**
     * Validates a string expression that may contain model references escaped inside '$' symbols.
     * 
     * @param expression
     * @param pattern
     *            the containing pattern
     * @param ref
     *            a reference for the annotation parameter for error localization
     * @param validator
     *            the validator to report the found issues
     */
    public void validateStringExpression(String expression, Pattern pattern, ValueReference ref,
            IIssueCallback validator) {
        StringTokenizer tokenizer = new StringTokenizer(expression, "$", true);
        if (expression.isEmpty() || tokenizer.countTokens() == 0) {
            validator.error("Expression must not be empty.", ref, PatternLanguagePackage.Literals.STRING_VALUE__VALUE,
                    GENERAL_ISSUE_CODE);
            return;
        }
        boolean inExpression = false;
        boolean foundToken = false;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.equals("$")) {
                if (inExpression && !foundToken) {
                    validator.error("Empty reference ($$) in message is not allowed.", ref,
                            PatternLanguagePackage.Literals.STRING_VALUE__VALUE, GENERAL_ISSUE_CODE);
                }
                inExpression = !inExpression;
            } else if (inExpression) {
                validateModelExpression(token, pattern, ref, validator);
                foundToken = true;
            }
        }

            if (inExpression) {
            validator.error("Inconsistent model references - a $ character is missing.", ref,
                    PatternLanguagePackage.Literals.STRING_VALUE__VALUE, GENERAL_ISSUE_CODE);
        }
    }
}
