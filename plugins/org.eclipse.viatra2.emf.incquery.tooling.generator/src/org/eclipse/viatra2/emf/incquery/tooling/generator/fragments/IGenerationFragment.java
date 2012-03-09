/**
 * 
 */
package org.eclipse.viatra2.emf.incquery.tooling.generator.fragments;

import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.viatra2.emf.incquery.tooling.generator.ExtensionGenerator;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.xtext.generator.IFileSystemAccess;

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
	
	public void generateFiles(Pattern pattern, IFileSystemAccess fsa);

	//public Iterable<JvmGenericType> inferFiles(Pattern pattern);
	public Iterable<IPluginExtension> extensionContribution(Pattern pattern, ExtensionGenerator exGen);
}
