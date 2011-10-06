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

package org.eclipse.viatra2.emf.incquery.runtime.internal;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.ReteContainerBuildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherRuntimeContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;



/**
 * Global registry of active EMF-IncQuery engines.
 * @author Bergmann Gábor
 *
 */
public class EngineManager {
	private static EngineManager instance = new EngineManager();

	/**
	 * @return the singleton instance
	 */
	public static EngineManager getInstance() {
		return instance;
	}
	Map<Notifier, WeakReference<ReteEngine<String>>> engines;
	
	EngineManager() {
		super();
		engines = new WeakHashMap<Notifier, WeakReference<ReteEngine<String>>>();
	}
	
	/**
	 * Creates an EMF-IncQuery engine at an EMF root or retrieves an already existing one.
	 * 
	 * Note: if emfRoot is a resourceSet, the scope will include even those resources that are not part of the resourceSet but are referenced. 
	 * 	This is mainly to support nsURI-based instance-level references to registered EPackages.
	 * 
	 * @param emfRoot the EMF root where this engine should operate
	 * @param reteThreads experimental feature; 0 is recommended
	 * @return a new or previously existing engine
	 * @throws IncQueryRuntimeException
	 */
	public ReteEngine<String> getReteEngine(Notifier emfRoot, int reteThreads) throws IncQueryRuntimeException {
		WeakReference<ReteEngine<String>> weakReference = engines.get(emfRoot);
		ReteEngine<String> engine = weakReference != null ? weakReference.get() : null;
		if (engine == null) {
			IPatternMatcherRuntimeContext<String> context;
			if (emfRoot instanceof EObject) 
				context = new EMFPatternMatcherRuntimeContext.ForEObject<String>((EObject)emfRoot);
			else if (emfRoot instanceof Resource) 
				context = new EMFPatternMatcherRuntimeContext.ForResource<String>((Resource)emfRoot);
			else if (emfRoot instanceof ResourceSet) 
				context = new EMFPatternMatcherRuntimeContext.ForResourceSet<String>((ResourceSet)emfRoot);
			else throw new IncQueryRuntimeException(IncQueryRuntimeException.INVALID_EMFROOT);
			
			engine = buildReteEngine(context, reteThreads);
			if (engine != null) engines.put(emfRoot, new WeakReference<ReteEngine<String>>(engine));
		}
		return engine;
	}
	
	private ReteEngine<String> buildReteEngine(
			IPatternMatcherRuntimeContext<String> context, int reteThreads) throws IncQueryRuntimeException {
		ReteEngine<String> engine;
		engine = new ReteEngine<String>(context, reteThreads);
		ReteContainerBuildable<String> buildable = new ReteContainerBuildable<String>(engine);

		Collection<ViatraEMFPatternmatcherBuildAdvisor> advisors = 
			Activator.getDefault().getContributedPatternBuildAdvisors();	
		if (advisors==null || advisors.isEmpty()) {
			engine.setBuilder(new MultiplexerPatternBuilder(buildable, context));
			Set<String> patternSet = Activator.getDefault().getContributedStatelessPatternBuilders().keySet(); 
			try {
				engine.buildMatchersCoalesced(patternSet);
			} catch (RetePatternBuildException e) {
				throw new IncQueryRuntimeException(e);
			}
		} else {
			advisors.iterator().next().applyBuilder(engine, buildable, context);
		}
		return engine;
	}
		
	/**
	 * EXPERIMENTAL: Creates an EMF-IncQuery engine that executes post-commit, or retrieves an already existing one.
	 * @param emfRoot the EMF root where this engine should operate
	 * @param reteThreads experimental feature; 0 is recommended
	 * @return a new or previously existing engine
	 * @throws IncQueryRuntimeException
	 */	
	public ReteEngine<String> getReteEngine(final TransactionalEditingDomain editingDomain, int reteThreads) throws IncQueryRuntimeException {
		final ResourceSet resourceSet = editingDomain.getResourceSet();
		WeakReference<ReteEngine<String>> weakReference = engines.get(resourceSet);
		ReteEngine<String> engine = weakReference != null ? weakReference.get() : null;
		if (engine == null) {
			IPatternMatcherRuntimeContext<String> context = new EMFPatternMatcherRuntimeContext.ForTransactionalEditingDomain<String>(editingDomain);
			engine = buildReteEngine(context, reteThreads);
			if (engine != null) engines.put(resourceSet, new WeakReference<ReteEngine<String>>(engine));
		}
		return engine;
	}	

	/**
	 * Disconnects the engine that was previously attached at the given root. 
	 * Matcher objects will continue to return stale results. 
	 * If no references are retained to the matchers or the engine, they can eventually be GC'ed, 
	 * 	and they won't block the EMF model from being GC'ed anymore. 
	 * @return true is an engine was found and disconnected, false if no engine was found for the given root.
	 */
	public boolean killEngine(Notifier emfRoot) {
		WeakReference<ReteEngine<String>> weakReference = engines.get(emfRoot);
		ReteEngine<String> engine = weakReference != null ? weakReference.get() : null;
		if (engine == null) return false;
		else {
			engine.killEngine();
			engines.remove(emfRoot);
			return true;
		}
	}
}
