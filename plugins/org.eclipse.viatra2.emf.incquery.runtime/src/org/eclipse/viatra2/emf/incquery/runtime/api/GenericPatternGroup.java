/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.runtime.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternGroup;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Generic implementation of {@link IPatternGroup}, covering an arbitrarily chosen set of patterns. 
 * Use the public constructor or static GenericPatternGroup.of(...) methods to instantiate.
 * 
 * @author Mark Czotter
 * 
 */
public class GenericPatternGroup extends BasePatternGroup {
	
	private Set<Pattern> patterns;
	
	/**
	 * Creates a GenericPatternGroup object with a set of patterns.
	 * 
	 * @param patterns
	 */
	public GenericPatternGroup(Set<Pattern> patterns) {
		this.patterns = patterns;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IPatternGroup#getPatterns()
	 */
	@Override
	public Set<Pattern> getPatterns() {
		return patterns;
	}
	
	/**
	 * Creates a generic {@link IPatternGroup} instance from
	 * {@link IMatcherFactory} objects.
	 * 
	 * @param matcherFactories
	 * @return
	 */
	public static IPatternGroup of(Set<IMatcherFactory> matcherFactories) {
		return new GenericPatternGroup(patterns(matcherFactories));
	}

	/**
	 * Creates a generic {@link IPatternGroup} instance from
	 * {@link Pattern} objects.
	 * 
	 * @param matcherFactories
	 * @return
	 */
	public static IPatternGroup of(Pattern... patterns) {
		return new GenericPatternGroup(new HashSet<Pattern>(Arrays.asList(patterns)));
	}	

	/**
	 * Creates a generic {@link IPatternGroup} instance from
	 * {@link IMatcherFactory} objects.
	 * 
	 * @param matcherFactories
	 * @return
	 */
	public static IPatternGroup of(IMatcherFactory... matcherFactories) {
		return of(new HashSet<IMatcherFactory>(Arrays.asList(matcherFactories)));
	}	

	/**
	 * Creates a generic {@link IPatternGroup} instance from other
	 * {@link IPatternGroup} objects (subgroups).
	 * 
	 * @param matcherFactories
	 * @return
	 */
	public static IPatternGroup of(IPatternGroup... subGroups) {
		Set<Pattern> patterns = new HashSet<Pattern>();
		for (IPatternGroup group : subGroups) {
			patterns.addAll(group.getPatterns());
		}
		return new GenericPatternGroup(patterns);
	}	
}

