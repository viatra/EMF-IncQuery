package org.eclipse.viatra2.emf.incquery.queryexplorer.observable;

import java.util.HashMap;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.DatabindingAdapter;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;
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
	public IObservableValue getObservableParameter(IPatternMatch match,	String parameterName) {
		if (parameterMap.size() > 0) {
			String expression = parameterMap.get(parameterName);
			return DatabindingUtil.getObservableValue(match, expression);
		}
		return null;
	}

	public void putToParameterMap(String key, String value) {
		parameterMap.put(key, value);
	}
}
