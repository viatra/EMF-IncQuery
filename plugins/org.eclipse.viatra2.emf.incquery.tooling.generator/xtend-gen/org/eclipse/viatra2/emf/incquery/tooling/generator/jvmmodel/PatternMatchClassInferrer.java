package org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel;

import com.google.inject.Inject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel.JavadocInferrer;
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFJvmTypesBuilder;
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.common.types.JvmAnnotationReference;
import org.eclipse.xtext.common.types.JvmConstructor;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmMember;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.JvmVisibility;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.compiler.ImportManager;
import org.eclipse.xtext.xbase.lib.BooleanExtensions;
import org.eclipse.xtext.xbase.lib.CollectionExtensions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.StringExtensions;

/**
 * {@link IPatternMatch} implementation inferer.
 * 
 * @author Mark Czotter
 */
@SuppressWarnings("all")
public class PatternMatchClassInferrer {
  @Inject
  private EMFJvmTypesBuilder _eMFJvmTypesBuilder;
  
  @Inject
  private IQualifiedNameProvider _iQualifiedNameProvider;
  
  @Inject
  private EMFPatternLanguageJvmModelInferrerUtil _eMFPatternLanguageJvmModelInferrerUtil;
  
  @Inject
  private JavadocInferrer _javadocInferrer;
  
  /**
   * Infers the {@link IPatternMatch} implementation class from {@link Pattern} parameters.
   */
  public JvmDeclaredType inferMatchClass(final Pattern pattern, final boolean isPrelinkingPhase, final String matchPackageName) {
      String _matchClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
      final Procedure1<JvmGenericType> _function = new Procedure1<JvmGenericType>() {
          public void apply(final JvmGenericType it) {
            {
              it.setPackageName(matchPackageName);
              CharSequence _javadocMatchClass = PatternMatchClassInferrer.this._javadocInferrer.javadocMatchClass(pattern);
              String _string = _javadocMatchClass.toString();
              PatternMatchClassInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
              it.setFinal(true);
              EList<JvmTypeReference> _superTypes = it.getSuperTypes();
              JvmTypeReference _newTypeRef = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternMatch.class);
              CollectionExtensions.<JvmTypeReference>operator_add(_superTypes, _newTypeRef);
              EList<JvmTypeReference> _superTypes_1 = it.getSuperTypes();
              JvmTypeReference _newTypeRef_1 = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch.class);
              CollectionExtensions.<JvmTypeReference>operator_add(_superTypes_1, _newTypeRef_1);
            }
          }
        };
      JvmGenericType _class = this._eMFJvmTypesBuilder.toClass(pattern, _matchClassName, _function);
      final JvmGenericType matchClass = _class;
      this.inferMatchClassFields(matchClass, pattern);
      this.inferMatchClassConstructors(matchClass, pattern);
      this.inferMatchClassGetters(matchClass, pattern);
      this.inferMatchClassSetters(matchClass, pattern);
      this.inferMatchClassMethods(matchClass, pattern);
      return matchClass;
  }
  
  /**
   * Infers fields for Match class based on the input 'pattern'.
   */
  public boolean inferMatchClassFields(final JvmDeclaredType matchClass, final Pattern pattern) {
    boolean _xblockexpression = false;
    {
      EList<Variable> _parameters = pattern.getParameters();
      for (final Variable variable : _parameters) {
        EList<JvmMember> _members = matchClass.getMembers();
        String _fieldName = this._eMFPatternLanguageJvmModelInferrerUtil.fieldName(variable);
        JvmTypeReference _calculateType = this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(variable);
        JvmField _field = this._eMFJvmTypesBuilder.toField(pattern, _fieldName, _calculateType);
        CollectionExtensions.<JvmField>operator_add(_members, _field);
      }
      EList<JvmMember> _members_1 = matchClass.getMembers();
      JvmTypeReference _newTypeRef = this._eMFJvmTypesBuilder.newTypeRef(pattern, java.lang.String.class);
      JvmTypeReference _addArrayTypeDimension = this._eMFJvmTypesBuilder.addArrayTypeDimension(_newTypeRef);
      final Procedure1<JvmField> _function = new Procedure1<JvmField>() {
          public void apply(final JvmField it) {
            {
              it.setStatic(true);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("{");
                    {
                      EList<Variable> _parameters = pattern.getParameters();
                      boolean _hasElements = false;
                      for(final Variable variable : _parameters) {
                        if (!_hasElements) {
                          _hasElements = true;
                        } else {
                          _builder.appendImmediate(", ", "");
                        }
                        _builder.append("\"");
                        String _name = variable.getName();
                        _builder.append(_name, "");
                        _builder.append("\"");
                      }
                    }
                    _builder.append("}");
                    return _builder;
                  }
                };
              PatternMatchClassInferrer.this._eMFJvmTypesBuilder.setInitializer(it, _function);
            }
          }
        };
      JvmField _field_1 = this._eMFJvmTypesBuilder.toField(pattern, "parameterNames", _addArrayTypeDimension, _function);
      boolean _operator_add = CollectionExtensions.<JvmField>operator_add(_members_1, _field_1);
      _xblockexpression = (_operator_add);
    }
    return _xblockexpression;
  }
  
  /**
   * Infers constructors for Match class based on the input 'pattern'.
   */
  public boolean inferMatchClassConstructors(final JvmDeclaredType matchClass, final Pattern pattern) {
    EList<JvmMember> _members = matchClass.getMembers();
    String _matchClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
    final Procedure1<JvmConstructor> _function = new Procedure1<JvmConstructor>() {
        public void apply(final JvmConstructor it) {
          {
            it.setVisibility(JvmVisibility.PUBLIC);
            EList<Variable> _parameters = pattern.getParameters();
            for (final Variable variable : _parameters) {
              {
                JvmTypeReference _calculateType = PatternMatchClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(variable);
                final JvmTypeReference javaType = _calculateType;
                EList<JvmFormalParameter> _parameters_1 = it.getParameters();
                String _name = variable.getName();
                JvmFormalParameter _parameter = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.toParameter(variable, _name, javaType);
                CollectionExtensions.<JvmFormalParameter>operator_add(_parameters_1, _parameter);
              }
            }
            final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                public CharSequence apply(final ImportManager it) {
                  StringConcatenation _builder = new StringConcatenation();
                  {
                    EList<Variable> _parameters = pattern.getParameters();
                    for(final Variable variable : _parameters) {
                      _builder.append("this.");
                      String _fieldName = PatternMatchClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.fieldName(variable);
                      _builder.append(_fieldName, "");
                      _builder.append(" = ");
                      String _name = variable.getName();
                      _builder.append(_name, "");
                      _builder.append(";");
                      _builder.newLineIfNotEmpty();
                    }
                  }
                  return _builder;
                }
              };
            PatternMatchClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
          }
        }
      };
    JvmConstructor _constructor = this._eMFJvmTypesBuilder.toConstructor(pattern, _matchClassName, _function);
    boolean _operator_add = CollectionExtensions.<JvmConstructor>operator_add(_members, _constructor);
    return _operator_add;
  }
  
  /**
   * Infers getters for Match class based on the input 'pattern'.
   */
  public void inferMatchClassGetters(final JvmDeclaredType matchClass, final Pattern pattern) {
      EList<JvmMember> _members = matchClass.getMembers();
      JvmTypeReference _newTypeRef = this._eMFJvmTypesBuilder.newTypeRef(pattern, java.lang.Object.class);
      final Procedure1<JvmOperation> _function = new Procedure1<JvmOperation>() {
          public void apply(final JvmOperation it) {
            {
              EList<JvmAnnotationReference> _annotations = it.getAnnotations();
              JvmAnnotationReference _annotation = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
              CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
              EList<JvmFormalParameter> _parameters = it.getParameters();
              JvmTypeReference _newTypeRef = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, java.lang.String.class);
              JvmFormalParameter _parameter = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "parameterName", _newTypeRef);
              CollectionExtensions.<JvmFormalParameter>operator_add(_parameters, _parameter);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    StringConcatenation _builder = new StringConcatenation();
                    {
                      EList<Variable> _parameters = pattern.getParameters();
                      for(final Variable variable : _parameters) {
                        _builder.append("if (\"");
                        String _name = variable.getName();
                        _builder.append(_name, "");
                        _builder.append("\".equals(parameterName)) return this.");
                        String _fieldName = PatternMatchClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.fieldName(variable);
                        _builder.append(_fieldName, "");
                        _builder.append(";");
                        _builder.newLineIfNotEmpty();
                      }
                    }
                    _builder.append("return null;");
                    _builder.newLine();
                    return _builder;
                  }
                };
              PatternMatchClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          }
        };
      JvmOperation _method = this._eMFJvmTypesBuilder.toMethod(pattern, "get", _newTypeRef, _function);
      CollectionExtensions.<JvmOperation>operator_add(_members, _method);
      EList<Variable> _parameters = pattern.getParameters();
      for (final Variable variable : _parameters) {
        EList<JvmMember> _members_1 = matchClass.getMembers();
        String _name = variable.getName();
        String _firstUpper = StringExtensions.toFirstUpper(_name);
        String _operator_plus = StringExtensions.operator_plus("get", _firstUpper);
        JvmTypeReference _calculateType = this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(variable);
        final Procedure1<JvmOperation> _function_1 = new Procedure1<JvmOperation>() {
            public void apply(final JvmOperation it) {
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("return this.");
                    String _fieldName = PatternMatchClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.fieldName(variable);
                    _builder.append(_fieldName, "");
                    _builder.append(";");
                    _builder.newLineIfNotEmpty();
                    return _builder;
                  }
                };
              PatternMatchClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          };
        JvmOperation _method_1 = this._eMFJvmTypesBuilder.toMethod(pattern, _operator_plus, _calculateType, _function_1);
        CollectionExtensions.<JvmOperation>operator_add(_members_1, _method_1);
      }
  }
  
  /**
   * Infers setters for Match class based on the input 'pattern'.
   */
  public void inferMatchClassSetters(final JvmDeclaredType matchClass, final Pattern pattern) {
      EList<JvmMember> _members = matchClass.getMembers();
      JvmTypeReference _newTypeRef = this._eMFJvmTypesBuilder.newTypeRef(pattern, boolean.class);
      final Procedure1<JvmOperation> _function = new Procedure1<JvmOperation>() {
          public void apply(final JvmOperation it) {
            {
              EList<JvmAnnotationReference> _annotations = it.getAnnotations();
              JvmAnnotationReference _annotation = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
              CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
              EList<JvmFormalParameter> _parameters = it.getParameters();
              JvmTypeReference _newTypeRef = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, java.lang.String.class);
              JvmFormalParameter _parameter = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "parameterName", _newTypeRef);
              CollectionExtensions.<JvmFormalParameter>operator_add(_parameters, _parameter);
              EList<JvmFormalParameter> _parameters_1 = it.getParameters();
              JvmTypeReference _newTypeRef_1 = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, java.lang.Object.class);
              JvmFormalParameter _parameter_1 = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "newValue", _newTypeRef_1);
              CollectionExtensions.<JvmFormalParameter>operator_add(_parameters_1, _parameter_1);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    StringConcatenation _builder = new StringConcatenation();
                    {
                      EList<Variable> _parameters = pattern.getParameters();
                      for(final Variable variable : _parameters) {
                        _builder.append("if (\"");
                        String _name = variable.getName();
                        _builder.append(_name, "");
                        _builder.append("\".equals(parameterName) && newValue instanceof ");
                        JvmTypeReference _calculateType = PatternMatchClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(variable);
                        String _simpleName = _calculateType.getSimpleName();
                        _builder.append(_simpleName, "");
                        _builder.append(") {");
                        _builder.newLineIfNotEmpty();
                        _builder.append("\t");
                        _builder.append("this.");
                        String _fieldName = PatternMatchClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.fieldName(variable);
                        _builder.append(_fieldName, "	");
                        _builder.append(" = (");
                        JvmTypeReference _calculateType_1 = PatternMatchClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(variable);
                        String _simpleName_1 = _calculateType_1.getSimpleName();
                        _builder.append(_simpleName_1, "	");
                        _builder.append(") newValue;");
                        _builder.newLineIfNotEmpty();
                        _builder.append("\t");
                        _builder.append("return true;");
                        _builder.newLine();
                        _builder.append("}");
                        _builder.newLine();
                      }
                    }
                    _builder.append("return false;");
                    _builder.newLine();
                    return _builder;
                  }
                };
              PatternMatchClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          }
        };
      JvmOperation _method = this._eMFJvmTypesBuilder.toMethod(pattern, "set", _newTypeRef, _function);
      CollectionExtensions.<JvmOperation>operator_add(_members, _method);
      EList<Variable> _parameters = pattern.getParameters();
      for (final Variable variable : _parameters) {
        EList<JvmMember> _members_1 = matchClass.getMembers();
        String _name = variable.getName();
        String _firstUpper = StringExtensions.toFirstUpper(_name);
        String _operator_plus = StringExtensions.operator_plus("set", _firstUpper);
        final Procedure1<JvmOperation> _function_1 = new Procedure1<JvmOperation>() {
            public void apply(final JvmOperation it) {
              {
                EList<JvmFormalParameter> _parameters = it.getParameters();
                String _name = variable.getName();
                JvmTypeReference _calculateType = PatternMatchClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(variable);
                JvmFormalParameter _parameter = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, _name, _calculateType);
                CollectionExtensions.<JvmFormalParameter>operator_add(_parameters, _parameter);
                final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                    public CharSequence apply(final ImportManager it) {
                      StringConcatenation _builder = new StringConcatenation();
                      _builder.append("this.");
                      String _fieldName = PatternMatchClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.fieldName(variable);
                      _builder.append(_fieldName, "");
                      _builder.append(" = ");
                      String _name = variable.getName();
                      _builder.append(_name, "");
                      _builder.append(";");
                      _builder.newLineIfNotEmpty();
                      return _builder;
                    }
                  };
                PatternMatchClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
              }
            }
          };
        JvmOperation _method_1 = this._eMFJvmTypesBuilder.toMethod(pattern, _operator_plus, null, _function_1);
        CollectionExtensions.<JvmOperation>operator_add(_members_1, _method_1);
      }
  }
  
  /**
   * Infers methods for Match class based on the input 'pattern'.
   */
  public boolean inferMatchClassMethods(final JvmDeclaredType matchClass, final Pattern pattern) {
    boolean _xblockexpression = false;
    {
      EList<JvmMember> _members = matchClass.getMembers();
      JvmTypeReference _newTypeRef = this._eMFJvmTypesBuilder.newTypeRef(pattern, java.lang.String.class);
      final Procedure1<JvmOperation> _function = new Procedure1<JvmOperation>() {
          public void apply(final JvmOperation it) {
            {
              EList<JvmAnnotationReference> _annotations = it.getAnnotations();
              JvmAnnotationReference _annotation = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
              CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("return \"");
                    QualifiedName _fullyQualifiedName = PatternMatchClassInferrer.this._iQualifiedNameProvider.getFullyQualifiedName(pattern);
                    _builder.append(_fullyQualifiedName, "");
                    _builder.append("\";");
                    _builder.newLineIfNotEmpty();
                    return _builder;
                  }
                };
              PatternMatchClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          }
        };
      JvmOperation _method = this._eMFJvmTypesBuilder.toMethod(pattern, "patternName", _newTypeRef, _function);
      CollectionExtensions.<JvmOperation>operator_add(_members, _method);
      EList<JvmMember> _members_1 = matchClass.getMembers();
      JvmTypeReference _newTypeRef_1 = this._eMFJvmTypesBuilder.newTypeRef(pattern, java.lang.String.class);
      JvmTypeReference _addArrayTypeDimension = this._eMFJvmTypesBuilder.addArrayTypeDimension(_newTypeRef_1);
      final Procedure1<JvmOperation> _function_1 = new Procedure1<JvmOperation>() {
          public void apply(final JvmOperation it) {
            {
              EList<JvmAnnotationReference> _annotations = it.getAnnotations();
              JvmAnnotationReference _annotation = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
              CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("return ");
                    String _matchClassName = PatternMatchClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
                    _builder.append(_matchClassName, "");
                    _builder.append(".parameterNames;");
                    _builder.newLineIfNotEmpty();
                    return _builder;
                  }
                };
              PatternMatchClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          }
        };
      JvmOperation _method_1 = this._eMFJvmTypesBuilder.toMethod(pattern, "parameterNames", _addArrayTypeDimension, _function_1);
      CollectionExtensions.<JvmOperation>operator_add(_members_1, _method_1);
      EList<JvmMember> _members_2 = matchClass.getMembers();
      JvmTypeReference _newTypeRef_2 = this._eMFJvmTypesBuilder.newTypeRef(pattern, java.lang.Object.class);
      JvmTypeReference _addArrayTypeDimension_1 = this._eMFJvmTypesBuilder.addArrayTypeDimension(_newTypeRef_2);
      final Procedure1<JvmOperation> _function_2 = new Procedure1<JvmOperation>() {
          public void apply(final JvmOperation it) {
            {
              EList<JvmAnnotationReference> _annotations = it.getAnnotations();
              JvmAnnotationReference _annotation = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
              CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("return new Object[]{");
                    {
                      EList<Variable> _parameters = pattern.getParameters();
                      boolean _hasElements = false;
                      for(final Variable variable : _parameters) {
                        if (!_hasElements) {
                          _hasElements = true;
                        } else {
                          _builder.appendImmediate(", ", "");
                        }
                        String _fieldName = PatternMatchClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.fieldName(variable);
                        _builder.append(_fieldName, "");
                      }
                    }
                    _builder.append("};");
                    _builder.newLineIfNotEmpty();
                    return _builder;
                  }
                };
              PatternMatchClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          }
        };
      JvmOperation _method_2 = this._eMFJvmTypesBuilder.toMethod(pattern, "toArray", _addArrayTypeDimension_1, _function_2);
      CollectionExtensions.<JvmOperation>operator_add(_members_2, _method_2);
      EList<JvmMember> _members_3 = matchClass.getMembers();
      JvmTypeReference _newTypeRef_3 = this._eMFJvmTypesBuilder.newTypeRef(pattern, java.lang.String.class);
      final Procedure1<JvmOperation> _function_3 = new Procedure1<JvmOperation>() {
          public void apply(final JvmOperation it) {
            {
              EList<JvmAnnotationReference> _annotations = it.getAnnotations();
              JvmAnnotationReference _annotation = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
              CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("StringBuilder result = new StringBuilder();");
                    _builder.newLine();
                    {
                      EList<Variable> _parameters = pattern.getParameters();
                      for(final Variable variable : _parameters) {
                        _builder.append("result.append(\"\\\"");
                        String _name = variable.getName();
                        _builder.append(_name, "");
                        _builder.append("\\\"=\" + prettyPrintValue(");
                        String _fieldName = PatternMatchClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.fieldName(variable);
                        _builder.append(_fieldName, "");
                        _builder.append(") + \"\\n\");");
                        _builder.newLineIfNotEmpty();
                      }
                    }
                    _builder.append("return result.toString();");
                    _builder.newLine();
                    return _builder;
                  }
                };
              PatternMatchClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          }
        };
      JvmOperation _method_3 = this._eMFJvmTypesBuilder.toMethod(pattern, "prettyPrint", _newTypeRef_3, _function_3);
      CollectionExtensions.<JvmOperation>operator_add(_members_3, _method_3);
      EList<JvmMember> _members_4 = matchClass.getMembers();
      JvmTypeReference _newTypeRef_4 = this._eMFJvmTypesBuilder.newTypeRef(pattern, int.class);
      final Procedure1<JvmOperation> _function_4 = new Procedure1<JvmOperation>() {
          public void apply(final JvmOperation it) {
            {
              EList<JvmAnnotationReference> _annotations = it.getAnnotations();
              JvmAnnotationReference _annotation = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
              CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("final int prime = 31;");
                    _builder.newLine();
                    _builder.append("int result = 1;");
                    _builder.newLine();
                    {
                      EList<Variable> _parameters = pattern.getParameters();
                      for(final Variable variable : _parameters) {
                        _builder.append("result = prime * result + ((");
                        String _fieldName = PatternMatchClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.fieldName(variable);
                        _builder.append(_fieldName, "");
                        _builder.append(" == null) ? 0 : ");
                        String _fieldName_1 = PatternMatchClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.fieldName(variable);
                        _builder.append(_fieldName_1, "");
                        _builder.append(".hashCode()); ");
                        _builder.newLineIfNotEmpty();
                      }
                    }
                    _builder.append("return result; ");
                    _builder.newLine();
                    return _builder;
                  }
                };
              PatternMatchClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          }
        };
      JvmOperation _method_4 = this._eMFJvmTypesBuilder.toMethod(pattern, "hashCode", _newTypeRef_4, _function_4);
      CollectionExtensions.<JvmOperation>operator_add(_members_4, _method_4);
      EList<JvmMember> _members_5 = matchClass.getMembers();
      JvmTypeReference _newTypeRef_5 = this._eMFJvmTypesBuilder.newTypeRef(pattern, boolean.class);
      final Procedure1<JvmOperation> _function_5 = new Procedure1<JvmOperation>() {
          public void apply(final JvmOperation it) {
            {
              EList<JvmAnnotationReference> _annotations = it.getAnnotations();
              JvmAnnotationReference _annotation = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
              CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
              EList<JvmFormalParameter> _parameters = it.getParameters();
              JvmTypeReference _newTypeRef = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, java.lang.Object.class);
              JvmFormalParameter _parameter = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "obj", _newTypeRef);
              CollectionExtensions.<JvmFormalParameter>operator_add(_parameters, _parameter);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    CharSequence _equalsMethodBody = PatternMatchClassInferrer.this.equalsMethodBody(pattern, it);
                    return _equalsMethodBody;
                  }
                };
              PatternMatchClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          }
        };
      JvmOperation _method_5 = this._eMFJvmTypesBuilder.toMethod(pattern, "equals", _newTypeRef_5, _function_5);
      CollectionExtensions.<JvmOperation>operator_add(_members_5, _method_5);
      EList<JvmMember> _members_6 = matchClass.getMembers();
      JvmTypeReference _newTypeRef_6 = this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern.class);
      final Procedure1<JvmOperation> _function_6 = new Procedure1<JvmOperation>() {
          public void apply(final JvmOperation it) {
            {
              EList<JvmAnnotationReference> _annotations = it.getAnnotations();
              JvmAnnotationReference _annotation = PatternMatchClassInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
              CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("return ");
                    String _matcherClassName = PatternMatchClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.matcherClassName(pattern);
                    _builder.append(_matcherClassName, "");
                    _builder.append(".FACTORY.getPattern();");
                    return _builder;
                  }
                };
              PatternMatchClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          }
        };
      JvmOperation _method_6 = this._eMFJvmTypesBuilder.toMethod(pattern, "pattern", _newTypeRef_6, _function_6);
      boolean _operator_add = CollectionExtensions.<JvmOperation>operator_add(_members_6, _method_6);
      _xblockexpression = (_operator_add);
    }
    return _xblockexpression;
  }
  
  /**
   * Infers an equals method based on the 'pattern' parameter.
   */
  public CharSequence equalsMethodBody(final Pattern pattern, final ImportManager importManager) {
      JvmTypeReference _newTypeRef = this._eMFJvmTypesBuilder.newTypeRef(pattern, java.util.Arrays.class);
      JvmType _type = _newTypeRef.getType();
      importManager.addImportFor(_type);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("if (this == obj)");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("return true;");
      _builder.newLine();
      _builder.append("if (obj == null)");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("return false;");
      _builder.newLine();
      _builder.append("if (!(obj instanceof IPatternMatch))");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("return false;");
      _builder.newLine();
      _builder.append("IPatternMatch otherSig  = (IPatternMatch) obj;");
      _builder.newLine();
      _builder.append("if (!pattern().equals(otherSig.pattern()))");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("return false;");
      _builder.newLine();
      _builder.append("if (!");
      String _matchClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
      _builder.append(_matchClassName, "");
      _builder.append(".class.equals(obj.getClass()))");
      _builder.newLineIfNotEmpty();
      _builder.append("\t");
      _builder.append("return Arrays.deepEquals(toArray(), otherSig.toArray());");
      _builder.newLine();
      {
        EList<Variable> _parameters = pattern.getParameters();
        boolean _isEmpty = _parameters.isEmpty();
        boolean _operator_not = BooleanExtensions.operator_not(_isEmpty);
        if (_operator_not) {
          String _matchClassName_1 = this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
          _builder.append(_matchClassName_1, "");
          _builder.append(" other = (");
          String _matchClassName_2 = this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
          _builder.append(_matchClassName_2, "");
          _builder.append(") obj;");
          _builder.newLineIfNotEmpty();
          {
            EList<Variable> _parameters_1 = pattern.getParameters();
            for(final Variable variable : _parameters_1) {
              _builder.append("if (");
              String _fieldName = this._eMFPatternLanguageJvmModelInferrerUtil.fieldName(variable);
              _builder.append(_fieldName, "");
              _builder.append(" == null) {if (other.");
              String _fieldName_1 = this._eMFPatternLanguageJvmModelInferrerUtil.fieldName(variable);
              _builder.append(_fieldName_1, "");
              _builder.append(" != null) return false;}");
              _builder.newLineIfNotEmpty();
              _builder.append("else if (!");
              String _fieldName_2 = this._eMFPatternLanguageJvmModelInferrerUtil.fieldName(variable);
              _builder.append(_fieldName_2, "");
              _builder.append(".equals(other.");
              String _fieldName_3 = this._eMFPatternLanguageJvmModelInferrerUtil.fieldName(variable);
              _builder.append(_fieldName_3, "");
              _builder.append(")) return false;");
              _builder.newLineIfNotEmpty();
            }
          }
        }
      }
      _builder.append("return true;");
      _builder.newLine();
      return _builder;
  }
}
