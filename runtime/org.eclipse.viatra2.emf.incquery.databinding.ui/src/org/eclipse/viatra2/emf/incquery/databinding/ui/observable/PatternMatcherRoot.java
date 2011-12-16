package org.eclipse.viatra2.emf.incquery.databinding.ui.observable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;


public class PatternMatcherRoot {
	
	private Map<IncQueryMatcher<?>, PatternMatcher> matchers;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private ViewerRootKey key;
	
	public PatternMatcherRoot(ViewerRootKey key) {
		matchers = new HashMap<IncQueryMatcher<?>, PatternMatcher>();
		this.key = key;
	}
	
	public void addMatcher(IncQueryMatcher<IPatternSignature> matcher) {
		List<PatternMatcher> oldValue = new ArrayList<PatternMatcher>(matchers.values());
		PatternMatcher pm = new PatternMatcher(this, matcher);
		this.matchers.put(matcher, pm);
		List<PatternMatcher> newValue = new ArrayList<PatternMatcher>(matchers.values());
		this.propertyChangeSupport.firePropertyChange("matchers", oldValue, newValue);
	}
	
	public void removeMatcher(IncQueryMatcher<?> matcher) {
		List<PatternMatcher> oldValue = new ArrayList<PatternMatcher>(matchers.values());
		this.matchers.get(matcher).dispose();
		this.matchers.remove(matcher);
		List<PatternMatcher> newValue = new ArrayList<PatternMatcher>(matchers.values());
		this.propertyChangeSupport.firePropertyChange("matchers", oldValue, newValue);
	}
	
	public List<PatternMatcher> getMatchers() {
		return new ArrayList<PatternMatcher>(matchers.values());
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	public String getText() {
		return key.toString();
	}
	
	public void dispose() {
		for (PatternMatcher pm : this.matchers.values()) {
			pm.dispose();
		}
	}
	
	public IEditorPart getEditorPart() {
		return this.key.getEditor();
	}
}
