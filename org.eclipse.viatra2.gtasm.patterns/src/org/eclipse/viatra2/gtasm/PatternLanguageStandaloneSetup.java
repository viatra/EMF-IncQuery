
package org.eclipse.viatra2.gtasm;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class PatternLanguageStandaloneSetup extends PatternLanguageStandaloneSetupGenerated{

	public static void doSetup() {
		new PatternLanguageStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

