/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.runtime.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra2.emf.incquery.runtime.extensibility.EMFIncQueryRuntimeLogger;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Admitted patterns go through sanitization checks (validation + name uniqueness).
 * 
 * @author Bergmann Gábor
 *
 */
public class PatternSanitizer {
	Set<Pattern> admittedPatterns = new HashSet<Pattern>();
	Map<String, Pattern> patternsByName = new HashMap<String, Pattern>();
	
//	PatternValidatorService validator;
	
	EMFIncQueryRuntimeLogger logger;
	EMFIncQueryRuntimeLogger errorOnlyLogger;
	

	/**
	 * Creates an instance of the sanitizer. 
	 * (Qualified) name conflicts will be detected within the lifecycle of the instance.  
	 * 
	 * @param logger where detected probems will be logged
	 */
	public PatternSanitizer(final EMFIncQueryRuntimeLogger logger) {
		super();

		this.logger = logger;
		this.errorOnlyLogger = new EMFIncQueryRuntimeLogger() {
			
			@Override
			public void logError(String message, Throwable cause) {
				logger.logError(message, cause);
			}
			
			@Override
			public void logError(String message) {
				logger.logError(message);
			}
			
			@Override
			public void logWarning(String message, Throwable cause) {}
			
			@Override
			public void logWarning(String message) {}
			
			@Override
			public void logDebug(String message) {}
		};
		
//		try {
//			this.validator = XtextInjectorProvider.INSTANCE.getInjector().getInstance(PatternValidatorService.class);	
//		} catch (ProvisionException ex) {
//			logger.logError("Can not properly sanitize patterns as validation service is unavailable.", ex);
//		} catch (ConfigurationException ex) {
//			logger.logError("Can not properly sanitize patterns as validation service is unavailable.", ex);			
//		}
	}
	

		
		
	/**
	 * Admits a new pattern, checking if it passes validation and name uniqueness checks. 
	 * Referenced patterns likewise go through the checks.
	 * 
	 * @param pattern a pattern that should be validated.
	 * @return false if the pattern was not possible to admit, true if it passed all validation checks (or was already admitted before)
	 */
	public boolean admit(Pattern pattern) {
		return admit(Collections.singletonList(pattern));
	}
	
	/**
	 * Admits new patterns, checking whether they all pass validation and name uniqueness checks.  
	 * Referenced patterns likewise go through the checks. 
	 * Will only admit any patterns if none of them have any errors.
	 * 
	 * @param patterns the collection of patterns that should be validated together.
	 * @return false if the patterns were not possible to admit, true if they passed all validation checks (or were already admitted before)
	 */
	public boolean admit(Collection<Pattern> patterns) {		
		Set<Pattern> toBeValidated = new HashSet<Pattern>();

		LinkedList<Pattern> unexplored = new LinkedList<Pattern>();	
		
		for (Pattern pattern : patterns) {
			if (!admittedPatterns.contains(pattern)) unexplored.add(pattern);
		} 
		
		while (!unexplored.isEmpty()) {
			Pattern current = unexplored.pollFirst();
			toBeValidated.add(current);
			final Set<Pattern> referencedPatterns = CorePatternLanguageHelper.getReferencedPatterns(current);
			for (Pattern referenced : referencedPatterns) {
				if (!admittedPatterns.contains(referenced) && !toBeValidated.contains(referenced))
					unexplored.add(referenced);
			}
		}
		
		
		// TODO validate(toBeValidated) as a group
		Set<Pattern> inadmissible = new HashSet<Pattern>();
		Map<String, Pattern> localsByName = new HashMap<String, Pattern>();
//		Set<Pattern> locallyAdmissible = new HashSet<Pattern>();	
		for (Pattern current : toBeValidated) {
			
			final String fullyQualifiedName = CorePatternLanguageHelper.getFullyQualifiedName(current);
			final boolean duplicate = patternsByName.containsKey(fullyQualifiedName) || localsByName.containsKey(fullyQualifiedName);
			if (duplicate) {
				inadmissible.add(current);
				logger.logError("Duplicate (qualified) name of pattern: " + fullyQualifiedName);
			} else {
				localsByName.put(fullyQualifiedName, current);
			}

			// TODO actual validation
			final boolean validationPassed = true; //validator == null || validator.validate(current, errorOnlyLogger);
			if (!validationPassed) {
				inadmissible.add(current);
			}	
			
//			if (validationPassed && !duplicate) { 
//				locallyAdmissible.add(current);
//			}
//			
//			
		}
		
		boolean ok = inadmissible.isEmpty();
		if (ok) {
			admittedPatterns.addAll(toBeValidated);
			patternsByName.putAll(localsByName);
		}
		return ok;
	}
	
	
}
