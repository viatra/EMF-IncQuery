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

import java.util.Set;

import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternGroup;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Generic implementation of {@link BasePatternGroup}.
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
	
}
