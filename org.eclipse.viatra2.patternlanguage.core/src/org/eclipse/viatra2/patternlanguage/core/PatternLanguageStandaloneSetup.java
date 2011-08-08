
package org.eclipse.viatra2.patternlanguage.core;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class PatternLanguageStandaloneSetup extends PatternLanguageStandaloneSetupGenerated{

	public static void doSetup() {
		new PatternLanguageStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

