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

package org.eclipse.viatra2.compiled.emf.runtime.internal;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.IManipulationListener;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.PredicateEvaluatorNode;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;

/**
 * Uses an EContentAdapter to virally spread in an EMF containment tree (up to ResourceSet) and forward notifications 
 * to a CoreEMFManipualtionListener.
 * @author Bergmann Gábor
 *
 */
public class EMFContentTreeViralListener extends EContentAdapter implements IManipulationListener {	
	protected Notifier rootNotifier;	
	CoreEMFManipulationListener coreListener;
	
	/**
	 * Prerequisite: engine has its network, framework and boundary fields
	 * initialized
	 * 
	 * @param engine
	 */
	public EMFContentTreeViralListener(ReteEngine<?> engine, Notifier rootNotifier) {
		super();
			
		this.rootNotifier = rootNotifier;
		this.coreListener = new CoreEMFManipulationListener(engine, rootNotifier instanceof ResourceSet);
		engine.addDisconnectable(this);	
		rootNotifier.eAdapters().add(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.Disconnectable#disconnect()
	 */
	@Override
	public void disconnect() {
		rootNotifier.eAdapters().remove(this);
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
		coreListener.engine.runAfterUpdateCallbacks();
	}

}

