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

    public void validateModelExpression(Pattern pattern, ValueReference ref, String expression, IIssueCallback validator) {
        String[] tokens = expression.split("\\.");
        String featureName = "";
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
            featureName = "name";
        } else if (tokens.length == 2) {
            featureName = tokens[1];
            if (classifier instanceof EClass) {
                EClass classDef = (EClass) classifier;
                if (classDef.getEStructuralFeature(featureName) == null) {
                    validator.error(
                            String.format("Invalid feature type %s in EClass %s", featureName, classifier.getName()),
                            ref, PatternLanguagePackage.Literals.STRING_VALUE__VALUE, UNKNOWN_ATTRIBUTE_CODE);
                }
            } else if (classifier == null) {
                return;
            } else {
                validator.error(String.format("Invalid parameter type %s", classifier.getName()), ref,
                        PatternLanguagePackage.Literals.STRING_VALUE__VALUE, GENERAL_ISSUE_CODE);
                return;
            }
        } else {
            featureName = tokens[1];
            validator.error("Only direct feature references are supported.", ref,
                    PatternLanguagePackage.Literals.STRING_VALUE__VALUE, GENERAL_ISSUE_CODE);
        }
    }


    public void validateStringExpression(Pattern pattern, ValueReference ref, String expression,
            IIssueCallback validator) {
        StringTokenizer tokenizer = new StringTokenizer(expression, "$", true);
        if (expression.isEmpty() || tokenizer.countTokens() == 0) {
            validator.error("Expression must not be empty.", ref, PatternLanguagePackage.Literals.STRING_VALUE__VALUE,
                    GENERAL_ISSUE_CODE);
            return;
        }
        boolean inExpression = false;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.equals("$")) {
                inExpression = !inExpression;
            } else if (inExpression) {
                if (token == null || token.isEmpty()) {
                    validator.error("Expression must not be empty.", ref,
                            PatternLanguagePackage.Literals.STRING_VALUE__VALUE, GENERAL_ISSUE_CODE);
                }
                validateModelExpression(pattern, ref, token, validator);
            }
        }

            if (inExpression) {
            validator.error("Inconsistent model references - a $ character is missing.", ref,
                    PatternLanguagePackage.Literals.STRING_VALUE__VALUE, GENERAL_ISSUE_CODE);
        }
    }
}
