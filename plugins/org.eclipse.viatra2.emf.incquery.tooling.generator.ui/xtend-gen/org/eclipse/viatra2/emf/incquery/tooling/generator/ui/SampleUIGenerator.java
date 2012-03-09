package org.eclipse.viatra2.emf.incquery.tooling.generator.ui;

import com.google.inject.Inject;
import java.util.ArrayList;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.viatra2.emf.incquery.tooling.generator.ExtensionGenerator;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment;
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class SampleUIGenerator implements IGenerationFragment {
  @Inject
  private EMFPatternLanguageJvmModelInferrerUtil _eMFPatternLanguageJvmModelInferrerUtil;
  
  public void generateFiles(final Pattern pattern, final IFileSystemAccess fsa) {
    String _packagePath = this._eMFPatternLanguageJvmModelInferrerUtil.getPackagePath(pattern);
    String _operator_plus = StringExtensions.operator_plus(_packagePath, "/handlers/");
    String _name = pattern.getName();
    String _operator_plus_1 = StringExtensions.operator_plus(_operator_plus, _name);
    String _operator_plus_2 = StringExtensions.operator_plus(_operator_plus_1, "Handler.java");
    CharSequence _patternHandler = this.patternHandler(pattern);
    fsa.generateFile(_operator_plus_2, _patternHandler);
  }
  
  public String[] getProjectDependencies() {
    ArrayList<String> _newArrayList = CollectionLiterals.<String>newArrayList("org.eclipse.core.runtime", "org.eclipse.ui", "org.eclipse.emf.ecore", "org.eclipse.pde.core", "org.eclipse.core.resources", "org.eclipse.viatra2.emf.incquery.runtime");
    return ((String[])Conversions.unwrapArray(_newArrayList, String.class));
  }
  
  public String getProjectPostfix() {
    return "ui";
  }
  
  public Iterable<IPluginExtension> extensionContribution(final Pattern pattern, final ExtensionGenerator exGen) {
    String _fullyQualifiedName = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
    String _operator_plus = StringExtensions.operator_plus(_fullyQualifiedName, "Command");
    final Procedure1<IPluginExtension> _function = new Procedure1<IPluginExtension>() {
        public void apply(final IPluginExtension it) {
          final Procedure1<IPluginElement> _function = new Procedure1<IPluginElement>() {
              public void apply(final IPluginElement it) {
                {
                  String _fullyQualifiedName = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
                  String _operator_plus = StringExtensions.operator_plus(_fullyQualifiedName, "CommandId");
                  exGen.contribAttribute(it, "commandId", _operator_plus);
                  exGen.contribAttribute(it, "style", "push");
                }
              }
            };
          exGen.contribElement(it, "command", _function);
        }
      };
    IPluginExtension _contribExtension = exGen.contribExtension(_operator_plus, "org.eclipse.ui.commands", _function);
    ArrayList<IPluginExtension> _newArrayList = CollectionLiterals.<IPluginExtension>newArrayList(_contribExtension);
    return _newArrayList;
  }
  
  public CharSequence patternHandler(final Pattern pattern) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package ");
    String _packageName = this._eMFPatternLanguageJvmModelInferrerUtil.getPackageName(pattern);
    _builder.append(_packageName, "");
    _builder.append(".handlers;");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("import java.util.Collection;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import org.eclipse.core.commands.AbstractHandler;");
    _builder.newLine();
    _builder.append("import org.eclipse.core.commands.ExecutionEvent;");
    _builder.newLine();
    _builder.append("import org.eclipse.core.commands.ExecutionException;");
    _builder.newLine();
    _builder.append("import org.eclipse.emf.ecore.resource.Resource;");
    _builder.newLine();
    _builder.append("import org.eclipse.emf.ecore.resource.ResourceSet;");
    _builder.newLine();
    _builder.append("import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;");
    _builder.newLine();
    _builder.append("import org.eclipse.core.resources.IFile;");
    _builder.newLine();
    _builder.append("import org.eclipse.emf.common.notify.Notifier;");
    _builder.newLine();
    _builder.append("import org.eclipse.emf.common.util.URI;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import org.eclipse.jface.dialogs.MessageDialog;");
    _builder.newLine();
    _builder.append("import org.eclipse.jface.viewers.IStructuredSelection;");
    _builder.newLine();
    _builder.append("import org.eclipse.swt.widgets.Display;");
    _builder.newLine();
    _builder.append("import org.eclipse.ui.handlers.HandlerUtil;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import ");
    String _packageName_1 = this._eMFPatternLanguageJvmModelInferrerUtil.getPackageName(pattern);
    String _operator_plus = StringExtensions.operator_plus(_packageName_1, ".");
    String _matcherClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matcherClassName(pattern);
    String _operator_plus_1 = StringExtensions.operator_plus(_operator_plus, _matcherClassName);
    _builder.append(_operator_plus_1, "");
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.append("import ");
    String _packageName_2 = this._eMFPatternLanguageJvmModelInferrerUtil.getPackageName(pattern);
    String _operator_plus_2 = StringExtensions.operator_plus(_packageName_2, ".");
    String _matchClassName = this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
    String _operator_plus_3 = StringExtensions.operator_plus(_operator_plus_2, _matchClassName);
    _builder.append(_operator_plus_3, "");
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("public class ");
    String _name = pattern.getName();
    String _operator_plus_4 = StringExtensions.operator_plus(_name, "Handler");
    _builder.append(_operator_plus_4, "");
    _builder.append(" extends AbstractHandler {");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("public Object execute(ExecutionEvent event) throws ExecutionException {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("//returns the selected element");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("Object firstElement = selection.getFirstElement();");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("//the filter is set in the command declaration no need for type checking");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("IFile file = (IFile)firstElement;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("//Loads the resource");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("ResourceSet resourceSet = new ResourceSetImpl();");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("URI fileURI = URI.createPlatformResourceURI(file.getFullPath()");
    _builder.newLine();
    _builder.append("\t\t\t\t");
    _builder.append(".toString(), false);");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("Resource resource = resourceSet.getResource(fileURI, true);");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("String matches = getMatches(resource);");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("//prints the match set to a dialog window ");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("MessageDialog.openInformation(Display.getCurrent().getActiveShell(), \"Match set of the \\\"");
    String _name_1 = pattern.getName();
    _builder.append(_name_1, "		");
    _builder.append("\\\" pattern\", ");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t\t\t");
    _builder.append("matches);");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("return null;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("/**");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("* Returns the match set of the ");
    String _name_2 = pattern.getName();
    _builder.append(_name_2, "	");
    _builder.append(" pattern on the input EMF resource");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("* @param emfRoot the container of the EMF model on which the pattern matching is invoked");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("* @return The serialized form of the match set");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("*/");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("private String getMatches(Notifier emfRoot){");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("//the match set will be serialized into a string builder");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("StringBuilder builder = new StringBuilder();");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("if(emfRoot != null) {\t");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("//get all matches of the pattern");
    _builder.newLine();
    _builder.append("\t\t\t");
    String _matcherClassName_1 = this._eMFPatternLanguageJvmModelInferrerUtil.matcherClassName(pattern);
    _builder.append(_matcherClassName_1, "			");
    _builder.append(" matcher = ");
    String _matcherClassName_2 = this._eMFPatternLanguageJvmModelInferrerUtil.matcherClassName(pattern);
    _builder.append(_matcherClassName_2, "			");
    _builder.append(".FACTORY.getMatcher(emfRoot);");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t\t");
    _builder.append("Collection<");
    String _matchClassName_1 = this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
    _builder.append(_matchClassName_1, "			");
    _builder.append("> matches = matcher.getAllMatches();");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t\t");
    _builder.append("//serializes the current match into the string builder");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("if(matches.size() > 0)");
    _builder.newLine();
    _builder.append("\t\t\t\t");
    _builder.append("for(");
    String _matchClassName_2 = this._eMFPatternLanguageJvmModelInferrerUtil.matchClassName(pattern);
    _builder.append(_matchClassName_2, "				");
    _builder.append(" match: matches) {");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t\t\t\t");
    _builder.append("builder.append(match.toString());");
    _builder.newLine();
    _builder.append("\t\t\t \t\t");
    _builder.append("builder.append(\"\\n\");");
    _builder.newLine();
    _builder.append("\t\t\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("else");
    _builder.newLine();
    _builder.append("\t\t\t\t");
    _builder.append("builder.append(\"The ");
    String _name_3 = pattern.getName();
    _builder.append(_name_3, "				");
    _builder.append(" pattern has an empty match set.\");\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("//returns the match set in a serialized form");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("return builder.toString();");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    return _builder;
  }
}
