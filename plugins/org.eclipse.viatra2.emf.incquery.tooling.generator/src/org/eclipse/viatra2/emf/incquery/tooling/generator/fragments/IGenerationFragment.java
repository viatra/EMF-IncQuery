/**
 * 
 */
package org.eclipse.viatra2.emf.incquery.tooling.generator.fragments;

import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.viatra2.emf.incquery.tooling.generator.ExtensionGenerator;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.xbase.lib.Pair;

/**
 * A code generation fragment is used by annotation processors for code
 * generation.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public interface IGenerationFragment {

	/**
	 * Returns the postfix used to define the destination project. The generated
	 * contents are put into the <code>model.project.name.postfix</code>
	 * project, or left in the <code>model.project.name</code> project if an
	 * empty postfix is returned.
	 * 
	 * @return A project postfix, or null
	 */
	public String getProjectPostfix();

	/**
	 * Returns an array of bundle id's to add to the destination project as dependency. This
	 * array need not to contain the model project, as it is added automatically
	 * to new generated projects.
	 * 
	 * @return A non-null (but possibly empty) array of dependencies to add.
	 */
	public String[] getProjectDependencies();
	
	/**
	 * Executes code generation for a selected pattern. All resulting files
	 * should be placed using the file system access component.
	 * 
	 * @param pattern
	 * @param fsa
	 */
	public void generateFiles(Pattern pattern, IFileSystemAccess fsa);
	
	/**
	 * Cleans up the previosly generated files for the selected pattern. Delete
	 * the files using the file system access component.
	 * 
	 * @param pattern
	 * @param fsa
	 */
	public void cleanUp(Pattern pattern, IFileSystemAccess fsa);

	//public Iterable<JvmGenericType> inferFiles(Pattern pattern);
	/**
	 * Returns a collection of extension contributions for the selected pattern.
	 * The {@link ExtensionGeneration} parameter provides a builder API for
	 * Xtend-based generators to have a readable generator.
	 * 
	 * @param pattern
	 * @param exGen
	 * @return a collection of plugin extensions
	 */
	public Iterable<IPluginExtension> extensionContribution(Pattern pattern, ExtensionGenerator exGen);
	
	/**
	 * Returns a collections of extensions, that need to be removed from the plugin.xml.
	 * @param pattern
	 * @return
	 */
	public Iterable<Pair<String, String>> removeExtension(Pattern pattern);
	
}
