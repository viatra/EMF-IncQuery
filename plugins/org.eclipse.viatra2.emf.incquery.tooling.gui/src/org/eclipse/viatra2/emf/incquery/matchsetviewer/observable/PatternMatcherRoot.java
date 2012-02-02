package org.eclipse.viatra2.emf.incquery.matchsetviewer.observable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternSignature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.emf.matcherbuilder.runtime.PatternRegistry;

/**
 * Each IEditingDomainProvider will be associated a PatternMatcherRoot element in the tree viewer.
 * PatterMatcherRoots are indexed with a ViewerRootKey.
 * 
 * It's children element will be PatterMatchers.
 *  
 * @author Tamas Szabo
 *
 */
public class PatternMatcherRoot {
	
	private Map<IFile, Set<IncQueryMatcher<? extends IPatternSignature>>> runtimeMatcherRegistry;
	private Map<IncQueryMatcher<? extends IPatternSignature>, PatternMatcher> matchers;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private ViewerRootKey key;
	
	public PatternMatcherRoot(ViewerRootKey key) {
		matchers = new HashMap<IncQueryMatcher<? extends IPatternSignature>, PatternMatcher>();
		runtimeMatcherRegistry = new HashMap<IFile, Set<IncQueryMatcher<? extends IPatternSignature>>>();
		this.key = key;
	}
	
	public void addMatcher(IncQueryMatcher<? extends IPatternSignature> matcher, boolean generated) {
		List<PatternMatcher> oldValue = new ArrayList<PatternMatcher>(matchers.values());
		PatternMatcher pm = new PatternMatcher(this, matcher, generated);
		this.matchers.put(matcher, pm);
		List<PatternMatcher> newValue = new ArrayList<PatternMatcher>(matchers.values());
		this.propertyChangeSupport.firePropertyChange("matchers", oldValue, newValue);
	}
	
	public void removeMatcher(IncQueryMatcher<? extends IPatternSignature> matcher) {
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
	
	public void registerPatternsFromFile(IFile file, PatternModel pm) {	
		
		try {
			if (!runtimeMatcherRegistry.containsKey(file)) {
				Set<IncQueryMatcher<? extends IPatternSignature>> setTmp = new HashSet<IncQueryMatcher<? extends IPatternSignature>>();
				EList<Pattern> patterns = pm.getPatterns();
				
				for (Pattern pattern : patterns) {	
					IMatcherFactory<GenericPatternSignature, GenericPatternMatcher> matcherFactory = 
						PatternRegistry.INSTANCE.getMatcherFactory(pattern);
		
					IncQueryMatcher<GenericPatternSignature> matcher = matcherFactory.getMatcher(key.getNotifier());
					setTmp.add(matcher);
					addMatcher(matcher, false);
				}
				
				runtimeMatcherRegistry.put(file, setTmp);
			}
		}
		catch (IncQueryRuntimeException e) {
			e.printStackTrace();
		}
	}
	
	public void unregisterPatternsFromFile(IFile file) {
		Set<IncQueryMatcher<? extends IPatternSignature>> setTmp = runtimeMatcherRegistry.get(file);
		if (setTmp != null) {
			for (IncQueryMatcher<? extends IPatternSignature> matcher : setTmp) {
				removeMatcher(matcher);
			}
			
			runtimeMatcherRegistry.remove(file);
		}
	}
}
