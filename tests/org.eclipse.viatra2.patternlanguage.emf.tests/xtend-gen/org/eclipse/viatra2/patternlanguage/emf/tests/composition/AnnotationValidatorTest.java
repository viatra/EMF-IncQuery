package org.eclipse.viatra2.patternlanguage.emf.tests.composition;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider;
import org.eclipse.viatra2.patternlanguage.core.validation.IssueCodes;
import org.eclipse.viatra2.patternlanguage.emf.tests.AbstractValidatorTest;
import org.eclipse.viatra2.patternlanguage.validation.EMFPatternLanguageJavaValidator;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.AssertableDiagnostics;
import org.eclipse.xtext.junit4.validation.ValidatorTester;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@SuppressWarnings("all")
@RunWith(XtextRunner.class)
@InjectWith(EMFPatternLanguageInjectorProvider.class)
public class AnnotationValidatorTest extends AbstractValidatorTest {
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
  public void unknownAnnotation() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\t@NonExistent\n\t\t\tpattern pattern2(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertWarning(IssueCodes.UNKNOWN_ANNOTATION);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void unknownAnnotationAttribute() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\t@Optional(unknown=1)\n\t\t\tpattern pattern2(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertError(IssueCodes.UNKNOWN_ANNOTATION_PARAMETER);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void unknownAnnotationAttributeTogetherWithValid() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\t@Param1(p1=\"1\", unknown=1)\n\t\t\tpattern pattern2(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertError(IssueCodes.UNKNOWN_ANNOTATION_PARAMETER);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void missingRequiredAttribute() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\t@Param2(p2=1)\n\t\t\tpattern pattern2(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertError(IssueCodes.MISSING_REQUIRED_ANNOTATION_PARAMETER);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void onlyRequiredAttributeSet() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\t@Param2(p1=1)\n\t\t\tpattern pattern2(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertOK();
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void bothRequiredAndOptionalAttributeSet() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\t@Param2(p1=1,p2=1)\n\t\t\tpattern pattern2(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertOK();
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void parameterTypeStringExpectedIntFound() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\t@Param1(p1=1)\n\t\t\tpattern pattern2(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertError(IssueCodes.MISTYPED_ANNOTATION_PARAMETER);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void parameterTypeStringExpectedBoolFound() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\t@Param1(p1=true)\n\t\t\tpattern pattern2(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertError(IssueCodes.MISTYPED_ANNOTATION_PARAMETER);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void parameterTypeStringExpectedVariableFound() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\t@Param1(p1=p)\n\t\t\tpattern pattern2(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertError(IssueCodes.MISTYPED_ANNOTATION_PARAMETER);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void parameterTypeStringExpectedDoubleFound() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\t@Param1(p1=1.1)\n\t\t\tpattern pattern2(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertError(IssueCodes.MISTYPED_ANNOTATION_PARAMETER);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void parameterTypeStringExpectedListFound() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\t@Param1(p1={1,2,3})\n\t\t\tpattern pattern2(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertError(IssueCodes.MISTYPED_ANNOTATION_PARAMETER);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void parameterTypeUncheckedIntFound() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\t@Param2(p1=1)\n\t\t\tpattern pattern2(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertOK();
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void parameterTypeUncheckedBoolFound() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\t@Param2(p1=true)\n\t\t\tpattern pattern2(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertOK();
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void parameterTypeUncheckedVariableFound() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\t@Param2(p1=p)\n\t\t\tpattern pattern2(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertOK();
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void parameterTypeUncheckedDoubleFound() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\t@Param2(p1=1.1)\n\t\t\tpattern pattern2(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertOK();
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void parameterTypeUncheckedListFound() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\t@Param2(p1={1,2,3})\n\t\t\tpattern pattern2(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertOK();
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void parameterTypeUncheckedStringFound() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\t@Param2(p1=\"{1,2,3}\")\n\t\t\tpattern pattern2(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertOK();
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
