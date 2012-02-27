package org.eclipse.viatra2.patternlanguage.jvmmodel;

import com.google.inject.Inject;
import java.util.Arrays;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.jvmmodel.EMFJvmTypesBuilder;
import org.eclipse.viatra2.patternlanguage.jvmmodel.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.viatra2.patternlanguage.jvmmodel.PatternMatchClassInferrer;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.common.types.JvmAnnotationReference;
import org.eclipse.xtext.common.types.JvmConstructor;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmMember;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmParameterizedTypeReference;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.JvmVisibility;
import org.eclipse.xtext.common.types.JvmWildcardTypeReference;
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.util.IAcceptor;
import org.eclipse.xtext.xbase.compiler.ImportManager;
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer;
import org.eclipse.xtext.xbase.lib.BooleanExtensions;
import org.eclipse.xtext.xbase.lib.CollectionExtensions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
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
  private EMFJvmTypesBuilder _eMFJvmTypesBuilder;
  
  @Inject
  private EMFPatternLanguageJvmModelInferrerUtil _eMFPatternLanguageJvmModelInferrerUtil;
  
  @Inject
  private PatternMatchClassInferrer _patternMatchClassInferrer;
  
  @Inject
  private IQualifiedNameProvider _iQualifiedNameProvider;
  
  @Inject
  private TypeReferences types;
  
  @Inject
  private ISerializer serializer;
  
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
      String _name = pattern.getName();
      boolean _isNullOrEmpty = StringExtensions.isNullOrEmpty(_name);
      if (_isNullOrEmpty) {
        return;
      }
      EObject _eContainer = pattern.eContainer();
      String _packageName = ((PatternModel) _eContainer).getPackageName();
      String packageName = _packageName;
      boolean _isNullOrEmpty_1 = StringExtensions.isNullOrEmpty(packageName);
      if (_isNullOrEmpty_1) {
        packageName = "";
      } else {
        String _operator_plus = StringExtensions.operator_plus(packageName, ".");
        packageName = _operator_plus;
      }
      String _name_1 = pattern.getName();
      String _operator_plus_1 = StringExtensions.operator_plus(packageName, _name_1);
      final String mainPackageName = _operator_plus_1;
      JvmDeclaredType _inferMatchClass = this._patternMatchClassInferrer.inferMatchClass(pattern, isPrelinkingPhase, mainPackageName);
      final JvmDeclaredType matchClass = _inferMatchClass;
      JvmParameterizedTypeReference _createTypeRef = this.types.createTypeRef(matchClass);
      final JvmParameterizedTypeReference matchClassRef = _createTypeRef;
      JvmDeclaredType _inferMatcherClass = this.inferMatcherClass(pattern, isPrelinkingPhase, mainPackageName, matchClassRef);
      final JvmDeclaredType matcherClass = _inferMatcherClass;
      JvmParameterizedTypeReference _createTypeRef_1 = this.types.createTypeRef(matcherClass);
      final JvmParameterizedTypeReference matcherClassRef = _createTypeRef_1;
      JvmGenericType _inferMatcherFactoryClass = this.inferMatcherFactoryClass(pattern, isPrelinkingPhase, mainPackageName, matchClassRef, matcherClassRef);
      final JvmGenericType matcherFactoryClass = _inferMatcherFactoryClass;
      JvmDeclaredType _inferProcessorClass = this.inferProcessorClass(pattern, isPrelinkingPhase, mainPackageName, matchClassRef);
      final JvmDeclaredType processorClass = _inferProcessorClass;
      EList<JvmMember> _members = matcherClass.getMembers();
      JvmTypeReference _cloneWithProxies = this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
      JvmTypeReference _cloneWithProxies_1 = this._eMFJvmTypesBuilder.cloneWithProxies(matcherClassRef);
      JvmTypeReference _newTypeRef = this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory.class, _cloneWithProxies, _cloneWithProxies_1);
      final Procedure1<JvmField> _function = new Procedure1<JvmField>() {
          public void apply(final JvmField it) {
            {
              it.setVisibility(JvmVisibility.PUBLIC);
              it.setStatic(true);
              it.setFinal(true);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append(" ");
                    _builder.append("new ");
                    String _simpleName = matcherFactoryClass.getSimpleName();
                    _builder.append(_simpleName, " ");
                    _builder.append("()");
                    return _builder;
                  }
                };
              EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setInitializer(it, _function);
            }
          }
        };
      JvmField _field = this._eMFJvmTypesBuilder.toField(pattern, "FACTORY", _newTypeRef, _function);
      CollectionExtensions.<JvmField>operator_add(_members, _field);
      acceptor.accept(matchClass);
      acceptor.accept(matcherClass);
      acceptor.accept(matcherFactoryClass);
      acceptor.accept(processorClass);
  }
  
  public JvmDeclaredType inferMatcherClass(final Pattern pattern, final boolean isPrelinkingPhase, final String matcherPackageName, final JvmTypeReference matchClassRef) {
    String _matcherClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matcherClassName(pattern);
    final Procedure1<JvmGenericType> _function = new Procedure1<JvmGenericType>() {
        public void apply(final JvmGenericType it) {
          {
            it.setPackageName(matcherPackageName);
            CharSequence _matcherClassJavadoc = EMFPatternLanguageJvmModelInferrer.this.matcherClassJavadoc(pattern);
            String _string = _matcherClassJavadoc.toString();
            EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
            EList<JvmTypeReference> _superTypes = it.getSuperTypes();
            JvmTypeReference _cloneWithProxies = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
            JvmTypeReference _newTypeRef = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcher.class, _cloneWithProxies);
            CollectionExtensions.<JvmTypeReference>operator_add(_superTypes, _newTypeRef);
            EList<JvmTypeReference> _superTypes_1 = it.getSuperTypes();
            JvmTypeReference _cloneWithProxies_1 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
            JvmTypeReference _newTypeRef_1 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher.class, _cloneWithProxies_1);
            CollectionExtensions.<JvmTypeReference>operator_add(_superTypes_1, _newTypeRef_1);
            EList<JvmMember> _members = it.getMembers();
            String _matcherClassName = EMFPatternLanguageJvmModelInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.matcherClassName(pattern);
            final Procedure1<JvmConstructor> _function = new Procedure1<JvmConstructor>() {
                public void apply(final JvmConstructor it) {
                  {
                    it.setVisibility(JvmVisibility.PUBLIC);
                    CharSequence _matcherConstructorNotifierJavadoc = EMFPatternLanguageJvmModelInferrer.this.matcherConstructorNotifierJavadoc(pattern);
                    String _string = _matcherConstructorNotifierJavadoc.toString();
                    EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
                    EList<JvmFormalParameter> _parameters = it.getParameters();
                    JvmTypeReference _newTypeRef = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.emf.common.notify.Notifier.class);
                    JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "notifier", _newTypeRef);
                    CollectionExtensions.<JvmFormalParameter>operator_add(_parameters, _parameter);
                    EList<JvmTypeReference> _exceptions = it.getExceptions();
                    JvmTypeReference _newTypeRef_1 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException.class);
                    CollectionExtensions.<JvmTypeReference>operator_add(_exceptions, _newTypeRef_1);
                    final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                        public CharSequence apply(final ImportManager it) {
                          CharSequence _matcherConstructorBodyNotifier = EMFPatternLanguageJvmModelInferrer.this.matcherConstructorBodyNotifier(pattern, it);
                          return _matcherConstructorBodyNotifier;
                        }
                      };
                    EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
                  }
                }
              };
            JvmConstructor _constructor = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toConstructor(pattern, _matcherClassName, _function);
            CollectionExtensions.<JvmConstructor>operator_add(_members, _constructor);
            EList<JvmMember> _members_1 = it.getMembers();
            String _matcherClassName_1 = EMFPatternLanguageJvmModelInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.matcherClassName(pattern);
            final Procedure1<JvmConstructor> _function_1 = new Procedure1<JvmConstructor>() {
                public void apply(final JvmConstructor it) {
                  {
                    it.setVisibility(JvmVisibility.PUBLIC);
                    CharSequence _matcherConstructorEngineJavadoc = EMFPatternLanguageJvmModelInferrer.this.matcherConstructorEngineJavadoc(pattern);
                    String _string = _matcherConstructorEngineJavadoc.toString();
                    EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
                    EList<JvmFormalParameter> _parameters = it.getParameters();
                    JvmTypeReference _newTypeRef = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine.class);
                    JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "engine", _newTypeRef);
                    CollectionExtensions.<JvmFormalParameter>operator_add(_parameters, _parameter);
                    EList<JvmTypeReference> _exceptions = it.getExceptions();
                    JvmTypeReference _newTypeRef_1 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException.class);
                    CollectionExtensions.<JvmTypeReference>operator_add(_exceptions, _newTypeRef_1);
                    final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                        public CharSequence apply(final ImportManager it) {
                          StringConcatenation _builder = new StringConcatenation();
                          _builder.append("super(engine, FACTORY);");
                          return _builder;
                        }
                      };
                    EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
                  }
                }
              };
            JvmConstructor _constructor_1 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toConstructor(pattern, _matcherClassName_1, _function_1);
            CollectionExtensions.<JvmConstructor>operator_add(_members_1, _constructor_1);
            EList<Variable> _parameters = pattern.getParameters();
            boolean _isEmpty = _parameters.isEmpty();
            boolean _operator_not = BooleanExtensions.operator_not(_isEmpty);
            if (_operator_not) {
              {
                EList<JvmMember> _members_2 = it.getMembers();
                JvmTypeReference _cloneWithProxies_2 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
                JvmTypeReference _newTypeRef_2 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, java.util.Collection.class, _cloneWithProxies_2);
                final Procedure1<JvmOperation> _function_2 = new Procedure1<JvmOperation>() {
                    public void apply(final JvmOperation it) {
                      {
                        CharSequence _javadocGetAllMatches = EMFPatternLanguageJvmModelInferrer.this.javadocGetAllMatches(pattern);
                        String _string = _javadocGetAllMatches.toString();
                        EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
                        EList<Variable> _parameters = pattern.getParameters();
                        for (final Variable parameter : _parameters) {
                          EList<JvmFormalParameter> _parameters_1 = it.getParameters();
                          String _name = parameter.getName();
                          JvmTypeReference _calculateType = EMFPatternLanguageJvmModelInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(parameter);
                          JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toParameter(parameter, _name, _calculateType);
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
                        EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
                      }
                    }
                  };
                JvmOperation _method = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toMethod(pattern, "getAllMatches", _newTypeRef_2, _function_2);
                CollectionExtensions.<JvmOperation>operator_add(_members_2, _method);
                EList<JvmMember> _members_3 = it.getMembers();
                JvmTypeReference _cloneWithProxies_3 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
                final Procedure1<JvmOperation> _function_3 = new Procedure1<JvmOperation>() {
                    public void apply(final JvmOperation it) {
                      {
                        CharSequence _javadocGetOneArbitraryMatch = EMFPatternLanguageJvmModelInferrer.this.javadocGetOneArbitraryMatch(pattern);
                        String _string = _javadocGetOneArbitraryMatch.toString();
                        EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
                        EList<Variable> _parameters = pattern.getParameters();
                        for (final Variable parameter : _parameters) {
                          EList<JvmFormalParameter> _parameters_1 = it.getParameters();
                          String _name = parameter.getName();
                          JvmTypeReference _calculateType = EMFPatternLanguageJvmModelInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(parameter);
                          JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toParameter(parameter, _name, _calculateType);
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
                        EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
                      }
                    }
                  };
                JvmOperation _method_1 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toMethod(pattern, "getOneArbitraryMatch", _cloneWithProxies_3, _function_3);
                CollectionExtensions.<JvmOperation>operator_add(_members_3, _method_1);
                EList<JvmMember> _members_4 = it.getMembers();
                JvmTypeReference _newTypeRef_3 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, boolean.class);
                final Procedure1<JvmOperation> _function_4 = new Procedure1<JvmOperation>() {
                    public void apply(final JvmOperation it) {
                      {
                        CharSequence _javadocHasMatch = EMFPatternLanguageJvmModelInferrer.this.javadocHasMatch(pattern);
                        String _string = _javadocHasMatch.toString();
                        EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
                        EList<Variable> _parameters = pattern.getParameters();
                        for (final Variable parameter : _parameters) {
                          EList<JvmFormalParameter> _parameters_1 = it.getParameters();
                          String _name = parameter.getName();
                          JvmTypeReference _calculateType = EMFPatternLanguageJvmModelInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(parameter);
                          JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toParameter(parameter, _name, _calculateType);
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
                        EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
                      }
                    }
                  };
                JvmOperation _method_2 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toMethod(pattern, "hasMatch", _newTypeRef_3, _function_4);
                CollectionExtensions.<JvmOperation>operator_add(_members_4, _method_2);
                EList<JvmMember> _members_5 = it.getMembers();
                JvmTypeReference _newTypeRef_4 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, int.class);
                final Procedure1<JvmOperation> _function_5 = new Procedure1<JvmOperation>() {
                    public void apply(final JvmOperation it) {
                      {
                        CharSequence _javadocCountMatches = EMFPatternLanguageJvmModelInferrer.this.javadocCountMatches(pattern);
                        String _string = _javadocCountMatches.toString();
                        EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
                        EList<Variable> _parameters = pattern.getParameters();
                        for (final Variable parameter : _parameters) {
                          EList<JvmFormalParameter> _parameters_1 = it.getParameters();
                          String _name = parameter.getName();
                          JvmTypeReference _calculateType = EMFPatternLanguageJvmModelInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(parameter);
                          JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toParameter(parameter, _name, _calculateType);
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
                        EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
                      }
                    }
                  };
                JvmOperation _method_3 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toMethod(pattern, "countMatches", _newTypeRef_4, _function_5);
                CollectionExtensions.<JvmOperation>operator_add(_members_5, _method_3);
                EList<JvmMember> _members_6 = it.getMembers();
                final Procedure1<JvmOperation> _function_6 = new Procedure1<JvmOperation>() {
                    public void apply(final JvmOperation it) {
                      {
                        CharSequence _javadocForEachMatch = EMFPatternLanguageJvmModelInferrer.this.javadocForEachMatch(pattern);
                        String _string = _javadocForEachMatch.toString();
                        EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
                        EList<Variable> _parameters = pattern.getParameters();
                        for (final Variable parameter : _parameters) {
                          EList<JvmFormalParameter> _parameters_1 = it.getParameters();
                          String _name = parameter.getName();
                          JvmTypeReference _calculateType = EMFPatternLanguageJvmModelInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(parameter);
                          JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toParameter(parameter, _name, _calculateType);
                          CollectionExtensions.<JvmFormalParameter>operator_add(_parameters_1, _parameter);
                        }
                        EList<JvmFormalParameter> _parameters_2 = it.getParameters();
                        JvmTypeReference _cloneWithProxies = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
                        JvmWildcardTypeReference _wildCardSuper = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.wildCardSuper(_cloneWithProxies);
                        JvmTypeReference _newTypeRef = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor.class, _wildCardSuper);
                        JvmFormalParameter _parameter_1 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "processor", _newTypeRef);
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
                        EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
                      }
                    }
                  };
                JvmOperation _method_4 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toMethod(pattern, "forEachMatch", null, _function_6);
                CollectionExtensions.<JvmOperation>operator_add(_members_6, _method_4);
                EList<JvmMember> _members_7 = it.getMembers();
                JvmTypeReference _newTypeRef_5 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, boolean.class);
                final Procedure1<JvmOperation> _function_7 = new Procedure1<JvmOperation>() {
                    public void apply(final JvmOperation it) {
                      {
                        CharSequence _javadocForOneArbitraryMatch = EMFPatternLanguageJvmModelInferrer.this.javadocForOneArbitraryMatch(pattern);
                        String _string = _javadocForOneArbitraryMatch.toString();
                        EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
                        EList<Variable> _parameters = pattern.getParameters();
                        for (final Variable parameter : _parameters) {
                          EList<JvmFormalParameter> _parameters_1 = it.getParameters();
                          String _name = parameter.getName();
                          JvmTypeReference _calculateType = EMFPatternLanguageJvmModelInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(parameter);
                          JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toParameter(parameter, _name, _calculateType);
                          CollectionExtensions.<JvmFormalParameter>operator_add(_parameters_1, _parameter);
                        }
                        EList<JvmFormalParameter> _parameters_2 = it.getParameters();
                        JvmTypeReference _cloneWithProxies = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
                        JvmWildcardTypeReference _wildCardSuper = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.wildCardSuper(_cloneWithProxies);
                        JvmTypeReference _newTypeRef = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor.class, _wildCardSuper);
                        JvmFormalParameter _parameter_1 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "processor", _newTypeRef);
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
                        EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
                      }
                    }
                  };
                JvmOperation _method_5 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toMethod(pattern, "forOneArbitraryMatch", _newTypeRef_5, _function_7);
                CollectionExtensions.<JvmOperation>operator_add(_members_7, _method_5);
              }
            } else {
              EList<JvmMember> _members_8 = it.getMembers();
              JvmTypeReference _newTypeRef_6 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, boolean.class);
              final Procedure1<JvmOperation> _function_8 = new Procedure1<JvmOperation>() {
                  public void apply(final JvmOperation it) {
                    {
                      CharSequence _javadocHasMatchNoParameter = EMFPatternLanguageJvmModelInferrer.this.javadocHasMatchNoParameter(pattern);
                      String _string = _javadocHasMatchNoParameter.toString();
                      EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
                      final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                          public CharSequence apply(final ImportManager it) {
                            StringConcatenation _builder = new StringConcatenation();
                            _builder.append("return rawHasMatch(new Object[]{});");
                            _builder.newLine();
                            return _builder;
                          }
                        };
                      EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
                    }
                  }
                };
              JvmOperation _method_6 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toMethod(pattern, "hasMatch", _newTypeRef_6, _function_8);
              CollectionExtensions.<JvmOperation>operator_add(_members_8, _method_6);
            }
            EList<JvmMember> _members_9 = it.getMembers();
            JvmTypeReference _cloneWithProxies_4 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
            final Procedure1<JvmOperation> _function_9 = new Procedure1<JvmOperation>() {
                public void apply(final JvmOperation it) {
                  {
                    EList<JvmAnnotationReference> _annotations = it.getAnnotations();
                    JvmAnnotationReference _annotation = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
                    CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
                    EList<JvmFormalParameter> _parameters = it.getParameters();
                    JvmTypeReference _newTypeRef = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple.class);
                    JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "t", _newTypeRef);
                    CollectionExtensions.<JvmFormalParameter>operator_add(_parameters, _parameter);
                    final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                        public CharSequence apply(final ImportManager it) {
                          StringConcatenation _builder = new StringConcatenation();
                          _builder.append("try {");
                          _builder.newLine();
                          _builder.append("\t");
                          _builder.append("return new ");
                          String _matchClassName = EMFPatternLanguageJvmModelInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
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
                              JvmTypeReference _calculateType = EMFPatternLanguageJvmModelInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(p);
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
                    EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
                  }
                }
              };
            JvmOperation _method_7 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toMethod(pattern, "tupleToMatch", _cloneWithProxies_4, _function_9);
            CollectionExtensions.<JvmOperation>operator_add(_members_9, _method_7);
            EList<JvmMember> _members_10 = it.getMembers();
            JvmTypeReference _cloneWithProxies_5 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
            final Procedure1<JvmOperation> _function_10 = new Procedure1<JvmOperation>() {
                public void apply(final JvmOperation it) {
                  {
                    EList<JvmAnnotationReference> _annotations = it.getAnnotations();
                    JvmAnnotationReference _annotation = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
                    CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
                    EList<JvmFormalParameter> _parameters = it.getParameters();
                    JvmTypeReference _newTypeRef = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, java.lang.Object.class);
                    JvmTypeReference _addArrayTypeDimension = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.addArrayTypeDimension(_newTypeRef);
                    JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "match", _addArrayTypeDimension);
                    CollectionExtensions.<JvmFormalParameter>operator_add(_parameters, _parameter);
                    final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                        public CharSequence apply(final ImportManager it) {
                          StringConcatenation _builder = new StringConcatenation();
                          _builder.append("try {");
                          _builder.newLine();
                          _builder.append("\t");
                          _builder.append("return new ");
                          String _matchClassName = EMFPatternLanguageJvmModelInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
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
                              JvmTypeReference _calculateType = EMFPatternLanguageJvmModelInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(p);
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
                    EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
                  }
                }
              };
            JvmOperation _method_8 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toMethod(pattern, "arrayToMatch", _cloneWithProxies_5, _function_10);
            CollectionExtensions.<JvmOperation>operator_add(_members_10, _method_8);
          }
        }
      };
    JvmGenericType _class = this._eMFJvmTypesBuilder.toClass(pattern, _matcherClassName, _function);
    return _class;
  }
  
  public JvmGenericType inferMatcherFactoryClass(final Pattern pattern, final boolean isPrelinkingPhase, final String matcherFactoryPackageName, final JvmTypeReference matchClassRef, final JvmTypeReference matcherClassRef) {
    String _matcherFactoryClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matcherFactoryClassName(pattern);
    final Procedure1<JvmGenericType> _function = new Procedure1<JvmGenericType>() {
        public void apply(final JvmGenericType it) {
          {
            it.setPackageName(matcherFactoryPackageName);
            CharSequence _matcherFactoryClassJavadoc = EMFPatternLanguageJvmModelInferrer.this.matcherFactoryClassJavadoc(pattern);
            String _string = _matcherFactoryClassJavadoc.toString();
            EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
            EList<JvmTypeReference> _superTypes = it.getSuperTypes();
            JvmTypeReference _cloneWithProxies = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
            JvmTypeReference _cloneWithProxies_1 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matcherClassRef);
            JvmTypeReference _newTypeRef = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcherFactory.class, _cloneWithProxies, _cloneWithProxies_1);
            CollectionExtensions.<JvmTypeReference>operator_add(_superTypes, _newTypeRef);
            EList<JvmMember> _members = it.getMembers();
            JvmTypeReference _cloneWithProxies_2 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matcherClassRef);
            final Procedure1<JvmOperation> _function = new Procedure1<JvmOperation>() {
                public void apply(final JvmOperation it) {
                  {
                    it.setVisibility(JvmVisibility.PROTECTED);
                    EList<JvmAnnotationReference> _annotations = it.getAnnotations();
                    JvmAnnotationReference _annotation = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
                    CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
                    EList<JvmFormalParameter> _parameters = it.getParameters();
                    JvmTypeReference _newTypeRef = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine.class);
                    JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "engine", _newTypeRef);
                    CollectionExtensions.<JvmFormalParameter>operator_add(_parameters, _parameter);
                    EList<JvmTypeReference> _exceptions = it.getExceptions();
                    JvmTypeReference _newTypeRef_1 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException.class);
                    CollectionExtensions.<JvmTypeReference>operator_add(_exceptions, _newTypeRef_1);
                    final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                        public CharSequence apply(final ImportManager it) {
                          StringConcatenation _builder = new StringConcatenation();
                          _builder.append("return new ");
                          String _matcherClassName = EMFPatternLanguageJvmModelInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.matcherClassName(pattern);
                          _builder.append(_matcherClassName, "");
                          _builder.append("(engine);");
                          _builder.newLineIfNotEmpty();
                          return _builder;
                        }
                      };
                    EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
                  }
                }
              };
            JvmOperation _method = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toMethod(pattern, "instantiate", _cloneWithProxies_2, _function);
            CollectionExtensions.<JvmOperation>operator_add(_members, _method);
            EList<JvmMember> _members_1 = it.getMembers();
            JvmTypeReference _newTypeRef_1 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern.class);
            final Procedure1<JvmOperation> _function_1 = new Procedure1<JvmOperation>() {
                public void apply(final JvmOperation it) {
                  {
                    it.setVisibility(JvmVisibility.PROTECTED);
                    EList<JvmAnnotationReference> _annotations = it.getAnnotations();
                    JvmAnnotationReference _annotation = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
                    CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
                    final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                        public CharSequence apply(final ImportManager it) {
                          StringConcatenation _builder = new StringConcatenation();
                          CharSequence _serializeToJava = EMFPatternLanguageJvmModelInferrer.this.serializeToJava(pattern);
                          _builder.append(_serializeToJava, "");
                          _builder.newLineIfNotEmpty();
                          _builder.append("throw new UnsupportedOperationException();");
                          _builder.newLine();
                          return _builder;
                        }
                      };
                    EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
                  }
                }
              };
            JvmOperation _method_1 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toMethod(pattern, "parsePattern", _newTypeRef_1, _function_1);
            CollectionExtensions.<JvmOperation>operator_add(_members_1, _method_1);
          }
        }
      };
    JvmGenericType _class = this._eMFJvmTypesBuilder.toClass(pattern, _matcherFactoryClassName, _function);
    return _class;
  }
  
  public CharSequence serializeToJava(final EObject pattern) {
      try {
        {
          String _serialize = this.serializer.serialize(pattern);
          final String parseString = _serialize;
          String[] _split = parseString.split("[\r\n]+");
          final String[] splits = _split;
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("String patternString = \"\"");
          final StringConcatenation stringRep = ((StringConcatenation) _builder);
          stringRep.newLine();
          for (final String s : splits) {
            {
              String _operator_plus = StringExtensions.operator_plus("+\"", s);
              String _operator_plus_1 = StringExtensions.operator_plus(_operator_plus, "\"");
              stringRep.append(_operator_plus_1);
              stringRep.newLine();
            }
          }
          stringRep.append(";");
          return stringRep;
        }
      } catch (final Throwable _t) {
        if (_t instanceof Exception) {
          final Exception e = (Exception)_t;
          e.printStackTrace();
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
      return "";
  }
  
  public JvmDeclaredType inferProcessorClass(final Pattern pattern, final boolean isPrelinkingPhase, final String processorPackageName, final JvmTypeReference matchClassRef) {
    String _processorClassName = this._eMFPatternLanguageJvmModelInferrerUtil.processorClassName(pattern);
    final Procedure1<JvmGenericType> _function = new Procedure1<JvmGenericType>() {
        public void apply(final JvmGenericType it) {
          {
            it.setPackageName(processorPackageName);
            CharSequence _processorClassJavadoc = EMFPatternLanguageJvmModelInferrer.this.processorClassJavadoc(pattern);
            String _string = _processorClassJavadoc.toString();
            EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
            it.setAbstract(true);
            EList<JvmTypeReference> _superTypes = it.getSuperTypes();
            JvmTypeReference _cloneWithProxies = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
            JvmTypeReference _newTypeRef = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor.class, _cloneWithProxies);
            CollectionExtensions.<JvmTypeReference>operator_add(_superTypes, _newTypeRef);
            EList<JvmMember> _members = it.getMembers();
            final Procedure1<JvmOperation> _function = new Procedure1<JvmOperation>() {
                public void apply(final JvmOperation it) {
                  {
                    CharSequence _javadocProcess = EMFPatternLanguageJvmModelInferrer.this.javadocProcess(pattern);
                    String _string = _javadocProcess.toString();
                    EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
                    it.setAbstract(true);
                    EList<Variable> _parameters = pattern.getParameters();
                    for (final Variable parameter : _parameters) {
                      EList<JvmFormalParameter> _parameters_1 = it.getParameters();
                      String _name = parameter.getName();
                      JvmTypeReference _calculateType = EMFPatternLanguageJvmModelInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(parameter);
                      JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toParameter(parameter, _name, _calculateType);
                      CollectionExtensions.<JvmFormalParameter>operator_add(_parameters_1, _parameter);
                    }
                  }
                }
              };
            JvmOperation _method = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toMethod(pattern, "process", null, _function);
            CollectionExtensions.<JvmOperation>operator_add(_members, _method);
            EList<JvmMember> _members_1 = it.getMembers();
            final Procedure1<JvmOperation> _function_1 = new Procedure1<JvmOperation>() {
                public void apply(final JvmOperation it) {
                  {
                    EList<JvmAnnotationReference> _annotations = it.getAnnotations();
                    JvmAnnotationReference _annotation = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
                    CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
                    EList<JvmFormalParameter> _parameters = it.getParameters();
                    JvmTypeReference _cloneWithProxies = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
                    JvmFormalParameter _parameter = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "match", _cloneWithProxies);
                    CollectionExtensions.<JvmFormalParameter>operator_add(_parameters, _parameter);
                    final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                        public CharSequence apply(final ImportManager it) {
                          StringConcatenation _builder = new StringConcatenation();
                          _builder.append("process(");
                          {
                            EList<Variable> _parameters = pattern.getParameters();
                            boolean _hasElements = false;
                            for(final Variable p : _parameters) {
                              if (!_hasElements) {
                                _hasElements = true;
                              } else {
                                _builder.appendImmediate(", ", "");
                              }
                              _builder.append("match.get");
                              String _name = p.getName();
                              String _firstUpper = StringExtensions.toFirstUpper(_name);
                              _builder.append(_firstUpper, "");
                              _builder.append("()");
                            }
                          }
                          _builder.append(");  \t\t\t\t");
                          _builder.newLineIfNotEmpty();
                          return _builder;
                        }
                      };
                    EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
                  }
                }
              };
            JvmOperation _method_1 = EMFPatternLanguageJvmModelInferrer.this._eMFJvmTypesBuilder.toMethod(pattern, "process", null, _function_1);
            CollectionExtensions.<JvmOperation>operator_add(_members_1, _method_1);
          }
        }
      };
    JvmGenericType _class = this._eMFJvmTypesBuilder.toClass(pattern, _processorClassName, _function);
    return _class;
  }
  
  public CharSequence matcherConstructorBodyNotifier(final Pattern pattern, final ImportManager manager) {
      JvmTypeReference _newTypeRef = this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.EngineManager.class);
      JvmType _type = _newTypeRef.getType();
      manager.addImportFor(_type);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("this(EngineManager.getInstance().getIncQueryEngine(notifier));");
      return _builder;
  }
  
  public CharSequence matcherClassJavadoc(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Generated pattern matcher API of the ");
    QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(pattern);
    _builder.append(_fullyQualifiedName, "");
    _builder.append(" pattern, ");
    _builder.newLineIfNotEmpty();
    _builder.append("providing pattern-specific query methods.");
    _builder.newLine();
    _builder.newLine();
    String _serialize = this.serializer.serialize(pattern);
    _builder.append(_serialize, "");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("@see ");
    String _matchClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
    _builder.append(_matchClassName, "");
    _builder.newLineIfNotEmpty();
    _builder.append("@see ");
    String _matcherFactoryClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matcherFactoryClassName(pattern);
    _builder.append(_matcherFactoryClassName, "");
    _builder.newLineIfNotEmpty();
    _builder.append("@see ");
    String _processorClassName = this._eMFPatternLanguageJvmModelInferrerUtil.processorClassName(pattern);
    _builder.append(_processorClassName, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence matcherFactoryClassJavadoc(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("A pattern-specific matcher factory that can instantiate ");
    String _matcherClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matcherClassName(pattern);
    _builder.append(_matcherClassName, "");
    _builder.append(" in a type-safe way.");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("@see ");
    String _matcherClassName_1 = this._eMFPatternLanguageJvmModelInferrerUtil.matcherClassName(pattern);
    _builder.append(_matcherClassName_1, "");
    _builder.newLineIfNotEmpty();
    _builder.append("@see ");
    String _matchClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
    _builder.append(_matchClassName, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence processorClassJavadoc(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("A match processor tailored for the ");
    QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(pattern);
    _builder.append(_fullyQualifiedName, "");
    _builder.append(" pattern.");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("Clients should derive an (anonymous) class that implements the abstract process().");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence matcherConstructorNotifierJavadoc(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet). ");
    _builder.newLine();
    _builder.append("If a pattern matcher is already constructed with the same root, only a lightweight reference is created.");
    _builder.newLine();
    _builder.append("The match set will be incrementally refreshed upon updates from the given EMF root and below.");
    _builder.newLine();
    _builder.append("<p>Note: if emfRoot is a resourceSet, the scope will include even those resources that are not part of the resourceSet but are referenced. ");
    _builder.newLine();
    _builder.append("This is mainly to support nsURI-based instance-level references to registered EPackages.");
    _builder.newLine();
    _builder.append("@param emfRoot the root of the EMF tree where the pattern matcher will operate. Recommended: Resource or ResourceSet.");
    _builder.newLine();
    _builder.append("@throws IncQueryRuntimeException if an error occurs during pattern matcher creation");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence matcherConstructorEngineJavadoc(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Initializes the pattern matcher within an existing EMF-IncQuery engine. ");
    _builder.newLine();
    _builder.append("If the pattern matcher is already constructed in the engine, only a lightweight reference is created.");
    _builder.newLine();
    _builder.append("The match set will be incrementally refreshed upon updates.");
    _builder.newLine();
    _builder.append("@param engine the existing EMF-IncQuery engine in which this matcher will be created.");
    _builder.newLine();
    _builder.append("@throws IncQueryRuntimeException if an error occurs during pattern matcher creation");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence javadocGetAllMatches(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.");
    _builder.newLine();
    {
      EList<Variable> _parameters = pattern.getParameters();
      for(final Variable p : _parameters) {
        _builder.append("@param ");
        String _name = p.getName();
        _builder.append(_name, "");
        _builder.append(" the fixed value of pattern parameter ");
        String _name_1 = p.getName();
        _builder.append(_name_1, "");
        _builder.append(", or null if not bound.");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("@return matches represented as a ");
    String _matchClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
    _builder.append(_matchClassName, "");
    _builder.append(" object.");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence javadocGetOneArbitraryMatch(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.");
    _builder.newLine();
    _builder.append("Neither determinism nor randomness of selection is guaranteed.");
    _builder.newLine();
    {
      EList<Variable> _parameters = pattern.getParameters();
      for(final Variable p : _parameters) {
        _builder.append("@param ");
        String _name = p.getName();
        _builder.append(_name, "");
        _builder.append(" the fixed value of pattern parameter ");
        String _name_1 = p.getName();
        _builder.append(_name_1, "");
        _builder.append(", or null if not bound.");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("@return a match represented as a ");
    String _matchClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
    _builder.append(_matchClassName, "");
    _builder.append(" object, or null if no match is found.");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence javadocHasMatch(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,");
    _builder.newLine();
    _builder.append("under any possible substitution of the unspecified parameters (if any).");
    _builder.newLine();
    {
      EList<Variable> _parameters = pattern.getParameters();
      for(final Variable p : _parameters) {
        _builder.append("@param ");
        String _name = p.getName();
        _builder.append(_name, "");
        _builder.append(" the fixed value of pattern parameter ");
        String _name_1 = p.getName();
        _builder.append(_name_1, "");
        _builder.append(", or null if not bound.");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("@return true if the input is a valid (partial) match of the pattern.");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence javadocHasMatchNoParameter(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Indicates whether the (parameterless) pattern matches or not. ");
    _builder.newLine();
    _builder.append("@return true if the pattern has a valid match.");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence javadocCountMatches(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.");
    _builder.newLine();
    {
      EList<Variable> _parameters = pattern.getParameters();
      for(final Variable p : _parameters) {
        _builder.append("@param ");
        String _name = p.getName();
        _builder.append(_name, "");
        _builder.append(" the fixed value of pattern parameter ");
        String _name_1 = p.getName();
        _builder.append(_name_1, "");
        _builder.append(", or null if not bound.");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("@return the number of pattern matches found.");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence javadocForEachMatch(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.");
    _builder.newLine();
    {
      EList<Variable> _parameters = pattern.getParameters();
      for(final Variable p : _parameters) {
        _builder.append("@param ");
        String _name = p.getName();
        _builder.append(_name, "");
        _builder.append(" the fixed value of pattern parameter ");
        String _name_1 = p.getName();
        _builder.append(_name_1, "");
        _builder.append(", or null if not bound.");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("@param processor the action that will process each pattern match.");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence javadocForOneArbitraryMatch(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.  ");
    _builder.newLine();
    _builder.append("Neither determinism nor randomness of selection is guaranteed.");
    _builder.newLine();
    {
      EList<Variable> _parameters = pattern.getParameters();
      for(final Variable p : _parameters) {
        _builder.append("@param ");
        String _name = p.getName();
        _builder.append(_name, "");
        _builder.append(" the fixed value of pattern parameter ");
        String _name_1 = p.getName();
        _builder.append(_name_1, "");
        _builder.append(", or null if not bound.");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("@param processor the action that will process the selected match. ");
    _builder.newLine();
    _builder.append("@return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence javadocProcess(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Defines the action that is to be executed on each match.");
    _builder.newLine();
    {
      EList<Variable> _parameters = pattern.getParameters();
      for(final Variable p : _parameters) {
        _builder.append("@param ");
        String _name = p.getName();
        _builder.append(_name, "");
        _builder.append(" the value of pattern parameter ");
        String _name_1 = p.getName();
        _builder.append(_name_1, "");
        _builder.append(" in the currently processed match ");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
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
