
package org.eclipse.viatra2.patternlanguage;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class EMFPatternLanguageStandaloneSetup extends EMFPatternLanguageStandaloneSetupGenerated{

	public static void doSetup() {
		new EMFPatternLanguageStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

