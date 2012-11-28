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
package org.eclipse.incquery.databinding.runtime.util.validation;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.incquery.patternlanguage.annotations.IPatternAnnotationAdditionalValidator;
import org.eclipse.incquery.patternlanguage.emf.types.IEMFTypeProvider;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternLanguagePackage;
import org.eclipse.incquery.patternlanguage.patternLanguage.StringValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.ValueReference;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.patternlanguage.validation.IIssueCallback;

import com.google.inject.Inject;

/**
 * A validator for observable value annotations
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ObservableValuePatternValidator implements IPatternAnnotationAdditionalValidator {

    private static final String VALIDATOR_BASE_CODE = "org.eclipse.viatra2.emf.incquery.databinding.";
    public static final String GENERAL_ISSUE_CODE = VALIDATOR_BASE_CODE + "general";
    public static final String UNKNOWN_VARIABLE_CODE = VALIDATOR_BASE_CODE + "unknown_variable";
    public static final String UNKNOWN_ATTRIBUTE_CODE = VALIDATOR_BASE_CODE + "unknown_attribute";
    public static final String UNDEFINED_NAME_CODE = VALIDATOR_BASE_CODE + "undefined_name";

    @Inject
    private IEMFTypeProvider typeProvider;
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.viatra2.patternlanguage.core.annotations.IPatternAnnotationAdditionalValidator#
     * executeAdditionalValidation(org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation,
     * org.eclipse.viatra2.patternlanguage.core.validation.PatternLanguageJavaValidator)
     */
    @Override
    public void executeAdditionalValidation(Annotation annotation, IIssueCallback validator) {
        Pattern pattern = (Pattern) annotation.eContainer();
        ValueReference ref = CorePatternLanguageHelper.getFirstAnnotationParameter(annotation, "expression");

        if (ref instanceof StringValue) {
            String value = ((StringValue) ref).getValue();
            if (value.contains("$")) {
                validator.warning("The expressions are not required to be escaped using $ characters.", ref,
                        PatternLanguagePackage.Literals.STRING_VALUE__VALUE, GENERAL_ISSUE_CODE);
            }

            validateParameterExpression(pattern, ref, value, validator);
        }
    }
    /**
     * @param pattern
     * @param ref
     * @param expression
     * @param validator
     */
    private void validateParameterExpression(Pattern pattern, ValueReference ref, String expression,
            IIssueCallback validator) {
        String[] tokens = expression.split("\\.");
        String featureName = "";
        if (expression.isEmpty() || tokens.length == 0) {
            validator.error("Expression must not be empty.", ref,
                    PatternLanguagePackage.Literals.STRING_VALUE__VALUE, GENERAL_ISSUE_CODE);
            return;
        } else if (tokens.length == 1) {
            featureName = "name";
        } else if (tokens.length == 2) {
            featureName = tokens[1];
        } else {
            featureName = tokens[1];
            validator.error("Only direct feature references are supported.", ref,
                    PatternLanguagePackage.Literals.STRING_VALUE__VALUE, GENERAL_ISSUE_CODE);
        }

        Variable parameter = CorePatternLanguageHelper.getParameterByName(pattern, tokens[0]);
        if (parameter == null) {
            validator.error(String.format("Unknown parameter name %s", tokens[0]), ref,
                    PatternLanguagePackage.Literals.STRING_VALUE__VALUE, UNKNOWN_VARIABLE_CODE);
            return;
        }
        EClassifier classifier = typeProvider.getClassifierForPatternParameterVariable(parameter);
        if (!(classifier instanceof EClass)) {
            validator.error(String.format("Invalid parameter type %s", classifier.getName()), ref,
                    PatternLanguagePackage.Literals.STRING_VALUE__VALUE, GENERAL_ISSUE_CODE);
            return;
        }
        EClass classDef = (EClass) classifier;
        if (classDef.getEStructuralFeature(featureName) == null) {
            validator.error(
                    String.format("Invalid feature type %s in EClass %s", featureName, classifier.getName()), ref,
                    PatternLanguagePackage.Literals.STRING_VALUE__VALUE, UNKNOWN_ATTRIBUTE_CODE);
        }
    }

}
