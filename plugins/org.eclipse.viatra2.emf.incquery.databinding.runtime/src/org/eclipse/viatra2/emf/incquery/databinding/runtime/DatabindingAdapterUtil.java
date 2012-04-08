package org.eclipse.viatra2.emf.incquery.databinding.runtime;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
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
	 * Registers the given changeListener for the appropriate features of the given signature.
	 * The features will be computed based on the message parameter.
	 * 
	 * @param signature the signature instance
	 * @param changeListener the changle listener 
	 * @param message the message which can be found in the appropriate PatternUI annotation
	 * @return the list of IObservableValue instances for which the IValueChangeListener was registered
	 */
	public static List<IObservableValue> observeFeatures(IPatternMatch match,	IValueChangeListener changeListener, String message) {
		List<IObservableValue> affectedValues = new ArrayList<IObservableValue>();
		if (message != null) {
			String[] tokens = message.split("\\$");

			for (int i = 0; i < tokens.length; i++) {
				
				//odd tokens 
				if (i % 2 != 0) {
					IObservableValue value = DatabindingAdapterUtil.getObservableValue(match, tokens[i]);
					if (value != null) {
						value.addValueChangeListener(changeListener);
						affectedValues.add(value);
					}
				}
			}
		}
		return affectedValues;
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
	
	public static String getMessage(IPatternMatch match, String message) {
		String[] tokens = message.split("\\$");
		String newText = "";
		
		for (int i = 0;i<tokens.length;i++) {
			if (i % 2 == 0) {
				newText += tokens[i];
			}
			else {
				String[] objectTokens = tokens[i].split("\\.");
				if (objectTokens.length > 0) {
					Object o = null;
					EStructuralFeature feature = null;
					
					if (objectTokens.length == 1) {
						o = match.get(objectTokens[0]);
						feature = DatabindingAdapterUtil.getFeature(o, "name");
					}
					if (objectTokens.length == 2) {
						o = match.get(objectTokens[0]);
						feature = DatabindingAdapterUtil.getFeature(o, objectTokens[1]);
					}
					
					if (o != null && feature != null) {
						Object value = ((EObject) o).eGet(feature);
						if (value != null) {
							newText += value.toString();
						}
						else {
							newText += "null";
						}
					}
					else if (o != null) {
						newText += o.toString();
					}
				}	
				else {
					newText += "[no such parameter]";
				}
			}
		}
		
		return newText;
	}
}
