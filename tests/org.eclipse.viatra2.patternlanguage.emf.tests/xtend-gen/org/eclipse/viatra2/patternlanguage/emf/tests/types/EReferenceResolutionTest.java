package org.eclipse.viatra2.patternlanguage.emf.tests.types;

import com.google.inject.Inject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionHead;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionTail;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage.Literals;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Type;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ReferenceType;
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
public class EReferenceResolutionTest {
  @Inject
  private ParseHelper parseHelper;
  
  @Inject
  private ValidationTestHelper _validationTestHelper;
  
  @Test
  public void referenceResolution() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\timport \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern resolutionTest(Name : Pattern, Body) = {\n\t\t\t\tPattern.bodies(Name, Body);\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        this._validationTestHelper.assertNoErrors(model);
        EList<Pattern> _patterns = model.getPatterns();
        Pattern _get = _patterns.get(0);
        final Pattern pattern = _get;
        EList<PatternBody> _bodies = pattern.getBodies();
        PatternBody _get_1 = _bodies.get(0);
        EList<Constraint> _constraints = _get_1.getConstraints();
        Constraint _get_2 = _constraints.get(0);
        final PathExpressionConstraint constraint = ((PathExpressionConstraint) _get_2);
        PathExpressionHead _head = constraint.getHead();
        PathExpressionTail _tail = _head.getTail();
        final PathExpressionTail tail = _tail;
        Type _type = tail.getType();
        final ReferenceType type = ((ReferenceType) _type);
        EStructuralFeature _refname = type.getRefname();
        EClassifier _eType = _refname.getEType();
        Assert.assertEquals(_eType, Literals.PATTERN_BODY);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void referenceResolutionChain() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\timport \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern resolutionTest(Name : Pattern, Constraint) = {\n\t\t\t\tPattern.bodies.constraints(Name, Constraint);\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        this._validationTestHelper.assertNoErrors(model);
        EList<Pattern> _patterns = model.getPatterns();
        Pattern _get = _patterns.get(0);
        final Pattern pattern = _get;
        EList<PatternBody> _bodies = pattern.getBodies();
        PatternBody _get_1 = _bodies.get(0);
        EList<Constraint> _constraints = _get_1.getConstraints();
        Constraint _get_2 = _constraints.get(0);
        final PathExpressionConstraint constraint = ((PathExpressionConstraint) _get_2);
        PathExpressionHead _head = constraint.getHead();
        PathExpressionTail _tail = _head.getTail();
        final PathExpressionTail interim = _tail;
        Type _type = interim.getType();
        final ReferenceType interimType = ((ReferenceType) _type);
        EStructuralFeature _refname = interimType.getRefname();
        EClassifier _eType = _refname.getEType();
        Assert.assertEquals(_eType, Literals.PATTERN_BODY);
        PathExpressionTail _tail_1 = interim.getTail();
        final PathExpressionTail tail = _tail_1;
        Type _type_1 = tail.getType();
        final ReferenceType type = ((ReferenceType) _type_1);
        EStructuralFeature _refname_1 = type.getRefname();
        EClassifier _eType_1 = _refname_1.getEType();
        Assert.assertEquals(_eType_1, Literals.CONSTRAINT);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
