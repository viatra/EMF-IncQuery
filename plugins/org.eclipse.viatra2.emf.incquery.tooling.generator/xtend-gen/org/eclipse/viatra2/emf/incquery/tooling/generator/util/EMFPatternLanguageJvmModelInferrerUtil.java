package org.eclipse.viatra2.emf.incquery.tooling.generator.util;

import com.google.inject.Inject;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFJvmTypesBuilder;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.eclipse.xtext.xbase.typing.ITypeProvider;

/**
 * Utility class for the EMFPatternLanguageJvmModelInferrer.
 * 
 * @author Mark Czotter
 */
@SuppressWarnings("all")
public class EMFPatternLanguageJvmModelInferrerUtil {
  @Inject
  private EMFJvmTypesBuilder _eMFJvmTypesBuilder;
  
  @Inject
  private ISerializer serializer;
  
  private Logger logger = new Function0<Logger>() {
    public Logger apply() {
      Class<? extends Object> _class = EMFPatternLanguageJvmModelInferrerUtil.this.getClass();
      Logger _logger = Logger.getLogger(_class);
      return _logger;
    }
  }.apply();
  
  private String MULTILINE_COMMENT_PATTERN = "(/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/)";
  
  @Inject
  private IWorkspaceRoot workspaceRoot;
  
  @Inject
  private ITypeProvider typeProvider;
  
  public String bundleName(final Pattern pattern) {
    String _xblockexpression = null;
    {
      Resource _eResource = pattern.eResource();
      URI _uRI = _eResource.getURI();
      String _platformString = _uRI.toPlatformString(true);
      Path _path = new Path(_platformString);
      IFile _file = this.workspaceRoot.getFile(_path);
      IProject _project = _file.getProject();
      final IProject project = _project;
      String _name = project.getName();
      _xblockexpression = (_name);
    }
    return _xblockexpression;
  }
  
  /**
   * Returns the MatcherFactoryClass name based on the Pattern's name
   */
  public String matcherFactoryClassName(final Pattern pattern) {
    String _name = pattern.getName();
    String _firstUpper = StringExtensions.toFirstUpper(_name);
    String _operator_plus = StringExtensions.operator_plus(_firstUpper, "MatcherFactory");
    return _operator_plus;
  }
  
  /**
   * Returns the MatcherClass name based on the Pattern's name
   */
  public String matcherClassName(final Pattern pattern) {
    String _name = pattern.getName();
    String _firstUpper = StringExtensions.toFirstUpper(_name);
    String _operator_plus = StringExtensions.operator_plus(_firstUpper, "Matcher");
    return _operator_plus;
  }
  
  /**
   * Returns the MatchClass name based on the Pattern's name
   */
  public String matchClassName(final Pattern pattern) {
    String _name = pattern.getName();
    String _firstUpper = StringExtensions.toFirstUpper(_name);
    String _operator_plus = StringExtensions.operator_plus(_firstUpper, "Match");
    return _operator_plus;
  }
  
  /**
   * Returns the ProcessorClass name based on the Pattern's name
   */
  public String processorClassName(final Pattern pattern) {
    String _name = pattern.getName();
    String _firstUpper = StringExtensions.toFirstUpper(_name);
    String _operator_plus = StringExtensions.operator_plus(_firstUpper, "Processor");
    return _operator_plus;
  }
  
  /**
   * Returns the field name of Variable
   */
  public String fieldName(final Variable variable) {
    String _name = variable.getName();
    String _firstUpper = StringExtensions.toFirstUpper(_name);
    return _firstUpper;
  }
  
  /**
   * Calculates type for a Variable.
   * See the XBaseUsageCrossReferencer class, possible solution for local variable usage
   * TODO: improve type calculation
   * @return JvmTypeReference pointing the EClass that defines the Variable's type.
   */
  public JvmTypeReference calculateType(final Variable variable) {
    JvmTypeReference _typeForIdentifiable = this.typeProvider.getTypeForIdentifiable(variable);
    return _typeForIdentifiable;
  }
  
  /**
   * Serializes the EObject into Java String variable.
   */
  public CharSequence serializeToJava(final EObject eObject) {
      String _serialize = this.serialize(eObject);
      final String parseString = _serialize;
      boolean _isNullOrEmpty = StringExtensions.isNullOrEmpty(parseString);
      if (_isNullOrEmpty) {
        return "";
      }
      String[] _split = parseString.split("[\r\n]+");
      final String[] splits = _split;
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("String patternString = \"\"");
      final StringConcatenation stringRep = ((StringConcatenation) _builder);
      stringRep.newLine();
      for (final String s : splits) {
        {
          String _operator_plus = StringExtensions.operator_plus("+\" ", s);
          String _operator_plus_1 = StringExtensions.operator_plus(_operator_plus, " \"");
          stringRep.append(_operator_plus_1);
          stringRep.newLine();
        }
      }
      stringRep.append(";");
      return stringRep;
  }
  
  /**
   * Serializes the input for Javadoc
   */
  public String serializeToJavadoc(final Pattern pattern) {
      String _serialize = this.serialize(pattern);
      final String javadocString = _serialize;
      boolean _isNullOrEmpty = StringExtensions.isNullOrEmpty(javadocString);
      if (_isNullOrEmpty) {
        return "Serialization error, check Log";
      }
      return javadocString;
  }
  
  /**
   * Serializes EObject to a String representation. Escapes only the double qoutes.
   */
  private String serialize(final EObject eObject) {
      try {
        {
          ICompositeNode _node = NodeModelUtils.getNode(eObject);
          final ICompositeNode eObjectNode = _node;
          boolean _operator_notEquals = ObjectExtensions.operator_notEquals(eObjectNode, null);
          if (_operator_notEquals) {
            String _text = eObjectNode.getText();
            String _escape = this.escape(_text);
            return _escape;
          }
        }
      } catch (final Throwable _t) {
        if (_t instanceof Exception) {
          final Exception e = (Exception)_t;
          boolean _operator_notEquals_1 = ObjectExtensions.operator_notEquals(this.logger, null);
          if (_operator_notEquals_1) {
            EClass _eClass = eObject.eClass();
            String _name = _eClass.getName();
            String _operator_plus = StringExtensions.operator_plus("Error when serializing ", _name);
            this.logger.error(_operator_plus, e);
          }
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
      return null;
  }
  
  private String escape(final String escapable) {
      boolean _operator_equals = ObjectExtensions.operator_equals(escapable, null);
      if (_operator_equals) {
        return null;
      }
      String _replaceAll = escapable.replaceAll("\"", "\\\\\"");
      String escapedString = _replaceAll;
      String _replaceAll_1 = escapedString.replaceAll(this.MULTILINE_COMMENT_PATTERN, " ");
      escapedString = _replaceAll_1;
      return escapedString;
  }
  
  /**
   * Returns the packageName: PatternModel.packageName + Pattern.name, packageName is ignored, when nullOrEmpty.
   */
  public String getPackageName(final Pattern pattern) {
      EObject _eContainer = pattern.eContainer();
      String _packageName = ((PatternModel) _eContainer).getPackageName();
      String packageName = _packageName;
      boolean _isNullOrEmpty = StringExtensions.isNullOrEmpty(packageName);
      if (_isNullOrEmpty) {
        packageName = "";
      } else {
        String _operator_plus = StringExtensions.operator_plus(packageName, ".");
        packageName = _operator_plus;
      }
      String _name = pattern.getName();
      String _operator_plus_1 = StringExtensions.operator_plus(packageName, _name);
      return _operator_plus_1;
  }
  
  public String getPackagePath(final Pattern pattern) {
    String _packageName = this.getPackageName(pattern);
    String _replace = _packageName.replace(".", "/");
    return _replace;
  }
}
