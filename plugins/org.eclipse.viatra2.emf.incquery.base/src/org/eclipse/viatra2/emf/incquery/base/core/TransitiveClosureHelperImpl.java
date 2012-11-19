/*******************************************************************************
 * Copyright (c) 2010-2012, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.base.core;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.viatra2.emf.incquery.base.api.TransitiveClosureHelper;
import org.eclipse.viatra2.emf.incquery.base.exception.IncQueryBaseException;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.incscc.IncSCCAlg;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.ITcObserver;

/**
 * Implementation class for the transitive closure.
 * The class is a wrapper for the tc algorithms on an emf model.
 * 
 * @author Tamas Szabo
 *
 */
public class TransitiveClosureHelperImpl extends EContentAdapter implements
		TransitiveClosureHelper, ITcObserver<EObject> {

	private IncSCCAlg<EObject> sccAlg;
	private Set<EReference> refToObserv;
	private EMFDataSource dataSource;
	private ArrayList<ITcObserver<EObject>> observers;
	private Notifier notifier;
	
	public TransitiveClosureHelperImpl(Notifier emfRoot,
			Set<EReference> refToObserv) throws IncQueryBaseException {
		this.refToObserv = refToObserv;
		this.notifier = emfRoot;
		this.observers = new ArrayList<ITcObserver<EObject>>();
		
		if (emfRoot instanceof EObject)
			dataSource = new EMFDataSource.ForEObject((EObject) emfRoot,
					refToObserv);
		else if (emfRoot instanceof Resource)
			dataSource = new EMFDataSource.ForResource((Resource) emfRoot,
					refToObserv);
		else if (emfRoot instanceof ResourceSet)
			dataSource = new EMFDataSource.ForResourceSet((ResourceSet) emfRoot, refToObserv);
		else
			throw new IncQueryBaseException(
					IncQueryBaseException.INVALID_EMFROOT);

		this.sccAlg = new IncSCCAlg<EObject>(dataSource);
		this.sccAlg.attachObserver(this);
		emfRoot.eAdapters().add(this);
	}

	private void visitObjectForEReference(EObject obj, boolean isInsert) {
		for (EReference ref : obj.eClass().getEReferences()) {
			if (refToObserv.contains(ref)) {
				Object o = obj.eGet(ref);

				if (o instanceof EObjectEList<?>) {
					@SuppressWarnings("unchecked")
					EObjectEList<EObject> list = (EObjectEList<EObject>) o;
					Iterator<EObject> it = list.iterator();

					while (it.hasNext()) {
						EObject target = it.next();
						if (isInsert) {
							nodeInserted(target);
							edgeInserted(obj, target);
						} else {
							edgeDeleted(obj, target);
							nodeDeleted(target);
						}
					}
				} else {
					EObject target = (EObject) o;
					if (isInsert) {
						nodeInserted(target);
						edgeInserted(obj, target);
					} else {
						edgeDeleted(obj, target);
						nodeDeleted(target);
					}
				}
			}
		}
	}

	@Override
	public void notifyChanged(Notification notification) {
		super.notifyChanged(notification);
		Object feature = notification.getFeature();
		Object oldValue = notification.getOldValue();
		Object newValue = notification.getNewValue();
		Object notifier = notification.getNotifier();
		
		//System.out.println(notification);
		if (feature instanceof EReference && 
				(oldValue == null || oldValue instanceof EObject) && 
				(newValue == null || newValue instanceof EObject) && 
				(notifier == null || notifier instanceof EObject)) {

			EReference ref = (EReference) feature;
			EObject oldValueObj = (EObject) oldValue;
			EObject newValueObj = (EObject) newValue;
			EObject notifierObj = (EObject) notifier;

			if (ref.isContainment()) {
				// Inserting nodes
				if (notification.getEventType() == Notification.ADD
						&& oldValueObj == null && newValueObj != null) {
					nodeInserted(newValueObj);
					visitObjectForEReference(newValueObj, true);
				}
				if (notification.getEventType() == Notification.REMOVE
						&& newValueObj == null && oldValueObj != null) {
					visitObjectForEReference(oldValueObj, false);
					nodeDeleted(oldValueObj);
				}
			} else // Inserting edges (excusively -> edge or node modification)
			if (refToObserv.contains(ref)) {

				if (notification.getEventType() == Notification.ADD
						&& newValueObj != null) {
					edgeInserted(notifierObj, newValueObj);
				}
				else if (notification.getEventType() == Notification.REMOVE
						&& oldValueObj != null) {
					edgeDeleted(notifierObj, oldValueObj);
				}
				else if (notification.getEventType() == Notification.SET) {
					if (oldValueObj != null) {
						edgeDeleted(notifierObj, oldValueObj);
					}

					if (newValueObj != null) {
						edgeInserted(notifierObj, newValueObj);
					}
				}
			}
		}
	}

	private void edgeInserted(EObject source, EObject target) {
		dataSource.notifyEdgeInserted(source, target);
	}

	private void edgeDeleted(EObject source, EObject target) {
		dataSource.notifyEdgeDeleted(source, target);
	}

	private void nodeInserted(EObject node) {
		dataSource.notifyNodeInserted(node);
	}

	private void nodeDeleted(EObject node) {
		dataSource.notifyNodeDeleted(node);
	}

	@Override
	public void attachObserver(ITcObserver<EObject> to) {
		this.observers.add(to);
	}

	@Override
	public void detachObserver(ITcObserver<EObject> to) {
		this.observers.remove(to);
	}

	@Override
	public Set<EObject> getAllReachableTargets(EObject source) {
		return this.sccAlg.getAllReachableTargets(source);
	}

	@Override
	public Set<EObject> getAllReachableSources(EObject target) {
		return this.sccAlg.getAllReachableSources(target);
	}

	@Override
	public boolean isReachable(EObject source, EObject target) {
		return this.sccAlg.isReachable(source, target);
	}

	@Override
	public void tupleInserted(EObject source, EObject target) {
		for (ITcObserver<EObject> to : observers) {
			to.tupleInserted(source, target);
		}
	}

	@Override
	public void tupleDeleted(EObject source, EObject target) {
		for (ITcObserver<EObject> to : observers) {
			to.tupleDeleted(source, target);
		}
	}

	@Override
	public void dispose() {
		this.sccAlg.dispose();
		this.notifier.eAdapters().remove(this);
	}
}
