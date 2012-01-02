package org.eclipse.viatra2.patternlanguage.jvmmodel;

import com.google.inject.Inject;
import java.util.Arrays;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmMember;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.util.IAcceptor;
import org.eclipse.xtext.xbase.compiler.ImportManager;
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder;
import org.eclipse.xtext.xbase.lib.CollectionExtensions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.StringExtensions;

/**
 * <p>Infers a JVM model from the source model.</p>
 * 
 * <p>The JVM model should contain all elements that would appear in the Java code
 * which is generated from the source model. Other models link against the JVM model rather than the source model.</p>
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
    String _name = pattern.getName();
    String _firstUpper = StringExtensions.toFirstUpper(_name);
    String _operator_plus = StringExtensions.operator_plus(_firstUpper, "Matcher");
    final Procedure1<JvmGenericType> _function = new Procedure1<JvmGenericType>() {
        public void apply(final JvmGenericType it) {
          {
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
    JvmGenericType _class = this._jvmTypesBuilder.toClass(pattern, _operator_plus, _function);
    acceptor.accept(_class);
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
