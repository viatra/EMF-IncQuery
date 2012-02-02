package org.eclipse.viatra2.emf.incquery.gui.wizards;


import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;

/**
 * A project wizard for extracting the example zip file into the workspace.
 * @author Zoltan Ujhelyi
 *
 */
public class NewSampleProjectWizard extends ProjectUnzipperNewWizard {

	/**
	 * Redefining the {@link ProjectUnzipperNewWizard} constructor with the example zip file.
	 */
	public NewSampleProjectWizard() {
		super("IncQueryExampleWizard", "Create Project",
				"The project to put our example.",
				"org.eclipse.viatra2.emf.incquery.sample", FileLocator.find(
						IncQueryGUIPlugin.getDefault().getBundle(), new Path("examples/example.zip"), null));
	}

}
