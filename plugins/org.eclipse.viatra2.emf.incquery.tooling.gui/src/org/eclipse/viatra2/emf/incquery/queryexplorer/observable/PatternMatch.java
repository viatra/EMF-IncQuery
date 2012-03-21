package org.eclipse.viatra2.emf.incquery.queryexplorer.observable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
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
	private IPatternMatch signature;
	private PatternMatcher parent;
	private String message;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private ParameterValueChangedListener listener;
	
	public PatternMatch(PatternMatcher parent, IPatternMatch signature) {
		this.parent = parent;
		this.signature = signature;
		this.message = DatabindingUtil.getMessage(signature.patternName(), parent.isGenerated());
		this.listener = new ParameterValueChangedListener();
		if (message != null) {
			updateText();
			DatabindingUtil.observeFeatures(signature, listener, message);
		}
		else {
			this.text = signature.toString();
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
			updateText();
		}
	}
	
	private void updateText() {
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
						o = signature.get(objectTokens[0]);
						feature = DatabindingUtil.getFeature(o, "name");
					}
					if (objectTokens.length == 2) {
						o = signature.get(objectTokens[0]);
						feature = DatabindingUtil.getFeature(o, objectTokens[1]);
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
		
		this.setText(newText);
	}

	public IPatternMatch getSignature() {
		return signature;
	}
	
	public Object[] getLocationObjects() {
		return this.signature.toArray();
	}
}
