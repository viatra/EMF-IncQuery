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

package org.eclipse.viatra2.emf.incquery.runtime.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.EMFIncQueryRuntimeLogger;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.PredicateEvaluatorNode;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction;

/**
 * Uses an EContentAdapter to virally spread in an EMF containment tree (up to ResourceSet) and forward notifications 
 * to a CoreEMFManipualtionListener.
 * @author Bergmann Gábor
 *
 */
public class EMFContentTreeViralListener extends EContentAdapter implements ExtensibleEMFManipulationListener {	
	protected Set<Notifier> rootNotifiers;
	CoreEMFManipulationListener coreListener;
	private EMFIncQueryRuntimeLogger logger;
	
	/**
	 * Prerequisite: engine has its network, framework and boundary fields
	 * initialized
	 * 
	 * @param engine
	 */
	public EMFContentTreeViralListener(ReteEngine<?> engine, Notifier rootNotifier, EMFPatternMatcherRuntimeContext<?> context, EMFIncQueryRuntimeLogger logger) {
		this(engine, Collections.singletonList(rootNotifier), context, logger);
	}
	/**
	 * Prerequisite: engine has its network, framework and boundary fields
	 * initialized
	 * 
	 * @param engine
	 */
	public EMFContentTreeViralListener(ReteEngine<?> engine, Collection<Notifier> rootNotifiers, EMFPatternMatcherRuntimeContext<?> context, EMFIncQueryRuntimeLogger logger) {
		super();
		
		this.logger = logger;
		this.rootNotifiers = new HashSet<Notifier>(rootNotifiers);
		this.coreListener = new CoreEMFManipulationListener(engine, context, logger); //, rootNotifier instanceof ResourceSet);
		for (Notifier notifier : rootNotifiers) {
			notifier.eAdapters().add(this);
		}
		engine.addDisconnectable(this);	

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.internal.ExtensibleEMFManipulationListener#addRoot(org.eclipse.emf.common.notify.Notifier)
	 */
	@Override
	public void addRoot(Notifier notifier) {
		if (rootNotifiers.add(notifier)) {
			notifier.eAdapters().add(this);
			if (notifier instanceof EObject) {
				coreListener.attachedTree((EObject) notifier, Direction.INSERT);
			} else if (notifier instanceof Resource) {
				for (EObject eObj : ((Resource) notifier).getContents()) {
					coreListener.attachedTree(eObj, Direction.INSERT);
				}
			} else if (notifier instanceof ResourceSet) {
				for (Resource r : ((ResourceSet) notifier).getResources()) {
					for (EObject eObj : r.getContents()) {
						coreListener.attachedTree(eObj, Direction.INSERT);
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.Disconnectable#disconnect()
	 */
	@Override
	public void disconnect() {
		for (Notifier notifier : rootNotifiers) {
			notifier.eAdapters().remove(this);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.IManipulationListener#registerSensitiveTerm(java.lang.Object, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.PredicateEvaluatorNode)
	 */
	@Override
	public void registerSensitiveTerm(Object element,
			PredicateEvaluatorNode termEvaluatorNode) {
		coreListener.registerSensitiveTerm(element, termEvaluatorNode);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.IManipulationListener#unregisterSensitiveTerm(java.lang.Object, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.PredicateEvaluatorNode)
	 */
	@Override
	public void unregisterSensitiveTerm(Object element,
			PredicateEvaluatorNode termEvaluatorNode) {
		coreListener.unregisterSensitiveTerm(element, termEvaluatorNode);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.util.EContentAdapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	@Override
	public void notifyChanged(Notification notification) {
		coreListener.handleEMFNotification(notification);
		super.notifyChanged(notification);
		coreListener.reteEngine.runAfterUpdateCallbacks();
	}

}

