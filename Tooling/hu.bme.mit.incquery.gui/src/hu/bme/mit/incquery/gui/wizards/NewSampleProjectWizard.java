package hu.bme.mit.incquery.gui.wizards;

import hu.bme.mit.incquery.gui.IncQueryGUIPlugin;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

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
				"hu.bme.mit.incquery.sample", FileLocator.find(
						IncQueryGUIPlugin.getDefault().getBundle(), new Path("examples/example.zip"), null));
	}

}
