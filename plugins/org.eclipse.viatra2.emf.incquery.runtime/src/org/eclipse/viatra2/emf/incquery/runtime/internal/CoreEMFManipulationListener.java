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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.EMFIncQueryRuntimeLogger;
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
	
	protected ReteEngine<?> reteEngine;
	protected ReteBoundary<?> boundary;
	protected ReteContainer headContainer;
	protected EMFPatternMatcherRuntimeContext<?> context;
	protected EMFIncQueryRuntimeLogger logger;
	
//	/** 
//	 * Contains the set of resources that have finished loading and are not unloaded yet.
//	 * Notification is accepted only from these resources.
//	 * If null, no check is performed and the collection is not maintained. 
//	 */
//	protected Collection<Resource> activeResources;

	/** TermEvaluatorNodes that have to be notified on changes affecting a given ModelElement */
	protected Map<Object, Set<PredicateEvaluatorNode>> sensitiveTerms;
	
	/**
	 * Prerequisite: reteEngine has its network, framework and boundary fields
	 * initialized
	 * 
	 * @param reteEngine
	 */
	public CoreEMFManipulationListener(ReteEngine<?> reteEngine, EMFPatternMatcherRuntimeContext<?> context, EMFIncQueryRuntimeLogger logger) { 
		//, boolean resourceLoadingFilter) {
		super();
		this.reteEngine = reteEngine;
		this.context = context;
		this.boundary = reteEngine.getBoundary();
		this.headContainer = reteEngine.getReteNet().getHeadContainer();

		this.sensitiveTerms = new HashMap<Object, Set<PredicateEvaluatorNode>>();
		this.logger = logger;
//		if (resourceLoadingFilter) this.activeResources = new HashSet<Resource>();
	}
	
	// TODO deferred notifications from TransactionalEditingDomains?
	@SuppressWarnings("deprecation")
	public void handleEMFNotification(final Notification notification) {
		try {
			final Object oldValue = notification.getOldValue();
			final Object newValue = notification.getNewValue();
			final int eventType = notification.getEventType();
			switch(eventType) {
			case Notification.ADD: 
				featureUpdate(Direction.INSERT, newValue, notification);
				break;
			case Notification.ADD_MANY: 
				for (Object newElement: (Collection<?>)newValue) 
					featureUpdate(Direction.INSERT, newElement, notification);
				break;
			case Notification.CREATE: 
				break;
			case Notification.MOVE: break; // currently no support for ordering
			case Notification.REMOVE: 
				featureUpdate(Direction.REVOKE, oldValue, notification);
				break;
			case Notification.REMOVE_MANY:
				for (Object oldElement: (Collection<?>)oldValue) 
					featureUpdate(Direction.REVOKE, oldElement, notification);
				break;
			//case Notification.REMOVING_ADAPTER: break;
			case Notification.RESOLVE:  //TODO is it safe to ignore all of them? 
				break;
			case Notification.UNSET:  //TODO Fallthrough?			
			case Notification.SET:
				featureUpdate(Direction.REVOKE, oldValue, notification);
				featureUpdate(Direction.INSERT, newValue, notification);			
				break;
			case Notification.REMOVING_ADAPTER:
				break;
			}
		} catch (Exception ex) {
			logger.logError(
					"EMF-IncQuery encountered an error in processing the EMF model. " +
					"This happened while handling the following update notification: " + 
					notification, ex);
			//throw new IncQueryRuntimeException(IncQueryRuntimeException.EMF_MODEL_PROCESSING_ERROR, ex);
		}
	}
	
	private void featureUpdate(Direction direction, Object changedValue, Notification notification) {
		final Object notifier = notification.getNotifier();		

		if (changedValue == null) return; // null-valued attributes / references are simply not stored
	
		if (notifier instanceof ResourceSet)
		{
			if (notification.getFeatureID(ResourceSet.class) == ResourceSet.RESOURCE_SET__RESOURCES)
			{
				Resource resourceChanged = (Resource)changedValue;
				EList<EObject> contents = resourceChanged.getContents();
				for (EObject eObject : contents) {
					attachedTree(eObject, direction);
				}		
			}
		}
		else if (notifier instanceof Resource)
		{
			if (notification.getFeatureID(Resource.class) == Resource.RESOURCE__CONTENTS)
			{
				EObject eObjectChanged = (EObject)changedValue;
				attachedTree(eObjectChanged, direction);
			}
		}
		else if (notifier instanceof EObject)
		{
			Object oFeature = notification.getFeature();
			if (oFeature instanceof EStructuralFeature)
				EMFModelComprehension.visitFeature(visitor(direction), (EObject) notifier, (EStructuralFeature) oFeature, changedValue);
//			if (oFeature instanceof EAttribute) {
//				EAttribute feature = (EAttribute) oFeature;
//				if (!feature.isDerived()) {
//					attributeReferenceUpdate(direction, feature, notifier, changedValue);
//				}
//			} else if (oFeature instanceof EReference) {
//				EReference feature = (EReference) oFeature;
//				if (!feature.isDerived()) {
//					if (feature.isContainment()) {
//						containmentReferenceUpdate(direction, feature, notifier, changedValue);
//						attachedTree((EObject)changedValue, direction);
//					} else { 
//						nonContainmentReferenceUpdate(direction, feature, notifier, changedValue);
//					}
//				}
//			}
		}

	}

	public void attachedTree(EObject root, final Direction direction) {
		new EMFContainmentHierarchyTraversal(root).accept(visitor(direction));
	}

	/**
	 * @param direction
	 * @return
	 */
	protected EMFVisitor visitor(final Direction direction) {
		return new EMFVisitor(){

			@Override
			public void visitAttribute(EObject source, EAttribute feature, Object target) {
				attributeReferenceUpdate(direction, feature, source, target);
			}

			@Override
			public void visitElement(EObject source) {
				nodeUpdateCore(direction, source.eClass(), source);
			}

//			@Override
//			public void visitExternalReference(EObject source, EReference feature, EObject target) {
//				nonContainmentReferenceUpdate(direction, feature, source, target);	
//			}

			@Override
			public void visitNonContainmentReference(EObject source, EReference feature, EObject target) {
				nonContainmentReferenceUpdate(direction, feature, source, target);	
			}
			
			/* (non-Javadoc)
			 * @see org.eclipse.viatra2.emf.incquery.runtime.internal.EMFVisitor#visitInternalContainment(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EReference, org.eclipse.emf.ecore.EObject)
			 */
			@Override
			public void visitInternalContainment(EObject source, EReference feature, EObject target) {
				containmentReferenceUpdate(direction, feature, source, target);
			}
		};
	}
	
	private void nodeUpdateCore(Direction direction, Object nodeType, Object node) {
		boundary.updateUnary(direction, node, nodeType);
		boundary.updateUnary(direction, node, null); // global supertype
		boundary.updateInstantiation(direction, nodeType, node);
	}
	private void edgeUpdateCore(Direction direction, Object oFeature, Object source, Object target) {
		boundary.updateBinaryEdge(direction, source, target, oFeature);
		boundary.updateBinaryEdge(direction, source, target, null); // global supertype
	}
	private void containmentReferenceUpdate(Direction direction, EReference reference, Object source, Object target) {
		if (target != null) {
			edgeUpdateCore(direction, reference, source, target);
			boundary.updateContainment(direction, source, target);
			if (reference.getEOpposite() != null) // update opposite also
				edgeUpdateCore(direction, reference.getEOpposite(), target, source);			
		}
	}
	private void nonContainmentReferenceUpdate(Direction direction, EReference reference, Object source, Object target) {
		if (target != null) {
			if (reference.getEOpposite() != null && reference.getEOpposite().isContainment()) {
				return; // SKIP core update of containment's opposite, defer to when containment is updated		
			} else {
				if (direction==Direction.INSERT) context.considerForExpansion((EObject) target);
				edgeUpdateCore(direction, reference, source, target);	
			}
		}
	}
	private void attributeReferenceUpdate(Direction direction, EAttribute attribute, Object host, Object value) {
		if (value != null) {
			edgeUpdateCore(direction, attribute, host, value);
			// TODO Workaround, Gaben: fix this (NPE on deletion, duplicate containment with identical attribute values)
			//boundary.updateContainment(direction, host, value);
			nodeUpdateCore(direction, attribute.getEAttributeType(), value); // FIXME is this the correct, direct type?
		}
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
