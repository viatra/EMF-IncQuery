package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

public class PatternRegistry {

	private static PatternRegistry instance;
	private Map<IFile, PatternModel> registeredPatterModels;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private Map<Pattern, String> patternNameMap;
	
	public static PatternRegistry getInstance() {
		if (instance == null) {
			instance = new PatternRegistry();
		}
		return instance;
	}
	
	protected PatternRegistry() {
		registeredPatterModels = new HashMap<IFile, PatternModel>();
		patternNameMap = new HashMap<Pattern, String>();
	}
	
	public void registerPatternModel(IFile file, PatternModel pm) {
		List<String> oldValue = getPatternNames();
		this.registeredPatterModels.put(file, pm);
		
		for (Pattern p : pm.getPatterns()) {
			patternNameMap.put(p, CorePatternLanguageHelper.getFullyQualifiedName(p));
		}
		
		List<String> newValue = getPatternNames();
		this.propertyChangeSupport.firePropertyChange("patternNames", oldValue, newValue);
	}
	
	public void unregisterPatternModel(IFile file) {
		List<String> oldValue = getPatternNames();
		PatternModel pm = this.registeredPatterModels.remove(file);
		
		if (pm != null) {
			for (Pattern p : pm.getPatterns()) {
				patternNameMap.remove(p);
			}
		}
		
		List<String> newValue = getPatternNames();
		this.propertyChangeSupport.firePropertyChange("patternNames", oldValue, newValue);
	}
	
	public List<String> getPatternNames() {
		List<String> patterns = new ArrayList<String>();
		patterns.addAll(patternNameMap.values());
		return patterns;
	}
	
	public List<Pattern> getPatterns() {
		List<Pattern> patterns = new ArrayList<Pattern>();
		for (PatternModel pm : registeredPatterModels.values()) {
			patterns.addAll(pm.getPatterns());
		}
		return patterns;
	}
	
	public List<IFile> getFiles() {
		List<IFile> files = new ArrayList<IFile>();
		files.addAll(registeredPatterModels.keySet());
		return files;
	}
	
	public PatternModel getPatternModel(IFile file) {
		return registeredPatterModels.get(file);
	}
	
	public List<PatternModel> getPatternModels() {
		List<PatternModel> models = new ArrayList<PatternModel>();
		models.addAll(registeredPatterModels.values());
		return models;
	}
	

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
}
