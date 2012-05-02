/**
 * 
 */
package org.eclipse.viatra2.emf.incquery.runtime.extensibility;

import com.google.inject.Injector;

/**
 * Interface for providing external Guice modules for EMF-IncQuery
 * @author Zoltan Ujhelyi
 *
 */
public interface IInjectorProvider {

	public Injector getInjector();
}
