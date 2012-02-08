package org.eclipse.viatra2.patternlanguage.emf.tests.imports;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.emf.tests.AbstractValidatorTest;
import org.eclipse.viatra2.patternlanguage.validation.EMFIssueCodes;
import org.eclipse.viatra2.patternlanguage.validation.EMFPatternLanguageJavaValidator;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.AssertableDiagnostics;
import org.eclipse.xtext.junit4.validation.AssertableDiagnostics.DiagnosticPredicate;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.eclipse.xtext.junit4.validation.ValidatorTester;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@SuppressWarnings("all")
@RunWith(XtextRunner.class)
@InjectWith(EMFPatternLanguageInjectorProvider.class)
public class ImportValidationTest extends AbstractValidatorTest {
  @Inject
  private ParseHelper parseHelper;
  
  @Inject
  private EMFPatternLanguageJavaValidator validator;
  
  @Inject
  private Injector injector;
  
  private ValidatorTester<EMFPatternLanguageJavaValidator> tester;
  
  @Inject
  private ValidationTestHelper _validationTestHelper;
  
  @Before
  public void initialize() {
    ValidatorTester<EMFPatternLanguageJavaValidator> _validatorTester = new ValidatorTester<EMFPatternLanguageJavaValidator>(this.validator, this.injector);
    this.tester = _validatorTester;
  }
  
  @Test
  public void duplicateImport() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\timport \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\t\t\timport \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern resolutionTest(Name) = {\n\t\t\t\tPattern(Name);\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        this._validationTestHelper.assertNoErrors(model);
        AssertableDiagnostics _validate = this.tester.validate(model);
        DiagnosticPredicate _warningCode = this.getWarningCode(EMFIssueCodes.DUPLICATE_IMPORT);
        _validate.assertAll(_warningCode);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
