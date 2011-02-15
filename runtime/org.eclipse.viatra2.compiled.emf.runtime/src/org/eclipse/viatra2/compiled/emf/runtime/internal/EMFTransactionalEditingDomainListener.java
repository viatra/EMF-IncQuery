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

package org.eclipse.viatra2.compiled.emf.runtime.internal;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.IManipulationListener;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.PredicateEvaluatorNode;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;


/** 
 * Forwards notifications from a TransactionalEditingDomain to a CoreEMFManipualtionListener.
 * @author Bergmann Gábor
 */
public class EMFTransactionalEditingDomainListener implements ResourceSetListener, IManipulationListener {

	protected TransactionalEditingDomain domain;	
	protected ResourceSet resourceSet;
	
	CoreEMFManipulationListener coreListener;
	
	/**
	 * Prerequisite: engine has its network, framework and boundary fields
	 * initialized
	 * 
	 * @param engine
	 */
	public EMFTransactionalEditingDomainListener(ReteEngine<?> engine,
			TransactionalEditingDomain domain) {
		super();
			
		this.domain = domain;
		this.resourceSet = domain.getResourceSet();
		this.coreListener = new CoreEMFManipulationListener(engine, true /*TODO DEFERRED Tree traversals? */);
		engine.addDisconnectable(this);		
		domain.addResourceSetListener(this);
	}

	@Override
	public NotificationFilter getFilter() {
		return null;
	}

	@Override
	public boolean isAggregatePrecommitListener() {
		return false;
	}

	@Override
	public boolean isPostcommitOnly() {
		return true;
	}

	@Override
	public boolean isPrecommitOnly() {
		return false;
	}

	@Override
	public void resourceSetChanged(ResourceSetChangeEvent event) {
		for (Notification noti : event.getNotifications()) {
			coreListener.handleEMFNotification(noti);
		}
		coreListener.engine.runAfterUpdateCallbacks();
	}
	
	@Override
	public Command transactionAboutToCommit(ResourceSetChangeEvent event)
			throws RollbackException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.adapters.IManipulationListener#registerSensitiveTerm(org.eclipse.viatra2.core.IModelElement, org.eclipse.viatra2.gtasm.patternmatcher.incremental.adapters.GTASMTermEvaluatorNode)
	 */
	@Override
	public void registerSensitiveTerm(Object element, PredicateEvaluatorNode termEvaluatorNode) {
		coreListener.registerSensitiveTerm(element, termEvaluatorNode);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.adapters.IManipulationListener#unregisterSensitiveTerm(org.eclipse.viatra2.core.IModelElement, org.eclipse.viatra2.gtasm.patternmatcher.incremental.adapters.GTASMTermEvaluatorNode)
	 */
	@Override
	public void unregisterSensitiveTerm(Object element, PredicateEvaluatorNode termEvaluatorNode) {
		coreListener.unregisterSensitiveTerm(element, termEvaluatorNode);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.adapters.Disconnectable#disconnect()
	 */
	@Override
	public void disconnect() {
		domain.removeResourceSetListener(this);
	}
	

}
