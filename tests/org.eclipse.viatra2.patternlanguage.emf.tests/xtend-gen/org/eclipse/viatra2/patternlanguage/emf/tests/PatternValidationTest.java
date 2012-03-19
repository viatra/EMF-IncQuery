package org.eclipse.viatra2.patternlanguage.emf.tests;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider;
import org.eclipse.viatra2.patternlanguage.core.validation.IssueCodes;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
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
public class PatternValidationTest {
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
  public void emptyBodyValidation() {
    try {
      {
        EObject _parse = this.parseHelper.parse("pattern resolutionTest(Name) = {}");
        final PatternModel model = ((PatternModel) _parse);
        AssertableDiagnostics _validate = this.tester.validate(model);
        _validate.assertError(IssueCodes.PATTERN_BODY_EMPTY);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
