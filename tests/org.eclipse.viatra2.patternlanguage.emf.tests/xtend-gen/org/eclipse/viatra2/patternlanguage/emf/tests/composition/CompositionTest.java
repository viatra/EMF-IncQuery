package org.eclipse.viatra2.patternlanguage.emf.tests.composition;

import com.google.inject.Inject;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.Test;
import org.junit.runner.RunWith;

@SuppressWarnings("all")
@RunWith(XtextRunner.class)
@InjectWith(EMFPatternLanguageInjectorProvider.class)
public class CompositionTest {
  @Inject
  private ParseHelper parseHelper;
  
  @Inject
  private ValidationTestHelper _validationTestHelper;
  
  @Test
  public void testSimpleComposition() {
    try {
      EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern calledPattern(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t}\n\n\t\t\tpattern callPattern(p : Pattern) = {\n\t\t\t\tfind calledPattern(p);\n\t\t\t}");
      this._validationTestHelper.assertNoErrors(_parse);
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testRecursiveComposition() {
    try {
      EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern calledPattern(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t} or {\n\t\t\t\tfind calledPattern(p);\n\t\t\t}");
      this._validationTestHelper.assertNoErrors(_parse);
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testNegativeComposition() {
    try {
      EObject _parse = this.parseHelper.parse("import \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern calledPattern(p : Pattern) = {\n\t\t\t\tPattern(p);\n\t\t\t} or {\n\t\t\t\tneg find calledPattern(p);\n\t\t\t}");
      this._validationTestHelper.assertNoErrors(_parse);
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testMissingComposition() {
    try {
      EObject _parse = this.parseHelper.parse("\n\t\t\timport \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern callPattern(p : Pattern) = {\n\t\t\t\tfind calledPatternMissing(p);\n\t\t\t}");
      EClass _patternCompositionConstraint = PatternLanguagePackage.eINSTANCE.getPatternCompositionConstraint();
      this._validationTestHelper.assertError(_parse, _patternCompositionConstraint, Diagnostic.LINKING_DIAGNOSTIC, "Couldn\'t resolve reference to Pattern \'calledPatternMissing\'.");
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
