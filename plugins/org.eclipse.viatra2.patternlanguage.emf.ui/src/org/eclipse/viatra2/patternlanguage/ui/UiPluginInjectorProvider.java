package org.eclipse.viatra2.patternlanguage.ui;

import org.eclipse.viatra2.emf.incquery.runtime.extensibility.IInjectorProvider;

import com.google.inject.Injector;

public class UiPluginInjectorProvider implements IInjectorProvider {

	@Override
	public Injector getInjector() {
		// TODO Auto-generated method stub
		return EMFPatternLanguageUIActivator.getInstance().getInjector(EMFPatternLanguageUIActivator.ORG_ECLIPSE_VIATRA2_PATTERNLANGUAGE_EMFPATTERNLANGUAGE);
	}

}
