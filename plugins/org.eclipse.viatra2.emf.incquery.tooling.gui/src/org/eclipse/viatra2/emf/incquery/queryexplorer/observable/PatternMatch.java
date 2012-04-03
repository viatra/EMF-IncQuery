package org.eclipse.viatra2.emf.incquery.queryexplorer.observable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.DatabindingAdapterUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

/**
 * A PatternMatch is associated to every Signature of a matcher.
 * It is the lowest level element is the treeviewer.
 * 
 * @author Tamas Szabo
 *
 */
public class PatternMatch {

	private String text;
	private IPatternMatch match;
	private PatternMatcher parent;
	private String message;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private ParameterValueChangedListener listener;
	private List<IObservableValue> affectedValues;
	
	public PatternMatch(PatternMatcher parent, IPatternMatch match) {
		this.parent = parent;
		this.match = match;
		this.message = DatabindingUtil.getMessage(match, parent.isGenerated());
		this.listener = new ParameterValueChangedListener();
		if (message != null) {
			setText(DatabindingAdapterUtil.getMessage(match, message));
			affectedValues = DatabindingAdapterUtil.observeFeatures(match, listener, message);
		}
		else {
			this.text = match.toString();
		}
	}
	
	public void dispose() {
		if (affectedValues != null) {
			for (IObservableValue val : affectedValues) {
				val.removeValueChangeListener(listener);
			}
		}
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	public void setText(String text) {
		propertyChangeSupport.firePropertyChange("text", this.text,	this.text = text);
	}

	public String getText() {
		return text;
	}

	public PatternMatcher getParent() {
		return parent;
	}
	
	private class ParameterValueChangedListener implements IValueChangeListener {
		@Override
		public void handleValueChange(ValueChangeEvent event) {
			setText(DatabindingAdapterUtil.getMessage(match, message));
		}
	}

	public IPatternMatch getSignature() {
		return match;
	}
	
	public Object[] getLocationObjects() {
		return this.match.toArray();
	}
}
