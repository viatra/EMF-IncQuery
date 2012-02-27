package org.eclipse.viatra2.patternlanguage.jvmmodel;

import com.google.inject.Inject;
import java.util.Arrays;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.EntityType;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassConstraint;
import org.eclipse.viatra2.patternlanguage.jvmmodel.EMFJvmTypesBuilder;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.xbase.lib.BooleanExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class EMFPatternLanguageJvmModelInferrerUtil {
  @Inject
  private EMFJvmTypesBuilder _eMFJvmTypesBuilder;
  
  public String matcherFactoryClassName(final Pattern pattern) {
    String _name = pattern.getName();
    String _firstUpper = StringExtensions.toFirstUpper(_name);
    String _operator_plus = StringExtensions.operator_plus(_firstUpper, "MatcherFactory");
    return _operator_plus;
  }
  
  public String matcherClassName(final Pattern pattern) {
    String _name = pattern.getName();
    String _firstUpper = StringExtensions.toFirstUpper(_name);
    String _operator_plus = StringExtensions.operator_plus(_firstUpper, "Matcher");
    return _operator_plus;
  }
  
  public String matchClassName(final Pattern pattern) {
    String _name = pattern.getName();
    String _firstUpper = StringExtensions.toFirstUpper(_name);
    String _operator_plus = StringExtensions.operator_plus(_firstUpper, "Match");
    return _operator_plus;
  }
  
  public String processorClassName(final Pattern pattern) {
    String _name = pattern.getName();
    String _firstUpper = StringExtensions.toFirstUpper(_name);
    String _operator_plus = StringExtensions.operator_plus(_firstUpper, "Processor");
    return _operator_plus;
  }
  
  public String fieldName(final Variable variable) {
    String _name = variable.getName();
    String _firstUpper = StringExtensions.toFirstUpper(_name);
    String _operator_plus = StringExtensions.operator_plus("f", _firstUpper);
    return _operator_plus;
  }
  
  public JvmTypeReference calculateType(final Variable variable) {
      EObject _eContainer = variable.eContainer();
      if ((_eContainer instanceof Pattern)) {
        {
          EObject _eContainer_1 = variable.eContainer();
          final Pattern pattern = ((Pattern) _eContainer_1);
          EList<PatternBody> _bodies = pattern.getBodies();
          for (final PatternBody body : _bodies) {
            EList<Constraint> _constraints = body.getConstraints();
            for (final Constraint constraint : _constraints) {
              {
                JvmTypeReference _typeRef = this.getTypeRef(constraint, variable);
                final JvmTypeReference typeRef = _typeRef;
                boolean _operator_notEquals = ObjectExtensions.operator_notEquals(typeRef, null);
                if (_operator_notEquals) {
                  return typeRef;
                }
              }
            }
          }
        }
      }
      JvmTypeReference _newTypeRef = this._eMFJvmTypesBuilder.newTypeRef(variable, java.lang.Object.class);
      return _newTypeRef;
  }
  
  protected JvmTypeReference _getTypeRef(final Constraint constraint, final Variable variable) {
    return null;
  }
  
  protected JvmTypeReference _getTypeRef(final EClassConstraint constraint, final Variable variable) {
      EntityType _type = constraint.getType();
      final EntityType entityType = _type;
      VariableReference _var = constraint.getVar();
      final VariableReference variableRef = _var;
      boolean _operator_notEquals = ObjectExtensions.operator_notEquals(variableRef, null);
      if (_operator_notEquals) {
        boolean _operator_or = false;
        Variable _variable = variableRef.getVariable();
        boolean _operator_equals = ObjectExtensions.operator_equals(_variable, variable);
        if (_operator_equals) {
          _operator_or = true;
        } else {
          boolean _operator_and = false;
          String _var_1 = variableRef.getVar();
          boolean _isNullOrEmpty = StringExtensions.isNullOrEmpty(_var_1);
          boolean _operator_not = BooleanExtensions.operator_not(_isNullOrEmpty);
          if (!_operator_not) {
            _operator_and = false;
          } else {
            String _var_2 = variableRef.getVar();
            String _name = variable.getName();
            boolean _equals = _var_2.equals(_name);
            _operator_and = BooleanExtensions.operator_and(_operator_not, _equals);
          }
          _operator_or = BooleanExtensions.operator_or(_operator_equals, _operator_and);
        }
        if (_operator_or) {
          if ((entityType instanceof ClassType)) {
            {
              EClass _classname = ((ClassType) entityType).getClassname();
              Class<? extends Object> _instanceClass = _classname.getInstanceClass();
              final Class<? extends Object> clazz = _instanceClass;
              boolean _operator_notEquals_1 = ObjectExtensions.operator_notEquals(clazz, null);
              if (_operator_notEquals_1) {
                {
                  JvmTypeReference _newTypeRef = this._eMFJvmTypesBuilder.newTypeRef(variable, clazz);
                  final JvmTypeReference typeref = _newTypeRef;
                  boolean _operator_notEquals_2 = ObjectExtensions.operator_notEquals(typeref, null);
                  if (_operator_notEquals_2) {
                    return typeref;
                  }
                }
              }
            }
          }
        }
      }
      return null;
  }
  
  public JvmTypeReference getTypeRef(final Constraint constraint, final Variable variable) {
    if (constraint instanceof EClassConstraint) {
      return _getTypeRef((EClassConstraint)constraint, variable);
    } else if (constraint != null) {
      return _getTypeRef(constraint, variable);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(constraint, variable).toString());
    }
  }
}
