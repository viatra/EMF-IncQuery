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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra2.emf.incquery.base.api.IncQueryBaseFactory;
import org.eclipse.viatra2.emf.incquery.base.api.NavigationHelper;
import org.eclipse.viatra2.emf.incquery.base.api.ParameterizedNavigationHelper;
import org.eclipse.viatra2.emf.incquery.base.exception.IncQueryBaseException;
import org.eclipse.viatra2.emf.incquery.runtime.IncQueryRuntimePlugin;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.EMFIncQueryRuntimeLogger;
import org.eclipse.viatra2.emf.incquery.runtime.internal.EMFPatternMatcherRuntimeContext;
import org.eclipse.viatra2.emf.incquery.runtime.internal.PatternSanitizer;
import org.eclipse.viatra2.emf.incquery.runtime.internal.matcherbuilder.EPMBuilder;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.ReteContainerBuildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherRuntimeContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.remote.Address;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * A EMF-IncQuery engine back-end, attached to a model such as an EMF resource. 
 * The engine hosts pattern matchers, and will listen on EMF update notifications stemming from the given model in order to maintain live results. 
 * 
 * Pattern matchers within this engine may be instantiated in the following ways: <ul>
 *  <li> Instantiate the specific matcher class generated for the pattern, by passing to the constructor either this engine or the EMF model root.
 *  <li> Use the matcher factory associated with the generated matcher class to achieve the same.
 *  <li> Use GenericMatcherFactory instead of the various generated factories.
 *  </ul>
 * 
 * The engine can be disposed in order to detach from the EMF model and stop listening on update notifications.
 * 
 * @author Bergmann Gábor
 *
 */
public class IncQueryEngine {

	/**
	 * The engine manager responsible for this engine.
	 */
	private EngineManager manager;
	/**
	 * The model to which the engine is attached.
	 */
	private Notifier emfRoot;	
	/**
	 * The base index keeping track of basic EMF contents of the model.
	 */
	private ParameterizedNavigationHelper baseIndex;
	/**
	 * The RETE pattern matcher component of the EMF-IncQuery engine.
	 */	
	private ReteEngine<Pattern> reteEngine = null;
	/**
	 * A sanitizer to catch faulty patterns.
	 */	
	private PatternSanitizer sanitizer = null;
	/**
	 * EXPERIMENTAL
	 */
	private int reteThreads = 0;
	
	private EMFIncQueryRuntimeLogger logger;
	private static EMFIncQueryRuntimeLogger defaultLogger;
	// TODO IncQueryBase?
	
	/**
	 * @param manager
	 * @param emfRoot
	 */
	IncQueryEngine(EngineManager manager, Notifier emfRoot) {
		super();
		this.manager = manager;
		this.emfRoot = emfRoot;
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
	 * @throws IncQueryBaseException if the base index could not be constructed
	 */
	protected ParameterizedNavigationHelper getBaseIndexInternal() throws IncQueryBaseException {
		if (baseIndex == null) {
			baseIndex = IncQueryBaseFactory.getInstance().createManualNavigationHelper(getEmfRoot());
		}
		return baseIndex;
	}
	
	/**
	 * Provides access to the internal base index component of the engine, responsible for keeping track of basic EMF contents of the model.
	 * @return the baseIndex the NavigationHelper maintaining the base index
	 * @throws IncQueryBaseException if the base index could not be constructed
	 */
	public NavigationHelper getBaseIndex() throws IncQueryBaseException {
		return getBaseIndexInternal();
	}	
	
	
	
	/**
	 * Provides access to the internal RETE pattern matcher component of the EMF-IncQuery engine.
	 * @noreference A typical user would not need to call this method.
	 */
	public ReteEngine<Pattern> getReteEngine() throws IncQueryRuntimeException {
		if (reteEngine == null) {
			EMFPatternMatcherRuntimeContext<Pattern> context;
			if (emfRoot instanceof EObject) 
				context = new EMFPatternMatcherRuntimeContext.ForEObject<Pattern>((EObject)emfRoot, this);
			else if (emfRoot instanceof Resource) 
				context = new EMFPatternMatcherRuntimeContext.ForResource<Pattern>((Resource)emfRoot, this);
			else if (emfRoot instanceof ResourceSet) 
				context = new EMFPatternMatcherRuntimeContext.ForResourceSet<Pattern>((ResourceSet)emfRoot, this);
			else throw new IncQueryRuntimeException(IncQueryRuntimeException.INVALID_EMFROOT);
			
			reteEngine = buildReteEngineInternal(context);
			//if (reteEngine != null) engines.put(emfRoot, new WeakReference<ReteEngine<String>>(engine));
		}
		return reteEngine;
		
	}
	/**
	 * Disconnects the engine. 
	 * Matcher objects will continue to return stale results. 
	 * If no references are retained to the matchers or the engine, they can eventually be GC'ed, 
	 * 	and they won't block the EMF model from being GC'ed anymore. 
	 * 
	 * Cannot be reversed.
	 * @return true is an engine was found and disconnected, false if no engine was found for the given root.
	 */
	public void dispose() {
		manager.killInternal(emfRoot);
		killInternal();
	}
	
	private ReteEngine<Pattern> buildReteEngineInternal(IPatternMatcherRuntimeContext<Pattern> context) 
			throws IncQueryRuntimeException 
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
		if (reteEngine != null) {
			reteEngine.killEngine();
			reteEngine = null;
		}
		sanitizer = null;
		if (baseIndex != null) {
			baseIndex.dispose();
		}
	}

	/**
	 * Run-time events (such as exceptions during expression evaluation) will be logged to this logger.
	 * <p>
	 * DEFAULT BEHAVIOUR:
	 * If Eclipse is running, the default logger pipes to the Eclipse Error Log.
	 * Otherwise, messages are written to stderr.
	 * In both cases, debug messages are ignored.
	 * </p>
	 * Custom logger can be provided via setter to override the default behaviour.
	 * @return the logger that errors will be logged to during runtime execution.
	 */
	public EMFIncQueryRuntimeLogger getLogger() {
		if (logger == null) {
			logger = createLogger();
		}
		return logger;
	}

	/**
	 * Creates a new logger instance
	 */
	private static EMFIncQueryRuntimeLogger createLogger() {
		final IncQueryRuntimePlugin plugin = IncQueryRuntimePlugin.getDefault();
		EMFIncQueryRuntimeLogger newLogger;
		if (plugin !=null) newLogger = new EMFIncQueryRuntimeLogger() {
			@Override
			public void logDebug(String message) {
				//plugin.getLog().log(new Status(IStatus.INFO, IncQueryRuntimePlugin.PLUGIN_ID, message));
			}

			@Override
			public void logError(String message) {
				plugin.getLog().log(new Status(IStatus.ERROR, IncQueryRuntimePlugin.PLUGIN_ID, message));
			}

			@Override
			public void logError(String message, Throwable cause) {
				plugin.getLog().log(new Status(IStatus.ERROR, IncQueryRuntimePlugin.PLUGIN_ID, message, cause));
			}

			@Override
			public void logWarning(String message) {
				plugin.getLog().log(new Status(IStatus.WARNING, IncQueryRuntimePlugin.PLUGIN_ID, message));
			}

			@Override
			public void logWarning(String message, Throwable cause) {
				plugin.getLog().log(new Status(IStatus.WARNING, IncQueryRuntimePlugin.PLUGIN_ID, message, cause));
			}
		}; else newLogger = new EMFIncQueryRuntimeLogger() {
			@Override
			public void logDebug(String message) {
				System.err.println("[DEBUG] " + message);
			}
			@Override
			public void logError(String message) {
				System.err.println("[ERROR] " + message);
			}
			@Override
			public void logError(String message, Throwable cause) {
				System.err.println("[ERROR] " + message);
				cause.printStackTrace();
			}
			@Override
			public void logWarning(String message) {
				System.err.println("[WARNING] " + message);
			}
			@Override
			public void logWarning(String message, Throwable cause) {
				System.err.println("[WARNING] " + message);
				cause.printStackTrace();
			}				
		};
		return newLogger;
	}

	/**
	 * Run-time events (such as exceptions during expression evaluation) will be logged to the specified logger.
	 * <p>
	 * DEFAULT BEHAVIOUR:
	 * If Eclipse is running, the default logger pipes to the Eclipse Error Log.
	 * Otherwise, messages are written to stderr.
	 * In both cases, debug messages are ignored.
	 * </p>
	 * @param logger a custom logger that errors will be logged to during runtime execution.
	 */
	public void setLogger(EMFIncQueryRuntimeLogger logger) {
		this.logger = logger;
	}
	
	/**
	 * Returns the default logger
	 */
	public static EMFIncQueryRuntimeLogger getDefaultLogger() {
		if(defaultLogger == null) {
			defaultLogger = createLogger();
		}
		return defaultLogger;
	}

	
	/**
	 * @return the sanitizer
	 */
	public PatternSanitizer getSanitizer() {
		if (sanitizer == null) {
			sanitizer = new PatternSanitizer(getLogger());
		}
		return sanitizer;
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
