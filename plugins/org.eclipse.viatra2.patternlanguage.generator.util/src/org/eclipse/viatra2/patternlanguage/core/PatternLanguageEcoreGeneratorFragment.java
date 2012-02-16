package org.eclipse.viatra2.patternlanguage.core;

import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.generator.ecore.EcoreGeneratorFragment;

/**
 * A redefined Ecore generator fragment implementation that does not export
 * EMF packages in the runtime plug-in.
 * @author Zoltan Ujhelyi
 *
 */
public class PatternLanguageEcoreGeneratorFragment extends
		EcoreGeneratorFragment {

	@Override
	protected String getTemplate() {
		return EcoreGeneratorFragment.class.getName().replaceAll("\\.", "::");
	}
	
	@Override
	public String[] getExportedPackagesRt(Grammar grammar) {
		return null;
	}

}
