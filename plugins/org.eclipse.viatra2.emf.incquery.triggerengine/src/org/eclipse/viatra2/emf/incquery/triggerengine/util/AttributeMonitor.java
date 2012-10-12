package org.eclipse.viatra2.emf.incquery.triggerengine.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.NotificationProvider;

public class AttributeMonitor<T extends IPatternMatch> extends NotificationProvider<T> {

	private ChangeListener changeListener;
	private Map<IObservableValue, T> observableMap;
	private Map<T, List<IObservableValue>> observableMapReversed;
	public Collection<T> matchModificationEvents;
	
	public AttributeMonitor() {
		this.changeListener = new ChangeListener();
		this.observableMap = new HashMap<IObservableValue, T>();
		this.observableMapReversed = new HashMap<T, List<IObservableValue>>();
		this.matchModificationEvents = new HashSet<T>();
	}
	
	private class ChangeListener implements IValueChangeListener {
		@Override
		public void handleValueChange(ValueChangeEvent event) {
			IObservableValue val = event.getObservableValue();
			if (val != null) {
				T match = observableMap.get(val);
				matchModificationEvents.add(match);
			}
			notfiyListeners();
		}
	}
	
	public void clear() {
		this.matchModificationEvents.clear();
	}
	
	public void registerFor(T match) {
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
	
	public void unregisterForAll() {
		for (T match : observableMapReversed.keySet()) {
			unregisterFor(match);
		}
	}
	
	public void unregisterFor(T match) {
		List<IObservableValue> observables = observableMapReversed.get(match);
		if (observables != null) {
			for (IObservableValue val : observables) {
				val.removeValueChangeListener(changeListener);
			}
		}
	}

	@Override
	public void dispose() {
		this.unregisterForAll();
	}
}