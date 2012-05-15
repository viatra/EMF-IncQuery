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
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Interface for an EMF-IncQuery matcher associated with a graph pattern.
 * 
 * @param <Match> the IPatternMatch type representing a single match of this pattern.
 * @author Bergmann Gábor
 */
public interface IncQueryMatcher<Match extends IPatternMatch> {
	// REFLECTION
	/** The pattern that will be matched. */
	public abstract Pattern getPattern();
	/** Fully qualified name of the pattern. */
	public abstract String getPatternName();
	/** Returns the index of the symbolic parameter with the given name. */
	public abstract Integer getPositionOfParameter(String parameterName);
	/** Returns the array of symbolic parameter names. */
	public abstract String[] getParameterNames();
	
	// ALL MATCHES
	/** 
	 * Returns the set of all pattern matches. 
	 * @return matches represented as a Match object.
	 */
	public abstract Collection<Match> getAllMatches();	
	/** 
	 * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
	 * @param partialMatch a partial match of the pattern where each non-null field binds the corresponding pattern parameter to a fixed value. 
	 * @return matches represented as a Match object.
	 */
	public abstract Collection<Match> getAllMatches(Match partialMatch);
	// variant(s) with input binding as pattern-specific parameters: not declared in interface
		
	// SINGLE MATCH
	/** 
	 * Returns an arbitrarily chosen pattern match.
	 * Neither determinism nor randomness of selection is guaranteed.  
	 * @return a match represented as a Match object, or null if no match is found.
	 */
	public abstract Match getOneArbitraryMatch();
	/** 
	 * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
	 * Neither determinism nor randomness of selection is guaranteed.
	 * @param partialMatch a partial match of the pattern where each non-null field binds the corresponding pattern parameter to a fixed value. 
	 * @return a match represented as a Match object, or null if no match is found.
	 */
	public abstract Match getOneArbitraryMatch(Match partialMatch);
	// variant(s) with input binding as pattern-specific parameters: not declared in interface
	
	// MATCH CHECKING
	/**
	 * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
	 * 	under any possible substitution of the unspecified parameters (if any).
	 * @param partialMatch a (partial) match of the pattern where each non-null field binds the corresponding pattern parameter to a fixed value. 
	 * @return true if the input is a valid (partial) match of the pattern.
	 */
	public abstract boolean hasMatch(Match partialMatch);
	// variant(s) with input binding as pattern-specific parameters: not declared in interface

	// NUMBER OF MATCHES
	/** 
	 * Returns the number of all pattern matches.
	 * @return the number of pattern matches found.
	 */
	public abstract int countMatches();
	/** 
	 * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
	 * @param partialMatch a partial match of the pattern where each non-null field binds the corresponding pattern parameter to a fixed value. 
	 * @return the number of pattern matches found.
	 */
	public abstract int countMatches(Match partialMatch);
	// variant(s) with input binding as pattern-specific parameters: not declared in interface

	// FOR EACH MATCH
	/** 
	 * Executes the given processor on each match of the pattern. 
	 * @param action the action that will process each pattern match. 
	 */
	public abstract void forEachMatch(IMatchProcessor<? super Match> processor);
	/** 
	 * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
	 * @param parameters array where each non-null element binds the corresponding pattern parameter to a fixed value. 
	 * @param processor the action that will process each pattern match. 
	 */
	public abstract void forEachMatch(Match partialMatch, IMatchProcessor<? super Match> processor);
	// variant(s) with input binding as pattern-specific parameters: not declared in interface			

	// FOR ONE ARBITRARY MATCH
	/** 
	 * Executes the given processor on an arbitrarily chosen match of the pattern.  
	 * Neither determinism nor randomness of selection is guaranteed.
	 * 
	 * @param processor the action that will process the selected match. 
	 * @return true if the pattern has at least one match, false if the processor was not invoked
	 */
	public abstract boolean forOneArbitraryMatch(IMatchProcessor<? super Match> processor);
	/** 
	 * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.  
	 * Neither determinism nor randomness of selection is guaranteed.
	 * 
	 * @param parameters array where each non-null element binds the corresponding pattern parameter to a fixed value. 
	 * @param processor the action that will process the selected match. 
	 * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
	 */
	public abstract boolean forOneArbitraryMatch(Match partialMatch, IMatchProcessor<? super Match> processor);
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
	 * Registers a new filtered delta monitor on this pattern matcher.
	 * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, 
	 *   considering those matches only that conform to the given fixed values of some parameters. 
	 * It can also be reset to track changes from a later point in time, 
	 * and changes can even be acknowledged on an individual basis. 
	 * See {@link DeltaMonitor} for details.
	 * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
	 * @param partialMatch a partial match of the pattern where each non-null field binds the corresponding pattern parameter to a fixed value. 
	 * @return the delta monitor.
	 */
	public abstract DeltaMonitor<Match> newFilteredDeltaMonitor(boolean fillAtStart, Match partialMatch);
	/**
	 * Registers a callback that will be run each time EMF-IncQuery match sets are refreshed after a model update.
	 * Typically useful to check delta monitors. 
	 * When the callback is issued, the pattern match sets are guaranteed to reflect the post-state after the update.
	 * <p> Callbacks are issued after each elementary change (i.e. possibly at incomplete transient states).
	 * This can have a negative effect on performance, therefore clients are advised to use it as a last resort only.
	 *   Consider coarser-grained timing (e.g EMF Transaction pre/post-commit) instead, whenever available.	
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
	
	// ARRAY-BASED INTERFACE
	
	/** Converts the array representation of a pattern match to a Match object. */
	public Match arrayToMatch(Object[] parameters);
	/** Converts the Match object of a pattern match to the array representation. */
	public Object[] matchToArray(Match partialMatch);
	
	/** 
	 * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
	 * @param parameters array where each non-null element binds the corresponding pattern parameter to a fixed value. 
	 * @pre size of input array must be equal to the number of parameters.
	 * @return matches represented as a Match object.
	 */
	public abstract Collection<Match> rawGetAllMatches(Object[] parameters);
	/** 
	 * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
	 * Neither determinism nor randomness of selection is guaranteed.
	 * 
	 * @param parameters array where each non-null element binds the corresponding pattern parameter to a fixed value. 
	 * @pre size of input array must be equal to the number of parameters. 
	 * @return a match represented as a Match object, or null if no match is found.
	 */
	public abstract Match rawGetOneArbitraryMatch(Object[] parameters);
	/**
	 * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
	 * 	under any possible substitution of the unspecified parameters.
	 * @param parameters array where each non-null element binds the corresponding pattern parameter to a fixed value. 
	 * @return true if the input is a valid (partial) match of the pattern.
	 */
	public abstract boolean rawHasMatch(Object[] parameters);
	/** 
	 * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
	 * @param parameters array where each non-null element binds the corresponding pattern parameter to a fixed value. 
	 * @pre size of input array must be equal to the number of parameters. 
	 * @return the number of pattern matches found.
	 */
	public abstract int rawCountMatches(Object[] parameters);
	/** 
	 * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
	 * @param parameters array where each non-null element binds the corresponding pattern parameter to a fixed value. 
	 * @pre size of input array must be equal to the number of parameters.
	 * @param action the action that will process each pattern match. 
	 */
	public abstract void rawForEachMatch(Object[] parameters, IMatchProcessor<? super Match> processor);
	/** 
	 * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.  
	 * Neither determinism nor randomness of selection is guaranteed.
	 * 
	 * @param parameters array where each non-null element binds the corresponding pattern parameter to a fixed value. 
	 * @pre size of input array must be equal to the number of parameters.
	 * @param processor the action that will process the selected match. 
	 * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
	 */
	public abstract boolean rawForOneArbitraryMatch(Object[] parameters, IMatchProcessor<? super Match> processor);
	/** 
	 * Registers a new filtered delta monitor on this pattern matcher.
	 * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, 
	 *   considering those matches only that conform to the given fixed values of some parameters. 
	 * It can also be reset to track changes from a later point in time, 
	 * and changes can even be acknowledged on an individual basis. 
	 * See {@link DeltaMonitor} for details.
	 * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
	 * @param parameters array where each non-null element binds the corresponding pattern parameter to a fixed value. 
	 * @return the delta monitor.
	 */
	public abstract DeltaMonitor<Match> rawNewFilteredDeltaMonitor(boolean fillAtStart, final Object[] parameters);
}