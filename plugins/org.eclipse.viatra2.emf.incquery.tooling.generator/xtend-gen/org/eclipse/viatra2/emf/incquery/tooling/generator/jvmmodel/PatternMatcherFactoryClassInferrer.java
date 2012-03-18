package org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel;

import com.google.inject.Inject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel.JavadocInferrer;
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFJvmTypesBuilder;
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.common.types.JvmAnnotationReference;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmMember;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.JvmVisibility;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.compiler.ImportManager;
import org.eclipse.xtext.xbase.lib.CollectionExtensions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * {@link IMatcherFactory} implementation inferrer.
 * 
 * @author Mark Czotter
 */
@SuppressWarnings("all")
public class PatternMatcherFactoryClassInferrer {
  @Inject
  private EMFJvmTypesBuilder _eMFJvmTypesBuilder;
  
  @Inject
  private EMFPatternLanguageJvmModelInferrerUtil _eMFPatternLanguageJvmModelInferrerUtil;
  
  @Inject
  private JavadocInferrer _javadocInferrer;
  
  @Inject
  private IQualifiedNameProvider _iQualifiedNameProvider;
  
  /**
   * Infers the {@link IMatcherFactory} implementation class from {@link Pattern}.
   */
  public JvmDeclaredType inferMatcherFactoryClass(final Pattern pattern, final boolean isPrelinkingPhase, final String matcherFactoryPackageName, final JvmTypeReference matchClassRef, final JvmTypeReference matcherClassRef) {
      String _matcherFactoryClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matcherFactoryClassName(pattern);
      final Procedure1<JvmGenericType> _function = new Procedure1<JvmGenericType>() {
          public void apply(final JvmGenericType it) {
            {
              it.setPackageName(matcherFactoryPackageName);
              CharSequence _javadocMatcherFactoryClass = PatternMatcherFactoryClassInferrer.this._javadocInferrer.javadocMatcherFactoryClass(pattern);
              String _string = _javadocMatcherFactoryClass.toString();
              PatternMatcherFactoryClassInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
              EList<JvmTypeReference> _superTypes = it.getSuperTypes();
              JvmTypeReference _cloneWithProxies = PatternMatcherFactoryClassInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
              JvmTypeReference _cloneWithProxies_1 = PatternMatcherFactoryClassInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matcherClassRef);
              JvmTypeReference _newTypeRef = PatternMatcherFactoryClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcherFactory.class, _cloneWithProxies, _cloneWithProxies_1);
              CollectionExtensions.<JvmTypeReference>operator_add(_superTypes, _newTypeRef);
            }
          }
        };
      JvmGenericType _class = this._eMFJvmTypesBuilder.toClass(pattern, _matcherFactoryClassName, _function);
      final JvmGenericType matcherFactoryClass = _class;
      this.inferMatcherFactoryMethods(matcherFactoryClass, pattern, matcherClassRef);
      return matcherFactoryClass;
  }
  
  /**
   * Infers methods for MatcherFactory class based on the input 'pattern'.
   */
  public boolean inferMatcherFactoryMethods(final JvmDeclaredType matcherFactoryClass, final Pattern pattern, final JvmTypeReference matcherClassRef) {
    boolean _xblockexpression = false;
    {
      EList<JvmMember> _members = matcherFactoryClass.getMembers();
      JvmTypeReference _cloneWithProxies = this._eMFJvmTypesBuilder.cloneWithProxies(matcherClassRef);
      final Procedure1<JvmOperation> _function = new Procedure1<JvmOperation>() {
          public void apply(final JvmOperation it) {
            {
              it.setVisibility(JvmVisibility.PROTECTED);
              EList<JvmAnnotationReference> _annotations = it.getAnnotations();
              JvmAnnotationReference _annotation = PatternMatcherFactoryClassInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
              CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
              EList<JvmFormalParameter> _parameters = it.getParameters();
              JvmTypeReference _newTypeRef = PatternMatcherFactoryClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine.class);
              JvmFormalParameter _parameter = PatternMatcherFactoryClassInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "engine", _newTypeRef);
              CollectionExtensions.<JvmFormalParameter>operator_add(_parameters, _parameter);
              EList<JvmTypeReference> _exceptions = it.getExceptions();
              JvmTypeReference _newTypeRef_1 = PatternMatcherFactoryClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException.class);
              CollectionExtensions.<JvmTypeReference>operator_add(_exceptions, _newTypeRef_1);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("return new ");
                    String _matcherClassName = PatternMatcherFactoryClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.matcherClassName(pattern);
                    _builder.append(_matcherClassName, "");
                    _builder.append("(engine);");
                    _builder.newLineIfNotEmpty();
                    return _builder;
                  }
                };
              PatternMatcherFactoryClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          }
        };
      JvmOperation _method = this._eMFJvmTypesBuilder.toMethod(pattern, "instantiate", _cloneWithProxies, _function);
      CollectionExtensions.<JvmOperation>operator_add(_members, _method);
      EList<JvmMember> _members_1 = matcherFactoryClass.getMembers();
      JvmTypeReference _newTypeRef = this._eMFJvmTypesBuilder.newTypeRef(pattern, java.lang.String.class);
      final Procedure1<JvmOperation> _function_1 = new Procedure1<JvmOperation>() {
          public void apply(final JvmOperation it) {
            {
              it.setVisibility(JvmVisibility.PROTECTED);
              EList<JvmAnnotationReference> _annotations = it.getAnnotations();
              JvmAnnotationReference _annotation = PatternMatcherFactoryClassInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
              CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("return \"");
                    String _bundleName = PatternMatcherFactoryClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.bundleName(pattern);
                    _builder.append(_bundleName, "");
                    _builder.append("\";");
                    _builder.newLineIfNotEmpty();
                    return _builder;
                  }
                };
              PatternMatcherFactoryClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          }
        };
      JvmOperation _method_1 = this._eMFJvmTypesBuilder.toMethod(pattern, "getBundleName", _newTypeRef, _function_1);
      CollectionExtensions.<JvmOperation>operator_add(_members_1, _method_1);
      EList<JvmMember> _members_2 = matcherFactoryClass.getMembers();
      JvmTypeReference _newTypeRef_1 = this._eMFJvmTypesBuilder.newTypeRef(pattern, java.lang.String.class);
      final Procedure1<JvmOperation> _function_2 = new Procedure1<JvmOperation>() {
          public void apply(final JvmOperation it) {
            {
              it.setVisibility(JvmVisibility.PROTECTED);
              EList<JvmAnnotationReference> _annotations = it.getAnnotations();
              JvmAnnotationReference _annotation = PatternMatcherFactoryClassInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
              CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
              final Function1<ImportManager,CharSequence> _function = new Function1<ImportManager,CharSequence>() {
                  public CharSequence apply(final ImportManager it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("return \"");
                    QualifiedName _fullyQualifiedName = PatternMatcherFactoryClassInferrer.this._iQualifiedNameProvider.getFullyQualifiedName(pattern);
                    _builder.append(_fullyQualifiedName, "");
                    _builder.append("\";");
                    _builder.newLineIfNotEmpty();
                    return _builder;
                  }
                };
              PatternMatcherFactoryClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          }
        };
      JvmOperation _method_2 = this._eMFJvmTypesBuilder.toMethod(pattern, "patternName", _newTypeRef_1, _function_2);
      boolean _operator_add = CollectionExtensions.<JvmOperation>operator_add(_members_2, _method_2);
      _xblockexpression = (_operator_add);
    }
    return _xblockexpression;
  }
}
