package org.eclipse.viatra2.emf.incquery.queryexplorer.observable;

import java.util.HashMap;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.DatabindingAdapter;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

public class RuntimeDatabindingAdapter extends DatabindingAdapter<IPatternMatch> {

	private HashMap<String, String> parameterMap;
	
	public RuntimeDatabindingAdapter() {
		parameterMap = new HashMap<String, String>();
	}
	
	@Override
	public String[] getParameterNames() {
		return parameterMap.keySet().toArray(new String[parameterMap.keySet().size()]);
	}

	@Override
	public IObservableValue getObservableParameter(IPatternMatch signature,	String parameterName) {
		if (parameterMap.size() > 0) {
			String expression = parameterMap.get(parameterName);
			String[] tokens = expression.split("\\.");
			
			Object o = signature.get(tokens[0]);
			if (o != null && o instanceof EObject) {
				EObject eObj = (EObject) o;
				EStructuralFeature feature = eObj.eClass().getEStructuralFeature(tokens[1]);
				if (feature != null) {
					return EMFProperties.value(feature).observe(eObj);
				}
			}
		}
		return null;
	}

	public void putToParameterMap(String key, String value) {
		parameterMap.put(key, value);
	}
}
