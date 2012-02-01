package org.eclipse.viatra2.emf.incquery.databinding.runtime;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature;

/**
 * The class is used to observ given parameters of a match set.
 * 
 * @author Tamas Szabo
 *
 * @param <T> the type parameter of the signature
 */
public abstract class DatabindingAdapter<T extends IPatternSignature> {
	
	/**
	 * Returns the array of observable valuess.
	 * 
	 * @return the array of values
	 */
	public abstract String[] getParameterNames();
	
	/**
	 * Returns an observable value for the given signature and parameterName.
	 * 
	 * @param signature the signature
	 * @param parameterName the parameter name
	 * @return an observable value
	 */
	public abstract IObservableValue getObservableParameter(T signature, String parameterName);
}
