/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

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

/**
 * Utility class used by the Query Explorer for the maintenance of registered
 * patterns.
 * 
 * @author Tamas Szabo
 * 
 */
public class PatternRegistry {

	private static PatternRegistry instance;
	private Map<IFile, PatternModel> registeredPatterModels;
	private List<Pattern> activePatterns;
	private Map<String, Pattern> patternNameMap;

	public synchronized static PatternRegistry getInstance() {
		if (instance == null) {
			instance = new PatternRegistry();
		}
		return instance;
	}

	protected PatternRegistry() {
		registeredPatterModels = new HashMap<IFile, PatternModel>();
		patternNameMap = new HashMap<String, Pattern>();
		activePatterns = new ArrayList<Pattern>();
	}

	public void addGeneratedPattern(Pattern pattern, String patternFqn) {
		this.patternNameMap.put(patternFqn, pattern);
	}

	public boolean isGenerated(Pattern pattern) {
		return DatabindingUtil.getGeneratedPatterns().contains(pattern);
	}

	/**
	 * Unregisters the given pattern from the registry.
	 * 
	 * @param patternFqn
	 *            the fully qualified name of the pattern
	 */
	public void unregisterPattern(String patternFqn) {
		Pattern pattern = this.patternNameMap.get(patternFqn);
		if (pattern != null) {
			this.patternNameMap.remove(patternFqn);
		}
	}

	/**
	 * Registers the patterns within the given (parsed) pattern model.
	 * 
	 * @param file
	 *            the eiq file instance
	 * @param pm
	 *            the parsed pattern model
	 * @return the list of patterns registered
	 */
	public List<Pattern> registerPatternModel(IFile file, PatternModel pm) {
		this.registeredPatterModels.put(file, pm);
		List<Pattern> newPatterns = new ArrayList<Pattern>();

		if (pm != null) {
			for (Pattern p : pm.getPatterns()) {
				Annotation annotation = DatabindingUtil.getAnnotation(p,
						DatabindingUtil.OFF_ANNOTATION);

				if (annotation == null) {
					String patternFqn = CorePatternLanguageHelper
							.getFullyQualifiedName(p);
					patternNameMap.put(patternFqn, p);
					newPatterns.add(p);
					activePatterns.add(p);
				}
			}
		}

		return newPatterns;
	}

	/**
	 * Sets the given pattern as active.
	 * 
	 * @param p
	 *            the pattern instance
	 */
	public void addActivePattern(Pattern p) {
		//list must be used to retain ordering but duplicate elements are not allowed
		if (!activePatterns.contains(p)) {
			activePatterns.add(p);
		}
	}

	public PatternModel getPatternModelForFile(IFile file) {
		return registeredPatterModels.get(file);
	}

	/**
	 * Returns true if there are no (generic) patterns registered, false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return registeredPatterModels.isEmpty();
	}

	/**
	 * Unregisters the patterns within the given eiq file.
	 * 
	 * @param file
	 *            the eiq file instance
	 * @return the set of removed patterns
	 */
	public Set<Pattern> unregisterPatternModel(IFile file) {
		Set<Pattern> removedPatterns = new HashSet<Pattern>();
		PatternModel pm = this.registeredPatterModels.remove(file);

		if (pm != null) {
			for (Pattern p : pm.getPatterns()) {
				String patternFqn = CorePatternLanguageHelper
						.getFullyQualifiedName(p);
				if (activePatterns.remove(p)) {
					removedPatterns.add(p);
				}
				patternNameMap.remove(patternFqn);
			}
		}

		return removedPatterns;
	}

	/**
	 * Sets the given pattern as passive.
	 * 
	 * @param p
	 *            the pattern instance
	 */
	public void removeActivePattern(Pattern p) {
		activePatterns.remove(p);
	}

	/**
	 * Returns the pattern associated with the given fully qualified name.
	 * 
	 * @param patternFqn
	 *            the fqn of the pattern
	 * @return the pattern instance
	 */
	public Pattern getPatternByFqn(String patternFqn) {
		return patternNameMap.get(patternFqn);
	}

	/**
	 * Returns the list of active patterns.
	 * 
	 * @return the list of active patterns
	 */
	public List<Pattern> getActivePatterns() { 
		return new ArrayList<Pattern>(activePatterns);
	}

	/**
	 * Returns true if the given pattern is currently active, false otherwise.
	 * 
	 * @param patternFqn
	 *            the fqn of the pattern
	 * @return true if the pattern is active, false otherwise
	 */
	public boolean isActive(String patternFqn) {
		for (Pattern p : activePatterns) {
			if (CorePatternLanguageHelper.getFullyQualifiedName(p).matches(
					patternFqn)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the names of the patterns registered in the registry.
	 * 
	 * @return the list of names of the patterns
	 */
	public List<String> getPatternNames() {
		List<String> patterns = new ArrayList<String>();
		patterns.addAll(patternNameMap.keySet());
		return patterns;
	}

	/**
	 * Returns the list of (generic) patterns registered in the registry.
	 * 
	 * @return the list of (generic) patterns registered
	 */
	public List<Pattern> getPatterns() {
		List<Pattern> patterns = new ArrayList<Pattern>();
		for (PatternModel pm : registeredPatterModels.values()) {
			patterns.addAll(pm.getPatterns());
		}
		return patterns;
	}

	/**
	 * Returns the list of eiq files from which patterns are registered.
	 * 
	 * @return the list of eiq files
	 */
	public List<IFile> getFiles() {
		List<IFile> files = new ArrayList<IFile>();
		files.addAll(registeredPatterModels.keySet());
		return files;
	}

	/**
	 * Returns the eiq file instance that the given pattern can be found in.
	 * 
	 * @param pattern
	 *            the pattern instance
	 * @return the eiq file
	 */
	public IFile getFileForPattern(Pattern pattern) {
		if (pattern != null && patternNameMap.containsValue(pattern)) {
			for (Entry<IFile, PatternModel> entry : registeredPatterModels
					.entrySet()) {
				EList<Pattern> patterns = entry.getValue().getPatterns();
				if (patterns.size() > 0 && patterns.contains(pattern)) {
					return entry.getKey();
				}
			}
		}
		return null;
	}
}
