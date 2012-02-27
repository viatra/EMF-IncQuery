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
import org.eclipse.xtext.junit4.validation.AssertableDiagnostics.DiagnosticPredicate;
import org.eclipse.xtext.junit4.validation.ValidatorTester;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@SuppressWarnings("all")
@RunWith(XtextRunner.class)
@InjectWith(EMFPatternLanguageInjectorProvider.class)
public class CompositionValidatorTest extends AbstractValidatorTest {
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
  public void duplicatePatterns() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern calledPattern(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}\n\n\t\t\tpattern calledPattern(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        DiagnosticPredicate _errorCode = this.getErrorCode(IssueCodes.DUPLICATE_PATTERN_DEFINITION);
        DiagnosticPredicate _errorCode_1 = this.getErrorCode(IssueCodes.DUPLICATE_PATTERN_DEFINITION);
        _validate.assertAll(_errorCode, _errorCode_1);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void duplicateParameters() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern calledPattern(p : Pattern, p) = {\n\t\t\t\tPattern(p);\n\t\t\t}\n\n\t\t\tpattern callPattern(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        DiagnosticPredicate _errorCode = this.getErrorCode(IssueCodes.DUPLICATE_PATTERN_PARAMETER_NAME);
        DiagnosticPredicate _errorCode_1 = this.getErrorCode(IssueCodes.DUPLICATE_PATTERN_PARAMETER_NAME);
        _validate.assertAll(_errorCode, _errorCode_1);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testTooFewParameters() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern calledPattern(p : Pattern, p2) = {\n\t\t\t\tPattern(p);\n\t\t\t}\n\n\t\t\tpattern callPattern(p : Pattern) = {\n\t\t\t\tfind calledPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertError(IssueCodes.WRONG_NUMBER_PATTERNCALL_PARAMETER);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testTooMuchParameters() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern calledPattern(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}\n\n\t\t\tpattern callPattern(p : Pattern) = {\n\t\t\t\tfind calledPattern(p, p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertError(IssueCodes.WRONG_NUMBER_PATTERNCALL_PARAMETER);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testSymbolicParameterSafe() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern calledPattern(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t\tneg find calledPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertOK();
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testQuantifiedLocalVariable() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern calledPattern(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}\n\n\t\t\tpattern callerPattern() = {\n\t\t\t\tPattern(p);\n\t\t\t\tneg find calledPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertOK();
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  @Ignore
  public void testSymbolicParameterUnsafe() {
    try {
      {
        EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern calledPattern(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t} or {\n\t\t\t\tneg find calledPattern(p);\n\t\t\t}");
        final EObject model = _parse;
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertError(IssueCodes.WRONG_NUMBER_PATTERNCALL_PARAMETER);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
