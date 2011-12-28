package org.eclipse.viatra2.patternlanguage.core.scoping;

import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.MapBasedScope;
import org.eclipse.xtext.xbase.XVariableDeclaration;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.scoping.LocalVariableScopeContext;
import org.eclipse.xtext.xbase.scoping.XbaseScopeProvider;

@SuppressWarnings("all")
public class PatternLanguageScopeProvider extends XbaseScopeProvider {
  public IScope createLocalVarScope(final IScope parent, final LocalVariableScopeContext scopeContext) {
      EObject _context = scopeContext.getContext();
      final EObject context = _context;
      boolean matched = false;
      if (!matched) {
        if (context instanceof PatternBody) {
          final PatternBody context_1 = (PatternBody) context;
          matched=true;
          {
            EList<Variable> _variables = context_1.getVariables();
            final Function1<Variable,IEObjectDescription> _function = new Function1<Variable,IEObjectDescription>() {
                public IEObjectDescription apply(final Variable e) {
                  IEObjectDescription _createIEObjectDescription = PatternLanguageScopeProvider.this.createIEObjectDescription(e);
                  return _createIEObjectDescription;
                }
              };
            List<IEObjectDescription> _map = ListExtensions.<Variable, IEObjectDescription>map(_variables, _function);
            final List<IEObjectDescription> descriptions = _map;
            IScope _createLocalVarScope = super.createLocalVarScope(parent, scopeContext);
            IScope _createScope = MapBasedScope.createScope(_createLocalVarScope, descriptions);
            return _createScope;
          }
        }
      }
      if (!matched) {
        if (context instanceof Pattern) {
          final Pattern context_2 = (Pattern) context;
          matched=true;
          {
            EList<Variable> _parameters = context_2.getParameters();
            final Function1<Variable,IEObjectDescription> _function_1 = new Function1<Variable,IEObjectDescription>() {
                public IEObjectDescription apply(final Variable e) {
                  IEObjectDescription _createIEObjectDescription = PatternLanguageScopeProvider.this.createIEObjectDescription(e);
                  return _createIEObjectDescription;
                }
              };
            List<IEObjectDescription> _map_1 = ListExtensions.<Variable, IEObjectDescription>map(_parameters, _function_1);
            final List<IEObjectDescription> descriptions_1 = _map_1;
            IScope _createScope_1 = MapBasedScope.createScope(IScope.NULLSCOPE, descriptions_1);
            return _createScope_1;
          }
        }
      }
      IScope _createLocalVarScope_1 = super.createLocalVarScope(parent, scopeContext);
      return _createLocalVarScope_1;
  }
  
  public IEObjectDescription createIEObjectDescription(final XVariableDeclaration parameter) {
    String _name = parameter.getName();
    QualifiedName _create = QualifiedName.create(_name);
    IEObjectDescription _create_1 = EObjectDescription.create(_create, parameter, null);
    return _create_1;
  }
  
  public IEObjectDescription createIEObjectDescription(final Variable parameter) {
    String _name = parameter.getName();
    QualifiedName _create = QualifiedName.create(_name);
    IEObjectDescription _create_1 = EObjectDescription.create(_create, parameter, null);
    return _create_1;
  }
}
