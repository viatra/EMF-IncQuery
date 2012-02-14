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

package org.eclipse.viatra2.patternlanguage.emf.matcherbuilder.runtime;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.viatra2.emf.incquery.runtime.api.GenericMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.BuilderRegistry;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.emf.matcherbuilder.internal.EPMStatelessReteBuilder;

/**
 * @author Bergmann Gábor
 *
 */
public class PatternRegistry {
	public static PatternRegistry INSTANCE = new PatternRegistry();
	
	private Map<String, Pattern> patternByFQN = new HashMap<String, Pattern>();
	private EPMStatelessReteBuilder builder = new EPMStatelessReteBuilder(this);
	
	

	public Pattern findPattern(String fqn) {
		return patternByFQN.get(fqn);
	}
	
	public IMatcherFactory<GenericPatternMatch, GenericPatternMatcher> getMatcherFactory(String fqn) {
		if (findPattern(fqn) == null) return null;
		return new GenericMatcherFactory(fqn);
	}
	public IMatcherFactory<GenericPatternMatch, GenericPatternMatcher> getMatcherFactory(Pattern pattern) {
		String fqn = fqnOf(pattern);
		if (findPattern(fqn) == null) registerSingle(pattern); // TODO handle dependencies?
		return new GenericMatcherFactory(fqn);
	}
	
	/**
	 * Dependencies are not resolved yet.
	 * @param pattern
	 */
	public void registerSingle(Pattern pattern) {
		String patternFQN = fqnOf(pattern);
		
		patternByFQN.put(patternFQN, pattern);
		BuilderRegistry.registerStatelessPatternBuilder(patternFQN, builder);
	}
	/**
	 * Dependencies are not resolved yet.
	 * @param pattern
	 */
	public void registerAllInModel(PatternModel patternModel) {
		for (Pattern pattern : patternModel.getPatterns()) {
			registerSingle(pattern);
		}
	}
	
	/**
	 * Returns the fully qualified name of a pattern
	 * TODO this code duplicates PatternNameProvider.java from the patternlanguage.core project
	 * @param pattern
	 * @return the fully qualified name of the pattern
	 */
	public static String fqnOf(Pattern pattern) {
		PatternModel model = (PatternModel) pattern.eContainer();
		return model.getPackageName() + "." + pattern.getName();
	}

}
