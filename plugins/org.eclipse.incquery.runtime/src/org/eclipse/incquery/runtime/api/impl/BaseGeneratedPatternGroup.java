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
package org.eclipse.incquery.runtime.api.impl;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IMatcherFactory;

/**
 * @author Mark Czotter
 *
 */
public abstract class BaseGeneratedPatternGroup extends BasePatternGroup {

	@Override
	public Set<Pattern> getPatterns() {
		return patterns(matcherFactories);
	}

	/**
	 * Returns {@link IMatcherFactory} objects for handling them as a group. 
	 * To be filled by constructors of subclasses.
	 */
	protected Set<IMatcherFactory<?>> matcherFactories = new HashSet<IMatcherFactory<?>>();
}
