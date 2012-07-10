package org.eclipse.viatra2.emf.incquery.tooling.generator.derived;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenFeature;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.Document;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.viatra2.emf.incquery.base.logging.EMFIncQueryRuntimeLogger;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHandler.FeatureKind;
import org.eclipse.viatra2.emf.incquery.tooling.generator.ExtensionGenerator;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment;
import org.eclipse.viatra2.emf.incquery.tooling.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.AnnotationParameter;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.BoolValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.StringValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Type;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ValueReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableValue;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.StringExtensions;

import com.google.common.base.Objects;
import com.google.inject.Inject;

@SuppressWarnings("all")
public class DerivedFeatureGenerator implements IGenerationFragment {
  @Inject
  private IEiqGenmodelProvider provider;
  
  @Inject
  private DerivedFeatureSourceCodeUtil _derivedFeatureSourceCodeUtil;
  
  /**
   * usage: @DerivedFeature(
   * 			feature="featureName", (default: patten name)
   * 			source="Src" (default: first parameter),
   * 			target="Trg" (default: second parameter),
   * 			kind="single/many/counter/sum/iteration" (default: feature.isMany?many:single)
   * 			keepCache="true/false" (default: true)
   * 			disabled="true/false" (default: false)
   * 		  )
   */
  private static String annotationLiteral = "DerivedFeature";
  
  private static String DERIVED_EXTENSION_POINT = "org.eclipse.viatra2.emf.incquery.base.wellbehaving.derived.features";
  
  private static String IMPORT_QUALIFIER = "org.eclipse.viatra2.emf.incquery.runtime.derived";
  
  private static String FEATUREKIND_IMPORT = "FeatureKind";
  
  private static String HELPER_IMPORT = "IncqueryFeatureHelper";
  
  private static String HANDLER_NAME = "IncqueryFeatureHandler";
  
  private static String HANDLER_FIELD_SUFFIX = "Handler";
  
  private static String DERIVED_EXTENSION_PREFIX = "extension.derived.";
  
  private static Map<String,FeatureKind> kinds = new Function0<Map<String,FeatureKind>>() {
    public Map<String,FeatureKind> apply() {
      Pair<String,FeatureKind> _of = Pair.<String, FeatureKind>of("single", FeatureKind.SINGLE_REFERENCE);
      Pair<String,FeatureKind> _of_1 = Pair.<String, FeatureKind>of("many", FeatureKind.MANY_REFERENCE);
      Pair<String,FeatureKind> _of_2 = Pair.<String, FeatureKind>of("counter", FeatureKind.COUNTER);
      Pair<String,FeatureKind> _of_3 = Pair.<String, FeatureKind>of("sum", FeatureKind.SUM);
      Pair<String,FeatureKind> _of_4 = Pair.<String, FeatureKind>of("iteration", FeatureKind.ITERATION);
      HashMap<String,FeatureKind> _newHashMap = CollectionLiterals.<String, FeatureKind>newHashMap(_of, _of_1, _of_2, _of_3, _of_4);
      return _newHashMap;
    }
  }.apply();
  
  /**
   * override cleanUp(Pattern pattern, IFileSystemAccess fsa) {
   * throw new UnsupportedOperationException("Auto-generated function stub")
   * }
   * 
   * override extensionContribution(Pattern pattern, ExtensionGenerator exGen) {
   * throw new UnsupportedOperationException("Auto-generated function stub")
   * }
   * 
   * override generateFiles(Pattern pattern, IFileSystemAccess fsa) {
   * throw new UnsupportedOperationException("Auto-generated function stub")
   * }
   * 
   * override getAdditionalBinIncludes() {
   * throw new UnsupportedOperationException("Auto-generated function stub")
   * }
   * 
   * override getProjectDependencies() {
   * throw new UnsupportedOperationException("Auto-generated function stub")
   * }
   * 
   * override getProjectPostfix() {
   * throw new UnsupportedOperationException("Auto-generated function stub")
   * }
   * 
   * override getRemovableExtensions() {
   * throw new UnsupportedOperationException("Auto-generated function stub")
   * }
   * 
   * override removeExtension(Pattern pattern) {
   * throw new UnsupportedOperationException("Auto-generated function stub")
   * }
   * 
   * }
   */
  public void generateFiles(final Pattern pattern, final IFileSystemAccess fsa) {
    this.processJavaFiles(pattern, true);
  }
  
  public void cleanUp(final Pattern pattern, final IFileSystemAccess fsa) {
    this.processJavaFiles(pattern, false);
  }
  
  private void processJavaFiles(final Pattern pattern, final boolean generate) {
    try {
      boolean _hasAnnotationLiteral = this.hasAnnotationLiteral(pattern, DerivedFeatureGenerator.annotationLiteral);
      if (_hasAnnotationLiteral) {
        try {
          final HashMap<String,Object> parameters = this.processDerivedFeatureAnnotation(pattern);
          Object _get = parameters.get("package");
          final GenPackage pckg = ((GenPackage) _get);
          Object _get_1 = parameters.get("source");
          final EClass source = ((EClass) _get_1);
          Object _get_2 = parameters.get("feature");
          final EStructuralFeature feature = ((EStructuralFeature) _get_2);
          final GenClass genSourceClass = this.findGenClassForSource(pckg, source, pattern);
          final GenFeature genFeature = this.findGenFeatureForFeature(genSourceClass, feature, pattern);
          final IJavaProject javaProject = this.findJavaProject(pckg);
          final ICompilationUnit compunit = this.findJavaFile(pckg, genSourceClass, javaProject);
          final String docSource = compunit.getSource();
          final ASTParser parser = ASTParser.newParser(AST.JLS3);
          Document _document = new Document(docSource);
          final Document document = _document;
          parser.setSource(compunit);
          ASTNode _createAST = parser.createAST(null);
          final CompilationUnit astNode = ((CompilationUnit) _createAST);
          final AST ast = astNode.getAST();
          final ASTRewrite rewrite = ASTRewrite.create(ast);
          List _types = astNode.types();
          final List<AbstractTypeDeclaration> types = ((List<AbstractTypeDeclaration>) _types);
          final Function1<AbstractTypeDeclaration,Boolean> _function = new Function1<AbstractTypeDeclaration,Boolean>() {
              public Boolean apply(final AbstractTypeDeclaration it) {
                boolean _xblockexpression = false;
                {
                  final AbstractTypeDeclaration type = ((AbstractTypeDeclaration) it);
                  SimpleName _name = type.getName();
                  String _identifier = _name.getIdentifier();
                  String _className = genSourceClass.getClassName();
                  boolean _equals = Objects.equal(_identifier, _className);
                  _xblockexpression = (_equals);
                }
                return Boolean.valueOf(_xblockexpression);
              }
            };
          AbstractTypeDeclaration _findFirst = IterableExtensions.<AbstractTypeDeclaration>findFirst(types, _function);
          final TypeDeclaration type = ((TypeDeclaration) _findFirst);
          final ListRewrite bodyDeclListRewrite = rewrite.getListRewrite(type, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
          if (generate) {
            this.ensureImports(ast, rewrite, astNode, type);
            String _name = genFeature.getName();
            this.ensureHandlerField(ast, bodyDeclListRewrite, type, _name);
            this.ensureGetterMethod(ast, document, type, rewrite, bodyDeclListRewrite, genSourceClass, genFeature, pattern, parameters);
          } else {
            String _name_1 = genFeature.getName();
            this.removeHandlerField(ast, bodyDeclListRewrite, type, _name_1);
            this.restoreGetterMethod(ast, document, compunit, type, rewrite, bodyDeclListRewrite, genSourceClass, genFeature);
          }
          Map _options = javaProject.getOptions(true);
          final TextEdit edits = rewrite.rewriteAST(document, _options);
          edits.apply(document);
          final String newSource = document.get();
          IBuffer _buffer = compunit.getBuffer();
          _buffer.setContents(newSource);
          IBuffer _buffer_1 = compunit.getBuffer();
          _buffer_1.save(null, false);
        } catch (final Throwable _t) {
          if (_t instanceof IllegalArgumentException) {
            final IllegalArgumentException e = (IllegalArgumentException)_t;
            if (generate) {
              EMFIncQueryRuntimeLogger _defaultLogger = IncQueryEngine.getDefaultLogger();
              String _message = e.getMessage();
              _defaultLogger.logError(_message, e);
            }
          } else {
            throw Exceptions.sneakyThrow(_t);
          }
        }
      }
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private GenClass findGenClassForSource(final GenPackage pckg, final EClass source, final Pattern pattern) {
    EList<GenClass> _genClasses = pckg.getGenClasses();
    final Function1<GenClass,Boolean> _function = new Function1<GenClass,Boolean>() {
        public Boolean apply(final GenClass it) {
          EClass _ecoreClass = it.getEcoreClass();
          boolean _equals = Objects.equal(_ecoreClass, source);
          return Boolean.valueOf(_equals);
        }
      };
    final GenClass genSourceClass = IterableExtensions.<GenClass>findFirst(_genClasses, _function);
    boolean _equals = Objects.equal(genSourceClass, null);
    if (_equals) {
      String _fullyQualifiedName = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
      String _plus = ("Derived feature pattern " + _fullyQualifiedName);
      String _plus_1 = (_plus + ": Source EClass ");
      String _name = source.getName();
      String _plus_2 = (_plus_1 + _name);
      String _plus_3 = (_plus_2 + " not found in GenPackage ");
      String _plus_4 = (_plus_3 + pckg);
      String _plus_5 = (_plus_4 + "!");
      IllegalArgumentException _illegalArgumentException = new IllegalArgumentException(_plus_5);
      throw _illegalArgumentException;
    }
    return genSourceClass;
  }
  
  private GenFeature findGenFeatureForFeature(final GenClass genSourceClass, final EStructuralFeature feature, final Pattern pattern) {
    EList<GenFeature> _genFeatures = genSourceClass.getGenFeatures();
    final Function1<GenFeature,Boolean> _function = new Function1<GenFeature,Boolean>() {
        public Boolean apply(final GenFeature it) {
          EStructuralFeature _ecoreFeature = it.getEcoreFeature();
          boolean _equals = Objects.equal(_ecoreFeature, feature);
          return Boolean.valueOf(_equals);
        }
      };
    final GenFeature genFeature = IterableExtensions.<GenFeature>findFirst(_genFeatures, _function);
    boolean _equals = Objects.equal(genFeature, null);
    if (_equals) {
      String _fullyQualifiedName = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
      String _plus = ("Derived feature pattern " + _fullyQualifiedName);
      String _plus_1 = (_plus + ": Feature ");
      String _name = feature.getName();
      String _plus_2 = (_plus_1 + _name);
      String _plus_3 = (_plus_2 + " not found in GenClass ");
      String _name_1 = genSourceClass.getName();
      String _plus_4 = (_plus_3 + _name_1);
      String _plus_5 = (_plus_4 + "!");
      IllegalArgumentException _illegalArgumentException = new IllegalArgumentException(_plus_5);
      throw _illegalArgumentException;
    }
    return genFeature;
  }
  
  private IJavaProject findJavaProject(final GenPackage pckg) {
    IJavaProject _xblockexpression = null;
    {
      GenModel _genModel = pckg.getGenModel();
      final String projectDir = _genModel.getModelProjectDirectory();
      IJavaProject _locateProject = ProjectLocator.locateProject(projectDir);
      _xblockexpression = (_locateProject);
    }
    return _xblockexpression;
  }
  
  private ICompilationUnit findJavaFile(final GenPackage pckg, final GenClass genSourceClass, final IJavaProject javaProject) {
    try {
      ICompilationUnit _xblockexpression = null;
      {
        EPackage _ecorePackage = pckg.getEcorePackage();
        final String prefix = _ecorePackage.getName();
        final String suffix = pckg.getClassPackageSuffix();
        final String base = pckg.getBasePackage();
        String packageNameTmp = "";
        boolean _and = false;
        boolean _notEquals = (!Objects.equal(base, null));
        if (!_notEquals) {
          _and = false;
        } else {
          boolean _notEquals_1 = (!Objects.equal(base, ""));
          _and = (_notEquals && _notEquals_1);
        }
        if (_and) {
          packageNameTmp = base;
        }
        boolean _and_1 = false;
        boolean _notEquals_2 = (!Objects.equal(prefix, null));
        if (!_notEquals_2) {
          _and_1 = false;
        } else {
          boolean _notEquals_3 = (!Objects.equal(prefix, ""));
          _and_1 = (_notEquals_2 && _notEquals_3);
        }
        if (_and_1) {
          boolean _notEquals_4 = (!Objects.equal(packageNameTmp, ""));
          if (_notEquals_4) {
            String _plus = (packageNameTmp + ".");
            String _plus_1 = (_plus + prefix);
            packageNameTmp = _plus_1;
          } else {
            packageNameTmp = prefix;
          }
        }
        boolean _and_2 = false;
        boolean _notEquals_5 = (!Objects.equal(suffix, null));
        if (!_notEquals_5) {
          _and_2 = false;
        } else {
          boolean _notEquals_6 = (!Objects.equal(suffix, ""));
          _and_2 = (_notEquals_5 && _notEquals_6);
        }
        if (_and_2) {
          boolean _notEquals_7 = (!Objects.equal(packageNameTmp, ""));
          if (_notEquals_7) {
            String _plus_2 = (packageNameTmp + ".");
            String _plus_3 = (_plus_2 + suffix);
            packageNameTmp = _plus_3;
          } else {
            packageNameTmp = suffix;
          }
        }
        final String packageName = packageNameTmp;
        IPackageFragment[] _packageFragments = javaProject.getPackageFragments();
        final Function1<IPackageFragment,Boolean> _function = new Function1<IPackageFragment,Boolean>() {
            public Boolean apply(final IPackageFragment it) {
              String _elementName = it.getElementName();
              boolean _equals = Objects.equal(_elementName, packageName);
              return Boolean.valueOf(_equals);
            }
          };
        final IPackageFragment implPackage = IterableExtensions.<IPackageFragment>findFirst(((Iterable<IPackageFragment>)Conversions.doWrapArray(_packageFragments)), _function);
        ICompilationUnit[] _compilationUnits = implPackage.getCompilationUnits();
        final Function1<ICompilationUnit,Boolean> _function_1 = new Function1<ICompilationUnit,Boolean>() {
            public Boolean apply(final ICompilationUnit it) {
              String _elementName = it.getElementName();
              String _className = genSourceClass.getClassName();
              String _plus = (_className + ".java");
              boolean _equals = Objects.equal(_elementName, _plus);
              return Boolean.valueOf(_equals);
            }
          };
        ICompilationUnit _findFirst = IterableExtensions.<ICompilationUnit>findFirst(((Iterable<ICompilationUnit>)Conversions.doWrapArray(_compilationUnits)), _function_1);
        _xblockexpression = (_findFirst);
      }
      return _xblockexpression;
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private void ensureImports(final AST ast, final ASTRewrite rewrite, final CompilationUnit astNode, final TypeDeclaration type) {
    final ListRewrite importListRewrite = rewrite.getListRewrite(astNode, CompilationUnit.IMPORTS_PROPERTY);
    List _imports = astNode.imports();
    final List<ImportDeclaration> imports = ((List<ImportDeclaration>) _imports);
    final Function1<ImportDeclaration,Boolean> _function = new Function1<ImportDeclaration,Boolean>() {
        public Boolean apply(final ImportDeclaration it) {
          Name _name = it.getName();
          String _fullyQualifiedName = _name.getFullyQualifiedName();
          String _plus = (DerivedFeatureGenerator.IMPORT_QUALIFIER + ".");
          String _plus_1 = (_plus + DerivedFeatureGenerator.HANDLER_NAME);
          boolean _equals = Objects.equal(_fullyQualifiedName, _plus_1);
          return Boolean.valueOf(_equals);
        }
      };
    final ImportDeclaration handlerImport = IterableExtensions.<ImportDeclaration>findFirst(imports, _function);
    boolean _equals = Objects.equal(handlerImport, null);
    if (_equals) {
      final ImportDeclaration handlerImportNew = ast.newImportDeclaration();
      Name _newName = ast.newName(DerivedFeatureGenerator.IMPORT_QUALIFIER);
      SimpleName _newSimpleName = ast.newSimpleName(DerivedFeatureGenerator.HANDLER_NAME);
      QualifiedName _newQualifiedName = ast.newQualifiedName(_newName, _newSimpleName);
      handlerImportNew.setName(_newQualifiedName);
      importListRewrite.insertLast(handlerImportNew, null);
    }
    final Function1<ImportDeclaration,Boolean> _function_1 = new Function1<ImportDeclaration,Boolean>() {
        public Boolean apply(final ImportDeclaration it) {
          Name _name = it.getName();
          String _fullyQualifiedName = _name.getFullyQualifiedName();
          String _plus = (DerivedFeatureGenerator.IMPORT_QUALIFIER + ".");
          String _plus_1 = (_plus + DerivedFeatureGenerator.HANDLER_NAME);
          String _plus_2 = (_plus_1 + ".");
          String _plus_3 = (_plus_2 + DerivedFeatureGenerator.FEATUREKIND_IMPORT);
          boolean _equals = Objects.equal(_fullyQualifiedName, _plus_3);
          return Boolean.valueOf(_equals);
        }
      };
    final ImportDeclaration kindImport = IterableExtensions.<ImportDeclaration>findFirst(imports, _function_1);
    boolean _equals_1 = Objects.equal(kindImport, null);
    if (_equals_1) {
      final ImportDeclaration kindImportNew = ast.newImportDeclaration();
      String _plus = (DerivedFeatureGenerator.IMPORT_QUALIFIER + ".");
      String _plus_1 = (_plus + DerivedFeatureGenerator.HANDLER_NAME);
      Name _newName_1 = ast.newName(_plus_1);
      SimpleName _newSimpleName_1 = ast.newSimpleName(DerivedFeatureGenerator.FEATUREKIND_IMPORT);
      QualifiedName _newQualifiedName_1 = ast.newQualifiedName(_newName_1, _newSimpleName_1);
      kindImportNew.setName(_newQualifiedName_1);
      importListRewrite.insertLast(kindImportNew, null);
    }
    final Function1<ImportDeclaration,Boolean> _function_2 = new Function1<ImportDeclaration,Boolean>() {
        public Boolean apply(final ImportDeclaration it) {
          Name _name = it.getName();
          String _fullyQualifiedName = _name.getFullyQualifiedName();
          String _plus = (DerivedFeatureGenerator.IMPORT_QUALIFIER + ".");
          String _plus_1 = (_plus + DerivedFeatureGenerator.HELPER_IMPORT);
          boolean _equals = Objects.equal(_fullyQualifiedName, _plus_1);
          return Boolean.valueOf(_equals);
        }
      };
    final ImportDeclaration helperImport = IterableExtensions.<ImportDeclaration>findFirst(imports, _function_2);
    boolean _equals_2 = Objects.equal(helperImport, null);
    if (_equals_2) {
      final ImportDeclaration helperImportNew = ast.newImportDeclaration();
      Name _newName_2 = ast.newName(DerivedFeatureGenerator.IMPORT_QUALIFIER);
      SimpleName _newSimpleName_2 = ast.newSimpleName(DerivedFeatureGenerator.HELPER_IMPORT);
      QualifiedName _newQualifiedName_2 = ast.newQualifiedName(_newName_2, _newSimpleName_2);
      helperImportNew.setName(_newQualifiedName_2);
      importListRewrite.insertLast(helperImportNew, null);
    }
  }
  
  private void ensureHandlerField(final AST ast, final ListRewrite bodyDeclListRewrite, final TypeDeclaration type, final String featureName) {
    FieldDeclaration[] _fields = type.getFields();
    final Function1<FieldDeclaration,Boolean> _function = new Function1<FieldDeclaration,Boolean>() {
        public Boolean apply(final FieldDeclaration it) {
          boolean _xblockexpression = false;
          {
            List _fragments = it.fragments();
            final List<VariableDeclarationFragment> fragments = ((List<VariableDeclarationFragment>) _fragments);
            final Function1<VariableDeclarationFragment,Boolean> _function = new Function1<VariableDeclarationFragment,Boolean>() {
                public Boolean apply(final VariableDeclarationFragment it) {
                  SimpleName _name = it.getName();
                  String _identifier = _name.getIdentifier();
                  String _plus = (featureName + DerivedFeatureGenerator.HANDLER_FIELD_SUFFIX);
                  boolean _equals = Objects.equal(_identifier, _plus);
                  return Boolean.valueOf(_equals);
                }
              };
            boolean _exists = IterableExtensions.<VariableDeclarationFragment>exists(fragments, _function);
            _xblockexpression = (_exists);
          }
          return Boolean.valueOf(_xblockexpression);
        }
      };
    final FieldDeclaration handler = IterableExtensions.<FieldDeclaration>findFirst(((Iterable<FieldDeclaration>)Conversions.doWrapArray(_fields)), _function);
    boolean _equals = Objects.equal(handler, null);
    if (_equals) {
      final VariableDeclarationFragment handlerFragment = ast.newVariableDeclarationFragment();
      String _plus = (featureName + DerivedFeatureGenerator.HANDLER_FIELD_SUFFIX);
      SimpleName _newSimpleName = ast.newSimpleName(_plus);
      handlerFragment.setName(_newSimpleName);
      final FieldDeclaration handlerField = ast.newFieldDeclaration(handlerFragment);
      SimpleName _newSimpleName_1 = ast.newSimpleName(DerivedFeatureGenerator.HANDLER_NAME);
      SimpleType _newSimpleType = ast.newSimpleType(_newSimpleName_1);
      handlerField.setType(_newSimpleType);
      List _modifiers = handlerField.modifiers();
      Modifier _newModifier = ast.newModifier(ModifierKeyword.PRIVATE_KEYWORD);
      _modifiers.add(_newModifier);
      final TagElement handlerTag = ast.newTagElement();
      final TextElement tagText = ast.newTextElement();
      String _plus_1 = ("EMF-IncQuery handler for derived feature " + featureName);
      tagText.setText(_plus_1);
      List _fragments = handlerTag.fragments();
      _fragments.add(tagText);
      final Javadoc javaDoc = ast.newJavadoc();
      List _tags = javaDoc.tags();
      _tags.add(handlerTag);
      handlerField.setJavadoc(javaDoc);
      bodyDeclListRewrite.insertLast(handlerField, null);
    }
  }
  
  private void removeHandlerField(final AST ast, final ListRewrite bodyDeclListRewrite, final TypeDeclaration type, final String featureName) {
    FieldDeclaration[] _fields = type.getFields();
    final Function1<FieldDeclaration,Boolean> _function = new Function1<FieldDeclaration,Boolean>() {
        public Boolean apply(final FieldDeclaration it) {
          boolean _xblockexpression = false;
          {
            List _fragments = it.fragments();
            final List<VariableDeclarationFragment> fragments = ((List<VariableDeclarationFragment>) _fragments);
            final Function1<VariableDeclarationFragment,Boolean> _function = new Function1<VariableDeclarationFragment,Boolean>() {
                public Boolean apply(final VariableDeclarationFragment it) {
                  SimpleName _name = it.getName();
                  String _identifier = _name.getIdentifier();
                  String _plus = (featureName + DerivedFeatureGenerator.HANDLER_FIELD_SUFFIX);
                  boolean _equals = Objects.equal(_identifier, _plus);
                  return Boolean.valueOf(_equals);
                }
              };
            boolean _exists = IterableExtensions.<VariableDeclarationFragment>exists(fragments, _function);
            _xblockexpression = (_exists);
          }
          return Boolean.valueOf(_xblockexpression);
        }
      };
    final FieldDeclaration handler = IterableExtensions.<FieldDeclaration>findFirst(((Iterable<FieldDeclaration>)Conversions.doWrapArray(_fields)), _function);
    boolean _notEquals = (!Objects.equal(handler, null));
    if (_notEquals) {
      bodyDeclListRewrite.remove(handler, null);
    }
  }
  
  private void ensureGetterMethod(final AST ast, final Document document, final TypeDeclaration type, final ASTRewrite rewrite, final ListRewrite bodyDeclListRewrite, final GenClass sourceClass, final GenFeature genFeature, final Pattern pattern, final Map<String,Object> parameters) {
    Object _get = parameters.get("sourceVar");
    final String sourceName = ((String) _get);
    Object _get_1 = parameters.get("targetVar");
    final String targetName = ((String) _get_1);
    Object _get_2 = parameters.get("kind");
    final FeatureKind kind = ((FeatureKind) _get_2);
    Object _get_3 = parameters.get("keepCache");
    final Boolean keepCache = ((Boolean) _get_3);
    final MethodDeclaration getMethod = this.findFeatureMethod(type, genFeature, "");
    final MethodDeclaration getGenMethod = this.findFeatureMethod(type, genFeature, "Gen");
    CharSequence methodSource = this._derivedFeatureSourceCodeUtil.methodBody(sourceClass, genFeature, pattern, sourceName, targetName, kind, (keepCache).booleanValue());
    String _string = methodSource.toString();
    MethodDeclaration dummyMethod = this.processDummyComputationUnit(_string);
    boolean _notEquals = (!Objects.equal(getMethod, null));
    if (_notEquals) {
      final Javadoc javadoc = getMethod.getJavadoc();
      boolean generatedBody = false;
      boolean _notEquals_1 = (!Objects.equal(javadoc, null));
      if (_notEquals_1) {
        List _tags = javadoc.tags();
        final List<TagElement> tags = ((List<TagElement>) _tags);
        final Function1<TagElement,Boolean> _function = new Function1<TagElement,Boolean>() {
            public Boolean apply(final TagElement it) {
              String _tagName = ((TagElement) it).getTagName();
              boolean _equals = Objects.equal(_tagName, "@generated");
              return Boolean.valueOf(_equals);
            }
          };
        final TagElement generatedTag = IterableExtensions.<TagElement>findFirst(tags, _function);
        final Function1<TagElement,Boolean> _function_1 = new Function1<TagElement,Boolean>() {
            public Boolean apply(final TagElement it) {
              String _tagName = ((TagElement) it).getTagName();
              boolean _equals = Objects.equal(_tagName, "@derived");
              return Boolean.valueOf(_equals);
            }
          };
        final TagElement derivedTag = IterableExtensions.<TagElement>findFirst(tags, _function_1);
        boolean _and = false;
        boolean _and_1 = false;
        boolean _equals = Objects.equal(derivedTag, null);
        if (!_equals) {
          _and_1 = false;
        } else {
          boolean _notEquals_2 = (!Objects.equal(generatedTag, null));
          _and_1 = (_equals && _notEquals_2);
        }
        if (!_and_1) {
          _and = false;
        } else {
          List _fragments = generatedTag.fragments();
          int _size = _fragments.size();
          boolean _equals_1 = (_size == 0);
          _and = (_and_1 && _equals_1);
        }
        if (_and) {
          generatedBody = true;
          SimpleName _name = getMethod.getName();
          final String methodName = _name.getIdentifier();
          ASTNode _copySubtree = ASTNode.copySubtree(ast, getMethod);
          final MethodDeclaration method = ((MethodDeclaration) _copySubtree);
          SimpleName _name_1 = method.getName();
          SimpleName _newSimpleName = ast.newSimpleName(methodName);
          rewrite.replace(_name_1, _newSimpleName, null);
          Block _body = method.getBody();
          Block _body_1 = dummyMethod.getBody();
          rewrite.replace(_body, _body_1, null);
          Javadoc _javadoc = method.getJavadoc();
          List _tags_1 = _javadoc.tags();
          final List<TagElement> methodtags = ((List<TagElement>) _tags_1);
          final Function1<TagElement,Boolean> _function_2 = new Function1<TagElement,Boolean>() {
              public Boolean apply(final TagElement it) {
                String _tagName = ((TagElement) it).getTagName();
                boolean _equals = Objects.equal(_tagName, "@generated");
                return Boolean.valueOf(_equals);
              }
            };
          final TagElement oldTag = IterableExtensions.<TagElement>findFirst(methodtags, _function_2);
          rewrite.set(oldTag, TagElement.TAG_NAME_PROPERTY, "@derived", null);
          final ListRewrite tagsRewrite = rewrite.getListRewrite(oldTag, TagElement.FRAGMENTS_PROPERTY);
          final TextElement tagText = ast.newTextElement();
          String _name_2 = genFeature.getName();
          String _plus = ("getter created by EMF-InccQuery for derived feature " + _name_2);
          tagText.setText(_plus);
          tagsRewrite.insertLast(tagText, null);
          bodyDeclListRewrite.insertLast(method, null);
          boolean _equals_2 = Objects.equal(getGenMethod, null);
          if (_equals_2) {
            SimpleName _name_3 = getMethod.getName();
            SimpleName _name_4 = getMethod.getName();
            String _identifier = _name_4.getIdentifier();
            String _plus_1 = (_identifier + "Gen");
            SimpleName _newSimpleName_1 = ast.newSimpleName(_plus_1);
            rewrite.replace(_name_3, _newSimpleName_1, null);
          } else {
            SimpleName _name_5 = getMethod.getName();
            SimpleName _name_6 = getMethod.getName();
            String _identifier_1 = _name_6.getIdentifier();
            String _plus_2 = ("_" + _identifier_1);
            SimpleName _newSimpleName_2 = ast.newSimpleName(_plus_2);
            rewrite.replace(_name_5, _newSimpleName_2, null);
          }
        }
        boolean _notEquals_3 = (!Objects.equal(derivedTag, null));
        if (_notEquals_3) {
          generatedBody = true;
          Block _body_2 = getMethod.getBody();
          Block _body_3 = dummyMethod.getBody();
          this.replaceMethodBody(ast, rewrite, _body_2, _body_3, javadoc, document, false, null, null, null);
        }
      }
      boolean _not = (!generatedBody);
      if (_not) {
        Block _body_4 = getMethod.getBody();
        Block _body_5 = dummyMethod.getBody();
        String _name_7 = genFeature.getName();
        String _plus_3 = ("getter created by EMF-InccQuery for derived feature " + _name_7);
        this.replaceMethodBody(ast, rewrite, _body_4, _body_5, javadoc, document, true, "@derived", _plus_3, null);
      }
    }
  }
  
  private void restoreGetterMethod(final AST ast, final Document document, final ICompilationUnit compunit, final TypeDeclaration type, final ASTRewrite rewrite, final ListRewrite bodyDeclListRewrite, final GenClass sourceClass, final GenFeature genFeature) {
    final MethodDeclaration getMethod = this.findFeatureMethod(type, genFeature, "");
    final MethodDeclaration getGenMethod = this.findFeatureMethod(type, genFeature, "Gen");
    boolean _notEquals = (!Objects.equal(getGenMethod, null));
    if (_notEquals) {
      boolean _notEquals_1 = (!Objects.equal(getMethod, null));
      if (_notEquals_1) {
        SimpleName _name = getGenMethod.getName();
        SimpleName _name_1 = getMethod.getName();
        String _identifier = _name_1.getIdentifier();
        SimpleName _newSimpleName = ast.newSimpleName(_identifier);
        rewrite.replace(_name, _newSimpleName, null);
        bodyDeclListRewrite.remove(getMethod, null);
      }
    } else {
      EStructuralFeature _ecoreFeature = genFeature.getEcoreFeature();
      boolean _isMany = _ecoreFeature.isMany();
      CharSequence methodSource = this._derivedFeatureSourceCodeUtil.defaultMethod(_isMany);
      String _string = methodSource.toString();
      MethodDeclaration dummyMethod = this.processDummyComputationUnit(_string);
      boolean _notEquals_2 = (!Objects.equal(getMethod, null));
      if (_notEquals_2) {
        final Javadoc javadoc = getMethod.getJavadoc();
        boolean _notEquals_3 = (!Objects.equal(javadoc, null));
        if (_notEquals_3) {
          List _tags = javadoc.tags();
          final List<TagElement> tags = ((List<TagElement>) _tags);
          final Function1<TagElement,Boolean> _function = new Function1<TagElement,Boolean>() {
              public Boolean apply(final TagElement it) {
                String _tagName = ((TagElement) it).getTagName();
                boolean _equals = Objects.equal(_tagName, "@derived");
                return Boolean.valueOf(_equals);
              }
            };
          final TagElement derivedTag = IterableExtensions.<TagElement>findFirst(tags, _function);
          final Function1<TagElement,Boolean> _function_1 = new Function1<TagElement,Boolean>() {
              public Boolean apply(final TagElement it) {
                String _tagName = ((TagElement) it).getTagName();
                boolean _equals = Objects.equal(_tagName, "@original");
                return Boolean.valueOf(_equals);
              }
            };
          final TagElement originalTag = IterableExtensions.<TagElement>findFirst(tags, _function_1);
          final Function1<TagElement,Boolean> _function_2 = new Function1<TagElement,Boolean>() {
              public Boolean apply(final TagElement it) {
                String _tagName = ((TagElement) it).getTagName();
                boolean _equals = Objects.equal(_tagName, "@generated");
                return Boolean.valueOf(_equals);
              }
            };
          final TagElement generatedTag = IterableExtensions.<TagElement>findFirst(tags, _function_2);
          boolean _and = false;
          boolean _notEquals_4 = (!Objects.equal(generatedTag, null));
          if (!_notEquals_4) {
            _and = false;
          } else {
            List _fragments = generatedTag.fragments();
            int _size = _fragments.size();
            boolean _equals = (_size == 0);
            _and = (_notEquals_4 && _equals);
          }
          if (_and) {
            return;
          }
          boolean _and_1 = false;
          boolean _notEquals_5 = (!Objects.equal(derivedTag, null));
          if (!_notEquals_5) {
            _and_1 = false;
          } else {
            boolean _notEquals_6 = (!Objects.equal(originalTag, null));
            _and_1 = (_notEquals_5 && _notEquals_6);
          }
          if (_and_1) {
            List _fragments_1 = originalTag.fragments();
            int _size_1 = _fragments_1.size();
            boolean _notEquals_7 = (_size_1 != 0);
            if (_notEquals_7) {
              final ListRewrite tagsRewrite = rewrite.getListRewrite(javadoc, Javadoc.TAGS_PROPERTY);
              tagsRewrite.remove(derivedTag, null);
              final List tagFragments = originalTag.fragments();
              StringBuilder _stringBuilder = new StringBuilder();
              final StringBuilder oldBody = _stringBuilder;
              for (final Object o : tagFragments) {
                if ((o instanceof TextElement)) {
                  String _text = ((TextElement) o).getText();
                  oldBody.append(_text);
                }
              }
              final MethodDeclaration oldMethod = this.prepareOriginalMethod(compunit, type, getMethod, oldBody);
              Block _body = getMethod.getBody();
              Block _body_1 = oldMethod.getBody();
              rewrite.replace(_body, _body_1, null);
              tagsRewrite.remove(originalTag, null);
              return;
            }
          }
          boolean _notEquals_8 = (!Objects.equal(generatedTag, null));
          if (_notEquals_8) {
            return;
          }
        }
        Block _body_2 = getMethod.getBody();
        Block _body_3 = dummyMethod.getBody();
        this.replaceMethodBody(ast, rewrite, _body_2, _body_3, javadoc, document, false, "@generated", "", "@derived");
      }
    }
  }
  
  private MethodDeclaration findFeatureMethod(final TypeDeclaration type, final GenFeature genFeature, final String suffix) {
    MethodDeclaration[] _methods = type.getMethods();
    final Function1<MethodDeclaration,Boolean> _function = new Function1<MethodDeclaration,Boolean>() {
        public Boolean apply(final MethodDeclaration it) {
          boolean _xifexpression = false;
          boolean _isBasicGet = genFeature.isBasicGet();
          if (_isBasicGet) {
            SimpleName _name = it.getName();
            String _identifier = _name.getIdentifier();
            String _getAccessor = genFeature.getGetAccessor();
            String _firstUpper = StringExtensions.toFirstUpper(_getAccessor);
            String _plus = ("basic" + _firstUpper);
            String _plus_1 = (_plus + suffix);
            boolean _equals = Objects.equal(_identifier, _plus_1);
            _xifexpression = _equals;
          } else {
            SimpleName _name_1 = it.getName();
            String _identifier_1 = _name_1.getIdentifier();
            String _getAccessor_1 = genFeature.getGetAccessor();
            String _plus_2 = (_getAccessor_1 + suffix);
            boolean _equals_1 = Objects.equal(_identifier_1, _plus_2);
            _xifexpression = _equals_1;
          }
          return Boolean.valueOf(_xifexpression);
        }
      };
    MethodDeclaration _findFirst = IterableExtensions.<MethodDeclaration>findFirst(((Iterable<MethodDeclaration>)Conversions.doWrapArray(_methods)), _function);
    return _findFirst;
  }
  
  private MethodDeclaration processDummyComputationUnit(final String dummySource) {
    MethodDeclaration _xblockexpression = null;
    {
      final ASTParser methodBodyParser = ASTParser.newParser(AST.JLS3);
      char[] _charArray = dummySource.toCharArray();
      methodBodyParser.setSource(_charArray);
      final Hashtable options = JavaCore.getOptions();
      JavaCore.setComplianceOptions(JavaCore.VERSION_1_5, options);
      methodBodyParser.setCompilerOptions(options);
      final ASTNode dummyAST = methodBodyParser.createAST(null);
      final CompilationUnit dummyCU = ((CompilationUnit) dummyAST);
      List _types = dummyCU.types();
      Object _get = _types.get(0);
      final TypeDeclaration dummyType = ((TypeDeclaration) _get);
      MethodDeclaration[] _methods = dummyType.getMethods();
      MethodDeclaration _get_1 = ((List<MethodDeclaration>)Conversions.doWrapArray(_methods)).get(0);
      _xblockexpression = (((MethodDeclaration) _get_1));
    }
    return _xblockexpression;
  }
  
  private MethodDeclaration prepareOriginalMethod(final ICompilationUnit cu, final TypeDeclaration type, final MethodDeclaration method, final StringBuilder originalBody) {
    try {
      MethodDeclaration _xblockexpression = null;
      {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("public class Dummy{public void DummyMethod()");
        originalBody.insert(0, _builder.toString());
        IImportDeclaration[] _imports = cu.getImports();
        final Procedure1<IImportDeclaration> _function = new Procedure1<IImportDeclaration>() {
            public void apply(final IImportDeclaration it) {
              try {
                String _source = it.getSource();
                String _plus = (_source + "\n");
                originalBody.insert(0, _plus);
              } catch (Exception _e) {
                throw Exceptions.sneakyThrow(_e);
              }
            }
          };
        IterableExtensions.<IImportDeclaration>forEach(((Iterable<IImportDeclaration>)Conversions.doWrapArray(_imports)), _function);
        originalBody.append("}");
        final String dummyCU = originalBody.toString();
        MethodDeclaration _processDummyComputationUnit = this.processDummyComputationUnit(dummyCU);
        _xblockexpression = (_processDummyComputationUnit);
      }
      return _xblockexpression;
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private void replaceMethodBody(final AST ast, final ASTRewrite rewrite, final Block oldBody, final Block newBody, final Javadoc javadoc, final Document document, final boolean keepOld, final String newTagName, final String newTagText, final String removeTagName) {
    List _tags = javadoc.tags();
    final List<TagElement> tags = ((List<TagElement>) _tags);
    final ListRewrite tagsRewrite = rewrite.getListRewrite(javadoc, Javadoc.TAGS_PROPERTY);
    final Function1<TagElement,Boolean> _function = new Function1<TagElement,Boolean>() {
        public Boolean apply(final TagElement it) {
          String _tagName = ((TagElement) it).getTagName();
          boolean _equals = Objects.equal(_tagName, "@original");
          return Boolean.valueOf(_equals);
        }
      };
    final TagElement originalTag = IterableExtensions.<TagElement>findFirst(tags, _function);
    final Function1<TagElement,Boolean> _function_1 = new Function1<TagElement,Boolean>() {
        public Boolean apply(final TagElement it) {
          String _tagName = ((TagElement) it).getTagName();
          boolean _equals = Objects.equal(_tagName, newTagName);
          return Boolean.valueOf(_equals);
        }
      };
    final TagElement newTag = IterableExtensions.<TagElement>findFirst(tags, _function_1);
    boolean _notEquals = (!Objects.equal(removeTagName, null));
    if (_notEquals) {
      final Function1<TagElement,Boolean> _function_2 = new Function1<TagElement,Boolean>() {
          public Boolean apply(final TagElement it) {
            String _tagName = ((TagElement) it).getTagName();
            boolean _equals = Objects.equal(_tagName, removeTagName);
            return Boolean.valueOf(_equals);
          }
        };
      final TagElement removeTag = IterableExtensions.<TagElement>findFirst(tags, _function_2);
      boolean _notEquals_1 = (!Objects.equal(removeTag, null));
      if (_notEquals_1) {
        tagsRewrite.remove(removeTag, null);
      }
    }
    boolean _equals = Objects.equal(originalTag, null);
    if (_equals) {
      if (keepOld) {
        final TagElement tag = ast.newTagElement();
        tag.setTagName("@original");
        final TextElement text = ast.newTextElement();
        String _string = oldBody.toString();
        String _defaultLineDelimiter = document.getDefaultLineDelimiter();
        String _replace = _string.replace("\n", _defaultLineDelimiter);
        text.setText(_replace);
        List _fragments = tag.fragments();
        _fragments.add(text);
        tagsRewrite.insertLast(tag, null);
      }
    } else {
      boolean _not = (!keepOld);
      if (_not) {
        tagsRewrite.remove(originalTag, null);
      }
    }
    boolean _equals_1 = Objects.equal(newTag, null);
    if (_equals_1) {
      final TagElement newTagToInsert = ast.newTagElement();
      newTagToInsert.setTagName(newTagName);
      final TextElement tagText = ast.newTextElement();
      tagText.setText(newTagText);
      List _fragments_1 = newTagToInsert.fragments();
      _fragments_1.add(tagText);
      tagsRewrite.insertLast(newTagToInsert, null);
    }
    rewrite.replace(oldBody, newBody, null);
  }
  
  public Iterable<IPluginExtension> extensionContribution(final Pattern pattern, final ExtensionGenerator exGen) {
    boolean _hasAnnotationLiteral = this.hasAnnotationLiteral(pattern, DerivedFeatureGenerator.annotationLiteral);
    if (_hasAnnotationLiteral) {
      try {
        final HashMap<String,Object> parameters = this.processDerivedFeatureAnnotation(pattern);
        String _derivedContributionId = this.derivedContributionId(pattern);
        final Procedure1<IPluginExtension> _function = new Procedure1<IPluginExtension>() {
            public void apply(final IPluginExtension it) {
              final Procedure1<IPluginElement> _function = new Procedure1<IPluginElement>() {
                  public void apply(final IPluginElement it) {
                    Object _get = parameters.get("package");
                    String _nSURI = ((GenPackage) _get).getNSURI();
                    exGen.contribAttribute(it, "package-nsUri", _nSURI);
                    Object _get_1 = parameters.get("source");
                    String _name = ((EClass) _get_1).getName();
                    exGen.contribAttribute(it, "classifier-name", _name);
                    Object _get_2 = parameters.get("feature");
                    String _name_1 = ((EStructuralFeature) _get_2).getName();
                    exGen.contribAttribute(it, "feature-name", _name_1);
                  }
                };
              exGen.contribElement(it, "wellbehaving-derived-feature", _function);
            }
          };
        IPluginExtension _contribExtension = exGen.contribExtension(_derivedContributionId, DerivedFeatureGenerator.DERIVED_EXTENSION_POINT, _function);
        final ArrayList<IPluginExtension> wellbehaving = CollectionLiterals.<IPluginExtension>newArrayList(_contribExtension);
        return wellbehaving;
      } catch (final Throwable _t) {
        if (_t instanceof IllegalArgumentException) {
          final IllegalArgumentException e = (IllegalArgumentException)_t;
          EMFIncQueryRuntimeLogger _defaultLogger = IncQueryEngine.getDefaultLogger();
          String _message = e.getMessage();
          _defaultLogger.logError(_message);
          return CollectionLiterals.<IPluginExtension>newArrayList();
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
    } else {
      return CollectionLiterals.<IPluginExtension>newArrayList();
    }
  }
  
  public Iterable<Pair<String,String>> removeExtension(final Pattern pattern) {
    String _derivedContributionId = this.derivedContributionId(pattern);
    Pair<String,String> _of = Pair.<String, String>of(_derivedContributionId, DerivedFeatureGenerator.DERIVED_EXTENSION_POINT);
    ArrayList<Pair<String,String>> _newArrayList = CollectionLiterals.<Pair<String,String>>newArrayList(_of);
    return _newArrayList;
  }
  
  public Collection<Pair<String,String>> getRemovableExtensions() {
    Pair<String,String> _of = Pair.<String, String>of(DerivedFeatureGenerator.DERIVED_EXTENSION_PREFIX, DerivedFeatureGenerator.DERIVED_EXTENSION_POINT);
    ArrayList<Pair<String,String>> _newArrayList = CollectionLiterals.<Pair<String,String>>newArrayList(_of);
    return _newArrayList;
  }
  
  private boolean hasAnnotationLiteral(final Pattern pattern, final String literal) {
    EList<Annotation> _annotations = pattern.getAnnotations();
    for (final Annotation a : _annotations) {
      String _name = a.getName();
      boolean _matches = _name.matches(literal);
      if (_matches) {
        return true;
      }
    }
    return false;
  }
  
  private String derivedContributionId(final Pattern pattern) {
    String _fullyQualifiedName = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
    String _plus = (DerivedFeatureGenerator.DERIVED_EXTENSION_PREFIX + _fullyQualifiedName);
    return _plus;
  }
  
  private HashMap<String,Object> processDerivedFeatureAnnotation(final Pattern pattern) {
    HashMap<String,Object> _hashMap = new HashMap<String,Object>();
    final HashMap<String,Object> parameters = _hashMap;
    String sourceTmp = "";
    String targetTmp = "";
    String featureTmp = "";
    String kindTmp = "";
    boolean keepCacheTmp = true;
    EList<Variable> _parameters = pattern.getParameters();
    int _size = _parameters.size();
    boolean _lessThan = (_size < 2);
    if (_lessThan) {
      String _fullyQualifiedName = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
      String _plus = ("Derived feature pattern " + _fullyQualifiedName);
      String _plus_1 = (_plus + " has less than 2 parameters!");
      IllegalArgumentException _illegalArgumentException = new IllegalArgumentException(_plus_1);
      throw _illegalArgumentException;
    }
    EList<Annotation> _annotations = pattern.getAnnotations();
    for (final Annotation a : _annotations) {
      String _name = a.getName();
      boolean _matches = _name.matches(DerivedFeatureGenerator.annotationLiteral);
      if (_matches) {
        EList<AnnotationParameter> _parameters_1 = a.getParameters();
        for (final AnnotationParameter ap : _parameters_1) {
          String _name_1 = ap.getName();
          boolean _matches_1 = _name_1.matches("source");
          if (_matches_1) {
            ValueReference _value = ap.getValue();
            VariableReference _value_1 = ((VariableValue) _value).getValue();
            String _var = _value_1.getVar();
            sourceTmp = _var;
          } else {
            String _name_2 = ap.getName();
            boolean _matches_2 = _name_2.matches("target");
            if (_matches_2) {
              ValueReference _value_2 = ap.getValue();
              VariableReference _value_3 = ((VariableValue) _value_2).getValue();
              String _var_1 = _value_3.getVar();
              targetTmp = _var_1;
            } else {
              String _name_3 = ap.getName();
              boolean _matches_3 = _name_3.matches("feature");
              if (_matches_3) {
                ValueReference _value_4 = ap.getValue();
                String _value_5 = ((StringValue) _value_4).getValue();
                featureTmp = _value_5;
              } else {
                String _name_4 = ap.getName();
                boolean _matches_4 = _name_4.matches("kind");
                if (_matches_4) {
                  ValueReference _value_6 = ap.getValue();
                  String _value_7 = ((StringValue) _value_6).getValue();
                  kindTmp = _value_7;
                } else {
                  String _name_5 = ap.getName();
                  boolean _matches_5 = _name_5.matches("keepCache");
                  if (_matches_5) {
                    ValueReference _value_8 = ap.getValue();
                    boolean _isValue = ((BoolValue) _value_8).isValue();
                    keepCacheTmp = _isValue;
                  }
                }
              }
            }
          }
        }
      }
    }
    boolean _equals = Objects.equal(featureTmp, "");
    if (_equals) {
      String _name_6 = pattern.getName();
      featureTmp = _name_6;
    }
    boolean _equals_1 = Objects.equal(sourceTmp, "");
    if (_equals_1) {
      EList<Variable> _parameters_2 = pattern.getParameters();
      Variable _get = _parameters_2.get(0);
      String _name_7 = _get.getName();
      sourceTmp = _name_7;
    }
    Map<String,Integer> _parameterPositionsByName = CorePatternLanguageHelper.getParameterPositionsByName(pattern);
    Set<String> _keySet = _parameterPositionsByName.keySet();
    boolean _contains = _keySet.contains(sourceTmp);
    boolean _not = (!_contains);
    if (_not) {
      String _fullyQualifiedName_1 = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
      String _plus_2 = ("Derived feature pattern " + _fullyQualifiedName_1);
      String _plus_3 = (_plus_2 + ": No parameter for source ");
      String _plus_4 = (_plus_3 + sourceTmp);
      String _plus_5 = (_plus_4 + " !");
      IllegalArgumentException _illegalArgumentException_1 = new IllegalArgumentException(_plus_5);
      throw _illegalArgumentException_1;
    }
    EList<Variable> _parameters_3 = pattern.getParameters();
    Map<String,Integer> _parameterPositionsByName_1 = CorePatternLanguageHelper.getParameterPositionsByName(pattern);
    Integer _get_1 = _parameterPositionsByName_1.get(sourceTmp);
    Variable _get_2 = _parameters_3.get((_get_1).intValue());
    final Type sourceType = _get_2.getType();
    boolean _or = false;
    boolean _not_1 = (!(sourceType instanceof ClassType));
    if (_not_1) {
      _or = true;
    } else {
      EClassifier _classname = ((ClassType) sourceType).getClassname();
      boolean _not_2 = (!(_classname instanceof EClass));
      _or = (_not_1 || _not_2);
    }
    if (_or) {
      String _fullyQualifiedName_2 = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
      String _plus_6 = ("Derived feature pattern " + _fullyQualifiedName_2);
      String _plus_7 = (_plus_6 + ": Source ");
      String _plus_8 = (_plus_7 + sourceTmp);
      String _plus_9 = (_plus_8 + " is not EClass!");
      IllegalArgumentException _illegalArgumentException_2 = new IllegalArgumentException(_plus_9);
      throw _illegalArgumentException_2;
    }
    EClassifier _classname_1 = ((ClassType) sourceType).getClassname();
    EClass source = ((EClass) _classname_1);
    parameters.put("sourceVar", sourceTmp);
    parameters.put("source", source);
    EPackage _ePackage = source.getEPackage();
    final GenPackage pckg = this.provider.findGenPackage(pattern, _ePackage);
    boolean _equals_2 = Objects.equal(pckg, null);
    if (_equals_2) {
      String _fullyQualifiedName_3 = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
      String _plus_10 = ("Derived feature pattern " + _fullyQualifiedName_3);
      String _plus_11 = (_plus_10 + ": GenPackage not found!");
      IllegalArgumentException _illegalArgumentException_3 = new IllegalArgumentException(_plus_11);
      throw _illegalArgumentException_3;
    }
    parameters.put("package", pckg);
    final String featureString = featureTmp;
    EList<EStructuralFeature> _eAllStructuralFeatures = source.getEAllStructuralFeatures();
    final Function1<EStructuralFeature,Boolean> _function = new Function1<EStructuralFeature,Boolean>() {
        public Boolean apply(final EStructuralFeature it) {
          String _name = it.getName();
          boolean _equals = Objects.equal(_name, featureString);
          return Boolean.valueOf(_equals);
        }
      };
    final Iterable<EStructuralFeature> features = IterableExtensions.<EStructuralFeature>filter(_eAllStructuralFeatures, _function);
    int _size_1 = IterableExtensions.size(features);
    boolean _notEquals = (_size_1 != 1);
    if (_notEquals) {
      String _fullyQualifiedName_4 = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
      String _plus_12 = ("Derived feature pattern " + _fullyQualifiedName_4);
      String _plus_13 = (_plus_12 + ": Feature ");
      String _plus_14 = (_plus_13 + featureTmp);
      String _plus_15 = (_plus_14 + " not found in class ");
      String _name_8 = source.getName();
      String _plus_16 = (_plus_15 + _name_8);
      String _plus_17 = (_plus_16 + "!");
      IllegalArgumentException _illegalArgumentException_4 = new IllegalArgumentException(_plus_17);
      throw _illegalArgumentException_4;
    }
    Iterator<EStructuralFeature> _iterator = features.iterator();
    final EStructuralFeature feature = _iterator.next();
    boolean _and = false;
    boolean _and_1 = false;
    boolean _and_2 = false;
    boolean _isDerived = feature.isDerived();
    if (!_isDerived) {
      _and_2 = false;
    } else {
      boolean _isTransient = feature.isTransient();
      _and_2 = (_isDerived && _isTransient);
    }
    if (!_and_2) {
      _and_1 = false;
    } else {
      boolean _isChangeable = feature.isChangeable();
      boolean _not_3 = (!_isChangeable);
      _and_1 = (_and_2 && _not_3);
    }
    if (!_and_1) {
      _and = false;
    } else {
      boolean _isVolatile = feature.isVolatile();
      _and = (_and_1 && _isVolatile);
    }
    boolean _not_4 = (!_and);
    if (_not_4) {
      String _fullyQualifiedName_5 = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
      String _plus_18 = ("Derived feature pattern " + _fullyQualifiedName_5);
      String _plus_19 = (_plus_18 + ": Feature ");
      String _plus_20 = (_plus_19 + featureTmp);
      String _plus_21 = (_plus_20 + " must be set derived, transient, volatile, non-changeable!");
      IllegalArgumentException _illegalArgumentException_5 = new IllegalArgumentException(_plus_21);
      throw _illegalArgumentException_5;
    }
    parameters.put("feature", feature);
    boolean _equals_3 = Objects.equal(kindTmp, "");
    if (_equals_3) {
      boolean _isMany = feature.isMany();
      if (_isMany) {
        kindTmp = "many";
      } else {
        kindTmp = "single";
      }
    }
    boolean _isEmpty = DerivedFeatureGenerator.kinds.isEmpty();
    if (_isEmpty) {
      DerivedFeatureGenerator.kinds.put("single", FeatureKind.SINGLE_REFERENCE);
      DerivedFeatureGenerator.kinds.put("many", FeatureKind.MANY_REFERENCE);
      DerivedFeatureGenerator.kinds.put("counter", FeatureKind.COUNTER);
      DerivedFeatureGenerator.kinds.put("sum", FeatureKind.SUM);
      DerivedFeatureGenerator.kinds.put("iteration", FeatureKind.ITERATION);
    }
    Set<String> _keySet_1 = DerivedFeatureGenerator.kinds.keySet();
    boolean _contains_1 = _keySet_1.contains(kindTmp);
    boolean _not_5 = (!_contains_1);
    if (_not_5) {
      String _fullyQualifiedName_6 = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
      String _plus_22 = ("Derived feature pattern " + _fullyQualifiedName_6);
      String _plus_23 = (_plus_22 + ": Kind not set, or not in ");
      Set<String> _keySet_2 = DerivedFeatureGenerator.kinds.keySet();
      String _plus_24 = (_plus_23 + _keySet_2);
      String _plus_25 = (_plus_24 + "!");
      IllegalArgumentException _illegalArgumentException_6 = new IllegalArgumentException(_plus_25);
      throw _illegalArgumentException_6;
    }
    final FeatureKind kind = DerivedFeatureGenerator.kinds.get(kindTmp);
    parameters.put("kind", kind);
    boolean _equals_4 = Objects.equal(targetTmp, "");
    if (_equals_4) {
      EList<Variable> _parameters_4 = pattern.getParameters();
      Variable _get_3 = _parameters_4.get(1);
      String _name_9 = _get_3.getName();
      targetTmp = _name_9;
    } else {
      Map<String,Integer> _parameterPositionsByName_2 = CorePatternLanguageHelper.getParameterPositionsByName(pattern);
      Set<String> _keySet_3 = _parameterPositionsByName_2.keySet();
      boolean _contains_2 = _keySet_3.contains(targetTmp);
      boolean _not_6 = (!_contains_2);
      if (_not_6) {
        String _fullyQualifiedName_7 = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
        String _plus_26 = ("Derived feature pattern " + _fullyQualifiedName_7);
        String _plus_27 = (_plus_26 + ": Target ");
        String _plus_28 = (_plus_27 + targetTmp);
        String _plus_29 = (_plus_28 + " not set or no such parameter!");
        IllegalArgumentException _illegalArgumentException_7 = new IllegalArgumentException(_plus_29);
        throw _illegalArgumentException_7;
      }
    }
    parameters.put("targetVar", targetTmp);
    parameters.put("keepCache", Boolean.valueOf(keepCacheTmp));
    return parameters;
  }
  
  public IPath[] getAdditionalBinIncludes() {
    return ((IPath[])Conversions.unwrapArray(CollectionLiterals.<IPath>newArrayList(), IPath.class));
  }
  
  public String[] getProjectDependencies() {
    return ((String[])Conversions.unwrapArray(CollectionLiterals.<String>newArrayList(), String.class));
  }
  
  public String getProjectPostfix() {
    return null;
  }
}
