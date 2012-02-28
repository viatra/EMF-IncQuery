package org.eclipse.viatra2.patternlanguage.jvmmodel;

import com.google.inject.Inject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.jvmmodel.EMFJvmTypesBuilder;
import org.eclipse.viatra2.patternlanguage.jvmmodel.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.viatra2.patternlanguage.jvmmodel.JavadocInferrer;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.common.types.JvmAnnotationReference;
import org.eclipse.xtext.common.types.JvmConstructor;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmMember;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.JvmVisibility;
import org.eclipse.xtext.common.types.JvmWildcardTypeReference;
import org.eclipse.xtext.xbase.compiler.ImportManager;
import org.eclipse.xtext.xbase.lib.BooleanExtensions;
import org.eclipse.xtext.xbase.lib.CollectionExtensions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * {@link IncQueryMatcher} implementation inferrer.
 * 
 * @author Mark Czotter
 */
@SuppressWarnings("all")
public class PatternMatcherClassInferrer {
  @Inject
  private EMFJvmTypesBuilder _eMFJvmTypesBuilder;
  
  @Inject
  private EMFPatternLanguageJvmModelInferrerUtil _eMFPatternLanguageJvmModelInferrerUtil;
  
  @Inject
  private JavadocInferrer _javadocInferrer;
  
  /**
   * Infers the {@link IncQueryMatcher} implementation class from a {@link Pattern}.
   */
  public JvmDeclaredType inferMatcherClass(final Pattern pattern, final boolean isPrelinkingPhase, final String matcherPackageName, final JvmTypeReference matchClassRef) {
      String _matcherClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matcherClassName(pattern);
      final Procedure1<JvmGenericType> _function = new Procedure1<JvmGenericType>() {
          public void apply(final JvmGenericType it) {
            {
              it.setPackageName(matcherPackageName);
              CharSequence _javadocMatcherClass = PatternMatcherClassInferrer.this._javadocInferrer.javadocMatcherClass(pattern);
              String _string = _javadocMatcherClass.toString();
              PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
              EList<JvmTypeReference> _superTypes = it.getSuperTypes();
              JvmTypeReference _cloneWithProxies = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
              JvmTypeReference _newTypeRef = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcher.class, _cloneWithProxies);
              CollectionExtensions.<JvmTypeReference>operator_add(_superTypes, _newTypeRef);
              EList<JvmTypeReference> _superTypes_1 = it.getSuperTypes();
              JvmTypeReference _cloneWithProxies_1 = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
              JvmTypeReference _newTypeRef_1 = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher.class, _cloneWithProxies_1);
              CollectionExtensions.<JvmTypeReference>operator_add(_superTypes_1, _newTypeRef_1);
            }
          }
        };
      JvmGenericType _class = this._eMFJvmTypesBuilder.toClass(pattern, _matcherClassName, _function);
      final JvmGenericType matcherClass = _class;
      this.inferMatcherClassConstructors(matcherClass, pattern);
      this.inferMatcherClassMethods(matcherClass, pattern, matchClassRef);
      this.inferMatcherClassToMatchMethods(matcherClass, pattern, matchClassRef);
      return matcherClass;
  }
  
  /**
   * Infers constructors for Matcher class based on the input 'pattern'.
   */
  public boolean inferMatcherClassConstructors(final JvmDeclaredType matcherClass, final Pattern pattern) {
    boolean _xblockexpression = false;
    {
      EList<JvmMember> _members = matcherClass.getMembers();
      String _matcherClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matcherClassName(pattern);
      final Procedure1<JvmConstructor> _function = new Procedure1<JvmConstructor>() {
          public void apply(final JvmConstructor it) {
            {
              it.setVisibility(JvmVisibility.PUBLIC);
              CharSequence _javadocMatcherConstructorNotifier = PatternMatcherClassInferrer.this._javadocInferrer.javadocMatcherConstructorNotifier(pattern);
              String _string = _javadocMatcherConstructorNotifier.toString();
              PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
              EList<JvmFormalParameter> _parameters = it.getParameters();
              JvmTypeReference _newTypeRef = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.emf.common.notify.Notifier.class);
              JvmFormalParameter _parameter = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "notifier", _newTypeRef);
              CollectionExtensions.<JvmFormalParameter>operator_add(_parameters, _parameter);
              EList<JvmTypeReference> _exceptions = it.getExceptions();
              JvmTypeReference _newTypeRef_1 = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException.class);
              CollectionExtensions.<JvmTypeReference>operator_add(_exceptions, _newTypeRef_1);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    CharSequence _matcherConstructorBodyNotifier = PatternMatcherClassInferrer.this.matcherConstructorBodyNotifier(pattern, it);
                    return _matcherConstructorBodyNotifier;
                  }
                };
              PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          }
        };
      JvmConstructor _constructor = this._eMFJvmTypesBuilder.toConstructor(pattern, _matcherClassName, _function);
      CollectionExtensions.<JvmConstructor>operator_add(_members, _constructor);
      EList<JvmMember> _members_1 = matcherClass.getMembers();
      String _matcherClassName_1 = this._eMFPatternLanguageJvmModelInferrerUtil.matcherClassName(pattern);
      final Procedure1<JvmConstructor> _function_1 = new Procedure1<JvmConstructor>() {
          public void apply(final JvmConstructor it) {
            {
              it.setVisibility(JvmVisibility.PUBLIC);
              CharSequence _javadocMatcherConstructorEngine = PatternMatcherClassInferrer.this._javadocInferrer.javadocMatcherConstructorEngine(pattern);
              String _string = _javadocMatcherConstructorEngine.toString();
              PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
              EList<JvmFormalParameter> _parameters = it.getParameters();
              JvmTypeReference _newTypeRef = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine.class);
              JvmFormalParameter _parameter = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "engine", _newTypeRef);
              CollectionExtensions.<JvmFormalParameter>operator_add(_parameters, _parameter);
              EList<JvmTypeReference> _exceptions = it.getExceptions();
              JvmTypeReference _newTypeRef_1 = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException.class);
              CollectionExtensions.<JvmTypeReference>operator_add(_exceptions, _newTypeRef_1);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("super(engine, FACTORY);");
                    return _builder;
                  }
                };
              PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          }
        };
      JvmConstructor _constructor_1 = this._eMFJvmTypesBuilder.toConstructor(pattern, _matcherClassName_1, _function_1);
      boolean _operator_add = CollectionExtensions.<JvmConstructor>operator_add(_members_1, _constructor_1);
      _xblockexpression = (_operator_add);
    }
    return _xblockexpression;
  }
  
  /**
   * Infers methods for Matcher class based on the input 'pattern'.
   */
  public boolean inferMatcherClassMethods(final JvmDeclaredType matcherClass, final Pattern pattern, final JvmTypeReference matchClassRef) {
    boolean _xifexpression = false;
    EList<Variable> _parameters = pattern.getParameters();
    boolean _isEmpty = _parameters.isEmpty();
    boolean _operator_not = BooleanExtensions.operator_not(_isEmpty);
    if (_operator_not) {
      boolean _xblockexpression = false;
      {
        EList<JvmMember> _members = matcherClass.getMembers();
        JvmTypeReference _cloneWithProxies = this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
        JvmTypeReference _newTypeRef = this._eMFJvmTypesBuilder.newTypeRef(pattern, java.util.Collection.class, _cloneWithProxies);
        final Procedure1<JvmOperation> _function = new Procedure1<JvmOperation>() {
            public void apply(final JvmOperation it) {
              {
                CharSequence _javadocGetAllMatchesMethod = PatternMatcherClassInferrer.this._javadocInferrer.javadocGetAllMatchesMethod(pattern);
                String _string = _javadocGetAllMatchesMethod.toString();
                PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
                EList<Variable> _parameters = pattern.getParameters();
                for (final Variable parameter : _parameters) {
                  EList<JvmFormalParameter> _parameters_1 = it.getParameters();
                  String _name = parameter.getName();
                  JvmTypeReference _calculateType = PatternMatcherClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(parameter);
                  JvmFormalParameter _parameter = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.toParameter(parameter, _name, _calculateType);
                  CollectionExtensions.<JvmFormalParameter>operator_add(_parameters_1, _parameter);
                }
                final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                    public CharSequence apply(final ImportManager it) {
                      StringConcatenation _builder = new StringConcatenation();
                      _builder.append("return rawGetAllMatches(new Object[]{");
                      {
                        EList<Variable> _parameters = pattern.getParameters();
                        boolean _hasElements = false;
                        for(final Variable p : _parameters) {
                          if (!_hasElements) {
                            _hasElements = true;
                          } else {
                            _builder.appendImmediate(", ", "");
                          }
                          String _name = p.getName();
                          _builder.append(_name, "");
                        }
                      }
                      _builder.append("});");
                      _builder.newLineIfNotEmpty();
                      return _builder;
                    }
                  };
                PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
              }
            }
          };
        JvmOperation _method = this._eMFJvmTypesBuilder.toMethod(pattern, "getAllMatches", _newTypeRef, _function);
        CollectionExtensions.<JvmOperation>operator_add(_members, _method);
        EList<JvmMember> _members_1 = matcherClass.getMembers();
        JvmTypeReference _cloneWithProxies_1 = this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
        final Procedure1<JvmOperation> _function_1 = new Procedure1<JvmOperation>() {
            public void apply(final JvmOperation it) {
              {
                CharSequence _javadocGetOneArbitraryMatchMethod = PatternMatcherClassInferrer.this._javadocInferrer.javadocGetOneArbitraryMatchMethod(pattern);
                String _string = _javadocGetOneArbitraryMatchMethod.toString();
                PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
                EList<Variable> _parameters = pattern.getParameters();
                for (final Variable parameter : _parameters) {
                  EList<JvmFormalParameter> _parameters_1 = it.getParameters();
                  String _name = parameter.getName();
                  JvmTypeReference _calculateType = PatternMatcherClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(parameter);
                  JvmFormalParameter _parameter = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.toParameter(parameter, _name, _calculateType);
                  CollectionExtensions.<JvmFormalParameter>operator_add(_parameters_1, _parameter);
                }
                final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                    public CharSequence apply(final ImportManager it) {
                      StringConcatenation _builder = new StringConcatenation();
                      _builder.append("return rawGetOneArbitraryMatch(new Object[]{");
                      {
                        EList<Variable> _parameters = pattern.getParameters();
                        boolean _hasElements = false;
                        for(final Variable p : _parameters) {
                          if (!_hasElements) {
                            _hasElements = true;
                          } else {
                            _builder.appendImmediate(", ", "");
                          }
                          String _name = p.getName();
                          _builder.append(_name, "");
                        }
                      }
                      _builder.append("});");
                      _builder.newLineIfNotEmpty();
                      return _builder;
                    }
                  };
                PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
              }
            }
          };
        JvmOperation _method_1 = this._eMFJvmTypesBuilder.toMethod(pattern, "getOneArbitraryMatch", _cloneWithProxies_1, _function_1);
        CollectionExtensions.<JvmOperation>operator_add(_members_1, _method_1);
        EList<JvmMember> _members_2 = matcherClass.getMembers();
        JvmTypeReference _newTypeRef_1 = this._eMFJvmTypesBuilder.newTypeRef(pattern, boolean.class);
        final Procedure1<JvmOperation> _function_2 = new Procedure1<JvmOperation>() {
            public void apply(final JvmOperation it) {
              {
                CharSequence _javadocHasMatchMethod = PatternMatcherClassInferrer.this._javadocInferrer.javadocHasMatchMethod(pattern);
                String _string = _javadocHasMatchMethod.toString();
                PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
                EList<Variable> _parameters = pattern.getParameters();
                for (final Variable parameter : _parameters) {
                  EList<JvmFormalParameter> _parameters_1 = it.getParameters();
                  String _name = parameter.getName();
                  JvmTypeReference _calculateType = PatternMatcherClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(parameter);
                  JvmFormalParameter _parameter = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.toParameter(parameter, _name, _calculateType);
                  CollectionExtensions.<JvmFormalParameter>operator_add(_parameters_1, _parameter);
                }
                final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                    public CharSequence apply(final ImportManager it) {
                      StringConcatenation _builder = new StringConcatenation();
                      _builder.append("return rawHasMatch(new Object[]{");
                      {
                        EList<Variable> _parameters = pattern.getParameters();
                        boolean _hasElements = false;
                        for(final Variable p : _parameters) {
                          if (!_hasElements) {
                            _hasElements = true;
                          } else {
                            _builder.appendImmediate(", ", "");
                          }
                          String _name = p.getName();
                          _builder.append(_name, "");
                        }
                      }
                      _builder.append("});");
                      _builder.newLineIfNotEmpty();
                      return _builder;
                    }
                  };
                PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
              }
            }
          };
        JvmOperation _method_2 = this._eMFJvmTypesBuilder.toMethod(pattern, "hasMatch", _newTypeRef_1, _function_2);
        CollectionExtensions.<JvmOperation>operator_add(_members_2, _method_2);
        EList<JvmMember> _members_3 = matcherClass.getMembers();
        JvmTypeReference _newTypeRef_2 = this._eMFJvmTypesBuilder.newTypeRef(pattern, int.class);
        final Procedure1<JvmOperation> _function_3 = new Procedure1<JvmOperation>() {
            public void apply(final JvmOperation it) {
              {
                CharSequence _javadocCountMatchesMethod = PatternMatcherClassInferrer.this._javadocInferrer.javadocCountMatchesMethod(pattern);
                String _string = _javadocCountMatchesMethod.toString();
                PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
                EList<Variable> _parameters = pattern.getParameters();
                for (final Variable parameter : _parameters) {
                  EList<JvmFormalParameter> _parameters_1 = it.getParameters();
                  String _name = parameter.getName();
                  JvmTypeReference _calculateType = PatternMatcherClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(parameter);
                  JvmFormalParameter _parameter = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.toParameter(parameter, _name, _calculateType);
                  CollectionExtensions.<JvmFormalParameter>operator_add(_parameters_1, _parameter);
                }
                final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                    public CharSequence apply(final ImportManager it) {
                      StringConcatenation _builder = new StringConcatenation();
                      _builder.append("return rawCountMatches(new Object[]{");
                      {
                        EList<Variable> _parameters = pattern.getParameters();
                        boolean _hasElements = false;
                        for(final Variable p : _parameters) {
                          if (!_hasElements) {
                            _hasElements = true;
                          } else {
                            _builder.appendImmediate(", ", "");
                          }
                          String _name = p.getName();
                          _builder.append(_name, "");
                        }
                      }
                      _builder.append("});");
                      _builder.newLineIfNotEmpty();
                      return _builder;
                    }
                  };
                PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
              }
            }
          };
        JvmOperation _method_3 = this._eMFJvmTypesBuilder.toMethod(pattern, "countMatches", _newTypeRef_2, _function_3);
        CollectionExtensions.<JvmOperation>operator_add(_members_3, _method_3);
        EList<JvmMember> _members_4 = matcherClass.getMembers();
        final Procedure1<JvmOperation> _function_4 = new Procedure1<JvmOperation>() {
            public void apply(final JvmOperation it) {
              {
                CharSequence _javadocForEachMatchMethod = PatternMatcherClassInferrer.this._javadocInferrer.javadocForEachMatchMethod(pattern);
                String _string = _javadocForEachMatchMethod.toString();
                PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
                EList<Variable> _parameters = pattern.getParameters();
                for (final Variable parameter : _parameters) {
                  EList<JvmFormalParameter> _parameters_1 = it.getParameters();
                  String _name = parameter.getName();
                  JvmTypeReference _calculateType = PatternMatcherClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(parameter);
                  JvmFormalParameter _parameter = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.toParameter(parameter, _name, _calculateType);
                  CollectionExtensions.<JvmFormalParameter>operator_add(_parameters_1, _parameter);
                }
                EList<JvmFormalParameter> _parameters_2 = it.getParameters();
                JvmTypeReference _cloneWithProxies = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
                JvmWildcardTypeReference _wildCardSuper = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.wildCardSuper(_cloneWithProxies);
                JvmTypeReference _newTypeRef = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor.class, _wildCardSuper);
                JvmFormalParameter _parameter_1 = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "processor", _newTypeRef);
                CollectionExtensions.<JvmFormalParameter>operator_add(_parameters_2, _parameter_1);
                final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                    public CharSequence apply(final ImportManager it) {
                      StringConcatenation _builder = new StringConcatenation();
                      _builder.append("rawForEachMatch(new Object[]{");
                      {
                        EList<Variable> _parameters = pattern.getParameters();
                        boolean _hasElements = false;
                        for(final Variable p : _parameters) {
                          if (!_hasElements) {
                            _hasElements = true;
                          } else {
                            _builder.appendImmediate(", ", "");
                          }
                          String _name = p.getName();
                          _builder.append(_name, "");
                        }
                      }
                      _builder.append("}, processor);");
                      _builder.newLineIfNotEmpty();
                      return _builder;
                    }
                  };
                PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
              }
            }
          };
        JvmOperation _method_4 = this._eMFJvmTypesBuilder.toMethod(pattern, "forEachMatch", null, _function_4);
        CollectionExtensions.<JvmOperation>operator_add(_members_4, _method_4);
        EList<JvmMember> _members_5 = matcherClass.getMembers();
        JvmTypeReference _newTypeRef_3 = this._eMFJvmTypesBuilder.newTypeRef(pattern, boolean.class);
        final Procedure1<JvmOperation> _function_5 = new Procedure1<JvmOperation>() {
            public void apply(final JvmOperation it) {
              {
                CharSequence _javadocForOneArbitraryMatchMethod = PatternMatcherClassInferrer.this._javadocInferrer.javadocForOneArbitraryMatchMethod(pattern);
                String _string = _javadocForOneArbitraryMatchMethod.toString();
                PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
                EList<Variable> _parameters = pattern.getParameters();
                for (final Variable parameter : _parameters) {
                  EList<JvmFormalParameter> _parameters_1 = it.getParameters();
                  String _name = parameter.getName();
                  JvmTypeReference _calculateType = PatternMatcherClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(parameter);
                  JvmFormalParameter _parameter = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.toParameter(parameter, _name, _calculateType);
                  CollectionExtensions.<JvmFormalParameter>operator_add(_parameters_1, _parameter);
                }
                EList<JvmFormalParameter> _parameters_2 = it.getParameters();
                JvmTypeReference _cloneWithProxies = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
                JvmWildcardTypeReference _wildCardSuper = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.wildCardSuper(_cloneWithProxies);
                JvmTypeReference _newTypeRef = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor.class, _wildCardSuper);
                JvmFormalParameter _parameter_1 = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "processor", _newTypeRef);
                CollectionExtensions.<JvmFormalParameter>operator_add(_parameters_2, _parameter_1);
                final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                    public CharSequence apply(final ImportManager it) {
                      StringConcatenation _builder = new StringConcatenation();
                      _builder.append("return rawForOneArbitraryMatch(new Object[]{");
                      {
                        EList<Variable> _parameters = pattern.getParameters();
                        boolean _hasElements = false;
                        for(final Variable p : _parameters) {
                          if (!_hasElements) {
                            _hasElements = true;
                          } else {
                            _builder.appendImmediate(", ", "");
                          }
                          String _name = p.getName();
                          _builder.append(_name, "");
                        }
                      }
                      _builder.append("}, processor);");
                      _builder.newLineIfNotEmpty();
                      return _builder;
                    }
                  };
                PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
              }
            }
          };
        JvmOperation _method_5 = this._eMFJvmTypesBuilder.toMethod(pattern, "forOneArbitraryMatch", _newTypeRef_3, _function_5);
        boolean _operator_add = CollectionExtensions.<JvmOperation>operator_add(_members_5, _method_5);
        _xblockexpression = (_operator_add);
      }
      _xifexpression = _xblockexpression;
    } else {
      EList<JvmMember> _members_6 = matcherClass.getMembers();
      JvmTypeReference _newTypeRef_4 = this._eMFJvmTypesBuilder.newTypeRef(pattern, boolean.class);
      final Procedure1<JvmOperation> _function_6 = new Procedure1<JvmOperation>() {
          public void apply(final JvmOperation it) {
            {
              CharSequence _javadocHasMatchMethodNoParameter = PatternMatcherClassInferrer.this._javadocInferrer.javadocHasMatchMethodNoParameter(pattern);
              String _string = _javadocHasMatchMethodNoParameter.toString();
              PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("return rawHasMatch(new Object[]{});");
                    _builder.newLine();
                    return _builder;
                  }
                };
              PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          }
        };
      JvmOperation _method_6 = this._eMFJvmTypesBuilder.toMethod(pattern, "hasMatch", _newTypeRef_4, _function_6);
      boolean _operator_add_1 = CollectionExtensions.<JvmOperation>operator_add(_members_6, _method_6);
      _xifexpression = _operator_add_1;
    }
    return _xifexpression;
  }
  
  /**
   * Infers tupleToMatch, arrayToMatch methods for Matcher class based on the input 'pattern'.
   */
  public boolean inferMatcherClassToMatchMethods(final JvmDeclaredType matcherClass, final Pattern pattern, final JvmTypeReference matchClassRef) {
    boolean _xblockexpression = false;
    {
      EList<JvmMember> _members = matcherClass.getMembers();
      JvmTypeReference _cloneWithProxies = this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
      final Procedure1<JvmOperation> _function = new Procedure1<JvmOperation>() {
          public void apply(final JvmOperation it) {
            {
              EList<JvmAnnotationReference> _annotations = it.getAnnotations();
              JvmAnnotationReference _annotation = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
              CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
              EList<JvmFormalParameter> _parameters = it.getParameters();
              JvmTypeReference _newTypeRef = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple.class);
              JvmFormalParameter _parameter = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "t", _newTypeRef);
              CollectionExtensions.<JvmFormalParameter>operator_add(_parameters, _parameter);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("try {");
                    _builder.newLine();
                    _builder.append("\t");
                    _builder.append("return new ");
                    String _matchClassName = PatternMatcherClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
                    _builder.append(_matchClassName, "	");
                    _builder.append("(");
                    {
                      EList<Variable> _parameters = pattern.getParameters();
                      boolean _hasElements = false;
                      for(final Variable p : _parameters) {
                        if (!_hasElements) {
                          _hasElements = true;
                        } else {
                          _builder.appendImmediate(", ", "	");
                        }
                        _builder.append("(");
                        JvmTypeReference _calculateType = PatternMatcherClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(p);
                        String _simpleName = _calculateType.getSimpleName();
                        _builder.append(_simpleName, "	");
                        _builder.append(") t.get(");
                        EList<Variable> _parameters_1 = pattern.getParameters();
                        int _indexOf = _parameters_1.indexOf(p);
                        _builder.append(_indexOf, "	");
                        _builder.append(")");
                      }
                    }
                    _builder.append(");\t");
                    _builder.newLineIfNotEmpty();
                    _builder.append("} catch(ClassCastException e) {");
                    _builder.newLine();
                    _builder.append("\t");
                    _builder.append("throw new IncQueryRuntimeException(e.getMessage());");
                    _builder.newLine();
                    _builder.append("}");
                    _builder.newLine();
                    return _builder;
                  }
                };
              PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          }
        };
      JvmOperation _method = this._eMFJvmTypesBuilder.toMethod(pattern, "tupleToMatch", _cloneWithProxies, _function);
      CollectionExtensions.<JvmOperation>operator_add(_members, _method);
      EList<JvmMember> _members_1 = matcherClass.getMembers();
      JvmTypeReference _cloneWithProxies_1 = this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
      final Procedure1<JvmOperation> _function_1 = new Procedure1<JvmOperation>() {
          public void apply(final JvmOperation it) {
            {
              EList<JvmAnnotationReference> _annotations = it.getAnnotations();
              JvmAnnotationReference _annotation = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
              CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
              EList<JvmFormalParameter> _parameters = it.getParameters();
              JvmTypeReference _newTypeRef = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, java.lang.Object.class);
              JvmTypeReference _addArrayTypeDimension = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.addArrayTypeDimension(_newTypeRef);
              JvmFormalParameter _parameter = PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "match", _addArrayTypeDimension);
              CollectionExtensions.<JvmFormalParameter>operator_add(_parameters, _parameter);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("try {");
                    _builder.newLine();
                    _builder.append("\t");
                    _builder.append("return new ");
                    String _matchClassName = PatternMatcherClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
                    _builder.append(_matchClassName, "	");
                    _builder.append("(");
                    {
                      EList<Variable> _parameters = pattern.getParameters();
                      boolean _hasElements = false;
                      for(final Variable p : _parameters) {
                        if (!_hasElements) {
                          _hasElements = true;
                        } else {
                          _builder.appendImmediate(", ", "	");
                        }
                        _builder.append("(");
                        JvmTypeReference _calculateType = PatternMatcherClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(p);
                        String _simpleName = _calculateType.getSimpleName();
                        _builder.append(_simpleName, "	");
                        _builder.append(") match[");
                        EList<Variable> _parameters_1 = pattern.getParameters();
                        int _indexOf = _parameters_1.indexOf(p);
                        _builder.append(_indexOf, "	");
                        _builder.append("]");
                      }
                    }
                    _builder.append(");");
                    _builder.newLineIfNotEmpty();
                    _builder.append("} catch(ClassCastException e) {");
                    _builder.newLine();
                    _builder.append("\t");
                    _builder.append("throw new IncQueryRuntimeException(e.getMessage());");
                    _builder.newLine();
                    _builder.append("}");
                    _builder.newLine();
                    return _builder;
                  }
                };
              PatternMatcherClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          }
        };
      JvmOperation _method_1 = this._eMFJvmTypesBuilder.toMethod(pattern, "arrayToMatch", _cloneWithProxies_1, _function_1);
      boolean _operator_add = CollectionExtensions.<JvmOperation>operator_add(_members_1, _method_1);
      _xblockexpression = (_operator_add);
    }
    return _xblockexpression;
  }
  
  /**
   * Infers body for Matcher class constructor (Notifier) based on the input 'pattern'.
   */
  public CharSequence matcherConstructorBodyNotifier(final Pattern pattern, final ImportManager manager) {
      JvmTypeReference _newTypeRef = this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.EngineManager.class);
      JvmType _type = _newTypeRef.getType();
      manager.addImportFor(_type);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("this(EngineManager.getInstance().getIncQueryEngine(notifier));");
      return _builder;
  }
}
