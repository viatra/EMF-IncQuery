package org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel;

import com.google.inject.Inject;
import java.util.Arrays;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel.EMFJvmTypesBuilder;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.EntityType;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassConstraint;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.xbase.lib.BooleanExtensions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;

/**
 * Utility class for the EMFPatternLanguageJvmModelInferrer.
 * 
 * @author Mark Czotter
 */
@SuppressWarnings("all")
public class EMFPatternLanguageJvmModelInferrerUtil {
  @Inject
  private EMFJvmTypesBuilder _eMFJvmTypesBuilder;
  
  @Inject
  private ISerializer serializer;
  
  private Logger logger = new Function0<Logger>() {
    public Logger apply() {
      Class<? extends Object> _class = EMFPatternLanguageJvmModelInferrerUtil.this.getClass();
      Logger _logger = Logger.getLogger(_class);
      return _logger;
    }
  }.apply();
  
  /**
   * Returns the MatcherFactoryClass name based on the Pattern's name
   */
  public String matcherFactoryClassName(final Pattern pattern) {
    String _name = pattern.getName();
    String _firstUpper = StringExtensions.toFirstUpper(_name);
    String _operator_plus = StringExtensions.operator_plus(_firstUpper, "MatcherFactory");
    return _operator_plus;
  }
  
  /**
   * Returns the MatcherClass name based on the Pattern's name
   */
  public String matcherClassName(final Pattern pattern) {
    String _name = pattern.getName();
    String _firstUpper = StringExtensions.toFirstUpper(_name);
    String _operator_plus = StringExtensions.operator_plus(_firstUpper, "Matcher");
    return _operator_plus;
  }
  
  /**
   * Returns the MatchClass name based on the Pattern's name
   */
  public String matchClassName(final Pattern pattern) {
    String _name = pattern.getName();
    String _firstUpper = StringExtensions.toFirstUpper(_name);
    String _operator_plus = StringExtensions.operator_plus(_firstUpper, "Match");
    return _operator_plus;
  }
  
  /**
   * Returns the ProcessorClass name based on the Pattern's name
   */
  public String processorClassName(final Pattern pattern) {
    String _name = pattern.getName();
    String _firstUpper = StringExtensions.toFirstUpper(_name);
    String _operator_plus = StringExtensions.operator_plus(_firstUpper, "Processor");
    return _operator_plus;
  }
  
  /**
   * Returns the field name of Variable
   */
  public String fieldName(final Variable variable) {
    String _name = variable.getName();
    String _firstUpper = StringExtensions.toFirstUpper(_name);
    String _operator_plus = StringExtensions.operator_plus("f", _firstUpper);
    return _operator_plus;
  }
  
  /**
   * Calculates type for a Variable.
   * See the XBaseUsageCrossReferencer class, possible solution for local variable usage
   * TODO: improve type calculation
   * @return JvmTypeReference pointing the EClass that defines the Variable's type.
   */
  public JvmTypeReference calculateType(final Variable variable) {
      try {
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
      } catch (final Throwable _t) {
        if (_t instanceof Exception) {
          final Exception e = (Exception)_t;
          boolean _operator_notEquals_1 = ObjectExtensions.operator_notEquals(this.logger, null);
          if (_operator_notEquals_1) {
            String _name = variable.getName();
            String _operator_plus = StringExtensions.operator_plus("Error during type calculation for ", _name);
            this.logger.error(_operator_plus, e);
          }
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
      JvmTypeReference _newTypeRef = this._eMFJvmTypesBuilder.newTypeRef(variable, java.lang.Object.class);
      return _newTypeRef;
  }
  
  /**
   * Returns the JvmTypeReference for variable if it used in the Constraint.
   */
  protected JvmTypeReference _getTypeRef(final Constraint constraint, final Variable variable) {
    return null;
  }
  
  /**
   * Returns the JvmTypeReference for variable if it used in the EClassConstraint.
   */
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
  
  /**
   * Serializes the EObject into Java String variable.
   */
  public CharSequence serializeToJava(final EObject eObject) {
      String _serialize = this.serialize(eObject);
      final String parseString = _serialize;
      boolean _isNullOrEmpty = StringExtensions.isNullOrEmpty(parseString);
      if (_isNullOrEmpty) {
        return "";
      }
      String[] _split = parseString.split("[\r\n]+");
      final String[] splits = _split;
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("String patternString = \"\"");
      final StringConcatenation stringRep = ((StringConcatenation) _builder);
      stringRep.newLine();
      for (final String s : splits) {
        {
          String _operator_plus = StringExtensions.operator_plus("+\" ", s);
          String _operator_plus_1 = StringExtensions.operator_plus(_operator_plus, " \"");
          stringRep.append(_operator_plus_1);
          stringRep.newLine();
        }
      }
      stringRep.append(";");
      return stringRep;
  }
  
  /**
   * Serializes the input for Javadoc
   */
  public String serializeToJavadoc(final Pattern pattern) {
      String _serialize = this.serialize(pattern);
      final String javadocString = _serialize;
      boolean _isNullOrEmpty = StringExtensions.isNullOrEmpty(javadocString);
      if (_isNullOrEmpty) {
        return "Serialization error, check Log";
      }
      return javadocString;
  }
  
  /**
   * Serializes EObject to a String representation. Escapes only the double qoutes.
   */
  private String serialize(final EObject eObject) {
    String _xtrycatchfinallyexpression = null;
    try {
      String _serialize = this.serializer.serialize(eObject);
      String _replaceAll = _serialize.replaceAll("\"", "\\\\\"");
      _xtrycatchfinallyexpression = _replaceAll;
    } catch (final Throwable _t) {
      if (_t instanceof Exception) {
        final Exception e = (Exception)_t;
        {
          boolean _operator_notEquals = ObjectExtensions.operator_notEquals(this.logger, null);
          if (_operator_notEquals) {
            EClass _eClass = eObject.eClass();
            String _name = _eClass.getName();
            String _operator_plus = StringExtensions.operator_plus("Error when serializing ", _name);
            this.logger.error(_operator_plus, e);
          }
          return null;
        }
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
    return _xtrycatchfinallyexpression;
  }
  
  /**
   * Returns the packageName: PatternModel.packageName + Pattern.name, packageName is ignored, when nullOrEmpty.
   */
  public String getPackageName(final Pattern pattern) {
      EObject _eContainer = pattern.eContainer();
      String _packageName = ((PatternModel) _eContainer).getPackageName();
      String packageName = _packageName;
      boolean _isNullOrEmpty = StringExtensions.isNullOrEmpty(packageName);
      if (_isNullOrEmpty) {
        packageName = "";
      } else {
        String _operator_plus = StringExtensions.operator_plus(packageName, ".");
        packageName = _operator_plus;
      }
      String _name = pattern.getName();
      String _operator_plus_1 = StringExtensions.operator_plus(packageName, _name);
      return _operator_plus_1;
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
