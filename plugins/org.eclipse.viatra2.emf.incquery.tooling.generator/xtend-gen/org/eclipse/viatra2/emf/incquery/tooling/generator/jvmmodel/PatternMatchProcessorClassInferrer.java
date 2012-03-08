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
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmMember;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.xbase.compiler.ImportManager;
import org.eclipse.xtext.xbase.lib.CollectionExtensions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.StringExtensions;

/**
 * {@link IMatchProcessor} implementation inferer.
 * 
 * @author Mark Czotter
 */
@SuppressWarnings("all")
public class PatternMatchProcessorClassInferrer {
  @Inject
  private EMFJvmTypesBuilder _eMFJvmTypesBuilder;
  
  @Inject
  private EMFPatternLanguageJvmModelInferrerUtil _eMFPatternLanguageJvmModelInferrerUtil;
  
  @Inject
  private JavadocInferrer _javadocInferrer;
  
  /**
   * Infers the {@link IMatchProcessor} implementation class from a {@link Pattern}.
   */
  public JvmDeclaredType inferProcessorClass(final Pattern pattern, final boolean isPrelinkingPhase, final String processorPackageName, final JvmTypeReference matchClassRef) {
      String _processorClassName = this._eMFPatternLanguageJvmModelInferrerUtil.processorClassName(pattern);
      final Procedure1<JvmGenericType> _function = new Procedure1<JvmGenericType>() {
          public void apply(final JvmGenericType it) {
            {
              it.setPackageName(processorPackageName);
              CharSequence _javadocProcessorClass = PatternMatchProcessorClassInferrer.this._javadocInferrer.javadocProcessorClass(pattern);
              String _string = _javadocProcessorClass.toString();
              PatternMatchProcessorClassInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
              it.setAbstract(true);
              EList<JvmTypeReference> _superTypes = it.getSuperTypes();
              JvmTypeReference _cloneWithProxies = PatternMatchProcessorClassInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
              JvmTypeReference _newTypeRef = PatternMatchProcessorClassInferrer.this._eMFJvmTypesBuilder.newTypeRef(pattern, org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor.class, _cloneWithProxies);
              CollectionExtensions.<JvmTypeReference>operator_add(_superTypes, _newTypeRef);
            }
          }
        };
      JvmGenericType _class = this._eMFJvmTypesBuilder.toClass(pattern, _processorClassName, _function);
      final JvmGenericType processorClass = _class;
      this.inferProcessorClassMethods(processorClass, pattern, matchClassRef);
      return processorClass;
  }
  
  /**
   * Infers methods for Processor class based on the input 'pattern'.
   */
  public boolean inferProcessorClassMethods(final JvmDeclaredType processorClass, final Pattern pattern, final JvmTypeReference matchClassRef) {
    boolean _xblockexpression = false;
    {
      EList<JvmMember> _members = processorClass.getMembers();
      final Procedure1<JvmOperation> _function = new Procedure1<JvmOperation>() {
          public void apply(final JvmOperation it) {
            {
              CharSequence _javadocProcessMethod = PatternMatchProcessorClassInferrer.this._javadocInferrer.javadocProcessMethod(pattern);
              String _string = _javadocProcessMethod.toString();
              PatternMatchProcessorClassInferrer.this._eMFJvmTypesBuilder.setDocumentation(it, _string);
              it.setAbstract(true);
              EList<Variable> _parameters = pattern.getParameters();
              for (final Variable parameter : _parameters) {
                EList<JvmFormalParameter> _parameters_1 = it.getParameters();
                String _name = parameter.getName();
                JvmTypeReference _calculateType = PatternMatchProcessorClassInferrer.this._eMFPatternLanguageJvmModelInferrerUtil.calculateType(parameter);
                JvmFormalParameter _parameter = PatternMatchProcessorClassInferrer.this._eMFJvmTypesBuilder.toParameter(parameter, _name, _calculateType);
                CollectionExtensions.<JvmFormalParameter>operator_add(_parameters_1, _parameter);
              }
            }
          }
        };
      JvmOperation _method = this._eMFJvmTypesBuilder.toMethod(pattern, "process", null, _function);
      CollectionExtensions.<JvmOperation>operator_add(_members, _method);
      EList<JvmMember> _members_1 = processorClass.getMembers();
      final Procedure1<JvmOperation> _function_1 = new Procedure1<JvmOperation>() {
          public void apply(final JvmOperation it) {
            {
              EList<JvmAnnotationReference> _annotations = it.getAnnotations();
              JvmAnnotationReference _annotation = PatternMatchProcessorClassInferrer.this._eMFJvmTypesBuilder.toAnnotation(pattern, java.lang.Override.class);
              CollectionExtensions.<JvmAnnotationReference>operator_add(_annotations, _annotation);
              EList<JvmFormalParameter> _parameters = it.getParameters();
              JvmTypeReference _cloneWithProxies = PatternMatchProcessorClassInferrer.this._eMFJvmTypesBuilder.cloneWithProxies(matchClassRef);
              JvmFormalParameter _parameter = PatternMatchProcessorClassInferrer.this._eMFJvmTypesBuilder.toParameter(pattern, "match", _cloneWithProxies);
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
              PatternMatchProcessorClassInferrer.this._eMFJvmTypesBuilder.setBody(it, _function);
            }
          }
        };
      JvmOperation _method_1 = this._eMFJvmTypesBuilder.toMethod(pattern, "process", null, _function_1);
      boolean _operator_add = CollectionExtensions.<JvmOperation>operator_add(_members_1, _method_1);
      _xblockexpression = (_operator_add);
    }
    return _xblockexpression;
  }
}
