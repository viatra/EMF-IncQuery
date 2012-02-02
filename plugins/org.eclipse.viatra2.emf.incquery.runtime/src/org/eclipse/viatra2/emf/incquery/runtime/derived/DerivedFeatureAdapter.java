/*******************************************************************************
 * Copyright (c) 2004-2011 Abel Hegedus and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    pvmellor - original code (http://wiki.eclipse.org/EMF/Recipes#Recipe:_Derived_Attribute_Notifier)
 *    Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.runtime.derived;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra2.emf.incquery.runtime.internal.EMFModelComprehension;
import org.eclipse.viatra2.emf.incquery.runtime.internal.EMFVisitor;

/**
 * @author Abel Hegedus
 *
 * TODO: move to incquery code
 *
 *
 */
public class DerivedFeatureAdapter extends AdapterImpl {
	private final InternalEObject source;
	private final EStructuralFeature derivedFeature;
	private final DerivedFeatureEMFVisitor visitor = new DerivedFeatureEMFVisitor();
	
	/**
	 * Only used for single reference!
	 */
	private Object currentValue;
	private Object oldValue;
	private EClassifier type;

	private List<EStructuralFeature> localFeatures = new ArrayList<EStructuralFeature>();
	private List<DependentFeaturePath> featurePaths = new ArrayList<DerivedFeatureAdapter.DependentFeaturePath>();

	/*
	 * Convenience constructor for a local and navigated dependency
	 */
	public DerivedFeatureAdapter(EObject source, EStructuralFeature derivedFeature,
			EStructuralFeature navigationFeature, EStructuralFeature dependantFeature, EStructuralFeature localFeature) {
		this(source, derivedFeature);
		addNavigatedDependency(navigationFeature, dependantFeature);
		addLocalDependency(localFeature);
	}

	/*
	 * Convenience constructor for a navigated dependency
	 */
	public DerivedFeatureAdapter(EObject source, EStructuralFeature derivedFeature,
			EStructuralFeature navigationFeature, EStructuralFeature dependantFeature) {
		this(source, derivedFeature);
		addNavigatedDependency(navigationFeature, dependantFeature);
	}

	/*
	 * Convenience constructor for a local dependency
	 */
	public DerivedFeatureAdapter(EObject source, EStructuralFeature derivedFeature,
			EStructuralFeature localFeature) {
		this(source, derivedFeature);
		addLocalDependency(localFeature);
	}

	public DerivedFeatureAdapter(EObject source, EStructuralFeature derivedFeature) {
		super();
		this.source = (InternalEObject) source;
		this.derivedFeature = derivedFeature;
		source.eAdapters().add(this);
	}

	public void addNavigatedDependency(EStructuralFeature navigationFeature,
			EStructuralFeature dependantFeature) {
		featurePaths.add(new DependentFeaturePath(navigationFeature, dependantFeature));
	}

	public void addLocalDependency(EStructuralFeature localFeature) {
		localFeatures.add(localFeature);
	}

	@Override
	public void notifyChanged(Notification notification) {
		//System.err.println("[Source: " + derivedFeature.getName() + "] New notification: " + notification);
		for(DependentFeaturePath path : featurePaths) {
			if (notification.getFeature().equals(path.getNavigationFeature())) {
				//System.err.println("Handling notification.");
				switch (notification.getEventType()) {
				case Notification.SET:
					EObject newValue = (EObject) notification.getNewValue();
					EObject oldValue = (EObject) notification.getOldValue();
					if (oldValue != null)
						oldValue.eAdapters().remove(path.getDependantAdapter());
					else System.err.println("oldValue null");
					if (newValue != null)
						newValue.eAdapters().add(path.getDependantAdapter());
					else System.err.println("newValue null");
					break;
				case Notification.ADD:
					EObject added = (EObject) notification.getNewValue();
					added.eAdapters().add(path.getDependantAdapter());
					break;
				case Notification.ADD_MANY: 
					EObject newValueCollection = (EObject) notification.getNewValue();
					for (Object newElement: (Collection<?>)newValueCollection) {
						((Notifier) newElement).eAdapters().add(path.getDependantAdapter());
					}
					break;
				case Notification.REMOVE:
					EObject removed = (EObject) notification.getOldValue();
					removed.eAdapters().remove(path.getDependantAdapter());
					break;
				case Notification.REMOVE_MANY:
					EObject oldValueCollection = (EObject) notification.getOldValue();
					for (Object oldElement: (Collection<?>)oldValueCollection) {
						((Notifier) oldElement).eAdapters().remove(path.getDependantAdapter());
					}
					break;
				case Notification.UNSET: 			
					EObject unset = (EObject) notification.getOldValue();
					unset.eAdapters().remove(path.getDependantAdapter());
					break;
				case Notification.CREATE: 
				case Notification.MOVE: // currently no support for ordering
				case Notification.RESOLVE: //TODO is it safe to ignore all of them? 
				case Notification.REMOVING_ADAPTER:
					break;
				default:
					System.err.println("Unhandled notification: " + notification.getEventType());
					return; // No notification
				}
				refreshDerivedFeature();
			}
		}
		if (localFeatures.contains(notification.getFeature())) {
			//System.err.println("Handling notification.");
			refreshDerivedFeature();
		}
	}

	@SuppressWarnings("unchecked")
	private void refreshDerivedFeature() {
		//System.err.println("[Notify: " + derivedFeature.getName() + "] Derived refresh.");
		if (source.eNotificationRequired()) {
			if(type == null) {
				type = derivedFeature.getEType();
			}
			if (derivedFeature.isMany()) {
				if(currentValue != null) {
					oldValue = new HashSet<EObject>((Collection<EObject>) currentValue);
				} else {
					oldValue = new HashSet<EObject>();
				}
				//if(currentValue == null) {
				currentValue = new HashSet<EObject>();
				//} else {
				//	((Collection<EObject>) currentValue).clear();
				//}
				Collection<? extends Object> targets = (Collection<? extends Object>) source.eGet(derivedFeature);
				for (Object target : targets) {
					EMFModelComprehension.visitFeature(visitor, source, derivedFeature, target);	
				}
				if(currentValue instanceof Collection<?> && oldValue instanceof Collection<?>) {
					((Collection<?>)oldValue).removeAll((Collection<?>) currentValue);
					if(((Collection<?>) oldValue).size() > 0) {
						sendRemoveManyNotification(source, derivedFeature, oldValue);
					}
				}
			} else {
				Object target = source.eGet(derivedFeature);
				EMFModelComprehension.visitFeature(visitor, source, derivedFeature, target);
			}
		}
	}
	
	private class DependentFeaturePath{
		private EStructuralFeature dependantFeature = null;
		private EStructuralFeature navigationFeature = null;
		
		private AdapterImpl dependantAdapter = new AdapterImpl() {
			
			@Override
			public void notifyChanged(Notification msg) {
				//System.err.println("[Dependant: " + derivedFeature.getName() + "] New notification: " + msg);
				if (msg.getFeature().equals(dependantFeature) ) {
					//System.err.println("Handling notification.");
					switch (msg.getEventType()) {
						case Notification.ADD:
						case Notification.ADD_MANY:
						case Notification.SET:
						case Notification.UNSET:
						case Notification.REMOVE:
						case Notification.REMOVE_MANY:
						case Notification.CREATE:
						case Notification.MOVE:
						case Notification.RESOLVE:
						case Notification.REMOVING_ADAPTER:
							break;
						default:
							System.err.println("Unhandled notification: " + msg.getEventType());
							return; // No notification
						}
					refreshDerivedFeature();
				}
			}
		};
		
		/**
		 * 
		 */
		public DependentFeaturePath(EStructuralFeature navigationFeature, EStructuralFeature dependantFeature) {
			this.dependantFeature = dependantFeature;
			this.navigationFeature = navigationFeature;
		}

		/**
		 * @return the dependantAdapter
		 */
		public AdapterImpl getDependantAdapter() {
			return dependantAdapter;
		}

		/**
		 * @return the dependantFeature
		 */
		public EStructuralFeature getDependantFeature() {
			return dependantFeature;
		}

		/**
		 * @return the navigationFeature
		 */
		public EStructuralFeature getNavigationFeature() {
			return navigationFeature;
		}
	}
	
	
	
	/**
	 * @param direction
	 * @return
	 */
	protected class DerivedFeatureEMFVisitor extends EMFVisitor{ 

		@Override
		public void visitAttribute(EObject source, EAttribute feature, Object target) {
			//System.err.println("Attribute refresh.");
			// send set notification
			sendSetNotification(source, feature, currentValue, target);
			storeSingleValue(feature, target);
		}


		@Override
		public void visitElement(EObject source) {
			return;
		}

//			@Override
//			public void visitExternalReference(EObject source, EReference feature, EObject target) {
//			}

		@Override
		public void visitNonContainmentReference(EObject source, EReference feature, EObject target) {
			//System.err.println("Non-containment reference refresh.");
			sendNotificationForEReference(source, feature, target);
		}

		@Override
		public void visitInternalContainment(EObject source, EReference feature, EObject target) {
			//System.err.println("Containment reference refresh.");
			sendNotificationForEReference(source, feature, target);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.viatra2.emf.incquery.runtime.internal.EMFVisitor#pruneSubtrees(org.eclipse.emf.ecore.EObject)
		 */
		@Override
		public boolean pruneSubtrees(EObject source) {
			return true;
		}
	}
	
	/**
	 * @param source
	 * @param feature
	 * @param target
	 */
	private void sendSetNotification(EObject source, EStructuralFeature feature, Object oldTarget, Object target) {
		source.eNotify(new ENotificationImpl((InternalEObject) source, Notification.SET,
				feature, oldTarget, target));
	}
	
	/**
	 * @param source
	 * @param feature
	 * @param target
	 */
	private void sendAddNotification(EObject source, EStructuralFeature feature, Object target) {
		source.eNotify(new ENotificationImpl((InternalEObject) source, Notification.ADD,
				feature, null, target));
	}
	
	/**
	 * @param source
	 * @param feature
	 * @param target
	 */
	private void sendRemoveManyNotification(EObject source, EStructuralFeature feature, Object oldTarget) {
		source.eNotify(new ENotificationImpl((InternalEObject) source, Notification.REMOVE_MANY,
				feature, oldTarget, null));
	}
	
	/**
	 * @param source
	 * @param feature
	 * @param target
	 */
	private void sendRemoveNotification(EObject source, EStructuralFeature feature, Object oldTarget) {
		source.eNotify(new ENotificationImpl((InternalEObject) source, Notification.REMOVE,
				feature, oldTarget, null));
	}
	
	@SuppressWarnings("unchecked")
	private void sendNotificationForEReference(EObject source, EReference feature, EObject target) {
		if(feature.isMany() && oldValue instanceof Collection<?> && currentValue instanceof Collection<?>) {
				// send ADD notification
			if(!((Collection<?>) oldValue).contains(target)) {
				sendAddNotification(source, feature, target);
				// add to currentValue
			}
			((Collection<EObject>)currentValue).add(target);
		} else {
			sendSetNotification(source, feature, currentValue, target);
		}
	}

	/**
	 * @param feature
	 * @param target
	 */
	private void storeSingleValue(EAttribute feature, Object target) {
		// store current value
		if(feature.isChangeable()) {
			if(target instanceof EObject) {
					currentValue = EcoreUtil.copy((EObject)target);
				}
		} else {
			currentValue = target;
		}
	}
}
