/**
 * 
 */
package org.eclipse.viatra2.emf.incquery.tooling.generator.ui;

import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment;

/**
 * A generator fragment for the sample UI project
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class SampleUIGenerator implements IGenerationFragment {

	/**
	 * 
	 */
	public SampleUIGenerator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getProjectPostfix() {
		return "ui";
	}

	@Override
	public String[] getProjectDependencies() {
		return new String[] { "org.eclipse.core.runtime", "org.eclipse.ui",
				"org.eclipse.emf.ecore", "org.eclipse.pde.core"};
	}

}
