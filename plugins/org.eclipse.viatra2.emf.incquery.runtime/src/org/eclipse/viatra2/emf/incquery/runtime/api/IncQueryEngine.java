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


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra2.emf.incquery.base.api.IncQueryBaseFactory;
import org.eclipse.viatra2.emf.incquery.base.api.NavigationHelper;
import org.eclipse.viatra2.emf.incquery.base.exception.IncQueryBaseException;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.EngineTaintListener;
import org.eclipse.viatra2.emf.incquery.runtime.internal.EMFPatternMatcherRuntimeContext;
import org.eclipse.viatra2.emf.incquery.runtime.internal.PatternSanitizer;
import org.eclipse.viatra2.emf.incquery.runtime.internal.XtextInjectorProvider;
import org.eclipse.viatra2.emf.incquery.runtime.internal.matcherbuilder.EPMBuilder;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.ReteContainerBuildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherRuntimeContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.remote.Address;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

import com.google.inject.Injector;

/**
 * A EMF-IncQuery engine back-end, attached to a model such as an EMF resource. 
 * The engine hosts pattern matchers, and will listen on EMF update notifications stemming from the given model in order to maintain live results. 
 * 
 * <p>Pattern matchers within this engine may be instantiated in the following ways: <ul>
 *  <li> Instantiate the specific matcher class generated for the pattern, by passing to the constructor either this engine or the EMF model root.
 *  <li> Use the matcher factory associated with the generated matcher class to achieve the same.
 *  <li> Use {@link GenericPatternMatcher} or {@link GenericMatcherFactory} instead of the various generated classes.
 *  </ul>
 * Additionally, a group of patterns (see {@link IPatternGroup}) can be initialized together before usage; 
 * 	this improves the performance of pattern matcher construction, unless the engine is in wildcard mode.
 * 
 * <p>The engine can be disposed in order to detach from the EMF model and stop listening on update notifications.
 * 
 * @author Bergmann Gábor
 *
 */
public class IncQueryEngine {

	/**
	 * The engine manager responsible for this engine. Null if this engine is unmanaged.
	 */
	private EngineManager manager;
	/**
	 * The model to which the engine is attached.
	 */
	private Notifier emfRoot;
	
	/**
	 * The base index keeping track of basic EMF contents of the model.
	 */
	private NavigationHelper baseIndex;
	/**
	 * Whether to initialize the base index in wildcard mode.
	 */
	private static final boolean WILDCARD_MODE_DEFAULT = false;
	/**
	 * The RETE pattern matcher component of the EMF-IncQuery engine.
	 */	
	private ReteEngine<Pattern> reteEngine = null;
	/**
	 * A sanitizer to catch faulty patterns.
	 */	
	private PatternSanitizer sanitizer = null;
	
	/**
	 * Indicates whether the engine is in a tainted, inconsistent state.
	 */
	private boolean tainted = false;
	private EngineTaintListener taintListener;
	private static class SelfTaintListener extends EngineTaintListener {
		WeakReference<IncQueryEngine> iqEngRef;
		public SelfTaintListener(IncQueryEngine iqEngine) {
			this.iqEngRef = new WeakReference<IncQueryEngine>(iqEngine);
		}
		@Override
		public void engineBecameTainted() {
			final IncQueryEngine iqEngine = iqEngRef.get();
			iqEngine.tainted = true;
		}
	}
	
	/**
	 * EXPERIMENTAL
	 */
	private int reteThreads = 0;
	
	private Logger logger;
  private Set<Runnable> afterWipeCallbacks;
	
	/**
	 * @param manager null if unmanaged
	 * @param emfRoot
	 * @throws IncQueryException if the emf root is invalid
	 */
	IncQueryEngine(EngineManager manager, Notifier emfRoot) throws IncQueryException {
		super();
		this.manager = manager;
		this.emfRoot = emfRoot;
	  this.afterWipeCallbacks = new HashSet<Runnable>();
		if (! (emfRoot instanceof EObject || emfRoot instanceof Resource || emfRoot instanceof ResourceSet)) 
			throw new IncQueryException(
					IncQueryException.INVALID_EMFROOT + (emfRoot == null ? "(null)" : emfRoot.getClass().getName()), 
					IncQueryException.INVALID_EMFROOT_SHORT);
	}	
	
	/**
	 * @return the root of the EMF model tree that this engine is attached to.
	 */
	public Notifier getEmfRoot() {
		return emfRoot;
	}
	
	/**
	 * Internal accessor for the base index.
	 * @return the baseIndex the NavigationHelper maintaining the base index
	 * @throws IncQueryException if the base index could not be constructed
	 */
	protected NavigationHelper getBaseIndexInternal() throws IncQueryException {
		return getBaseIndexInternal(WILDCARD_MODE_DEFAULT, true);
	}
	
	/**
	 * Internal accessor for the base index.
	 * @return the baseIndex the NavigationHelper maintaining the base index
	 * @throws IncQueryException if the base index could not be initialized
	 * @throws IncQueryBaseException if the base index could not be constructed
	 */
	protected NavigationHelper getBaseIndexInternal(boolean wildcardMode, boolean initNow) throws IncQueryException {
		if (baseIndex == null) {
			try {
			  // sync to avoid crazy compiler reordering which would matter if derived features use eIQ and call this reentrantly 
				synchronized (this) { 
          baseIndex = IncQueryBaseFactory.getInstance().createNavigationHelper(null, wildcardMode, getLogger());
        }
			} catch (IncQueryBaseException e) {
				throw new IncQueryException(
						"Could not create EMF-IncQuery base index", 
						"Could not create base index", 
						e);
			}
				
				if (initNow) {
  				initBaseIndex();
				}
				
		}
		return baseIndex;
	}

  /**
   * @throws IncQueryException
   */
  private synchronized void initBaseIndex() throws IncQueryException {
    try {
      baseIndex.addRoot(getEmfRoot());
    } catch (IncQueryBaseException e) {
      throw new IncQueryException(
          "Could not initialize EMF-IncQuery base index", 
          "Could not initialize base index", 
          e);
    }
  }
		
	/**
	 * Provides access to the internal base index component of the engine, responsible for keeping track of basic EMF contents of the model.
	 * @return the baseIndex the NavigationHelper maintaining the base index
	 * @throws IncQueryException if the base index could not be constructed
	 */
	public NavigationHelper getBaseIndex() throws IncQueryException {
		return getBaseIndexInternal();
	}	
	
	
	
	/**
	 * Provides access to the internal RETE pattern matcher component of the EMF-IncQuery engine.
	 * @noreference A typical user would not need to call this method.
	 */
	public ReteEngine<Pattern> getReteEngine() throws IncQueryException {
		if (reteEngine == null) {
		  // if uninitialized, don't initialize yet  
			getBaseIndexInternal(WILDCARD_MODE_DEFAULT, false);
			
      EMFPatternMatcherRuntimeContext context = new EMFPatternMatcherRuntimeContext(this, baseIndex);
//			if (emfRoot instanceof EObject) 
//				context = new EMFPatternMatcherRuntimeContext.ForEObject<Pattern>((EObject)emfRoot, this);
//			else if (emfRoot instanceof Resource) 
//				context = new EMFPatternMatcherRuntimeContext.ForResource<Pattern>((Resource)emfRoot, this);
//			else if (emfRoot instanceof ResourceSet) 
//				context = new EMFPatternMatcherRuntimeContext.ForResourceSet<Pattern>((ResourceSet)emfRoot, this);
//			else throw new IncQueryRuntimeException(IncQueryRuntimeException.INVALID_EMFROOT);
			
      synchronized (this) {
        reteEngine = buildReteEngineInternal(context);
      }
			
			// lazy initialization now, 
			initBaseIndex();
			
			//if (reteEngine != null) engines.put(emfRoot, new WeakReference<ReteEngine<String>>(engine));
		}
		return reteEngine;
		
	}
	/**
	 * Completely disconnects and dismantles the engine. 
	 * <p>Matcher objects will continue to return stale results. 
	 * If no references are retained to the matchers or the engine, they can eventually be GC'ed, 
	 * 	and they won't block the EMF model from being GC'ed anymore. 
	 * 
	 * <p>Cannot be reversed.
	 * <p>If the engine is managed (see {@link #isManaged()}), there may be other clients using it. 
   * Care should be taken with disposing such engines. 
	 */
	public void dispose() {
	  if(manager != null) {
	    manager.killInternal(emfRoot);
	  }
		killInternal();
	}
	
	/**
	 * Discards any pattern matcher caches and forgets known patterns. 
	 * The base index built directly on the underlying EMF model, however, 
	 * 	is kept in memory to allow reuse when new pattern matchers are built.
	 * Use this method if you have e.g. new versions of the same patterns, to be matched on the same model.
	 * 
	 * <p>Matcher objects will continue to return stale results. 
	 * If no references are retained to the matchers, they can eventually be GC'ed. 
	 * <p>If the engine is managed (see {@link #isManaged()}), there may be other clients using it. 
   * Care should be taken with wiping such engines. 
	 * 
	 */
	public void wipe() {
		if (reteEngine != null) {
			reteEngine.killEngine();
			reteEngine = null;
		}
		sanitizer = null;
		runAfterWipeCallbacks();
	}
	
	 /**
   * This will run before wipes.
   */
	//   * If there are any such, updates are settled before they are run. 
  public void runAfterWipeCallbacks() {
    try {
      if (!afterWipeCallbacks.isEmpty()) {
        //settle();
        for (Runnable runnable : new ArrayList<Runnable>(afterWipeCallbacks)) {
          runnable.run();
        }
      }
    } catch (Exception ex) {
      logger.fatal(
          "EMF-IncQuery encountered an error in delivering notifications about wipe. " , ex);
    }
  }
	
	private ReteEngine<Pattern> buildReteEngineInternal(IPatternMatcherRuntimeContext<Pattern> context) 
	{
		ReteEngine<Pattern> engine;
		engine = new ReteEngine<Pattern>(context, reteThreads);
		ReteContainerBuildable<Pattern> buildable = new ReteContainerBuildable<Pattern>(engine);
		EPMBuilder<Address<? extends Supplier>, Address<? extends Receiver>> builder = 
				new EPMBuilder<Address<? extends Supplier>, Address<? extends Receiver>> (buildable, context);
//		Collection<ViatraEMFPatternmatcherBuildAdvisor> advisors = 
//			BuilderRegistry.getContributedPatternBuildAdvisors();	
//		if (advisors==null || advisors.isEmpty()) {			
			engine.setBuilder(builder);
//			Set<Pattern> patternSet = BuilderRegistry.getContributedStatelessPatternBuilders().keySet(); 
//			try {
//				engine.buildMatchersCoalesced(patternSet);
//			} catch (RetePatternBuildException e) {
//				throw new IncQueryRuntimeException(e);
//			}
//		} else {
//			advisors.iterator().next().applyBuilder(engine, buildable, context);
//		}
		return engine;
	}


	/**
	 * To be called after already removed from engineManager.
	 */
	void killInternal() {
		wipe();
		if (baseIndex != null) {
			baseIndex.dispose();
		}
		getLogger().removeAppender(taintListener);
	}

	/**
	 * Run-time events (such as exceptions during expression evaluation) will be logged to this logger.
	 * <p>
	 * DEFAULT BEHAVIOUR:
	 * If Eclipse is running, the default logger pipes to the Eclipse Error Log.
	 * Otherwise, messages are written to stderr.
	 * </p>
	 * @return the logger that errors will be logged to during runtime execution.
	 */
	public Logger getLogger() {
		if (logger == null) {
			final int hash = System.identityHashCode(this);
			logger = Logger.getLogger(getDefaultLogger().getName() + "." + hash);
			if (logger == null)
				throw new AssertionError("Configuration error: unable to create EMF-IncQuery runtime logger for engine " + hash);
			
			// if an error is logged, the engine becomes tainted
			taintListener = new SelfTaintListener(this);
			logger.addAppender(taintListener);
		}
		return logger;
	}
//
//
//	/**
//	 * Run-time events (such as exceptions during expression evaluation) will be logged to the specified logger.
//	 * <p>
//	 * DEFAULT BEHAVIOUR:
//	 * If Eclipse is running, the default logger pipes to the Eclipse Error Log.
//	 * Otherwise, messages are written to stderr.
//	 * In both cases, debug messages are ignored.
//	 * </p>
//	 * @param logger a custom logger that errors will be logged to during runtime execution.
//	 */
//	public void setLogger(EMFIncQueryRuntimeLogger logger) {
//		this.logger = logger;
//	}


	
	/**
	 * @return the sanitizer
	 */
	public PatternSanitizer getSanitizer() {
		if (sanitizer == null) {
			sanitizer = new PatternSanitizer(getLogger());
		}
		return sanitizer;
	}

	/**
	 * Provides a static default logger.
	 */
	public static Logger getDefaultLogger() {
		if (defaultRuntimeLogger == null) {
			final Injector injector = XtextInjectorProvider.INSTANCE.getInjector();
			if (injector==null) 
				throw new AssertionError("Configuration error: EMF-IncQuery injector not initialized.");
			Logger parentLogger = injector.getInstance(Logger.class);
			if (parentLogger == null) 
				throw new AssertionError("Configuration error: EMF-IncQuery logger not found.");
			
			defaultRuntimeLogger = Logger.getLogger(parentLogger.getName() + ".runtime");
			if (defaultRuntimeLogger == null) 
				throw new AssertionError("Configuration error: unable to create default EMF-IncQuery runtime logger.");
		}
		
		return defaultRuntimeLogger;
	}
	private static Logger defaultRuntimeLogger;

	/**
	 * Specifies whether the base index should be built in wildcard mode. See {@link NavigationHelper} for the explanation of wildcard mode.
	 * @param wildcardMode the wildcardMode to set
	 * @throws IncQueryException if the base index could not be initialized
	 * @throws IllegalStateException if baseIndex is already constructed in the opposite mode, since the mode can not be changed once applied
	 */
	public void setWildcardMode(boolean wildcardMode) throws IncQueryException {
		if (baseIndex != null && baseIndex.isInWildcardMode() != wildcardMode)
			throw new IllegalStateException("Base index already built, cannot change wildcard mode anymore");
			
		if (wildcardMode != WILDCARD_MODE_DEFAULT) 
			getBaseIndexInternal(wildcardMode, true);		
	}

	/**
	 * Indicates whether the engine is in a tainted, inconsistent state due to some internal errors. 
	 * If true, results are no longer reliable; engine should be disposed.
	 * 
	 * <p>The engine is defined to be in a tainted state if any of its internal processes has logged a <strong>fatal</strong> error to the engine's logger. 
	 * The cause of the error can therefore be determined by checking the contents of the log.
	 * This is possible e.g. through a custom {@link Appender} that was attached to the engine's logger.  
	 * 
	 * @return the tainted state
	 */
	public boolean isTainted() {
		return tainted;
	}
	
	
	/**
	 * Indicates whether the engine is managed by {@link EngineManager}.
	 * 
	 * <p>If the engine is managed, there may be other clients using it. 
	 * Care should be taken with {@link #wipe()} and {@link #dispose()}. 
	 * Register a callback using {@link IncQueryMatcher#addCallbackAfterWipes(Runnable)} 
	 * or directly at {@link #getAfterWipeCallbacks()} to learn when a 
	 * client has called these dangerous methods.  
	 * 
	 * @return true if the engine is managed, and therefore potentially 
	 * shared with other clients querying the same EMF model
	 */
	public boolean isManaged() {
		return manager != null;
	}	

	/**
	 * @return the set of callbacks that will be issued after a wipe
	 */
	public Set<Runnable> getAfterWipeCallbacks() {
	  return afterWipeCallbacks;
	}
	
	
	
//	/**
//	 * EXPERIMENTAL: Creates an EMF-IncQuery engine that executes post-commit, or retrieves an already existing one.
//	 * @param emfRoot the EMF root where this engine should operate
//	 * @param reteThreads experimental feature; 0 is recommended
//	 * @return a new or previously existing engine
//	 * @throws IncQueryRuntimeException
//	 */	
//	public ReteEngine<String> getReteEngine(final TransactionalEditingDomain editingDomain, int reteThreads) throws IncQueryRuntimeException {
//		final ResourceSet resourceSet = editingDomain.getResourceSet();
//		WeakReference<ReteEngine<String>> weakReference = engines.get(resourceSet);
//		ReteEngine<String> engine = weakReference != null ? weakReference.get() : null;
//		if (engine == null) {
//			IPatternMatcherRuntimeContext<String> context = new EMFPatternMatcherRuntimeContext.ForTransactionalEditingDomain<String>(editingDomain);
//			engine = buildReteEngine(context, reteThreads);
//			if (engine != null) engines.put(resourceSet, new WeakReference<ReteEngine<String>>(engine));
//		}
//		return engine;
//	}	
	
}
