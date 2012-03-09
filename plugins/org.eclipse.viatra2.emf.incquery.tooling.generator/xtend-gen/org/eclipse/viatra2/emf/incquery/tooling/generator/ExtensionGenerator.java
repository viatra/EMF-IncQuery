package org.eclipse.viatra2.emf.incquery.tooling.generator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.pde.core.plugin.IExtensionsModelFactory;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginObject;
import org.eclipse.pde.internal.core.plugin.WorkspacePluginModel;
import org.eclipse.pde.internal.core.project.PDEProject;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class ExtensionGenerator {
  private IExtensionsModelFactory factory;
  
  public IExtensionsModelFactory setProject(final IProject project) {
    IExtensionsModelFactory _xblockexpression = null;
    {
      IFile _pluginXml = PDEProject.getPluginXml(project);
      final IFile plugin = _pluginXml;
      WorkspacePluginModel _workspacePluginModel = new WorkspacePluginModel(plugin, true);
      final WorkspacePluginModel fModel = _workspacePluginModel;
      IExtensionsModelFactory _factory = fModel.getFactory();
      IExtensionsModelFactory _factory_1 = this.factory = _factory;
      _xblockexpression = (_factory_1);
    }
    return _xblockexpression;
  }
  
  public IPluginExtension contribExtension(final String id, final String point, final Procedure1<? super IPluginExtension> initializer) {
    try {
      IPluginExtension _xblockexpression = null;
      {
        IPluginExtension _createExtension = this.factory.createExtension();
        final IPluginExtension ex = _createExtension;
        ex.setId(id);
        ex.setPoint(point);
        IPluginExtension _init = this.<IPluginExtension>init(ex, initializer);
        _xblockexpression = (_init);
      }
      return _xblockexpression;
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public IPluginElement contribElement(final IPluginObject parent, final String name, final Procedure1<? super IPluginElement> initializer) {
    try {
      IPluginElement _xblockexpression = null;
      {
        IPluginElement _createElement = this.factory.createElement(parent);
        final IPluginElement el = _createElement;
        el.setName(name);
        if ((parent instanceof IPluginExtension)) {
          ((IPluginExtension) parent).add(el);
        }
        IPluginElement _init = this.<IPluginElement>init(el, initializer);
        _xblockexpression = (_init);
      }
      return _xblockexpression;
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void contribAttribute(final IPluginElement element, final String name, final String value) {
    try {
      element.setAttribute(name, value);
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private <T extends Object> T init(final T obj, final Procedure1<? super T> init) {
      init.apply(obj);
      return obj;
  }
}
