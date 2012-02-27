package org.eclipse.viatra2.patternlanguage.jvmmodel;

import com.google.inject.Inject;
import java.util.Arrays;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.jvmmodel.EMFJvmTypesBuilder;
import org.eclipse.viatra2.patternlanguage.jvmmodel.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.viatra2.patternlanguage.jvmmodel.PatternMatchClassInferrer;
import org.eclipse.viatra2.patternlanguage.jvmmodel.PatternMatchProcessorClassInferrer;
import org.eclipse.viatra2.patternlanguage.jvmmodel.PatternMatcherClassInferrer;
import org.eclipse.viatra2.patternlanguage.jvmmodel.PatternMatcherFactoryClassInferrer;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmMember;
import org.eclipse.xtext.common.types.JvmParameterizedTypeReference;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.JvmVisibility;
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.util.IAcceptor;
import org.eclipse.xtext.xbase.compiler.ImportManager;
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer;
import org.eclipse.xtext.xbase.lib.CollectionExtensions;
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
  private PatternMatcherClassInferrer _patternMatcherClassInferrer;
  
  @Inject
  private PatternMatcherFactoryClassInferrer _patternMatcherFactoryClassInferrer;
  
  @Inject
  private PatternMatchProcessorClassInferrer _patternMatchProcessorClassInferrer;
  
  @Inject
  private TypeReferences types;
  
  /**
   * Is called for each Pattern instance in a resource.
   * 
   * @param pattern - the model to create one or more JvmDeclaredTypes from.
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
      String _packageName = this._eMFPatternLanguageJvmModelInferrerUtil.getPackageName(pattern);
      final String packageName = _packageName;
      JvmDeclaredType _inferMatchClass = this._patternMatchClassInferrer.inferMatchClass(pattern, isPrelinkingPhase, packageName);
      final JvmDeclaredType matchClass = _inferMatchClass;
      JvmParameterizedTypeReference _createTypeRef = this.types.createTypeRef(matchClass);
      final JvmParameterizedTypeReference matchClassRef = _createTypeRef;
      JvmDeclaredType _inferMatcherClass = this._patternMatcherClassInferrer.inferMatcherClass(pattern, isPrelinkingPhase, packageName, matchClassRef);
      final JvmDeclaredType matcherClass = _inferMatcherClass;
      JvmParameterizedTypeReference _createTypeRef_1 = this.types.createTypeRef(matcherClass);
      final JvmParameterizedTypeReference matcherClassRef = _createTypeRef_1;
      JvmDeclaredType _inferMatcherFactoryClass = this._patternMatcherFactoryClassInferrer.inferMatcherFactoryClass(pattern, isPrelinkingPhase, packageName, matchClassRef, matcherClassRef);
      final JvmDeclaredType matcherFactoryClass = _inferMatcherFactoryClass;
      JvmDeclaredType _inferProcessorClass = this._patternMatchProcessorClassInferrer.inferProcessorClass(pattern, isPrelinkingPhase, packageName, matchClassRef);
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
