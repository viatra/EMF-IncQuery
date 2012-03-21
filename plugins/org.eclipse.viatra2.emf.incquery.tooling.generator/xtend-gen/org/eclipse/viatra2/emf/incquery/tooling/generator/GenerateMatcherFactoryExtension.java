package org.eclipse.viatra2.emf.incquery.tooling.generator;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.viatra2.emf.incquery.tooling.generator.ExtensionGenerator;
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations;
import org.eclipse.xtext.xbase.lib.BooleanExtensions;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class GenerateMatcherFactoryExtension {
  @Inject
  private IJvmModelAssociations associations;
  
  @Inject
  private EMFPatternLanguageJvmModelInferrerUtil _eMFPatternLanguageJvmModelInferrerUtil;
  
  public ArrayList<IPluginExtension> extensionContribution(final Pattern pattern, final ExtensionGenerator exGen) {
    String _fullyQualifiedName = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
    final Procedure1<IPluginExtension> _function = new Procedure1<IPluginExtension>() {
        public void apply(final IPluginExtension it) {
          final Procedure1<IPluginElement> _function = new Procedure1<IPluginElement>() {
              public void apply(final IPluginElement it) {
                {
                  String _fullyQualifiedName = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
                  exGen.contribAttribute(it, "id", _fullyQualifiedName);
                  Set<EObject> _jvmElements = GenerateMatcherFactoryExtension.this.associations.getJvmElements(pattern);
                  final Function1<EObject,Boolean> _function = new Function1<EObject,Boolean>() {
                      public Boolean apply(final EObject it) {
                        boolean _operator_and = false;
                        if (!(it instanceof JvmType)) {
                          _operator_and = false;
                        } else {
                          String _simpleName = ((JvmType) it).getSimpleName();
                          String _matcherFactoryClassName = GenerateMatcherFactoryExtension.this._eMFPatternLanguageJvmModelInferrerUtil.matcherFactoryClassName(pattern);
                          boolean _equals = _simpleName.equals(_matcherFactoryClassName);
                          _operator_and = BooleanExtensions.operator_and((it instanceof JvmType), _equals);
                        }
                        return Boolean.valueOf(_operator_and);
                      }
                    };
                  EObject _findFirst = IterableExtensions.<EObject>findFirst(_jvmElements, _function);
                  final JvmIdentifiableElement el = ((JvmIdentifiableElement) _findFirst);
                  String _qualifiedName = el.getQualifiedName();
                  exGen.contribAttribute(it, "factory", _qualifiedName);
                }
              }
            };
          exGen.contribElement(it, "matcher", _function);
        }
      };
    IPluginExtension _contribExtension = exGen.contribExtension(_fullyQualifiedName, "org.eclipse.viatra2.emf.incquery.runtime.patternmatcher", _function);
    ArrayList<IPluginExtension> _newArrayList = CollectionLiterals.<IPluginExtension>newArrayList(_contribExtension);
    return _newArrayList;
  }
}
