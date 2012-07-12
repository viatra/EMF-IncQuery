/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.Disconnectable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.IManipulationListener;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.IPredicateTraceListener;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.ReteBoundary;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.Indexer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Library;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Network;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Production;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.remote.Address;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMask;


/**
 * @author Gabor Bergmann
 * 
 */
public class ReteEngine<PatternDescription> {

	protected Network reteNet;
	protected int reteThreads;
	protected ReteBoundary<PatternDescription> boundary;
	
	protected IPatternMatcherRuntimeContext<PatternDescription> context;
	
	protected Collection<Disconnectable> disconnectables;
	protected IManipulationListener manipulationListener;
	protected IPredicateTraceListener traceListener;
	// protected MachineListener machineListener;

	protected Map<PatternDescription, RetePatternMatcher> matchers;
//	protected Map<GTPattern, Map<Map<Integer, Scope>, RetePatternMatcher>> matchersScoped; // (pattern, scopemap) -> matcher
	
	protected IRetePatternBuilder<PatternDescription, Address<? extends Supplier>, Address<? extends Receiver>> builder;

	protected boolean parallelExecutionEnabled; // TRUE if Viatra can go on
												// while RETE does its job.
	
//	protected BlockingQueue<Throwable> caughtExceptions;
		
	/**
	 * @param context the context of the pattern matcher, conveying all information from the outside world.
	 * @param reteThreads the number of threads to operate the RETE network with; 
	 * 	0 means single-threaded operation, 
	 * 	1 starts an asynchronous thread to operate the RETE net, 
	 * 	>1 uses multiple RETE containers.
	 */
	public ReteEngine(IPatternMatcherRuntimeContext<PatternDescription> context, int reteThreads) {
		super();
		this.context = context;
		this.reteThreads = reteThreads;
		// this.framework = new WeakReference<IFramework>(context.getFramework());

		initEngine();

		this.builder = null;
	}

	/**
	 * initializes engine components
	 */
	void initEngine() {
		this.parallelExecutionEnabled = reteThreads > 0;
		
		this.disconnectables = new LinkedList<Disconnectable>();
//		this.caughtExceptions = new LinkedBlockingQueue<Throwable>();

		this.reteNet = new Network(reteThreads);
		this.boundary = new ReteBoundary<PatternDescription>(this); // prerequisite: network

		
		this.matchers = new HashMap<PatternDescription, RetePatternMatcher>();
		/*this.matchersScoped = new HashMap<PatternDescription, Map<Map<Integer,Scope>,RetePatternMatcher>>();*/
		
				
		//prerequisite: network, framework, boundary, disconnectables
		this.manipulationListener = context.subscribePatternMatcherForUpdates(this); 
		// prerequisite: boundary, disconnectables
		this.traceListener = context.subscribePatternMatcherForTraceInfluences(this); 

	}

	/**
	 * deconstructs engine components
	 */
	void deconstructEngine() {
		reteNet.kill();

		for (Disconnectable disc : disconnectables) {
			disc.disconnect();
		}

		this.matchers = null;
		this.disconnectables = null;

		this.reteNet = null;
		this.boundary = null;

		// this.machineListener = new MachineListener(this); // prerequisite:
		// framework, disconnectables
		this.manipulationListener = null;
		this.traceListener = null;

	}

	/**
	 * Deconstructs the engine to get rid of it finally
	 */
	public void killEngine() {
		deconstructEngine();
		// this.framework = null;
		this.builder = null;
	}

	/**
	 * Resets the engine to an after-initialization phase
	 * 
	 */
	public void reset() {
		deconstructEngine();

		initEngine();

		builder.refresh();
	}

	/**
	 * Accesses the patternmatcher for a given pattern, constructs one if
	 * a matcher is not available yet.
	 * 
	 * @pre: builder is set.
	 * @param gtPattern
	 *            the pattern to be matched.
	 * @return a patternmatcher object that can match occurences of the given
	 *         pattern.
	 * @throws RetePatternBuildException
	 *             if construction fails.
	 */
	public synchronized RetePatternMatcher accessMatcher(final PatternDescription gtPattern) throws RetePatternBuildException {
		RetePatternMatcher matcher;
		// String namespace = gtPattern.getNamespace().getName();
		// String name = gtPattern.getName();
		// String fqn = namespace + "." + name;
		matcher = matchers.get(gtPattern);
		if (matcher == null) {
			context.modelReadLock();
			try {
				if (parallelExecutionEnabled) reteNet.getStructuralChangeLock().lock();
				try {
					try {
						context.coalesceTraversals(new Callable<Void>() {	
							@Override
							public Void call() throws RetePatternBuildException {
								Address<? extends Production> prodNode;
								prodNode = boundary.accessProduction(gtPattern);
	
								matchers.put(gtPattern, new RetePatternMatcher(ReteEngine.this, prodNode));
								return null;
							}
						});
					} catch (InvocationTargetException ex) {
						final Throwable cause = ex.getCause();
						if (cause instanceof RetePatternBuildException) throw (RetePatternBuildException) cause;
						if (cause instanceof RuntimeException) throw (RuntimeException) cause;
						assert(false);
					}
				} finally {
					if (parallelExecutionEnabled) reteNet.getStructuralChangeLock().unlock();
					settle();
				}
			} finally {
				context.modelReadUnLock();
			}
			// reteNet.flushUpdates();
			matcher = matchers.get(gtPattern);
		}

		return matcher;
	}

	/**
	 * Constructs RETE pattern matchers for a collection of patterns, if they are not available yet. 
	 * Model traversal during the whole construction period is coalesced (which may have an effect on performance,
	 * depending on the matcher context).
	 * 
	 * @pre: builder is set.
	 * @param patterns the patterns to be matched.
	 * @throws RetePatternBuildException if construction fails.
	 */
	public synchronized void buildMatchersCoalesced(final Collection<PatternDescription> patterns) throws RetePatternBuildException {
		context.modelReadLock();
		try {
			if (parallelExecutionEnabled) reteNet.getStructuralChangeLock().lock();		
			try {
				try {
					context.coalesceTraversals(new Callable<Void>() {	
						@Override
						public Void call() throws RetePatternBuildException {
							for (PatternDescription gtPattern : patterns) {
								boundary.accessProduction(gtPattern);				
							}
							return null;
						}
					});
				} catch (InvocationTargetException ex) {
					final Throwable cause = ex.getCause();
					if (cause instanceof RetePatternBuildException) throw (RetePatternBuildException) cause;
					if (cause instanceof RuntimeException) throw (RuntimeException) cause;
					assert(false);
				}
			} finally {
				if (parallelExecutionEnabled) reteNet.getStructuralChangeLock().unlock();
			}
			settle();
		} finally {
			context.modelReadUnLock();
		}
	}	
	
//	/**
//	 * Accesses the patternmatcher for a given pattern with additional scoping, constructs one if
//	 * a matcher is not available yet.
//	 * 
//	 * @param gtPattern
//	 *            the pattern to be matched.
//	 * @param additionalScopeMap
//	 *            additional, optional scopes for the symbolic parameters 
//	 *            maps the position of the symbolic parameter to its additional scope (if any)
//	 *            @pre: scope.parent is non-root, i.e. this is a nontrivial constraint
//	 *            use the static method RetePatternMatcher.buildAdditionalScopeMap() to create from PatternCallSignature
//	 * @return a patternmatcher object that can match occurences of the given
//	 *         pattern.
//	 * @throws PatternMatcherCompileTimeException
//	 *             if construction fails.
//	 */
//	public synchronized RetePatternMatcher accessMatcherScoped(PatternDescription gtPattern, Map<Integer, Scope> additionalScopeMap)
//			throws PatternMatcherCompileTimeException {
//		if (additionalScopeMap.isEmpty()) return accessMatcher(gtPattern);		
//		
//		RetePatternMatcher matcher;
//		
//		Map<Map<Integer, Scope>, RetePatternMatcher> scopes = matchersScoped.get(gtPattern);
//		if (scopes == null) {
//			scopes = new HashMap<Map<Integer, Scope>, RetePatternMatcher>();
//			matchersScoped.put(gtPattern, scopes);
//		}
//		
//		matcher = scopes.get(additionalScopeMap);
//		if (matcher == null) {
//			context.modelReadLock();
//			try {
//				reteNet.getStructuralChangeLock().lock();
//				try {
//					Address<? extends Production> prodNode;
//					prodNode = boundary.accessProductionScoped(gtPattern, additionalScopeMap);
//
//					matcher = new RetePatternMatcher(this, prodNode);
//					scopes.put(additionalScopeMap, matcher);
//				} finally {
//					reteNet.getStructuralChangeLock().unlock();
//				}
//			} finally {
//				context.modelReadUnLock();
//			}
//			// reteNet.flushUpdates();
//		}
//
//		return matcher;
//	}	
	
	/**
	 * Returns an indexer that groups the contents of this Production node by
	 * their projections to a given mask. Designed to be called by a
	 * RetePatternMatcher.
	 * 
	 * @param production
	 *            the production node to be indexed.
	 * @param mask
	 *            the mask that defines the projection.
	 * @return the Indexer.
	 */
	synchronized Indexer accessProjection(Production production,
			TupleMask mask) {
		Library library = reteNet.getHeadContainer().getLibrary();
		Indexer result = library.peekProjectionIndexer(production, mask);
		if (result == null) {
			context.modelReadLock();
			try {
				if (parallelExecutionEnabled) reteNet.getStructuralChangeLock().lock();
				try {
					result = library.accessProjectionIndexerOnetime(production, mask);
				} finally {
					if (parallelExecutionEnabled) reteNet.getStructuralChangeLock().unlock();
				}
			} finally {
				context.modelReadUnLock();
			}
		}

		return result;
	}

	// /**
	// * Retrieves the patternmatcher for a given pattern fqn, returns null if
	// the matching network hasn't been constructed yet.
	// *
	// * @param fqn the fully qualified name of the pattern to be matched.
	// * @return the previously constructed patternmatcher object that can match
	// occurences of the given pattern, or null if it doesn't exist.
	// */
	// public RetePatternMatcher getMatcher(String fqn)
	// {
	// RetePatternMatcher matcher = matchersByFqn.get(fqn);
	// if (matcher == null)
	// {
	// Production prodNode = boundary.getProduction(fqn);
	//			
	// matcher = new RetePatternMatcher(this, prodNode);
	// matchersByFqn.put(fqn, matcher);
	// }
	//		
	// return matcher;
	// }

	/**
	 * Waits until the pattern matcher is in a steady state and output can be
	 * retrieved.
	 */
	public void settle() {
		reteNet.waitForReteTermination();
	}

	/**
	 * Waits until the pattern matcher is in a steady state and output can be
	 * retrieved. When steady state is reached, a retrieval action is executed
	 * before the steady state ceases.
	 * 
	 * @param action
	 *            the action to be run when reaching the steady-state.
	 */
	public void settle(Runnable action) {
		reteNet.waitForReteTermination(action);
	}

//	/**
//	 * @return the framework
//	 */
//	public IFramework getFramework() {
//		return framework.get();
//	}

	/**
	 * @return the reteNet
	 */
	public Network getReteNet() {
		return reteNet;
	}

	/**
	 * @return the boundary
	 */
	public ReteBoundary<PatternDescription> getBoundary() {
		return boundary;
	}

	// /**
	// * @return the pattern matcher builder
	// */
	// public IRetePatternBuilder getBuilder() {
	// return builder;
	// }

	/**
	 * @param builder
	 *            the pattern matcher builder to set
	 */
	public void setBuilder(IRetePatternBuilder<PatternDescription, Address<? extends Supplier>, Address<? extends Receiver>> builder) {
		this.builder = builder;
	}

	/**
	 * @return the manipulationListener
	 */
	public IManipulationListener getManipulationListener() {
		return manipulationListener;
	}

	/**
	 * @return the traceListener
	 */
	public IPredicateTraceListener geTraceListener() {
		return traceListener;
	}

	/**
	 * @param disc
	 *            the new Disconnectable adapter.
	 */
	public void addDisconnectable(Disconnectable disc) {
		disconnectables.add(disc);
	}

	/**
	 * @return the parallelExecutionEnabled
	 */
	public boolean isParallelExecutionEnabled() {
		return parallelExecutionEnabled;
	}


	/**
	 * @return the context
	 */
	public IPatternMatcherRuntimeContext<PatternDescription> getContext() {
		return context;
	}

	public IRetePatternBuilder<PatternDescription, Address<? extends Supplier>, Address<? extends Receiver>> getBuilder() {
		return builder;
	}

//	/**
//	 * For internal use only: logs exceptions occurring during term evaluation inside the RETE net.
//	 * @param e
//	 */
//	public void logEvaluatorException(Throwable e) {
//		try {
//			caughtExceptions.put(e);
//		} catch (InterruptedException e1) {
//			logEvaluatorException(e);
//		}
//	}
//	/**
//	 * Polls the exceptions caught and logged during term evaluation by this RETE engine.
//	 * Recommended usage: iterate polling until null is returned.
//	 * 
//	 * @return the next caught exception, or null if there are no more.
//	 */
//	public Throwable getNextLoggedEvaluatorException() {
//		return caughtExceptions.poll();
//	}
	

}
