package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

public class PatternRegistry {

	private static PatternRegistry instance;
	private Map<IFile, PatternModel> registeredPatterModels;
	private Set<Pattern> activePatterns;
	private Map<String, Pattern> patternNameMap;
	
	public static PatternRegistry getInstance() {
		if (instance == null) {
			instance = new PatternRegistry();
		}
		return instance;
	}
	
	protected PatternRegistry() {
		registeredPatterModels = new HashMap<IFile, PatternModel>();
		patternNameMap = new HashMap<String, Pattern>();
		activePatterns = new HashSet<Pattern>();
	}
	
	public void unregisterPattern(String patternFqn) {
		Pattern pattern = this.patternNameMap.get(patternFqn);
		if (pattern != null) {
			this.patternNameMap.remove(patternFqn);
		}
	}
	
	public Set<Pattern> registerPatternModel(IFile file, PatternModel pm) {
		this.registeredPatterModels.put(file, pm);
		Set<Pattern> newPatterns = new HashSet<Pattern>();
		
		if (pm != null) {
			for (Pattern p : pm.getPatterns()) {
				Annotation annotation = DatabindingUtil.getAnnotation(p, DatabindingUtil.OFF_ANNOTATION);
				
				if (annotation == null) {
					String patternFqn = CorePatternLanguageHelper.getFullyQualifiedName(p);
					patternNameMap.put(patternFqn, p);
					newPatterns.add(p);
					activePatterns.add(p);
				}
			}
		}

		return newPatterns;
	}
	
	public void addActivePattern(Pattern p) {
		activePatterns.add(p);
	}
	
	public Set<Pattern> unregisterPatternModel(IFile file) {
		Set<Pattern> removedPatterns = new HashSet<Pattern>();
		PatternModel pm = this.registeredPatterModels.remove(file);
		
		if (pm != null) {
			for (Pattern p : pm.getPatterns()) {
				String patternFqn = CorePatternLanguageHelper.getFullyQualifiedName(p);
				if (activePatterns.remove(p)) {
					removedPatterns.add(p);
				}
				patternNameMap.remove(patternFqn);
			}
		}
		
		return removedPatterns;
	}
	
	public void removeActivePattern(Pattern p) {
		activePatterns.remove(p);
	}
	
	public Pattern getPatternByFqn(String patternFqn) {
		return patternNameMap.get(patternFqn);
	}
	
	public Set<Pattern> getActivePatterns() {
		return activePatterns;
	}
	
	public boolean isActive(String patternFqn) {
		for (Pattern p : activePatterns) {
			if (CorePatternLanguageHelper.getFullyQualifiedName(p).matches(patternFqn)) {
				return true;
			}
		}
		return false;
	}
	
	public List<String> getPatternNames() {
		List<String> patterns = new ArrayList<String>();
		patterns.addAll(patternNameMap.keySet());
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
	
	public IFile getFileForPattern(Pattern pattern) {
		if(pattern != null && patternNameMap.containsValue(pattern)) {
			for (Entry<IFile, PatternModel> entry : registeredPatterModels.entrySet()) {
				EList<Pattern> patterns = entry.getValue().getPatterns();
				if(patterns.size() > 0 && patterns.contains(pattern)) {
					return entry.getKey();
				}
			}
		}
		return null;
	}
}
