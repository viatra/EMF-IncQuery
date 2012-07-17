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

package org.eclipse.viatra2.emf.incquery.runtime.api;

import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * This is a generic factory for EMF-IncQuery pattern matchers, for "interpretative" query execution.
 * Instantiate the factory with any registered pattern, 
 *  and then use the factory to obtain an actual pattern matcher operating on a given model. 
 * 
 * <p>When available, consider using the pattern-specific generated matcher API instead.
 *  
 * <p>The created matcher will be of type GenericPatternMatcher. 
 * Matches of the pattern will be represented as GenericPatternMatch. 
 * 
 * @see GenericPatternMatcher
 * @see GenericPatternMatch
 * @see GenericMatchProcessor
 * @author Bergmann Gábor
 */
@SuppressWarnings("unused")
public class GenericMatcherFactory extends BaseMatcherFactory<GenericPatternMatcher> 
	implements IMatcherFactory<GenericPatternMatcher>
{
	public Pattern pattern;
	
	/**
	 * Initializes a generic pattern factory for a given pattern.
	 * @param patternName the name of the pattern for which matchers are to be constructed.
	 */
	public GenericMatcherFactory(Pattern pattern) {
		super();
		this.pattern = pattern;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory#getPattern()
	 */
	@Override
	public Pattern getPattern() {
		return pattern;
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseMatcherFactory#instantiate(org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine)
	 */
	@Override
	public GenericPatternMatcher instantiate(IncQueryEngine engine) throws IncQueryRuntimeException {
		return new GenericPatternMatcher(pattern, engine);
	}


}
