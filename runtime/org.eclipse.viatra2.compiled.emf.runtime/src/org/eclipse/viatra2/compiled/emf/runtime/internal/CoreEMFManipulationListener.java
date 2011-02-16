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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.PredicateEvaluatorNode;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.ReteBoundary;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;

/**
 * Conducts the core functionality of notifying RETE about EMF changes. However, owner is responsible
 * for registering and receiving notifications directly as IResourceSetListener or EContentAdapter.
 * @author Bergmann Gábor
 *
 */
public class CoreEMFManipulationListener {
	
	protected ReteEngine<?> engine;
	protected ReteBoundary<?> boundary;
	protected ReteContainer headContainer;
	
	/** 
	 * Contains the set of resources that have finished loading and are not unloaded yet.
	 * Notification is accepted only from these resources.
	 * If null, no check is performed and the collection is not maintained. 
	 */
	protected Collection<Resource> activeResources;

	/** TermEvaluatorNodes that have to be notified on changes affecting a given ModelElement */
	protected Map<Object, Set<PredicateEvaluatorNode>> sensitiveTerms;
	
	/**
	 * Prerequisite: engine has its network, framework and boundary fields
	 * initialized
	 * 
	 * @param engine
	 */
	public CoreEMFManipulationListener(ReteEngine<?> engine, boolean resourceLoadingFilter) {
		super();
		this.engine = engine;
		this.boundary = engine.getBoundary();
		this.headContainer = engine.getReteNet().getHeadContainer();

		this.sensitiveTerms = new HashMap<Object, Set<PredicateEvaluatorNode>>();
		if (resourceLoadingFilter) this.activeResources = new HashSet<Resource>();
	}
	
	// TODO deferred notifications from TransactionalEditingDomains?
	public void handleEMFNotification(Notification noti) {
		final Object oFeature = noti.getFeature();
		final Object oldValue = noti.getOldValue();
		final Object newValue = noti.getNewValue();
		final Object notifier = noti.getNotifier();		
		final int eventType = noti.getEventType();
		
		if (notifier instanceof EObject && oFeature != null) {
			if (activeResources == null || activeResources.contains(((EObject)notifier).eResource()))
				handleRegularNotification(oFeature, oldValue, newValue, (EObject)notifier, eventType);
		} else if (notifier instanceof Resource && activeResources!= null) {
			Resource resource = (Resource) notifier;
			if (resource.getTimeStamp()==-1 && activeResources.contains(resource)) {
				activeResources.remove(resource);
				for (EObject topContent : resource.getContents())
					attachedTree(topContent, Direction.REVOKE); 
			}
			if (resource.getTimeStamp()!=0 && 
					resource.getTimeStamp()!=-1 && 
					!activeResources.contains(resource)) 
			{
				activeResources.add(resource);
				for (EObject topContent : resource.getContents())
					attachedTree(topContent, Direction.INSERT); 
			}
			
		}
	}

	@SuppressWarnings("deprecation")
	// TODO handle instantiation update? - no generics supported yet
	private void handleRegularNotification(final Object oFeature,
			final Object oldValue, final Object newValue,
			final EObject notifier, final int eventType) {
		switch(eventType) {
		case Notification.ADD: 
			featureUpdate(Direction.INSERT, oFeature, newValue, notifier);
			break;
		case Notification.ADD_MANY: 
			for (Object newElement: (Collection<?>)newValue) 
				featureUpdate(Direction.INSERT, oFeature, newElement, notifier);
			break;
		case Notification.CREATE: 
			break;
		case Notification.MOVE: break; // currently no support for ordering
		case Notification.REMOVE: 
			featureUpdate(Direction.REVOKE, oFeature, oldValue, notifier);
			break;
		case Notification.REMOVE_MANY:
			for (Object oldElement: (Collection<?>)oldValue) 
				featureUpdate(Direction.REVOKE, oFeature, oldElement, notifier);
			break;
		//case Notification.REMOVING_ADAPTER: break;
		case Notification.RESOLVE:  //TODO Fallthrough? 
		case Notification.UNSET:  //TODO Fallthrough?			
		case Notification.SET:
			featureUpdate(Direction.REVOKE, oFeature, oldValue, notifier);
			featureUpdate(Direction.INSERT, oFeature, newValue, notifier);			
			break;
		case Notification.REMOVING_ADAPTER:
			break;
		}
	}
	
	void featureUpdate(Direction direction, Object oFeature, Object changedValue, EObject notifier) {
		if (changedValue == null) return; // null-valued attributes are simply not stored
		
		boundary.updateBinaryEdge(direction, notifier, changedValue, oFeature);
		boundary.updateBinaryEdge(direction, notifier, changedValue, null); // global supertype
		if (oFeature instanceof EAttribute) {
			boundary.updateContainment(direction, notifier, changedValue);
			EAttribute feature = (EAttribute) oFeature;
			boundary.updateUnary(direction, changedValue, feature.getEAttributeType()); // FIXME is this the correct, direct type?					
			boundary.updateUnary(direction, changedValue, null); // global supertype	
			boundary.updateInstantiation(direction, feature.getEAttributeType(), changedValue);
		}
		if (oFeature instanceof EReference) {
			EReference feature = (EReference) oFeature;
			if (feature.isContainment()) {
				boundary.updateContainment(direction, notifier, changedValue);
				attachedTree((EObject)changedValue, direction);
			}
		}
	}

	private void attachedTree(EObject root, final Direction direction) {
		new EMFContainmentHierarchyTraversal(root).accept(new EMFVisitor(){

			@Override
			public void visitAttribute(EObject source, EAttribute feature, Object target) {
				if (target!=null)  {
					boundary.updateBinaryEdge(direction, source, target, feature);
					boundary.updateBinaryEdge(direction, source, target, null); // global supertype
					boundary.updateContainment(direction, source, target);
					boundary.updateUnary(direction, target, feature.getEAttributeType()); // FIXME is this the correct, direct type?
					boundary.updateUnary(direction, target, null); // global supertype
					boundary.updateInstantiation(direction, feature.getEAttributeType(), target);
				}

			}

			@Override
			public void visitElement(EObject source) {
				boundary.updateUnary(direction, source, source.eClass());
				boundary.updateUnary(direction, source, null); // global supertype
				boundary.updateInstantiation(direction, source.eClass(), source);
			}

			@Override
			public void visitExternalReference(EObject source, EReference feature, Object target) {
				if (target!=null) {
					boundary.updateBinaryEdge(direction, source, target, feature);
					boundary.updateBinaryEdge(direction, source, target, null); // global supertype
				}
			}

			@Override
			public void visitInternalReference(EObject source,
					EReference feature, Object target) {
				if (target!=null) {
					boundary.updateBinaryEdge(direction, source, target, feature);
					boundary.updateBinaryEdge(direction, source, target, null); // global supertype
					if (feature.isContainment()) boundary.updateContainment(direction, source, target);
				}				
			}
		});
	}
	
	public void registerSensitiveTerm(Object element, PredicateEvaluatorNode termEvaluatorNode) {
		Set<PredicateEvaluatorNode> nodes = sensitiveTerms.get(element);
		if (nodes == null) {
			nodes = new HashSet<PredicateEvaluatorNode>();
			sensitiveTerms.put(element, nodes);
		}
		nodes.add(termEvaluatorNode);
	}


	public void unregisterSensitiveTerm(Object element, PredicateEvaluatorNode termEvaluatorNode) {
		Set<PredicateEvaluatorNode> nodes = sensitiveTerms.get(element);
		nodes.remove(termEvaluatorNode);
		if (nodes.isEmpty())
			sensitiveTerms.remove(element);
	}


}
