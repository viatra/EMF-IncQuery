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

package org.eclipse.viatra2.emf.incquery.runtime.api.impl;

import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Provides common functionality of pattern-specific generated matcher factories.
 * @author Bergmann Gábor
 *
 */
public abstract class BaseGeneratedMatcherFactory<Signature extends IPatternMatch, Matcher extends BaseGeneratedMatcher<Signature>>
		extends BaseMatcherFactory<Signature, Matcher> 
{
	private Pattern pattern;
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory#getPattern()
	 */
	@Override
	public Pattern getPattern() {
		if (pattern == null) 
			pattern = parsePattern();
		return pattern;
	}
	
	protected abstract Pattern parsePattern();

}
