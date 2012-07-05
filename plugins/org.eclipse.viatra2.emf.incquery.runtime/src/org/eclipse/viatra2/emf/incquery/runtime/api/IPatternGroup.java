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

import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Generic interface for group of patterns.
 * 
 * It handles more than one patterns as a group, and provides functionality to
 * initialize the patterns together.
 * 
 * @author Mark Czotter
 * 
 */
public interface IPatternGroup {

	/**
	 * Initializes the currently assigned patterns within an
	 * {@link IncQueryEngine}.
	 * 
	 * @param engine
	 */
	public void prepare(IncQueryEngine engine);

	/**
	 * Returns the currently assigned {@link Pattern}s.
	 * 
	 * @return
	 */
	public Set<Pattern> getPatterns();

}
