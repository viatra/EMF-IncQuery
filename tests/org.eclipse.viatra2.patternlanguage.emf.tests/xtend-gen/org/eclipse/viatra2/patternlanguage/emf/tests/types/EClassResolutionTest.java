package org.eclipse.viatra2.patternlanguage.emf.tests.types;

import com.google.inject.Inject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.EntityType;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage.Literals;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassifierConstraint;
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
public class EClassResolutionTest {
  @Inject
  private ParseHelper parseHelper;
  
  @Inject
  private ValidationTestHelper _validationTestHelper;
  
  @Test
  public void eClassResolutionSuccess() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\timport \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern resolutionTest(Name) = {\n\t\t\t\tPattern(Name2);\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        this._validationTestHelper.assertNoErrors(model);
        EList<Pattern> _patterns = model.getPatterns();
        Pattern _get = _patterns.get(0);
        final Pattern pattern = _get;
        EList<PatternBody> _bodies = pattern.getBodies();
        PatternBody _get_1 = _bodies.get(0);
        EList<Constraint> _constraints = _get_1.getConstraints();
        Constraint _get_2 = _constraints.get(0);
        final EClassifierConstraint constraint = ((EClassifierConstraint) _get_2);
        EntityType _type = constraint.getType();
        final ClassType type = ((ClassType) _type);
        EClassifier _classname = type.getClassname();
        Assert.assertEquals(_classname, Literals.PATTERN);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void eClassifierResolutionSuccess() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\timport \"http://www.eclipse.org/emf/2002/Ecore\"\n\n\t\t\tpattern ECoreNamedElement(Name) = {\n\t\t\t\tEString(Name);\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        this._validationTestHelper.assertNoErrors(model);
        EList<Pattern> _patterns = model.getPatterns();
        Pattern _get = _patterns.get(0);
        final Pattern pattern = _get;
        EList<PatternBody> _bodies = pattern.getBodies();
        PatternBody _get_1 = _bodies.get(0);
        EList<Constraint> _constraints = _get_1.getConstraints();
        Constraint _get_2 = _constraints.get(0);
        final EClassifierConstraint constraint = ((EClassifierConstraint) _get_2);
        EntityType _type = constraint.getType();
        final ClassType type = ((ClassType) _type);
        EClassifier _classname = type.getClassname();
        Assert.assertEquals(_classname, org.eclipse.emf.ecore.EcorePackage.Literals.ESTRING);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void eClassResolutionFailed() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\timport \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern resolutionTest(Name) = {\n\t\t\t\tUndefinedType(Name2);\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        EList<Pattern> _patterns = model.getPatterns();
        Pattern _get = _patterns.get(0);
        final Pattern pattern = _get;
        EList<PatternBody> _bodies = pattern.getBodies();
        PatternBody _get_1 = _bodies.get(0);
        EList<Constraint> _constraints = _get_1.getConstraints();
        Constraint _get_2 = _constraints.get(0);
        final EClassifierConstraint constraint = ((EClassifierConstraint) _get_2);
        EntityType _type = constraint.getType();
        final ClassType type = ((ClassType) _type);
        this._validationTestHelper.assertError(type, org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguagePackage.Literals.CLASS_TYPE, Diagnostic.LINKING_DIAGNOSTIC, "reference to EClass");
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void eClassResolutionFailedMissingImport() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\tpattern resolutionTest(Name) = {\n\t\t\t\tPattern(Name2);\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        EList<Pattern> _patterns = model.getPatterns();
        Pattern _get = _patterns.get(0);
        final Pattern pattern = _get;
        EList<PatternBody> _bodies = pattern.getBodies();
        PatternBody _get_1 = _bodies.get(0);
        EList<Constraint> _constraints = _get_1.getConstraints();
        Constraint _get_2 = _constraints.get(0);
        final EClassifierConstraint constraint = ((EClassifierConstraint) _get_2);
        EntityType _type = constraint.getType();
        final ClassType type = ((ClassType) _type);
        this._validationTestHelper.assertError(type, org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguagePackage.Literals.CLASS_TYPE, Diagnostic.LINKING_DIAGNOSTIC, "reference to EClass");
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
