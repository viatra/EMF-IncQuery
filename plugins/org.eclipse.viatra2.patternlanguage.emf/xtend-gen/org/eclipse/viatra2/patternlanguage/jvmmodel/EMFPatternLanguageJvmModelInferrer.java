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
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Type;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassConstraint;
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
import org.eclipse.xtext.util.IAcceptor;
import org.eclipse.xtext.xbase.compiler.ImportManager;
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder;
import org.eclipse.xtext.xbase.lib.BooleanExtensions;
import org.eclipse.xtext.xbase.lib.CollectionExtensions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.StringExtensions;

/**
 * <p>Infers a JVM model from the source model.</p>
 * 
 * <p>The JVM model should contain all elements that would appear in the Java code
 * which is generated from the source model. Other models link against the JVM model rather than the source model.</p>
 * 
 * @author Mark Czotter
 */
@SuppressWarnings("all")
public class EMFPatternLanguageJvmModelInferrer extends AbstractModelInferrer {
  /**
   * convenience API to build and initialize JvmTypes and their members.
   */
  @Inject
  private JvmTypesBuilder _jvmTypesBuilder;
  
  /**
   * Is called for each Pattern instance in a resource.
   * 
   * @param element - the model to create one or more JvmDeclaredTypes from.
   * @param acceptor - each created JvmDeclaredType without a container should be passed to the acceptor in order get attached to the
   *                   current resource.
   * @param isPreLinkingPhase - whether the method is called in a pre linking phase, i.e. when the global index isn't fully updated. You
   *        must not rely on linking using the index if iPrelinkingPhase is <code>true</code>
   */
  protected void _infer(final Pattern pattern, final IAcceptor<JvmDeclaredType> acceptor, final boolean isPrelinkingPhase) {
      EObject _eContainer = pattern.eContainer();
      String _packageName = ((PatternModel) _eContainer).getPackageName();
      final String mainPackageName = _packageName;
      JvmDeclaredType _inferMatchClass = this.inferMatchClass(pattern, isPrelinkingPhase, mainPackageName);
      final JvmDeclaredType matchClass = _inferMatchClass;
      JvmGenericType _inferMatcherClass = this.inferMatcherClass(pattern, isPrelinkingPhase, mainPackageName);
      final JvmGenericType matcherClass = _inferMatcherClass;
      acceptor.accept(matchClass);
      acceptor.accept(matcherClass);
  }
  
  public JvmDeclaredType inferMatchClass(final Pattern pattern, final boolean isPrelinkingPhase, final String mainPackageName) {
    String _matchClassName = this.matchClassName(pattern);
    final Procedure1<JvmGenericType> _function = new Procedure1<JvmGenericType>() {
        public void apply(final JvmGenericType it) {
          {
            it.setPackageName(mainPackageName);
            EList<JvmTypeReference> _superTypes = it.getSuperTypes();
            JvmTypeReference _newTypeRef = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternMatch.class);
            CollectionExtensions.<JvmTypeReference>operator_add(_superTypes, _newTypeRef);
            EList<JvmTypeReference> _superTypes_1 = it.getSuperTypes();
            JvmTypeReference _newTypeRef_1 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch.class);
            CollectionExtensions.<JvmTypeReference>operator_add(_superTypes_1, _newTypeRef_1);
            EList<Variable> _parameters = pattern.getParameters();
            for (final Variable variable : _parameters) {
              EList<JvmMember> _members = it.getMembers();
              String _fieldName = EMFPatternLanguageJvmModelInferrer.this.fieldName(variable);
              JvmTypeReference _calculateType = EMFPatternLanguageJvmModelInferrer.this.calculateType(variable);
              JvmField _field = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toField(pattern, _fieldName, _calculateType);
              CollectionExtensions.<JvmField>operator_add(_members, _field);
            }
            EList<JvmMember> _members_1 = it.getMembers();
            JvmTypeReference _newTypeRef_2 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.newTypeRef(pattern, java.lang.String.class);
            JvmTypeReference _addArrayTypeDimension = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.addArrayTypeDimension(_newTypeRef_2);
            final Procedure1<JvmField> _function = new Procedure1<JvmField>() {
                public void apply(final JvmField it) {
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
                  EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.setInitializer(it, _function);
                }
              };
            JvmField _field_1 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toField(pattern, "parameterNames", _addArrayTypeDimension, _function);
            CollectionExtensions.<JvmField>operator_add(_members_1, _field_1);
            EList<JvmMember> _members_2 = it.getMembers();
            String _matchClassName = EMFPatternLanguageJvmModelInferrer.this.matchClassName(pattern);
            final Procedure1<JvmConstructor> _function_1 = new Procedure1<JvmConstructor>() {
                public void apply(final JvmConstructor it) {
                  {
                    it.setVisibility(JvmVisibility.PUBLIC);
                    EList<Variable> _parameters = pattern.getParameters();
                    for (final Variable variable : _parameters) {
                      {
                        JvmTypeReference _calculateType = EMFPatternLanguageJvmModelInferrer.this.calculateType(variable);
                        final JvmTypeReference javaType = _calculateType;
                        EList<JvmFormalParameter> _parameters_1 = it.getParameters();
                        String _name = variable.getName();
                        JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toParameter(variable, _name, javaType);
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
                              String _name = variable.getName();
                              _builder.append(_name, "");
                              _builder.append(" = ");
                              String _name_1 = variable.getName();
                              _builder.append(_name_1, "");
                              _builder.append(";");
                              _builder.newLineIfNotEmpty();
                            }
                          }
                          return _builder;
                        }
                      };
                    EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.setBody(it, _function);
                  }
                }
              };
            JvmConstructor _constructor = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toConstructor(pattern, _matchClassName, _function_1);
            CollectionExtensions.<JvmConstructor>operator_add(_members_2, _constructor);
            EList<JvmMember> _members_3 = it.getMembers();
            JvmTypeReference _newTypeRef_3 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.newTypeRef(pattern, java.lang.String.class);
            final Procedure1<JvmOperation> _function_2 = new Procedure1<JvmOperation>() {
                public void apply(final JvmOperation it) {
                  {
                    EList<JvmAnnotationReference> _annotations = it.getAnnotations();
                    JvmAnnotationReference _annotation = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
                    CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
                    final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                        public CharSequence apply(final ImportManager it) {
                          StringConcatenation _builder = new StringConcatenation();
                          _builder.append("return \"");
                          String _name = pattern.getName();
                          _builder.append(_name, "");
                          _builder.append("\";");
                          _builder.newLineIfNotEmpty();
                          return _builder;
                        }
                      };
                    EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.setBody(it, _function);
                  }
                }
              };
            JvmOperation _method = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toMethod(pattern, "patternName", _newTypeRef_3, _function_2);
            CollectionExtensions.<JvmOperation>operator_add(_members_3, _method);
            EList<JvmMember> _members_4 = it.getMembers();
            JvmTypeReference _newTypeRef_4 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.newTypeRef(pattern, java.lang.Object.class);
            final Procedure1<JvmOperation> _function_3 = new Procedure1<JvmOperation>() {
                public void apply(final JvmOperation it) {
                  {
                    EList<JvmAnnotationReference> _annotations = it.getAnnotations();
                    JvmAnnotationReference _annotation = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
                    CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
                    EList<JvmFormalParameter> _parameters = it.getParameters();
                    JvmTypeReference _newTypeRef = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.newTypeRef(pattern, java.lang.String.class);
                    JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toParameter(pattern, "parameterName", _newTypeRef);
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
                              String _fieldName = EMFPatternLanguageJvmModelInferrer.this.fieldName(variable);
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
                    EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.setBody(it, _function);
                  }
                }
              };
            JvmOperation _method_1 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toMethod(pattern, "get", _newTypeRef_4, _function_3);
            CollectionExtensions.<JvmOperation>operator_add(_members_4, _method_1);
            EList<Variable> _parameters_1 = pattern.getParameters();
            for (final Variable variable_1 : _parameters_1) {
              EList<JvmMember> _members_5 = it.getMembers();
              String _name = variable_1.getName();
              String _firstUpper = StringExtensions.toFirstUpper(_name);
              String _operator_plus = StringExtensions.operator_plus("get", _firstUpper);
              JvmTypeReference _calculateType_1 = EMFPatternLanguageJvmModelInferrer.this.calculateType(variable_1);
              final Procedure1<JvmOperation> _function_4 = new Procedure1<JvmOperation>() {
                  public void apply(final JvmOperation it) {
                    final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                        public CharSequence apply(final ImportManager it) {
                          StringConcatenation _builder = new StringConcatenation();
                          _builder.append("return this.");
                          String _fieldName = EMFPatternLanguageJvmModelInferrer.this.fieldName(variable_1);
                          _builder.append(_fieldName, "");
                          _builder.append(";");
                          _builder.newLineIfNotEmpty();
                          return _builder;
                        }
                      };
                    EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.setBody(it, _function);
                  }
                };
              JvmOperation _method_2 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toMethod(pattern, _operator_plus, _calculateType_1, _function_4);
              CollectionExtensions.<JvmOperation>operator_add(_members_5, _method_2);
            }
            EList<JvmMember> _members_6 = it.getMembers();
            JvmTypeReference _newTypeRef_5 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.newTypeRef(pattern, boolean.class);
            final Procedure1<JvmOperation> _function_5 = new Procedure1<JvmOperation>() {
                public void apply(final JvmOperation it) {
                  {
                    EList<JvmAnnotationReference> _annotations = it.getAnnotations();
                    JvmAnnotationReference _annotation = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
                    CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
                    EList<JvmFormalParameter> _parameters = it.getParameters();
                    JvmTypeReference _newTypeRef = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.newTypeRef(pattern, java.lang.String.class);
                    JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toParameter(pattern, "parameterName", _newTypeRef);
                    CollectionExtensions.<JvmFormalParameter>operator_add(_parameters, _parameter);
                    EList<JvmFormalParameter> _parameters_1 = it.getParameters();
                    JvmTypeReference _newTypeRef_1 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.newTypeRef(pattern, java.lang.Object.class);
                    JvmFormalParameter _parameter_1 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toParameter(pattern, "newValue", _newTypeRef_1);
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
                              JvmTypeReference _calculateType = EMFPatternLanguageJvmModelInferrer.this.calculateType(variable);
                              String _simpleName = _calculateType.getSimpleName();
                              _builder.append(_simpleName, "");
                              _builder.append(") {");
                              _builder.newLineIfNotEmpty();
                              _builder.append("\t");
                              _builder.append("this.");
                              String _fieldName = EMFPatternLanguageJvmModelInferrer.this.fieldName(variable);
                              _builder.append(_fieldName, "	");
                              _builder.append(" = (");
                              JvmTypeReference _calculateType_1 = EMFPatternLanguageJvmModelInferrer.this.calculateType(variable);
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
                    EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.setBody(it, _function);
                  }
                }
              };
            JvmOperation _method_3 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toMethod(pattern, "set", _newTypeRef_5, _function_5);
            CollectionExtensions.<JvmOperation>operator_add(_members_6, _method_3);
            EList<Variable> _parameters_2 = pattern.getParameters();
            for (final Variable variable_2 : _parameters_2) {
              EList<JvmMember> _members_7 = it.getMembers();
              String _name_1 = variable_2.getName();
              String _firstUpper_1 = StringExtensions.toFirstUpper(_name_1);
              String _operator_plus_1 = StringExtensions.operator_plus("set", _firstUpper_1);
              final Procedure1<JvmOperation> _function_6 = new Procedure1<JvmOperation>() {
                  public void apply(final JvmOperation it) {
                    {
                      EList<JvmFormalParameter> _parameters = it.getParameters();
                      String _name = variable_2.getName();
                      JvmTypeReference _calculateType = EMFPatternLanguageJvmModelInferrer.this.calculateType(variable_2);
                      JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toParameter(pattern, _name, _calculateType);
                      CollectionExtensions.<JvmFormalParameter>operator_add(_parameters, _parameter);
                      final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                          public CharSequence apply(final ImportManager it) {
                            StringConcatenation _builder = new StringConcatenation();
                            _builder.append("this.");
                            String _fieldName = EMFPatternLanguageJvmModelInferrer.this.fieldName(variable_2);
                            _builder.append(_fieldName, "");
                            _builder.append(" = ");
                            String _name = variable_2.getName();
                            _builder.append(_name, "");
                            _builder.append(";");
                            _builder.newLineIfNotEmpty();
                            return _builder;
                          }
                        };
                      EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.setBody(it, _function);
                    }
                  }
                };
              JvmOperation _method_4 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toMethod(pattern, _operator_plus_1, null, _function_6);
              CollectionExtensions.<JvmOperation>operator_add(_members_7, _method_4);
            }
            EList<JvmMember> _members_8 = it.getMembers();
            JvmTypeReference _newTypeRef_6 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.newTypeRef(pattern, java.lang.String.class);
            JvmTypeReference _addArrayTypeDimension_1 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.addArrayTypeDimension(_newTypeRef_6);
            final Procedure1<JvmOperation> _function_7 = new Procedure1<JvmOperation>() {
                public void apply(final JvmOperation it) {
                  {
                    EList<JvmAnnotationReference> _annotations = it.getAnnotations();
                    JvmAnnotationReference _annotation = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
                    CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
                    final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                        public CharSequence apply(final ImportManager it) {
                          StringConcatenation _builder = new StringConcatenation();
                          _builder.append("return parameterNames;");
                          _builder.newLine();
                          return _builder;
                        }
                      };
                    EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.setBody(it, _function);
                  }
                }
              };
            JvmOperation _method_5 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toMethod(pattern, "parameterNames", _addArrayTypeDimension_1, _function_7);
            CollectionExtensions.<JvmOperation>operator_add(_members_8, _method_5);
            EList<JvmMember> _members_9 = it.getMembers();
            JvmTypeReference _newTypeRef_7 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.newTypeRef(pattern, java.lang.Object.class);
            JvmTypeReference _addArrayTypeDimension_2 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.addArrayTypeDimension(_newTypeRef_7);
            final Procedure1<JvmOperation> _function_8 = new Procedure1<JvmOperation>() {
                public void apply(final JvmOperation it) {
                  {
                    EList<JvmAnnotationReference> _annotations = it.getAnnotations();
                    JvmAnnotationReference _annotation = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
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
                              String _fieldName = EMFPatternLanguageJvmModelInferrer.this.fieldName(variable);
                              _builder.append(_fieldName, "");
                            }
                          }
                          _builder.append("};");
                          _builder.newLineIfNotEmpty();
                          return _builder;
                        }
                      };
                    EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.setBody(it, _function);
                  }
                }
              };
            JvmOperation _method_6 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toMethod(pattern, "toArray", _addArrayTypeDimension_2, _function_8);
            CollectionExtensions.<JvmOperation>operator_add(_members_9, _method_6);
            EList<JvmMember> _members_10 = it.getMembers();
            JvmTypeReference _newTypeRef_8 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.newTypeRef(pattern, java.lang.String.class);
            final Procedure1<JvmOperation> _function_9 = new Procedure1<JvmOperation>() {
                public void apply(final JvmOperation it) {
                  {
                    EList<JvmAnnotationReference> _annotations = it.getAnnotations();
                    JvmAnnotationReference _annotation = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
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
                              String _fieldName = EMFPatternLanguageJvmModelInferrer.this.fieldName(variable);
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
                    EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.setBody(it, _function);
                  }
                }
              };
            JvmOperation _method_7 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toMethod(pattern, "prettyPrint", _newTypeRef_8, _function_9);
            CollectionExtensions.<JvmOperation>operator_add(_members_10, _method_7);
            EList<JvmMember> _members_11 = it.getMembers();
            JvmTypeReference _newTypeRef_9 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.newTypeRef(pattern, int.class);
            final Procedure1<JvmOperation> _function_10 = new Procedure1<JvmOperation>() {
                public void apply(final JvmOperation it) {
                  {
                    EList<JvmAnnotationReference> _annotations = it.getAnnotations();
                    JvmAnnotationReference _annotation = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
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
                              String _fieldName = EMFPatternLanguageJvmModelInferrer.this.fieldName(variable);
                              _builder.append(_fieldName, "");
                              _builder.append(" == null) ? 0 : ");
                              String _fieldName_1 = EMFPatternLanguageJvmModelInferrer.this.fieldName(variable);
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
                    EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.setBody(it, _function);
                  }
                }
              };
            JvmOperation _method_8 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toMethod(pattern, "hashCode", _newTypeRef_9, _function_10);
            CollectionExtensions.<JvmOperation>operator_add(_members_11, _method_8);
            EList<JvmMember> _members_12 = it.getMembers();
            JvmTypeReference _newTypeRef_10 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.newTypeRef(pattern, boolean.class);
            final Procedure1<JvmOperation> _function_11 = new Procedure1<JvmOperation>() {
                public void apply(final JvmOperation it) {
                  {
                    EList<JvmAnnotationReference> _annotations = it.getAnnotations();
                    JvmAnnotationReference _annotation = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
                    CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
                    EList<JvmFormalParameter> _parameters = it.getParameters();
                    JvmTypeReference _newTypeRef = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.newTypeRef(pattern, java.lang.Object.class);
                    JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toParameter(pattern, "obj", _newTypeRef);
                    CollectionExtensions.<JvmFormalParameter>operator_add(_parameters, _parameter);
                    final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                        public CharSequence apply(final ImportManager it) {
                          CharSequence _equalsMethodBody = EMFPatternLanguageJvmModelInferrer.this.equalsMethodBody(pattern, it);
                          return _equalsMethodBody;
                        }
                      };
                    EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.setBody(it, _function);
                  }
                }
              };
            JvmOperation _method_9 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toMethod(pattern, "equals", _newTypeRef_10, _function_11);
            CollectionExtensions.<JvmOperation>operator_add(_members_12, _method_9);
          }
        }
      };
    JvmGenericType _class = this._jvmTypesBuilder.toClass(pattern, _matchClassName, _function);
    return _class;
  }
  
  public JvmGenericType inferMatcherClass(final Pattern pattern, final boolean isPrelinkingPhase, final String mainPackageName) {
    String _matcherClassName = this.matcherClassName(pattern);
    final Procedure1<JvmGenericType> _function = new Procedure1<JvmGenericType>() {
        public void apply(final JvmGenericType it) {
          {
            it.setPackageName(mainPackageName);
            EList<JvmTypeReference> _superTypes = it.getSuperTypes();
            JvmTypeReference _newTypeRef = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternMatcher.class);
            CollectionExtensions.<JvmTypeReference>operator_add(_superTypes, _newTypeRef);
            EList<JvmMember> _members = it.getMembers();
            JvmTypeReference _newTypeRef_1 = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.newTypeRef(pattern, java.lang.String.class);
            final Procedure1<JvmOperation> _function = new Procedure1<JvmOperation>() {
                public void apply(final JvmOperation it) {
                  {
                    EList<Variable> _parameters = pattern.getParameters();
                    for (final Variable parameter : _parameters) {
                      {
                        JvmTypeReference _newTypeRef = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.newTypeRef(pattern, java.lang.Object.class);
                        final JvmTypeReference javaType = _newTypeRef;
                        EList<JvmFormalParameter> _parameters_1 = it.getParameters();
                        String _name = parameter.getName();
                        JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toParameter(parameter, _name, javaType);
                        CollectionExtensions.<JvmFormalParameter>operator_add(_parameters_1, _parameter);
                      }
                    }
                    final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                        public CharSequence apply(final ImportManager it) {
                          StringConcatenation _builder = new StringConcatenation();
                          _builder.append("return \"Hello ");
                          String _name = pattern.getName();
                          _builder.append(_name, "");
                          _builder.append("\";");
                          _builder.newLineIfNotEmpty();
                          return _builder;
                        }
                      };
                    EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.setBody(it, _function);
                  }
                }
              };
            JvmOperation _method = EMFPatternLanguageJvmModelInferrer.this._jvmTypesBuilder.toMethod(pattern, "getAllMatches", _newTypeRef_1, _function);
            CollectionExtensions.<JvmOperation>operator_add(_members, _method);
          }
        }
      };
    JvmGenericType _class = this._jvmTypesBuilder.toClass(pattern, _matcherClassName, _function);
    return _class;
  }
  
  public CharSequence equalsMethodBody(final Pattern pattern, final ImportManager importManager) {
      JvmTypeReference _newTypeRef = this._jvmTypesBuilder.newTypeRef(pattern, java.util.Arrays.class);
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
      _builder.append("if (!patternName().equals(otherSig.patternName()))");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("return false;");
      _builder.newLine();
      _builder.append("if (!");
      String _matchClassName = this.matchClassName(pattern);
      _builder.append(_matchClassName, "");
      _builder.append(".class.equals(obj.getClass()))");
      _builder.newLineIfNotEmpty();
      _builder.append("\t");
      _builder.append("return Arrays.deepEquals(toArray(), otherSig.toArray());");
      _builder.newLine();
      String _matchClassName_1 = this.matchClassName(pattern);
      _builder.append(_matchClassName_1, "");
      _builder.append(" other = (");
      String _matchClassName_2 = this.matchClassName(pattern);
      _builder.append(_matchClassName_2, "");
      _builder.append(") obj;");
      _builder.newLineIfNotEmpty();
      {
        EList<Variable> _parameters = pattern.getParameters();
        for(final Variable variable : _parameters) {
          _builder.append("if (");
          String _fieldName = this.fieldName(variable);
          _builder.append(_fieldName, "");
          _builder.append(" == null) {if (other.");
          String _fieldName_1 = this.fieldName(variable);
          _builder.append(_fieldName_1, "");
          _builder.append(" != null) return false;}");
          _builder.newLineIfNotEmpty();
          _builder.append("else if (!");
          String _fieldName_2 = this.fieldName(variable);
          _builder.append(_fieldName_2, "");
          _builder.append(".equals(other.");
          String _fieldName_3 = this.fieldName(variable);
          _builder.append(_fieldName_3, "");
          _builder.append(")) return false;");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("return true;");
      _builder.newLine();
      return _builder;
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
  
  public String fieldName(final Variable variable) {
    String _name = variable.getName();
    String _firstUpper = StringExtensions.toFirstUpper(_name);
    String _operator_plus = StringExtensions.operator_plus("f", _firstUpper);
    return _operator_plus;
  }
  
  public JvmTypeReference calculateType(final Variable variable) {
    boolean _operator_and = false;
    Type _type = variable.getType();
    boolean _operator_notEquals = ObjectExtensions.operator_notEquals(_type, null);
    if (!_operator_notEquals) {
      _operator_and = false;
    } else {
      Type _type_1 = variable.getType();
      String _typename = _type_1.getTypename();
      boolean _isNullOrEmpty = StringExtensions.isNullOrEmpty(_typename);
      boolean _operator_not = BooleanExtensions.operator_not(_isNullOrEmpty);
      _operator_and = BooleanExtensions.operator_and(_operator_notEquals, _operator_not);
    }
    if (_operator_and) {
      Type _type_2 = variable.getType();
      String _typename_1 = _type_2.getTypename();
      JvmTypeReference _newTypeRef = this._jvmTypesBuilder.newTypeRef(variable, _typename_1);
      return _newTypeRef;
    } else {
      {
        EObject _eContainer = variable.eContainer();
        if ((_eContainer instanceof Pattern)) {
          {
            EObject _eContainer_1 = variable.eContainer();
            final Pattern pattern = ((Pattern) _eContainer_1);
            EList<PatternBody> _bodies = pattern.getBodies();
            for (final PatternBody body : _bodies) {
              EList<Constraint> _constraints = body.getConstraints();
              for (final Constraint constraint : _constraints) {
                if ((constraint instanceof EClassConstraint)) {
                  {
                    EntityType _type_3 = ((EClassConstraint) constraint).getType();
                    final EntityType entityType = _type_3;
                    VariableReference _var = ((EClassConstraint) constraint).getVar();
                    final VariableReference variableRef = _var;
                    boolean _operator_or = false;
                    Variable _variable = variableRef.getVariable();
                    boolean _operator_equals = ObjectExtensions.operator_equals(_variable, variable);
                    if (_operator_equals) {
                      _operator_or = true;
                    } else {
                      String _var_1 = variableRef.getVar();
                      String _name = variable.getName();
                      boolean _equals = _var_1.equals(_name);
                      _operator_or = BooleanExtensions.operator_or(_operator_equals, _equals);
                    }
                    if (_operator_or) {
                      if ((entityType instanceof ClassType)) {
                        {
                          EClass _classname = ((ClassType) entityType).getClassname();
                          Class<? extends Object> _instanceClass = _classname.getInstanceClass();
                          final Class<? extends Object> clazz = _instanceClass;
                          JvmTypeReference _newTypeRef_1 = this._jvmTypesBuilder.newTypeRef(variable, clazz);
                          final JvmTypeReference typeref = _newTypeRef_1;
                          boolean _operator_notEquals_1 = ObjectExtensions.operator_notEquals(typeref, null);
                          if (_operator_notEquals_1) {
                            return typeref;
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
        JvmTypeReference _newTypeRef_2 = this._jvmTypesBuilder.newTypeRef(variable, java.lang.Object.class);
        return _newTypeRef_2;
      }
    }
  }
  
  public void infer(final EObject pattern, final IAcceptor<JvmDeclaredType> acceptor, final boolean isPrelinkingPhase) {
    if (pattern instanceof Pattern) {
      _infer((Pattern)pattern, acceptor, isPrelinkingPhase);
    } else if (pattern != null) {
      _infer(pattern, acceptor, isPrelinkingPhase);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(pattern, acceptor, isPrelinkingPhase).toString());
    }
  }
}
