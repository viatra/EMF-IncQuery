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

package org.eclipse.viatra2.compiled.emf.runtime;

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
import org.eclipse.viatra2.compiled.emf.runtime.exception.ViatraCompiledRuntimeException;
import org.eclipse.viatra2.compiled.emf.runtime.internal.EMFPatternMatcherRuntimeContext;
import org.eclipse.viatra2.compiled.emf.runtime.internal.EMFTransactionalEditingDomainListener;
import org.eclipse.viatra2.compiled.emf.runtime.internal.MultiplexerPatternBuilder;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.IManipulationListener;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.ReteContainerBuildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherRuntimeContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;



/**
 * TODO Exceptions?
 * @author Bergmann Gábor
 *
 */
public class EngineManager {
	private static EngineManager instance = new EngineManager();

	/**
	 * @return the instance
	 */
	public static EngineManager getInstance() {
		return instance;
	}
	Map<Notifier, WeakReference<ReteEngine<String>>> engines;
	
	EngineManager() {
		super();
		engines = new WeakHashMap<Notifier, WeakReference<ReteEngine<String>>>();
	}
	
	public ReteEngine<String> getReteEngine(Notifier emfRoot, int reteThreads) throws ViatraCompiledRuntimeException {
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
			else throw new ViatraCompiledRuntimeException(ViatraCompiledRuntimeException.INVALID_EMFROOT);
			
			engine = buildReteEngine(context, reteThreads);
			if (engine != null) engines.put(emfRoot, new WeakReference<ReteEngine<String>>(engine));
		}
		return engine;
	}
	
	/**
	 * @param context
	 * @param reteThreads
	 * @return
	 * @throws RetePatternBuildException 
	 * @throws ViatraCompiledRuntimeException 
	 */
	private ReteEngine<String> buildReteEngine(
			IPatternMatcherRuntimeContext<String> context, int reteThreads) throws ViatraCompiledRuntimeException {
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
				throw new ViatraCompiledRuntimeException(e);
			}
		} else {
			advisors.iterator().next().applyBuilder(engine, buildable, context);
		}
		return engine;
	}
			
	public ReteEngine<String> getReteEngine(final TransactionalEditingDomain editingDomain, int reteThreads) throws ViatraCompiledRuntimeException {
		ResourceSet resourceSet = editingDomain.getResourceSet();
		WeakReference<ReteEngine<String>> weakReference = engines.get(resourceSet);
		ReteEngine<String> engine = weakReference != null ? weakReference.get() : null;
		if (engine == null) {
			IPatternMatcherRuntimeContext<String> context = new 
				EMFPatternMatcherRuntimeContext.ForResourceSet<String>(resourceSet) {
					@Override
					public IManipulationListener subscribePatternMatcherForUpdates(
							ReteEngine<String> engine) {
						return new EMFTransactionalEditingDomainListener(engine, editingDomain);
					}
				};
			engine = buildReteEngine(context, reteThreads);
			if (engine != null) engines.put(resourceSet, new WeakReference<ReteEngine<String>>(engine));
		}
		return engine;
	}	

}
