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
package org.eclipse.viatra2.emf.incquery.runtime.api.impl;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternGroup;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Base implementation of {@link IPatternGroup}.
 * 
 * @author Mark Czotter
 * 
 */
public abstract class BasePatternGroup implements IPatternGroup {
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IPatternGroup#prepare(org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine)
	 */
	@Override
	public void prepare(IncQueryEngine engine) throws IncQueryRuntimeException {
		try {
			engine.getReteEngine().buildMatchersCoalesced(getPatterns());
		} catch (RetePatternBuildException e) {
			throw new IncQueryRuntimeException(e);
		}
	}
	
	/**
	 * Returns a set of {@link Pattern} objects, accessible from the
	 * {@link IMatcherFactory} objects.
	 * 
	 * @see IMatcherFactory#getPattern()
	 * @param matcherFactories
	 * @return
	 */
	public static Set<Pattern> patterns(Set<IMatcherFactory<?>> matcherFactories) {
		Set<Pattern> result = new HashSet<Pattern>();
		for (IMatcherFactory<?> factory : matcherFactories) {
			result.add(factory.getPattern());
		}
		return result;
	}
	
}
