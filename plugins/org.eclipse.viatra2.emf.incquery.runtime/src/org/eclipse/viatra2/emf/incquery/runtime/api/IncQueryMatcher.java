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

import java.util.Collection;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.misc.DeltaMonitor;

/**
 * Interface for an EMF-IncQuery matcher associated with a graph pattern.
 * 
 * @param <Match> the IPatternMatch type representing a single match of this pattern.
 * @author Bergmann Gábor
 */
public interface IncQueryMatcher<Match extends IPatternMatch> {
	// REFLECTION
	/** Fully qualified name of pattern. */
	public abstract String getPatternName();
	/** Returns the index of the symbolic parameter with the given name. */
	public abstract Integer getPositionOfParameter(String parameterName);
	/** Returns the array of symbolic parameter names. */
	public abstract String[] getParameterNames();
	
	// ALL MATCHES
	// variant(s) without input parameter
	/** 
	 * Returns the set of all pattern matches. 
	 * @return matches represented as an array containing the values of each parameters.
	 */
	public abstract Collection<Object[]> getAllMatchesAsArray();
	/** 
	 * Returns the set of all pattern matches. 
	 * @return matches represented as a Signature object.
	 */
	public abstract Collection<Match> getAllMatches();	
	// variant(s) with input binding as array
	/** 
	 * Returns the set of all pattern matches given some fixed parameters.
	 * @param parameters array where each non-null element binds the corresponding pattern parameter to a fixed value. 
	 * @pre size of input array must be equal to the number of parameters. 
	 * @return matches represented as an array containing the values of each parameter.
	 */
	public abstract Collection<Object[]> getAllMatchesAsArray(Object[] parameters);
	/** 
	 * Returns the set of all pattern matches given some fixed parameters.
	 * @param parameters array where each non-null element binds the corresponding pattern parameter to a fixed value. 
	 * @pre size of input array must be equal to the number of parameters.
	 * @return matches represented as a Signature object.
	 */
	public abstract Collection<Match> getAllMatches(Object[] parameters);
	// variant(s) with input binding as partial match object
	/** 
	 * Returns the set of all pattern matches given some fixed parameters.
	 * @param match a partial match of the pattern where each non-null field binds the corresponding pattern parameter to a fixed value. 
	 * @return matches represented as an array containing the values of each parameter.
	 */
	public abstract Collection<Object[]> getAllMatchesAsArray(Match match);
	/** 
	 * Returns the set of all pattern matches given some fixed parameters.
	 * @param match a partial match of the pattern where each non-null field binds the corresponding pattern parameter to a fixed value. 
	 * @return matches represented as a Signature object.
	 */
	public abstract Collection<Match> getAllMatches(Match match);
	// variant(s) with input binding as pattern-specific parameters: not declared in interface
		
	// SINGLE MATCH
	// variant(s) without input parameter
	/** 
	 * Returns an arbitrary pattern match.
	 * Neither determinism nor randomness of selection is guaranteed.  
	 * @return a match represented as an array containing the values of each parameter, or null if no match is found.
	 */
	public abstract Object[] getOneMatchAsArray();
	/** 
	 * Returns an arbitrary pattern match.
	 * Neither determinism nor randomness of selection is guaranteed.  
	 * @return a match represented as a Signature object, or null if no match is found.
	 */
	public abstract Match getOneMatch();
	// variant(s) with input binding as array
	/** 
	 * Returns an arbitrary pattern match given some fixed parameters.
	 * Neither determinism nor randomness of selection is guaranteed.
	 * @param parameters array where each non-null element binds the corresponding pattern parameter to a fixed value. 
	 * @pre size of input array must be equal to the number of parameters. 
	 * @return a match represented as an array containing the values of each parameter, or null if no match is found.
	 */
	public abstract Object[] getOneMatchAsArray(Object[] parameters);
	/** 
	 * Returns an arbitrary pattern match given some fixed parameters.
	 * Neither determinism nor randomness of selection is guaranteed.
	 * @param parameters array where each non-null element binds the corresponding pattern parameter to a fixed value. 
	 * @pre size of input array must be equal to the number of parameters. 
	 * @return a match represented as a Signature object, or null if no match is found.
	 */
	public abstract Match getOneMatch(Object[] parameters);
	// variant(s) with input binding as partial match object
	/** 
	 * Returns an arbitrary pattern match given some fixed parameters.
	 * Neither determinism nor randomness of selection is guaranteed.
	 * @param match a partial match of the pattern where each non-null field binds the corresponding pattern parameter to a fixed value. 
	 * @return a match represented as an array containing the values of each parameter, or null if no match is found.
	 */
	public abstract Object[] getOneMatchAsArray(Match match);
	/** 
	 * Returns an arbitrary pattern match given some fixed parameters.
	 * Neither determinism nor randomness of selection is guaranteed.
	 * @param match a partial match of the pattern where each non-null field binds the corresponding pattern parameter to a fixed value. 
	 * @return a match represented as a Signature object, or null if no match is found.
	 */
	public abstract Match getOneMatch(Match match);
	// variant(s) with input binding as pattern-specific parameters: not declared in interface
	
	// MATCH CHECKING
	// variant(s) with input binding as array
	/**
	 * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
	 * 	under any possible substitution of the unspecified parameters.
	 * @param parameters array where each non-null element binds the corresponding pattern parameter to a fixed value. 
	 * @return true if the input is a valid (partial) match of the pattern.
	 */
	public abstract boolean hasMatch(Object[] parameters);
	// variant(s) with input binding as partial match object
	/**
	 * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
	 * 	under any possible substitution of the unspecified parameters.
	 * @param match a partial match of the pattern where each non-null field binds the corresponding pattern parameter to a fixed value. 
	 * @return true if the input is a valid (partial) match of the pattern.
	 */
	public abstract boolean hasMatch(Match match);
	// variant(s) with input binding as pattern-specific parameters: not declared in interface

	// NUMBER OF MATCHES
	// variant(s) without input parameter
	/** 
	 * Returns the number of all pattern matches.
	 * @return the number of pattern matches found.
	 */
	public abstract int countMatches();
	// variant(s) with input binding as array
	/** 
	 * Returns the number of all pattern matches given some fixed parameters.
	 * @param parameters array where each non-null element binds the corresponding pattern parameter to a fixed value. 
	 * @pre size of input array must be equal to the number of parameters. 
	 * @return the number of pattern matches found.
	 */
	public abstract int countMatches(Object[] parameters);
	// variant(s) with input binding as partial match object
	/** 
	 * Returns the number of all pattern matches given some fixed parameters.
	 * @param match a partial match of the pattern where each non-null field binds the corresponding pattern parameter to a fixed value. 
	 * @return the number of pattern matches found.
	 */
	public abstract int countMatches(Match match);
	// variant(s) with input binding as pattern-specific parameters: not declared in interface
	
	// CHANGE MONITORING
	// attach delta monitor for high-level change detection
	/** 
	 * Registers a new delta monitor on this pattern matcher. 
	 * The DeltaMonitor can be used to track changes (delta) in the set of pattern matches from now on.
	 * It can also be reset to track changes from a later point in time, 
	 * and changes can even be acknowledged on an individual basis. 
	 * See {@link DeltaMonitor} for details.
	 * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty. 
	 * @return the delta monitor.
	 */
	public abstract DeltaMonitor<Match> newDeltaMonitor(boolean fillAtStart);
	/**
	 * Registers a callback that will be run each time EMF-IncQuery match sets are refreshed after a model update.
	 * Typically useful to check delta monitors. 
	 * When the callback is issued, the pattern match sets are guaranteed to reflect the post-state after the update.
	 * <p> If the pattern matcher is registered on an EMF TransactionalEditingDomain,
	 * 	callbacks are issued after each transaction.
	 * However if the pattern matcher is registered on an EMF Notifier (typically Resource or ResourceSet),
	 * 	callbacks are issued after each elementary change (i.e. possibly at incomplete transient states).
	 *  Therefore in this case, some higher-level source of invocation is preferable.	
	 * @param callback a Runnable to execute after each update.
	 * @return false if the callback was already registered.
	 */
	public boolean addCallbackAfterUpdates(Runnable callback);
	/**
	 * Removes a previously registered callback. See addCallbackAfterUpdates().
	 * @param callback the callback to remove.
	 * @return false if the callback was not registered.
	 */
	public boolean removeCallbackAfterUpdates(Runnable callback);
	
	// HELPER
	/** Converts the array representation of a pattern match to a signature object. */
	public Match arrayToSignature(Object[] parameters);
	/** Converts the signature object of a pattern match to the array representation. */
	public Object[] signatureToArray(Match match);

}