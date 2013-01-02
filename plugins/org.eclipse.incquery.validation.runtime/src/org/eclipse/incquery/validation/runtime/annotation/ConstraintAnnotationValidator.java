package org.eclipse.incquery.validation.runtime.annotation;

import org.eclipse.incquery.patternlanguage.annotations.IPatternAnnotationAdditionalValidator;
import org.eclipse.incquery.patternlanguage.emf.annotations.AnnotationExpressionValidator;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.StringValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.ValueReference;
import org.eclipse.incquery.patternlanguage.validation.IIssueCallback;

import com.google.inject.Inject;

public class ConstraintAnnotationValidator implements IPatternAnnotationAdditionalValidator {

    private static final String VALIDATOR_BASE_CODE = "org.eclipse.incquery.validation.";
    public static final String SEVERITY_ISSUE_CODE = VALIDATOR_BASE_CODE + "severity";
    @Inject
    private AnnotationExpressionValidator expressionValidator;

    @Override
    public void executeAdditionalValidation(Annotation annotation, IIssueCallback validator) {
        Pattern pattern = (Pattern) annotation.eContainer();
        ValueReference messageRef = CorePatternLanguageHelper.getFirstAnnotationParameter(annotation, "message");

        if (messageRef instanceof StringValue) {
            String value = ((StringValue) messageRef).getValue();
            expressionValidator.validateStringExpression(value, pattern, messageRef, validator);
        }

        ValueReference locationRef = CorePatternLanguageHelper.getFirstAnnotationParameter(annotation, "location");

        if (locationRef instanceof StringValue) {
            String value = ((StringValue) locationRef).getValue();
            expressionValidator.validateParameterString(value, pattern, locationRef, validator);
        }

        ValueReference severityRef = CorePatternLanguageHelper.getFirstAnnotationParameter(annotation, "severity");

        if (severityRef instanceof StringValue) {
            String value = ((StringValue) severityRef).getValue();
            if (!(value.equals("error") || value.equals("warning"))) {
                validator
                        .error("Severity must be either 'error' or 'warning'.", severityRef, null, SEVERITY_ISSUE_CODE);
            }
        }
    }

}
