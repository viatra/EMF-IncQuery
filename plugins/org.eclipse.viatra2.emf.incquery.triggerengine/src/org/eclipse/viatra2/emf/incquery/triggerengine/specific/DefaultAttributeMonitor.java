package org.eclipse.viatra2.emf.incquery.triggerengine.specific;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.AttributeMonitor;

public class DefaultAttributeMonitor<MatchType extends IPatternMatch> extends AttributeMonitor<MatchType> {

	private ChangeListener changeListener;
	private Map<IObservableValue, MatchType> observableMap;
	private Map<MatchType, List<IObservableValue>> observableMapReversed;
	
	public DefaultAttributeMonitor() {
		super();
		this.changeListener = new ChangeListener();
		this.observableMap = new HashMap<IObservableValue, MatchType>();
		this.observableMapReversed = new HashMap<MatchType, List<IObservableValue>>();
	}
	
	private class ChangeListener implements IValueChangeListener {
		@Override
		public void handleValueChange(ValueChangeEvent event) {
			IObservableValue val = event.getObservableValue();
			if (val != null) {
				notifyListeners(observableMap.get(val));
			}
		}
	}
	
	@Override
	public void registerFor(MatchType match) {
		List<IObservableValue> values = new ArrayList<IObservableValue>();
		for (String param : match.parameterNames()) {
			Object location = match.get(param);
			List<IObservableValue> observableValues = observeAllAttributes(changeListener, location);
			values.addAll(observableValues);
		}

		//inserting {observable,match} pairs
		for (IObservableValue val : values) {
			observableMap.put(val, match);
		}

		//inserting {match, list(observable)} pairs
		observableMapReversed.put(match, values);
	}

	private List<IObservableValue> observeAllAttributes(IValueChangeListener changeListener, Object object) {
		List<IObservableValue> affectedValues = new ArrayList<IObservableValue>();
		if (object instanceof EObject) {
			for (EStructuralFeature feature : ((EObject) object).eClass().getEAllStructuralFeatures()) {
				IObservableValue val = EMFProperties.value(feature).observe(object);
				affectedValues.add(val);
				val.addValueChangeListener(changeListener);
			}
		}
		return affectedValues;
	}
	
	@Override
	public void unregisterForAll() {
		for (MatchType match : observableMapReversed.keySet()) {
			unregisterFor(match);
		}
	}
	
	@Override
	public void unregisterFor(MatchType match) {
		List<IObservableValue> observables = observableMapReversed.get(match);
		if (observables != null) {
			for (IObservableValue val : observables) {
				val.removeValueChangeListener(changeListener);
			}
		}
	}
}