package org.eclipse.viatra2.patternlanguage.emf.tests.imports;

import com.google.inject.Inject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguagePackage.Literals;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@SuppressWarnings("all")
@RunWith(XtextRunner.class)
@InjectWith(EMFPatternLanguageInjectorProvider.class)
public class ImportResolutionTest {
  @Inject
  private ParseHelper parseHelper;
  
  @Inject
  private ValidationTestHelper _validationTestHelper;
  
  @Test
  public void importResolution() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\timport \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern resolutionTest(Name) = {\n\t\t\t\tPattern(Name);\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        this._validationTestHelper.assertNoErrors(model);
        EList<PackageImport> _importPackages = model.getImportPackages();
        PackageImport _get = _importPackages.get(0);
        final PackageImport importDecl = _get;
        EPackage _ePackage = importDecl.getEPackage();
        final EPackage ePackage = _ePackage;
        Assert.assertNotNull(ePackage);
        String _nsURI = ePackage.getNsURI();
        Assert.assertEquals(_nsURI, "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage");
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void multipleImportResolution() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\timport \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\t\t\timport \"http://www.eclipse.org/viatra2/patternlanguage/EMFPatternLanguage\"\n\n\t\t\tpattern resolutionTest(Name) = {\n\t\t\t\tPattern(Name);\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        this._validationTestHelper.assertNoErrors(model);
        EList<PackageImport> _importPackages = model.getImportPackages();
        PackageImport _get = _importPackages.get(0);
        PackageImport importDecl = _get;
        EPackage _ePackage = importDecl.getEPackage();
        EPackage ePackage = _ePackage;
        Assert.assertNotNull(ePackage);
        String _nsURI = ePackage.getNsURI();
        Assert.assertEquals(_nsURI, "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage");
        EList<PackageImport> _importPackages_1 = model.getImportPackages();
        PackageImport _get_1 = _importPackages_1.get(1);
        importDecl = _get_1;
        EPackage _ePackage_1 = importDecl.getEPackage();
        ePackage = _ePackage_1;
        Assert.assertNotNull(ePackage);
        String _nsURI_1 = ePackage.getNsURI();
        Assert.assertEquals(_nsURI_1, "http://www.eclipse.org/viatra2/patternlanguage/EMFPatternLanguage");
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void importResolutionFailed() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\timport \"http://nonexisting.package.uri\"\n\n\t\t\tpattern resolutionTest(Name) = {\n\t\t\t\tPattern(Name2);\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        EList<PackageImport> _importPackages = model.getImportPackages();
        PackageImport _get = _importPackages.get(0);
        final PackageImport importDecl = _get;
        this._validationTestHelper.assertError(importDecl, Literals.PACKAGE_IMPORT, Diagnostic.LINKING_DIAGNOSTIC, "reference to EPackage");
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
