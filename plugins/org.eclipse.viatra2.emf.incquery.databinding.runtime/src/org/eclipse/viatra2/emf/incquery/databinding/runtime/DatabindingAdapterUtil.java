package org.eclipse.viatra2.emf.incquery.databinding.runtime;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

public class DatabindingAdapterUtil {
	
	/**
	 * Returns an IObservableValue for the given match based on the given expression.
	 * If an attribute is not present in the expression than it tries with the 'name' attribute.
	 * If it is not present the returned value will be null.
	 * 
	 * @param match the match object
	 * @param expression the expression
	 * @return IObservableValue instance or null 
	 */
	public static IObservableValue getObservableValue(IPatternMatch match, String expression) {
		IObservableValue val = null;
		String[] objectTokens = expression.split("\\.");
		
		if (objectTokens.length > 0) {
			Object o = null;
			EStructuralFeature feature = null;
			
			if (objectTokens.length == 2) {
				o = match.get(objectTokens[0]);
				feature = getFeature(o, objectTokens[1]);
			}
			if (objectTokens.length == 1) {
				o = match.get(objectTokens[0]);
				feature = getFeature(o, "name");
			}
			if (o != null && feature != null) {
				val = EMFProperties.value(feature).observe(o);
			}
		}
		
		return val;
	}
	
	/**
	 * Get the structural feature with the given name of the given object.
	 * 
	 * @param o the object (must be an EObject)
	 * @param featureName the name of the feature
	 * @return the EStructuralFeature of the object or null if it can not be found
	 */
	public static EStructuralFeature getFeature(Object o, String featureName) {
		if (o != null && o instanceof EObject) {
			EStructuralFeature feature = ((EObject) o).eClass().getEStructuralFeature(featureName);
			return feature;
		}
		return null;
	}
}
