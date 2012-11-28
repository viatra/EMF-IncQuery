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
package org.eclipse.incquery.runtime.api;

/**
 * A default implementation of {@link IMatchUpdateListener} that contains two match processors, one for appearance, one for disappearance. 
 * Any of the two can be null; in this case, corresponding notifications will be ignored.
 * 
 * <p>Instantiate using either constructor.
 * 
 * @author Bergmann Gabor
 *
 */
public class MatchUpdateAdapter<Match extends IPatternMatch> implements IMatchUpdateListener<Match> {

	IMatchProcessor<Match> appearCallback;
	IMatchProcessor<Match> disappearCallback;
	
	
	/**
	 * Constructs an instance without any match processors registered yet. 
	 * 
	 * Use {@link #setAppearCallback(IMatchProcessor)} and {@link #setDisappearCallback(IMatchProcessor)} 
	 *   to specify optional match processors for match appearance and disappearance, respectively.
	 */
	public MatchUpdateAdapter() {
		super();
	}


	/**
	 * Constructs an instance by specifying match processors.
	 * 
	 * @param appearCallback a match processor that will be invoked on each new match that appears. 
	 *  If null, no callback will be executed on match appearance. See {@link IMatchProcessor} for details on how to implement. 
	 * @param disappearCallback a match processor that will be invoked on each existing match that disappears. 
	 *  If null, no callback will be executed on match disappearance. See {@link IMatchProcessor} for details on how to implement. 
	 */
	public MatchUpdateAdapter(
			IMatchProcessor<Match> appearCallback,
			IMatchProcessor<Match> disappearCallback) {
		super();
		setAppearCallback(appearCallback);
		setDisappearCallback(disappearCallback);
	}


	/**
	 * @return the match processor that will be invoked on each new match that appears. 
	 *  If null, no callback will be executed on match appearance.
	 */
	public IMatchProcessor<Match> getAppearCallback() {
		return appearCallback;
	}


	/**
	 * @param appearCallback a match processor that will be invoked on each new match that appears. 
	 *  If null, no callback will be executed on match appearance. See {@link IMatchProcessor} for details on how to implement. 
	 */
	public void setAppearCallback(IMatchProcessor<Match> appearCallback) {
		this.appearCallback = appearCallback;
	}


	/**
	 * @return the match processor that will be invoked on each existing match that disappears. 
	 *  If null, no callback will be executed on match disappearance.
	 */
	public IMatchProcessor<Match> getDisappearCallback() {
		return disappearCallback;
	}


	/**
	 * @param disappearCallback a match processor that will be invoked on each existing match that disappears. 
	 *  If null, no callback will be executed on match disappearance. See {@link IMatchProcessor} for details on how to implement. 
	 */
	public void setDisappearCallback(IMatchProcessor<Match> disappearCallback) {
		this.disappearCallback = disappearCallback;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IMatchUpdateListener#notifyAppearance(org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch)
	 */
	public void notifyAppearance(Match match) {
		if (appearCallback != null) appearCallback.process(match);
	};
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IMatchUpdateListener#notifyDisappearance(org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch)
	 */
	public void notifyDisappearance(Match match) {
		if (disappearCallback != null) disappearCallback.process(match);
	};
	
}
