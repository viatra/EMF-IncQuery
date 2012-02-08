package org.eclipse.viatra2.patternlanguage.emf.tests.resolution;

import com.google.inject.Inject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassConstraint;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@SuppressWarnings("all")
@RunWith(XtextRunner.class)
@InjectWith(EMFPatternLanguageInjectorProvider.class)
public class VariableResolutionTest {
  @Inject
  private ParseHelper parseHelper;
  
  @Inject
  private ValidationTestHelper _validationTestHelper;
  
  @Test
  public void parameterResolution() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\timport \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern resolutionTest(Name) = {\n\t\t\t\tPattern(Name);\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        this._validationTestHelper.assertNoErrors(model);
        EList<Pattern> _patterns = model.getPatterns();
        Pattern _get = _patterns.get(0);
        final Pattern pattern = _get;
        EList<Variable> _parameters = pattern.getParameters();
        Variable _get_1 = _parameters.get(0);
        final Variable parameter = _get_1;
        EList<PatternBody> _bodies = pattern.getBodies();
        PatternBody _get_2 = _bodies.get(0);
        EList<Constraint> _constraints = _get_2.getConstraints();
        Constraint _get_3 = _constraints.get(0);
        final EClassConstraint constraint = ((EClassConstraint) _get_3);
        String _name = parameter.getName();
        VariableReference _var = constraint.getVar();
        String _var_1 = _var.getVar();
        Assert.assertEquals(_name, _var_1);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void parameterResolutionFailed() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\timport \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern resolutionTest(Name) = {\n\t\t\t\tPattern(Name2);\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        this._validationTestHelper.assertNoErrors(model);
        EList<Pattern> _patterns = model.getPatterns();
        Pattern _get = _patterns.get(0);
        final Pattern pattern = _get;
        EList<Variable> _parameters = pattern.getParameters();
        Variable _get_1 = _parameters.get(0);
        final Variable parameter = _get_1;
        EList<PatternBody> _bodies = pattern.getBodies();
        PatternBody _get_2 = _bodies.get(0);
        EList<Constraint> _constraints = _get_2.getConstraints();
        Constraint _get_3 = _constraints.get(0);
        final EClassConstraint constraint = ((EClassConstraint) _get_3);
        String _name = parameter.getName();
        VariableReference _var = constraint.getVar();
        String _var_1 = _var.getVar();
        boolean _operator_notEquals = ObjectExtensions.operator_notEquals(_name, _var_1);
        Assert.assertTrue(_operator_notEquals);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void constraintVariableResolution() {
    try {
      {
        EObject _parse = this.parseHelper.parse("\n\t\t\timport \"http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage\"\n\n\t\t\tpattern resolutionTest(Name) = {\n\t\t\t\tPattern(Name2);\n\t\t\t\tPattern(Name2);\n\t\t\t}\n\t\t");
        final PatternModel model = ((PatternModel) _parse);
        this._validationTestHelper.assertNoErrors(model);
        EList<Pattern> _patterns = model.getPatterns();
        Pattern _get = _patterns.get(0);
        final Pattern pattern = _get;
        EList<Variable> _parameters = pattern.getParameters();
        Variable _get_1 = _parameters.get(0);
        final Variable parameter = _get_1;
        EList<PatternBody> _bodies = pattern.getBodies();
        PatternBody _get_2 = _bodies.get(0);
        EList<Constraint> _constraints = _get_2.getConstraints();
        Constraint _get_3 = _constraints.get(0);
        final EClassConstraint constraint0 = ((EClassConstraint) _get_3);
        EList<PatternBody> _bodies_1 = pattern.getBodies();
        PatternBody _get_4 = _bodies_1.get(0);
        EList<Constraint> _constraints_1 = _get_4.getConstraints();
        Constraint _get_5 = _constraints_1.get(0);
        final EClassConstraint constraint1 = ((EClassConstraint) _get_5);
        String _name = parameter.getName();
        VariableReference _var = constraint0.getVar();
        String _var_1 = _var.getVar();
        boolean _operator_notEquals = ObjectExtensions.operator_notEquals(_name, _var_1);
        Assert.assertTrue(_operator_notEquals);
        VariableReference _var_2 = constraint0.getVar();
        String _var_3 = _var_2.getVar();
        VariableReference _var_4 = constraint1.getVar();
        String _var_5 = _var_4.getVar();
        Assert.assertEquals(_var_3, _var_5);
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
