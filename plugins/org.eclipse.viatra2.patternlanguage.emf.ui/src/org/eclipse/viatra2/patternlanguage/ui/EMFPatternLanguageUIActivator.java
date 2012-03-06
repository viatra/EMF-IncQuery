package org.eclipse.viatra2.patternlanguage.ui;

import org.eclipse.viatra2.emf.incquery.tooling.generator.IncQueryGeneratorPlugin;
import org.eclipse.viatra2.patternlanguage.ui.internal.EMFPatternLanguageActivator;

import com.google.inject.Module;

public class EMFPatternLanguageUIActivator extends EMFPatternLanguageActivator {

	@Override
	protected Module getRuntimeModule(String grammar) {
		if (ORG_ECLIPSE_VIATRA2_PATTERNLANGUAGE_EMFPATTERNLANGUAGE.equals(grammar)) {
			//return IncQueryRuntimePlugin.getDefault().getRuntimeModule();
			return IncQueryGeneratorPlugin.INSTANCE.getRuntimeModule();
		}
		
		throw new IllegalArgumentException(grammar);
	}

}
