package org.eclipse.viatra2.emf.incquery.tooling.generator.util;

import com.google.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcherFactory;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCompositionConstraint;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguageFactory;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.lib.BooleanExtensions;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;

/**
 * @author Mark Czotter
 */
@SuppressWarnings("all")
public class XmiOutputBuilder {
  private Logger logger = new Function0<Logger>() {
    public Logger apply() {
      Class<? extends Object> _class = XmiOutputBuilder.this.getClass();
      Logger _logger = Logger.getLogger(_class);
      return _logger;
    }
  }.apply();
  
  @Inject
  private IQualifiedNameProvider qualifiedNameProvider;
  
  /**
   * Builds one model file (XMI) from the input into the folder.
   */
  public void build(final ResourceSet resourceSet, final IProject project) {
    try {
      {
        IFolder _folder = project.getFolder(BaseGeneratedMatcherFactory.XMI_OUTPUT_FOLDER);
        final IFolder folder = _folder;
        IFile _file = folder.getFile(BaseGeneratedMatcherFactory.GLOBAL_EIQ_FILENAME);
        final IFile file = _file;
        boolean _exists = folder.exists();
        boolean _operator_not = BooleanExtensions.operator_not(_exists);
        if (_operator_not) {
          folder.create(IResource.DEPTH_INFINITE, false, null);
        }
        boolean _exists_1 = file.exists();
        if (_exists_1) {
          file.delete(true, null);
        }
        PatternModel _createPatternModel = EMFPatternLanguageFactory.eINSTANCE.createPatternModel();
        final PatternModel xmiModelRoot = _createPatternModel;
        IPath _fullPath = file.getFullPath();
        String _oSString = _fullPath.toOSString();
        URI _createPlatformResourceURI = URI.createPlatformResourceURI(_oSString, true);
        Resource _createResource = resourceSet.createResource(_createPlatformResourceURI);
        final Resource xmiResource = _createResource;
        HashSet<Object> _newHashSet = CollectionLiterals.<Object>newHashSet();
        final HashSet<Object> importDeclarations = _newHashSet;
        EList<Resource> _resources = resourceSet.getResources();
        final Function1<Resource,Iterable<PackageImport>> _function = new Function1<Resource,Iterable<PackageImport>>() {
            public Iterable<PackageImport> apply(final Resource r) {
              TreeIterator<EObject> _allContents = r.getAllContents();
              Iterable<EObject> _iterable = IteratorExtensions.<EObject>toIterable(_allContents);
              Iterable<PackageImport> _filter = IterableExtensions.<PackageImport>filter(_iterable, org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport.class);
              return _filter;
            }
          };
        List<Iterable<PackageImport>> _map = ListExtensions.<Resource, Iterable<PackageImport>>map(_resources, _function);
        Iterable<PackageImport> _flatten = IterableExtensions.<PackageImport>flatten(_map);
        final Iterable<PackageImport> packageImports = _flatten;
        boolean _isEmpty = IterableExtensions.isEmpty(packageImports);
        boolean _operator_not_1 = BooleanExtensions.operator_not(_isEmpty);
        if (_operator_not_1) {
          for (final PackageImport importDecl : packageImports) {
            EPackage _ePackage = importDecl.getEPackage();
            boolean _contains = importDeclarations.contains(_ePackage);
            boolean _operator_not_2 = BooleanExtensions.operator_not(_contains);
            if (_operator_not_2) {
              {
                EPackage _ePackage_1 = importDecl.getEPackage();
                importDeclarations.add(_ePackage_1);
                EList<PackageImport> _importPackages = xmiModelRoot.getImportPackages();
                _importPackages.add(importDecl);
              }
            }
          }
        }
        HashMap<Object,Object> _newHashMap = CollectionLiterals.<Object, Object>newHashMap();
        final HashMap<Object,Object> fqnToPatternMap = _newHashMap;
        EList<Resource> _resources_1 = resourceSet.getResources();
        final Function1<Resource,Iterable<Pattern>> _function_1 = new Function1<Resource,Iterable<Pattern>>() {
            public Iterable<Pattern> apply(final Resource r) {
              TreeIterator<EObject> _allContents = r.getAllContents();
              Iterable<EObject> _iterable = IteratorExtensions.<EObject>toIterable(_allContents);
              Iterable<Pattern> _filter = IterableExtensions.<Pattern>filter(_iterable, org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern.class);
              return _filter;
            }
          };
        List<Iterable<Pattern>> _map_1 = ListExtensions.<Resource, Iterable<Pattern>>map(_resources_1, _function_1);
        Iterable<Pattern> _flatten_1 = IterableExtensions.<Pattern>flatten(_map_1);
        for (final Pattern pattern : _flatten_1) {
          {
            Pattern _copy = EcoreUtil.<Pattern>copy(pattern);
            final Pattern p = _copy;
            QualifiedName _fullyQualifiedName = this.qualifiedNameProvider.getFullyQualifiedName(pattern);
            String _string = _fullyQualifiedName.toString();
            final String fqn = _string;
            p.setName(fqn);
            Object _get = fqnToPatternMap.get(fqn);
            boolean _operator_notEquals = ObjectExtensions.operator_notEquals(_get, null);
            if (_operator_notEquals) {
              String _operator_plus = StringExtensions.operator_plus("Pattern already set in the Map: ", fqn);
              this.logger.error(_operator_plus);
            } else {
              {
                fqnToPatternMap.put(fqn, p);
                EList<Pattern> _patterns = xmiModelRoot.getPatterns();
                _patterns.add(p);
              }
            }
          }
        }
        TreeIterator<EObject> _eAllContents = xmiModelRoot.eAllContents();
        Iterable<EObject> _iterable = IteratorExtensions.<EObject>toIterable(_eAllContents);
        Iterable<PatternCompositionConstraint> _filter = IterableExtensions.<PatternCompositionConstraint>filter(_iterable, org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCompositionConstraint.class);
        for (final PatternCompositionConstraint constraint : _filter) {
          {
            Pattern _patternRef = constraint.getPatternRef();
            QualifiedName _fullyQualifiedName_1 = this.qualifiedNameProvider.getFullyQualifiedName(_patternRef);
            String _string_1 = _fullyQualifiedName_1.toString();
            final String fqn_1 = _string_1;
            Object _get_1 = fqnToPatternMap.get(fqn_1);
            final Object p_1 = _get_1;
            boolean _operator_equals = ObjectExtensions.operator_equals(p_1, null);
            if (_operator_equals) {
              String _operator_plus_1 = StringExtensions.operator_plus("Pattern not found: ", fqn_1);
              this.logger.error(_operator_plus_1);
            } else {
              constraint.setPatternRef(((Pattern) p_1));
            }
          }
        }
        EList<EObject> _contents = xmiResource.getContents();
        _contents.add(xmiModelRoot);
        xmiResource.save(null);
      }
    } catch (final Throwable _t) {
      if (_t instanceof Exception) {
        final Exception e = (Exception)_t;
        this.logger.error("Exception during XMI build!", e);
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
}
