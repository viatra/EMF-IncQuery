package org.eclipse.viatra2.emf.incquery.queryexplorer.observable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

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
	
	private Map<IFile, Set<String>> runtimeMatcherRegistry;
	private Map<String, PatternMatcher> matchers;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private ViewerRootKey key;
	
	private ILog logger = IncQueryGUIPlugin.getDefault().getLog(); 
	
	public PatternMatcherRoot(ViewerRootKey key) {
		matchers = new HashMap<String, PatternMatcher>();
		runtimeMatcherRegistry = new HashMap<IFile, Set<String>>();
		this.key = key;
	}
	
	public void addMatcher(IncQueryMatcher<? extends IPatternMatch> matcher, String patternFqn, boolean generated) {
		List<PatternMatcher> oldValue = new ArrayList<PatternMatcher>(matchers.values());
		PatternMatcher pm = new PatternMatcher(this, matcher, patternFqn, generated);
		this.matchers.put(patternFqn, pm);
		List<PatternMatcher> newValue = new ArrayList<PatternMatcher>(matchers.values());
		this.propertyChangeSupport.firePropertyChange(MATCHERS_ID, oldValue, newValue);
	}
	
	public void removeMatcher(String patternFqn) {
		List<PatternMatcher> oldValue = new ArrayList<PatternMatcher>(matchers.values());
		this.matchers.get(patternFqn).dispose();
		this.matchers.remove(patternFqn);
		List<PatternMatcher> newValue = new ArrayList<PatternMatcher>(matchers.values());
		this.propertyChangeSupport.firePropertyChange(MATCHERS_ID, oldValue, newValue);
	}
	
	public static final String MATCHERS_ID = "matchers";
	
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
		if (!runtimeMatcherRegistry.containsKey(file)) {
			Set<String> _patterns = new HashSet<String>();
			EList<Pattern> patterns = pm.getPatterns();
			IncQueryMatcher<GenericPatternMatch> matcher = null;
				
			for (Pattern pattern : patterns) {
				try {
					matcher = new GenericPatternMatcher(pattern, key.getNotifier());
				}
				catch (IncQueryRuntimeException e) {
					logger.log(new Status(IStatus.ERROR,
							IncQueryGUIPlugin.PLUGIN_ID,
							"Cannot initialize pattern matcher for pattern "
									+ pattern.getName(), e));
					matcher = null;
				}
				_patterns.add(CorePatternLanguageHelper.getFullyQualifiedName(pattern));
				addMatcher(matcher, CorePatternLanguageHelper.getFullyQualifiedName(pattern), false);
			}
				
			runtimeMatcherRegistry.put(file, _patterns);
		}
	}
	
	public void unregisterPatternsFromFile(IFile file) {
		Set<String> setTmp = runtimeMatcherRegistry.get(file);
		if (setTmp != null) {
			for (String pattern : setTmp) {
				removeMatcher(pattern);
			}
			
			runtimeMatcherRegistry.remove(file);
		}
	}
}
