package org.eclipse.viatra2.patternlanguage.emf.tests.basic;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider;
import org.eclipse.viatra2.patternlanguage.core.validation.IssueCodes;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.emf.tests.AbstractValidatorTest;
import org.eclipse.viatra2.patternlanguage.validation.EMFPatternLanguageJavaValidator;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.AssertableDiagnostics;
import org.eclipse.xtext.junit4.validation.AssertableDiagnostics.DiagnosticPredicate;
import org.eclipse.xtext.junit4.validation.ValidatorTester;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@SuppressWarnings("all")
@RunWith(XtextRunner.class)
@InjectWith(EMFPatternLanguageInjectorProvider.class)
public class ConstraintValidationTest extends AbstractValidatorTest {
  @Inject
  private ParseHelper parseHelper;
  
  @Inject
  private EMFPatternLanguageJavaValidator validator;
  
  @Inject
  private Injector injector;
  
  private ValidatorTester<EMFPatternLanguageJavaValidator> tester;
  
  @Before
  public void initialize() {
    ValidatorTester<EMFPatternLanguageJavaValidator> _validatorTester = new ValidatorTester<EMFPatternLanguageJavaValidator>(this.validator, this.injector);
    this.tester = _validatorTester;
  }
  
  @Test
  public void intConstantCompareValidation() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\tpattern constantCompareTest(Name) = {\n\t\t\t\t1 == 2\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        AssertableDiagnostics _validate = this.tester.validate(model);
        DiagnosticPredicate _warningCode = this.getWarningCode(IssueCodes.CONSTANT_COMPARE_CONSTRAINT);
        DiagnosticPredicate _warningCode_1 = this.getWarningCode(IssueCodes.CONSTANT_COMPARE_CONSTRAINT);
        _validate.assertAll(_warningCode, _warningCode_1);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void stringDoubleConstantCompareValidation() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\tpattern constantCompareTest(Name) = {\n\t\t\t\t1.2 == \"String\"\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        AssertableDiagnostics _validate = this.tester.validate(model);
        DiagnosticPredicate _warningCode = this.getWarningCode(IssueCodes.CONSTANT_COMPARE_CONSTRAINT);
        DiagnosticPredicate _warningCode_1 = this.getWarningCode(IssueCodes.CONSTANT_COMPARE_CONSTRAINT);
        _validate.assertAll(_warningCode, _warningCode_1);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void enumIntConstantCompareValidation() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\tpattern constantCompareTest(Name) = {\n\t\t\t\tfalse == 2\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        AssertableDiagnostics _validate = this.tester.validate(model);
        DiagnosticPredicate _warningCode = this.getWarningCode(IssueCodes.CONSTANT_COMPARE_CONSTRAINT);
        DiagnosticPredicate _warningCode_1 = this.getWarningCode(IssueCodes.CONSTANT_COMPARE_CONSTRAINT);
        _validate.assertAll(_warningCode, _warningCode_1);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void rightVariableCompareValidation() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\tpattern constantCompareTest(Name) = {\n\t\t\t\t1 == Name\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertOK();
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void rightNewVariableCompareValidation() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\tpattern constantCompareTest(Name) = {\n\t\t\t\t1 == Name2\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertOK();
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void leftVariableCompareValidation() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\tpattern constantCompareTest(Name) = {\n\t\t\t\tName == \"Test\"\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertOK();
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void leftNewVariableCompareValidation() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\tpattern constantCompareTest(Name) = {\n\t\t\t\tName2 == \"Test\"\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertOK();
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void bothVariableCompareValidation() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\tpattern constantCompareTest(Name) = {\n\t\t\t\tName == Name2\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertOK();
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void selfCompareValidation() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\tpattern constantCompareTest(Name) = {\n\t\t\t\tName == Name\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        AssertableDiagnostics _validate = this.tester.validate(model);
        DiagnosticPredicate _warningCode = this.getWarningCode(IssueCodes.SELF_COMPARE_CONSTRAINT);
        DiagnosticPredicate _warningCode_1 = this.getWarningCode(IssueCodes.SELF_COMPARE_CONSTRAINT);
        _validate.assertAll(_warningCode, _warningCode_1);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
