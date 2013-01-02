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

    @Inject
    private AnnotationExpressionValidator expressionValidator;

    @Override
    public void executeAdditionalValidation(Annotation annotation, IIssueCallback validator) {
        Pattern pattern = (Pattern) annotation.eContainer();
        ValueReference ref = CorePatternLanguageHelper.getFirstAnnotationParameter(annotation, "message");

        if (ref instanceof StringValue) {
            String value = ((StringValue) ref).getValue();
            expressionValidator.validateStringExpression(pattern, ref, value, validator);
        }
    }

}
