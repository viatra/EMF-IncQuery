/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.base.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.viatra2.emf.incquery.base.api.DataTypeListener;
import org.eclipse.viatra2.emf.incquery.base.api.FeatureListener;
import org.eclipse.viatra2.emf.incquery.base.api.InstanceListener;
import org.eclipse.viatra2.emf.incquery.base.comprehension.EMFModelComprehension;
import org.eclipse.viatra2.emf.incquery.base.comprehension.EMFVisitor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Table;

public class NavigationHelperContentAdapter extends EContentAdapter {

	public static final EClass eObjectClass = EcorePackage.eINSTANCE
			.getEObject();

	// value -> feature (attr or ref) -> holder(s)
	protected Map<Object, Map<EStructuralFeature, Set<EObject>>> featureMap;

	// feature -> holder(s)
	protected Map<EStructuralFeature, Set<EObject>> reversedFeatureMap;

	// eclass -> instance(s)
	protected Map<EClass, Set<EObject>> instanceMap;

	// edatatype -> multiset of value(s)
	protected Map<EDataType, Map<Object, Integer>> dataTypeMap;
	
	// source -> feature -> proxy target -> delayed visitors
	protected Table<EObject, EReference, ListMultimap<EObject, EMFVisitor>> unresolvableProxyFeaturesMap;
	
	// proxy source -> delayed visitors
	protected ListMultimap<EObject, EMFVisitor> unresolvableProxyObjectsMap;


	// static for all eClasses whose instances were encountered at least once
	private static Set<EClass> knownClasses = new HashSet<EClass>();
	// static for eclass -> all subtypes in knownClasses
	protected static Map<EClass, Set<EClass>> subTypeMap = new HashMap<EClass, Set<EClass>>();
	

	private NavigationHelperImpl navigationHelper;

	// since last run of after-update callbacks
	boolean isDirty = false;

	public NavigationHelperContentAdapter(NavigationHelperImpl navigationHelper) {
		this.navigationHelper = navigationHelper;
		this.featureMap = new HashMap<Object, Map<EStructuralFeature, Set<EObject>>>();
		this.instanceMap = new HashMap<EClass, Set<EObject>>();
		this.dataTypeMap = new HashMap<EDataType, Map<Object, Integer>>();
		this.unresolvableProxyFeaturesMap = HashBasedTable.create();
		this.unresolvableProxyObjectsMap = ArrayListMultimap.create();
	}

	public Map<EStructuralFeature, Set<EObject>> getReversedFeatureMap() {
		if (reversedFeatureMap == null) {
			reversedFeatureMap = new HashMap<EStructuralFeature, Set<EObject>>();
			initReversedFeatureMap();
		}
		return reversedFeatureMap;
	}

	@Override
	public void notifyChanged(Notification notification) {
		try {
			baseHandleNotification(notification);

			Object oFeature = notification.getFeature();
			final Object oNotifier = notification.getNotifier();
			if (oNotifier instanceof EObject
					&& oFeature instanceof EStructuralFeature) {
				final EObject notifier = (EObject) oNotifier;
				final EStructuralFeature feature = (EStructuralFeature) oFeature;
				final Object oldValue = notification.getOldValue();
				final Object newValue = notification.getNewValue();
				final int eventType = notification.getEventType();
				switch (eventType) {
				case Notification.ADD:
					featureUpdate(true, notifier, feature, newValue);
					break;
				case Notification.ADD_MANY:
					for (Object newElement : (Collection<?>) newValue)
						featureUpdate(true, notifier, feature, newElement);
					break;
				case Notification.CREATE:
					break;
				case Notification.MOVE:
					break; // currently no support for ordering
				case Notification.REMOVE:
					featureUpdate(false, notifier, feature, oldValue);
					break;
				case Notification.REMOVE_MANY:
					for (Object oldElement : (Collection<?>) oldValue)
						featureUpdate(false, notifier, feature, oldElement);
					break;
				case Notification.REMOVING_ADAPTER: break;
				case Notification.RESOLVE:
					featureResolve(notifier, feature, oldValue, newValue);
					break;
				case Notification.UNSET:
				case Notification.SET:
					featureUpdate(false, notifier, feature, oldValue);
					featureUpdate(true, notifier, feature, newValue);
					break;
				}
			}
			// if (feature instanceof EReference) {
			// handleReferenceChange(notification, (EReferenceImpl) feature);
			// }
			// if (feature instanceof EAttribute) {
			// handleAttributeChange(notification, (EAttribute) feature);
			// }
		} catch (Exception ex) {
			navigationHelper
					.getLogger()
					.fatal("EMF-IncQuery encountered an error in processing the EMF model. "
							+ "This happened while handling the following update notification: "
							+ notification, ex);
			// throw new
			// IncQueryRuntimeException(IncQueryRuntimeException.EMF_MODEL_PROCESSING_ERROR,
			// ex);
		}

		if (isDirty) {
			isDirty = false;
			navigationHelper.runAfterUpdateCallbacks();
		}

	}


	private void featureResolve(EObject source, EStructuralFeature feature, Object oldValue, Object newValue) {
		EReference reference = (EReference) feature;
		EObject proxy = (EObject) oldValue;
		EObject resolved = (EObject) newValue;
		
		final List<EMFVisitor> objectVisitors = popVisitorsSuspendedOnObject(proxy);
		for (EMFVisitor visitor : objectVisitors) {
			EMFModelComprehension.traverseObject(visitor, resolved);
		}
		
		final List<EMFVisitor> featureVisitors = popVisitorsSuspendedOnFeature(source, reference, proxy);
		for (EMFVisitor visitor : featureVisitors) {
			EMFModelComprehension.traverseFeature(visitor, source, reference, resolved);
		}
	}

	private void featureUpdate(boolean isInsertion, EObject notifier,
			EStructuralFeature feature, Object value) {
		EMFModelComprehension.traverseFeature(visitor(isInsertion), notifier,
				feature, value);
	}

	@Override
	protected void addAdapter(Notifier notifier) {
		try {
			if (notifier instanceof EObject) {
				EMFModelComprehension.traverseObject(visitor(true),
						(EObject) notifier);
			}
			super.addAdapter(notifier);
		} catch (Exception ex) {
			navigationHelper.getLogger().fatal(
					"EMF-IncQuery encountered an error in processing the EMF model. "
							+ "This happened while trying to add the object: "
							+ notifier, ex);
			// throw new
			// IncQueryRuntimeException(IncQueryRuntimeException.EMF_MODEL_PROCESSING_ERROR,
			// ex);
		}
	}

	@Override
	protected void removeAdapter(Notifier notifier) {
		try {
			if (notifier instanceof EObject) {
				EMFModelComprehension.traverseObject(visitor(false),
						(EObject) notifier);
			}
			super.removeAdapter(notifier);
		} catch (Exception ex) {
			navigationHelper
					.getLogger()
					.fatal("EMF-IncQuery encountered an error in processing the EMF model. "
							+ "This happened while trying to remove the object: "
							+ notifier, ex);
			// throw new
			// IncQueryRuntimeException(IncQueryRuntimeException.EMF_MODEL_PROCESSING_ERROR,
			// ex);
		}
	}

	protected EMFVisitor visitor(final boolean isInsertion) {
		return new NavigationHelperVisitor.ChangeVisitor(navigationHelper,
				isInsertion);
	}

	// @SuppressWarnings("unchecked")
	// public void handleReferenceChange(Notification notification, EReference
	// ref) {
	// EObject notifier = (EObject) notification.getNotifier();
	//
	// if (notification.getEventType() == Notification.REMOVE_MANY) {
	// for (EObject oldValue: (Collection<? extends EObject>)
	// notification.getOldValue()) {
	// handleReferenceRemove(ref, oldValue, null, notifier);
	// }
	// }
	// else if (notification.getEventType() == Notification.ADD_MANY) {
	// for (EObject newValue: (Collection<? extends EObject>)
	// notification.getNewValue()) {
	// handleReferenceAdd(ref, newValue, notifier);
	// }
	// }
	// else if (notification.getEventType() == Notification.ADD) {
	// handleReferenceAdd(ref, (EObject) notification.getNewValue(), notifier);
	// }
	// else if (notification.getEventType() == Notification.REMOVE) {
	// handleReferenceRemove(ref, (EObject) notification.getOldValue(),
	// (EObject) notification.getNewValue(), notifier);
	// }
	// if (notification.getEventType() == Notification.SET ||
	// notification.getEventType() == Notification.UNSET) {
	// EObject oldValue = (EObject) notification.getOldValue();
	// EObject newValue = (EObject) notification.getNewValue();
	//
	// if (oldValue != null) {
	// removeFeatureTuple(ref, oldValue, notifier);
	// removeInstanceTuple(oldValue.eClass(), oldValue);
	//
	// if (ref.isContainment())
	// navigationHelper.getVisitor().visitObjectForEAttribute(oldValue,
	// navigationHelper.getObservedFeatures(),
	// navigationHelper.getObservedDataTypes(), false);
	// }
	// if (newValue != null) {
	// handleReferenceAdd(ref, newValue, notifier);
	// }
	// }
	// }
	//
	// public void handleReferenceAdd(EReference ref, EObject newValue, EObject
	// notifier) {
	// insertFeatureTuple(ref, newValue, notifier);
	// insertInstanceTuple(newValue.eClass(), newValue);
	// if (ref.isContainment()) {
	// navigationHelper.getVisitor().visitObjectForEAttribute(newValue,
	// navigationHelper.getObservedFeatures(),
	// navigationHelper.getObservedDataTypes(), true);
	// }
	// }
	//
	// public void handleReferenceRemove(EReference ref, EObject oldValue,
	// EObject newValue, EObject notifier) {
	// removeFeatureTuple(ref, oldValue, notifier);
	// removeInstanceTuple(oldValue.eClass(), oldValue);
	//
	// if (ref.isContainment()) {
	// navigationHelper.getVisitor().visitObjectForEAttribute(oldValue,
	// navigationHelper.getObservedFeatures(),
	// navigationHelper.getObservedDataTypes(), false);
	// }
	//
	// if (newValue != null) {
	// handleReferenceAdd(ref, newValue, notifier);
	// }
	// }
	//
	// public void handleAttributeChange(Notification notification, EAttribute
	// attribute) {
	// Object oldValue = notification.getOldValue();
	// Object newValue = notification.getNewValue();
	// EObject notifier = (EObject) notification.getNotifier();
	// final EDataType eAttributeType = attribute.getEAttributeType();
	//
	// if (notification.getEventType() == Notification.SET ||
	// notification.getEventType() == Notification.UNSET) {
	// if (oldValue != null) {
	// removeFeatureTuple(attribute, oldValue, notifier);
	// dataTypeInstanceUpdate(eAttributeType, oldValue, false);
	// }
	// if (newValue != null) {
	// insertFeatureTuple(attribute, newValue, notifier);
	// dataTypeInstanceUpdate(eAttributeType, newValue, true);
	// }
	// }
	// }

	private void addToFeatureMap(EStructuralFeature feature, Object value,
			EObject holder) {
		Map<EStructuralFeature, Set<EObject>> mapVal = featureMap.get(value);
		Set<EObject> setVal = null;

		if (mapVal == null) {
			mapVal = new HashMap<EStructuralFeature, Set<EObject>>();
		}
		if ((setVal = mapVal.get(feature)) == null) {
			setVal = new HashSet<EObject>();
		}
		setVal.add(holder);
		mapVal.put(feature, setVal);
		featureMap.put(value, mapVal);
	}

	private void addToReversedFeatureMap(EStructuralFeature feature,
			EObject holder) {
		Set<EObject> setVal = reversedFeatureMap.get(feature);

		if (setVal == null) {
			setVal = new HashSet<EObject>();
		}
		setVal.add(holder);
		reversedFeatureMap.put(feature, setVal);
	}

	private void removeFromReversedFeatureMap(EStructuralFeature feature,
			EObject holder) {
		if (reversedFeatureMap.containsKey(feature)) {
			reversedFeatureMap.get(feature).remove(holder);

			if (reversedFeatureMap.get(feature).size() == 0) {
				reversedFeatureMap.remove(feature);
			}
		}
	}

	private void removeFromFeatureMap(EStructuralFeature feature, Object value,
			EObject holder) {
		if (featureMap.containsKey(value)
				&& featureMap.get(value).containsKey(feature)) {
			featureMap.get(value).get(feature).remove(holder);

			if (featureMap.get(value).get(feature).size() == 0) {
				featureMap.get(value).remove(feature);
			}

			if (featureMap.get(value).size() == 0) {
				featureMap.remove(value);
			}
		}
	}

	private void addToDataTypeMap(EDataType type, Object value) {
		Map<Object, Integer> valMap = dataTypeMap.get(type);
		if (valMap == null) {
			valMap = new HashMap<Object, Integer>();
			dataTypeMap.put(type, valMap);
		}
		if (valMap.get(value) == null) {
			valMap.put(value, Integer.valueOf(1));
		} else {
			Integer count = valMap.get(value);
			valMap.put(value, ++count);
		}
	}

	private void removeFromDataTypeMmap(EDataType type, Object value) {
		Map<Object, Integer> valMap = dataTypeMap.get(type);
		if (valMap != null) {
			if (valMap.get(value) != null) {
				Integer count = valMap.get(value);
				if (--count == 0) {
					valMap.remove(value);
				} else {
					valMap.put(value, count);
				}
			}
			if (valMap.size() == 0) {
				dataTypeMap.remove(type);
			}
		}
	}

	public void insertFeatureTuple(EStructuralFeature feature, Object value,
			EObject holder) {
		// if ((navigationHelper.getType() == NavigationHelperType.ALL) ||
		// navigationHelper.getObservedFeatures().contains(feature)) {
		addToFeatureMap(feature, value, holder);

		if (reversedFeatureMap != null) {
			addToReversedFeatureMap(feature, holder);
		}

		isDirty = true;
		notifyFeatureListeners(holder, feature, value, true);
		// }
	}

	public void removeFeatureTuple(EStructuralFeature feature, Object value,
			EObject holder) {
		// if ((navigationHelper.getType() == NavigationHelperType.ALL) ||
		// navigationHelper.getObservedFeatures().contains(feature)) {
		removeFromFeatureMap(feature, value, holder);

		if (reversedFeatureMap != null) {
			removeFromReversedFeatureMap(feature, holder);
		}

		isDirty = true;
		notifyFeatureListeners(holder, feature, value, false);
		// }
	}

	public void dataTypeInstanceUpdate(EDataType type, Object value,
			boolean isInsertion) {
		// if ((navigationHelper.getType() == NavigationHelperType.ALL) ||
		// navigationHelper.getObservedDataTypes().contains(type)) {
		if (isInsertion) {
			addToDataTypeMap(type, value);
		} else {
			removeFromDataTypeMmap(type, value);
		}
		isDirty = true;
		notifyDataTypeListeners(type, value, isInsertion);
		// }
	}

	public void insertInstanceTuple(EClass key, EObject value) {
		// if (navigationHelper.isObserved(key)) {
		if (instanceMap.containsKey(key)) {
			instanceMap.get(key).add(value);
		} else {
			HashSet<EObject> set = new HashSet<EObject>();
			set.add(value);
			instanceMap.put(key, set);
		}

		isDirty = true;
		notifyInstanceListeners(key, value, true);
		// }
	}

	void removeInstanceTuple(EClass key, EObject value) {
		// if (navigationHelper.isObserved(key)) {
		if (instanceMap.containsKey(key)) {
			instanceMap.get(key).remove(value);
			if (instanceMap.get(key).size() == 0) {
				instanceMap.remove(key);
			}
		}

		isDirty = true;
		notifyInstanceListeners(key, value, false);
		// }
	}

	/**
	 * Returns true if sup is a supertype of sub.
	 * 
	 * @param sub
	 *            subtype
	 * @param sup
	 *            supertype
	 * @return
	 */
	private boolean isSubTypeOf(EClass sub, EClass sup) {
		Set<EClass> set = subTypeMap.get(sup);
		if (set != null) {
			return set.contains(sub);
		} else {
			return false;
		}
	}

	/**
	 * put subtype information into cache
	 */
	protected void maintainTypeHierarchy(EClass clazz) {
		if (!knownClasses.contains(clazz)) {
			knownClasses.add(clazz);

			for (EClass superType : clazz.getEAllSuperTypes()) {
				maintainTypeHierarhyInternal(clazz, superType);
			}
			maintainTypeHierarhyInternal(clazz, eObjectClass);
		}
	}

	private void maintainTypeHierarhyInternal(EClass clazz, EClass superType) {
		if (navigationHelper.directlyObservedClasses.contains(superType)) {
			navigationHelper.getAllObservedClasses().add(clazz);
		}

		Set<EClass> set = subTypeMap.get(superType);
		if (set == null) {
			set = new HashSet<EClass>();
			subTypeMap.put(superType, set);
		}
		set.add(clazz);
	}

	private void notifyDataTypeListeners(EDataType type, Object value,
			boolean isInsertion) {
		for (Entry<DataTypeListener, Collection<EDataType>> entry : navigationHelper
				.getDataTypeListeners().entrySet()) {
			if (entry.getValue().contains(type)) {
				if (isInsertion) {
					entry.getKey().dataTypeInstanceInserted(type, value);
				} else {
					entry.getKey().dataTypeInstanceDeleted(type, value);
				}
			}
		}
	}

	private void notifyFeatureListeners(EObject host,
			EStructuralFeature feature, Object value, boolean isInsertion) {
		for (Entry<FeatureListener, Collection<EStructuralFeature>> entry : navigationHelper
				.getFeatureListeners().entrySet()) {
			if (entry.getValue().contains(feature)) {
				if (isInsertion) {
					entry.getKey().featureInserted(host, feature, value);
				} else {
					entry.getKey().featureDeleted(host, feature, value);
				}
			}
		}
	}

	private void notifyInstanceListeners(EClass clazz, EObject instance,
			boolean isInsertion) {
		for (Entry<InstanceListener, Collection<EClass>> entry : navigationHelper
				.getInstanceListeners().entrySet()) {
			for (EClass sup : entry.getValue()) {
				if (isSubTypeOf(clazz, sup) || clazz.equals(sup)) {
					if (isInsertion) {
						entry.getKey().instanceInserted(sup, instance);
					} else {
						entry.getKey().instanceDeleted(sup, instance);
					}
				}
			}
		}
	}

	private void initReversedFeatureMap() {
		for (Entry<Object, Map<EStructuralFeature, Set<EObject>>> valueToFeatureHolderMap : featureMap.entrySet()) {
			for (Entry<EStructuralFeature, Set<EObject>> featureToHolders : valueToFeatureHolderMap.getValue().entrySet()) {
				final EStructuralFeature feature = featureToHolders.getKey();
				for (EObject holder : featureToHolders.getValue()) {
					addToReversedFeatureMap(feature, holder);
				}	
			}
		}
	}

	// WORKAROUND for EContentAdapter bug
	// where proxy resolution during containment traversal would add a new
	// Resource to the ResourceSet (and thus the adapter)
	// that will be set as target twice:
	// - once when resolved (which happens while iterating through the
	// resources),
	// - and once when said iteration of resources reaches the end of the
	// resource list in the ResourceSet
	// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=385039
	
	@Override
	protected void setTarget(ResourceSet target) {
		basicSetTarget(target);
	    List<Resource> resources =  target.getResources();
	    for (int i = 0; i < resources.size(); ++i)
	    {
	      Notifier notifier = resources.get(i);
	      if (!notifier.eAdapters().contains(this))
	      {
	        addAdapter(notifier);
	      }
	    }	
	}
	
	private void baseHandleNotification(Notification notification) {
		if (notification.getNotifier() instanceof ResourceSet 
				&& notification.getEventType() == Notification.ADD_MANY
				&& notification.getFeatureID(ResourceSet.class) == ResourceSet.RESOURCE_SET__RESOURCES) 
		{
			@SuppressWarnings("unchecked") 
			Collection<Notifier> newValues = (Collection<Notifier>)notification.getNewValue();
			for (Notifier notifier : newValues) {
				if (!notifier.eAdapters().contains(this)) {
					addAdapter(notifier);
				}
			}    
		} else super.notifyChanged(notification);
	}
	
	// WORKAROUND (TMP) for eContents vs. derived features bug
	@Override
	protected void setTarget(EObject target) {
	    basicSetTarget(target);
	    spreadToChildren(target, true);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.util.EContentAdapter#unsetTarget(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	protected void unsetTarget(EObject target) {
	    basicUnsetTarget(target);
	    spreadToChildren(target, false);
	}
	/**
	 * @param target
	 */
	protected void spreadToChildren(EObject target, boolean add) {
		final EList<EReference> features = 
	    		target.eClass().getEAllReferences();
	    for (EReference feature : features) {
	    	if (!feature.isContainment()) continue;
	    	if (!EMFModelComprehension.representable(feature)) continue;
			if (feature.isMany()) {
				Collection<?> values = (Collection<?>) target.eGet(feature);
				for (Object value : values) {
					final Notifier notifier = (Notifier)value;
					if (add)
						addAdapter(notifier);
					else
						removeAdapter(notifier);
				}
			} else {
				Object value = target.eGet(feature);
				if (value != null) {
					final Notifier notifier = (Notifier)value;
					if (add)
						addAdapter(notifier);
					else
						removeAdapter(notifier);
				}
			}
		}
	}
	
	
	public void suspendVisitorOnUnresolvableFeature(EMFVisitor visitor, EObject source, EReference reference, EObject target, boolean isInsertion) {
		ListMultimap<EObject, EMFVisitor> targetToVisitor = unresolvableProxyFeaturesMap.get(source, reference);
		if (targetToVisitor == null) {
			targetToVisitor = ArrayListMultimap.create();
			unresolvableProxyFeaturesMap.put(source, reference, targetToVisitor);
		}
		if (isInsertion) 
			targetToVisitor.put(target, visitor);
		else
			targetToVisitor.remove(target, visitor);
		if (targetToVisitor.isEmpty()) 
			unresolvableProxyFeaturesMap.remove(source, reference);
	}

	public void suspendVisitorOnUnresolvableObject(EMFVisitor visitor, EObject source, boolean isInsertion) {
		if (isInsertion) 
			unresolvableProxyObjectsMap.put(source, visitor);
		else
			unresolvableProxyObjectsMap.remove(source, visitor);
	}
	
	List<EMFVisitor> popVisitorsSuspendedOnFeature(EObject source, EReference reference, EObject target) {
		ListMultimap<EObject, EMFVisitor> targetToVisitor = unresolvableProxyFeaturesMap.get(source, reference);
		if (targetToVisitor == null) return Collections.emptyList();
		final List<EMFVisitor> result = targetToVisitor.removeAll(target);
		if (targetToVisitor.isEmpty()) unresolvableProxyFeaturesMap.remove(source, reference);
		return result;
	}
	List<EMFVisitor> popVisitorsSuspendedOnObject(EObject source) {
		return unresolvableProxyObjectsMap.removeAll(source);	
	}

	
}
