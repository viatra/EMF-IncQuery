/**
 * 
 */
package org.eclipse.viatra2.emf.incquery.runtime.internal;

import com.google.inject.Injector;

/**
 * A singleton provider for Xtext injectors
 * @author Zoltan Ujhelyi
 *
 */
public class XtextInjectorProvider {

	public static XtextInjectorProvider INSTANCE = new XtextInjectorProvider();
	private Injector injector;
	
	
	private XtextInjectorProvider() {}


	public Injector getInjector() {
		return injector;
	}


	public void setInjector(Injector injector) {
		this.injector = injector;
	}
	
	
}
