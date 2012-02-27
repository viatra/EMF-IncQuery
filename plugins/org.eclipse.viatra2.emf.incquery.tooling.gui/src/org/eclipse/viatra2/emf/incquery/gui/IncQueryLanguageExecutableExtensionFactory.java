package org.eclipse.viatra2.emf.incquery.gui;

import org.eclipse.viatra2.patternlanguage.ui.EMFPatternLanguageExecutableExtensionFactory;
import org.osgi.framework.Bundle;

public class IncQueryLanguageExecutableExtensionFactory extends
		EMFPatternLanguageExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return IncQueryGUIPlugin.getDefault().getBundle();
	}

}
