/*******************************************************************************
 * Copyright (c) 2010-2012, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.runtime.api;


/**
 * An interface for low-level notifications about match appearance and disappearance.
 * 
 * <p> See {@link IncQueryMatcher#addCallbackOnMatchUpdate(IMatchUpdateListener, boolean)} for usage. 
 * Clients should consider using {@link MatchUpdateAdapter} or deriving their implementation from it.
 * 
 * @author Bergmann Gabor
 *
 */
public interface IMatchUpdateListener<Match extends IPatternMatch> {
	/**
	 * Will be invoked on each new match that appears.
	 * @param match the match that has just appeared.
	 */
	public void notifyAppearance(Match match);
	/**
	 * Will be invoked on each existing match that disappears.
	 * @param match the match that has just disappeared.
	 */
	public void notifyDisappearance(Match match);
}
