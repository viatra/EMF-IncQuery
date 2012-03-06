package org.eclipse.viatra2.patternlanguage.emf.tests.types;

import com.google.inject.Inject;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage.Literals;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionHead;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionTail;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Type;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ValueReference;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EnumValue;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ReferenceType;
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
public class EnumResolutionTest {
  @Inject
  private ParseHelper parseHelper;
  
  @Inject
  private ValidationTestHelper _validationTestHelper;
  
  @Test
  public void eEnumResolutionSuccess() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\timport \"http://www.eclipse.org/emf/2002/GenModel\"\n\n\t\t\tpattern resolutionTest(Model) = {\n\t\t\t\tGenModel(Model);\n\t\t\t\tGenModel.runtimeVersion(Model, ::EMF23);\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        this._validationTestHelper.assertNoErrors(model);
        EList<Pattern> _patterns = model.getPatterns();
        Pattern _get = _patterns.get(0);
        final Pattern pattern = _get;
        EList<PatternBody> _bodies = pattern.getBodies();
        PatternBody _get_1 = _bodies.get(0);
        EList<Constraint> _constraints = _get_1.getConstraints();
        Constraint _get_2 = _constraints.get(1);
        final PathExpressionConstraint constraint = ((PathExpressionConstraint) _get_2);
        PathExpressionHead _head = constraint.getHead();
        PathExpressionTail _tail = _head.getTail();
        final PathExpressionTail tail = _tail;
        Type _type = tail.getType();
        final ReferenceType type = ((ReferenceType) _type);
        EStructuralFeature _refname = type.getRefname();
        EClassifier _eType = _refname.getEType();
        Assert.assertEquals(_eType, Literals.GEN_RUNTIME_VERSION);
        PathExpressionHead _head_1 = constraint.getHead();
        ValueReference _dst = _head_1.getDst();
        final EnumValue value = ((EnumValue) _dst);
        EEnumLiteral _literal = value.getLiteral();
        EEnumLiteral _eEnumLiteral = Literals.GEN_RUNTIME_VERSION.getEEnumLiteral("EMF23");
        Assert.assertEquals(_literal, _eEnumLiteral);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void eEnumResolutionInvalidLiteral() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\timport \"http://www.eclipse.org/emf/2002/GenModel\"\n\n\t\t\tpattern resolutionTest(Model) = {\n\t\t\t\tGenModel(Model);\n\t\t\t\tGenModel.runtimeVersion(Model, ::NOTEXIST);\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        this._validationTestHelper.assertError(model, org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguagePackage.Literals.ENUM_VALUE, Diagnostic.LINKING_DIAGNOSTIC, "reference to EEnumLiteral");
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void eEnumResolutionNotEnum() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\timport \"http://www.eclipse.org/emf/2002/GenModel\"\n\n\t\t\tpattern resolutionTest(Model) = {\n\t\t\t\tGenModel(Model);\n\t\t\t\tGenModel.copyrightText(Model, ::EMF23);\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        this._validationTestHelper.assertError(model, org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguagePackage.Literals.ENUM_VALUE, Diagnostic.LINKING_DIAGNOSTIC, "reference to EEnumLiteral");
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
