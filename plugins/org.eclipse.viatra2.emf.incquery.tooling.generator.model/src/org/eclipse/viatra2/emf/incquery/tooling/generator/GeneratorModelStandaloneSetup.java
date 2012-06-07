
package org.eclipse.viatra2.emf.incquery.tooling.generator;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class GeneratorModelStandaloneSetup extends GeneratorModelStandaloneSetupGenerated{

	public static void doSetup() {
		new GeneratorModelStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

