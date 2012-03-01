package org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel;

import com.google.inject.Inject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;

@SuppressWarnings("all")
public class JavadocInferrer {
  @Inject
  private EMFPatternLanguageJvmModelInferrerUtil _eMFPatternLanguageJvmModelInferrerUtil;
  
  @Inject
  private IQualifiedNameProvider _iQualifiedNameProvider;
  
  /**
   * Infers javadoc for Match class based on the input 'pattern'.
   */
  public CharSequence javadocMatchClass(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Pattern-specific match representation of the ");
    QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(pattern);
    _builder.append(_fullyQualifiedName, "");
    _builder.append(" pattern, ");
    _builder.newLineIfNotEmpty();
    _builder.append("to be used in conjunction with ");
    String _matcherClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matcherClassName(pattern);
    _builder.append(_matcherClassName, "");
    _builder.append(".");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("<p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.");
    _builder.newLine();
    _builder.append("Each instance is a (possibly partial) substitution of pattern parameters, ");
    _builder.newLine();
    _builder.append("usable to represent a match of the pattern in the result of a query, ");
    _builder.newLine();
    _builder.append("or to specify the bound (fixed) input parameters when issuing a query.");
    _builder.newLine();
    _builder.newLine();
    _builder.append("@see ");
    String _matcherClassName_1 = this._eMFPatternLanguageJvmModelInferrerUtil.matcherClassName(pattern);
    _builder.append(_matcherClassName_1, "");
    _builder.newLineIfNotEmpty();
    _builder.append("@see ");
    String _processorClassName = this._eMFPatternLanguageJvmModelInferrerUtil.processorClassName(pattern);
    _builder.append(_processorClassName, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence javadocMatcherClass(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Generated pattern matcher API of the ");
    QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(pattern);
    _builder.append(_fullyQualifiedName, "");
    _builder.append(" pattern, ");
    _builder.newLineIfNotEmpty();
    _builder.append("providing pattern-specific query methods.");
    _builder.newLine();
    _builder.newLine();
    String _serializeToJavadoc = this._eMFPatternLanguageJvmModelInferrerUtil.serializeToJavadoc(pattern);
    _builder.append(_serializeToJavadoc, "");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("@see ");
    String _matchClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
    _builder.append(_matchClassName, "");
    _builder.newLineIfNotEmpty();
    _builder.append("@see ");
    String _matcherFactoryClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matcherFactoryClassName(pattern);
    _builder.append(_matcherFactoryClassName, "");
    _builder.newLineIfNotEmpty();
    _builder.append("@see ");
    String _processorClassName = this._eMFPatternLanguageJvmModelInferrerUtil.processorClassName(pattern);
    _builder.append(_processorClassName, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence javadocMatcherFactoryClass(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("A pattern-specific matcher factory that can instantiate ");
    String _matcherClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matcherClassName(pattern);
    _builder.append(_matcherClassName, "");
    _builder.append(" in a type-safe way.");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("@see ");
    String _matcherClassName_1 = this._eMFPatternLanguageJvmModelInferrerUtil.matcherClassName(pattern);
    _builder.append(_matcherClassName_1, "");
    _builder.newLineIfNotEmpty();
    _builder.append("@see ");
    String _matchClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
    _builder.append(_matchClassName, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence javadocProcessorClass(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("A match processor tailored for the ");
    QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(pattern);
    _builder.append(_fullyQualifiedName, "");
    _builder.append(" pattern.");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("Clients should derive an (anonymous) class that implements the abstract process().");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence javadocMatcherConstructorNotifier(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet). ");
    _builder.newLine();
    _builder.append("If a pattern matcher is already constructed with the same root, only a lightweight reference is created.");
    _builder.newLine();
    _builder.append("The match set will be incrementally refreshed upon updates from the given EMF root and below.");
    _builder.newLine();
    _builder.append("<p>Note: if emfRoot is a resourceSet, the scope will include even those resources that are not part of the resourceSet but are referenced. ");
    _builder.newLine();
    _builder.append("This is mainly to support nsURI-based instance-level references to registered EPackages.");
    _builder.newLine();
    _builder.append("@param emfRoot the root of the EMF tree where the pattern matcher will operate. Recommended: Resource or ResourceSet.");
    _builder.newLine();
    _builder.append("@throws IncQueryRuntimeException if an error occurs during pattern matcher creation");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence javadocMatcherConstructorEngine(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Initializes the pattern matcher within an existing EMF-IncQuery engine. ");
    _builder.newLine();
    _builder.append("If the pattern matcher is already constructed in the engine, only a lightweight reference is created.");
    _builder.newLine();
    _builder.append("The match set will be incrementally refreshed upon updates.");
    _builder.newLine();
    _builder.append("@param engine the existing EMF-IncQuery engine in which this matcher will be created.");
    _builder.newLine();
    _builder.append("@throws IncQueryRuntimeException if an error occurs during pattern matcher creation");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence javadocGetAllMatchesMethod(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.");
    _builder.newLine();
    {
      EList<Variable> _parameters = pattern.getParameters();
      for(final Variable p : _parameters) {
        _builder.append("@param ");
        String _name = p.getName();
        _builder.append(_name, "");
        _builder.append(" the fixed value of pattern parameter ");
        String _name_1 = p.getName();
        _builder.append(_name_1, "");
        _builder.append(", or null if not bound.");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("@return matches represented as a ");
    String _matchClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
    _builder.append(_matchClassName, "");
    _builder.append(" object.");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence javadocGetOneArbitraryMatchMethod(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.");
    _builder.newLine();
    _builder.append("Neither determinism nor randomness of selection is guaranteed.");
    _builder.newLine();
    {
      EList<Variable> _parameters = pattern.getParameters();
      for(final Variable p : _parameters) {
        _builder.append("@param ");
        String _name = p.getName();
        _builder.append(_name, "");
        _builder.append(" the fixed value of pattern parameter ");
        String _name_1 = p.getName();
        _builder.append(_name_1, "");
        _builder.append(", or null if not bound.");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("@return a match represented as a ");
    String _matchClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
    _builder.append(_matchClassName, "");
    _builder.append(" object, or null if no match is found.");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence javadocHasMatchMethod(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,");
    _builder.newLine();
    _builder.append("under any possible substitution of the unspecified parameters (if any).");
    _builder.newLine();
    {
      EList<Variable> _parameters = pattern.getParameters();
      for(final Variable p : _parameters) {
        _builder.append("@param ");
        String _name = p.getName();
        _builder.append(_name, "");
        _builder.append(" the fixed value of pattern parameter ");
        String _name_1 = p.getName();
        _builder.append(_name_1, "");
        _builder.append(", or null if not bound.");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("@return true if the input is a valid (partial) match of the pattern.");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence javadocHasMatchMethodNoParameter(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Indicates whether the (parameterless) pattern matches or not. ");
    _builder.newLine();
    _builder.append("@return true if the pattern has a valid match.");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence javadocCountMatchesMethod(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.");
    _builder.newLine();
    {
      EList<Variable> _parameters = pattern.getParameters();
      for(final Variable p : _parameters) {
        _builder.append("@param ");
        String _name = p.getName();
        _builder.append(_name, "");
        _builder.append(" the fixed value of pattern parameter ");
        String _name_1 = p.getName();
        _builder.append(_name_1, "");
        _builder.append(", or null if not bound.");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("@return the number of pattern matches found.");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence javadocForEachMatchMethod(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.");
    _builder.newLine();
    {
      EList<Variable> _parameters = pattern.getParameters();
      for(final Variable p : _parameters) {
        _builder.append("@param ");
        String _name = p.getName();
        _builder.append(_name, "");
        _builder.append(" the fixed value of pattern parameter ");
        String _name_1 = p.getName();
        _builder.append(_name_1, "");
        _builder.append(", or null if not bound.");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("@param processor the action that will process each pattern match.");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence javadocForOneArbitraryMatchMethod(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.  ");
    _builder.newLine();
    _builder.append("Neither determinism nor randomness of selection is guaranteed.");
    _builder.newLine();
    {
      EList<Variable> _parameters = pattern.getParameters();
      for(final Variable p : _parameters) {
        _builder.append("@param ");
        String _name = p.getName();
        _builder.append(_name, "");
        _builder.append(" the fixed value of pattern parameter ");
        String _name_1 = p.getName();
        _builder.append(_name_1, "");
        _builder.append(", or null if not bound.");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("@param processor the action that will process the selected match. ");
    _builder.newLine();
    _builder.append("@return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence javadocProcessMethod(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Defines the action that is to be executed on each match.");
    _builder.newLine();
    {
      EList<Variable> _parameters = pattern.getParameters();
      for(final Variable p : _parameters) {
        _builder.append("@param ");
        String _name = p.getName();
        _builder.append(_name, "");
        _builder.append(" the value of pattern parameter ");
        String _name_1 = p.getName();
        _builder.append(_name_1, "");
        _builder.append(" in the currently processed match ");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }
}
