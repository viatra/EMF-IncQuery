package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

public class PatternRegistry {

	private static PatternRegistry instance;
	private Map<IFile, PatternModel> registeredPatterModels;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	public static PatternRegistry getInstance() {
		if (instance == null) {
			instance = new PatternRegistry();
		}
		return instance;
	}
	
	protected PatternRegistry() {
		registeredPatterModels = new HashMap<IFile, PatternModel>();
	}
	
	public void registerPatternModel(IFile file, PatternModel pm) {
		List<String> oldValue = getPatternNames();
		this.registeredPatterModels.put(file, pm);
		List<String> newValue = getPatternNames();
		this.propertyChangeSupport.firePropertyChange("patternNames", oldValue, newValue);
	}
	
	public void unregisterPatternModel(IFile file) {
		List<String> oldValue = getPatternNames();
		this.registeredPatterModels.remove(file);
		List<String> newValue = getPatternNames();
		this.propertyChangeSupport.firePropertyChange("patternNames", oldValue, newValue);
	}
	
	public List<String> getPatternNames() {
		List<String> patterns = new ArrayList<String>();
		for (PatternModel pm : registeredPatterModels.values()) {
			for (Pattern p : pm.getPatterns()) {
				patterns.add(p.getName());
			}
		}
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
