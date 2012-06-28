package org.eclipse.viatra2.emf.incquery.base.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.viatra2.emf.incquery.base.api.DataTypeListener;
import org.eclipse.viatra2.emf.incquery.base.api.FeatureListener;
import org.eclipse.viatra2.emf.incquery.base.api.InstanceListener;

public class NavigationHelperContentAdapter extends EContentAdapter {

	// value -> feature (attr or ref) -> holder(s)
	protected Map<Object, Map<EStructuralFeature, Set<EObject>>> featureMap;
	
	//feature -> holder(s)
	protected Map<EStructuralFeature, Set<EObject>> reversedFeatureMap;
	
	// eclass -> instance(s)
	protected Map<EClass, Set<EObject>> instanceMap;
	
	protected Map<EDataType, Map<Object, Integer>> dataTypeMap;
	
	private NavigationHelperImpl navigationHelper;
	protected Map<EClass, Set<EClass>> subTypeMap;
	private Set<EClass> visitedClasses;
	
	public NavigationHelperContentAdapter(NavigationHelperImpl navigationHelper) {
		this.navigationHelper = navigationHelper;
		this.featureMap = new HashMap<Object, Map<EStructuralFeature, Set<EObject>>>();
		this.instanceMap = new HashMap<EClass, Set<EObject>>();
		this.subTypeMap = new HashMap<EClass, Set<EClass>>();
		this.dataTypeMap = new HashMap<EDataType, Map<Object,Integer>>();
		this.visitedClasses = new HashSet<EClass>();
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
		super.notifyChanged(notification);

		Object feature = notification.getFeature();

		if (feature instanceof EReference) {
			handleReferenceChange(notification, (EReferenceImpl) feature);
		}
		if (feature instanceof EAttribute) {
			handleAttributeChange(notification, (EAttribute) feature);
		}
	}

	@SuppressWarnings("unchecked")
	public void handleReferenceChange(Notification notification, EReference ref) {
		EObject notifier = (EObject) notification.getNotifier();
		
		if (notification.getEventType() == Notification.REMOVE_MANY) {
			for (EObject oldValue: (Collection<? extends EObject>) notification.getOldValue()) {
				handleReferenceRemove(ref, oldValue, null, notifier);
			}
		}
		else if (notification.getEventType() == Notification.ADD_MANY) {
			for (EObject newValue: (Collection<? extends EObject>) notification.getNewValue()) {
				handleReferenceAdd(ref, newValue, notifier);
			}
		}
		else if (notification.getEventType() == Notification.ADD) {
			handleReferenceAdd(ref, (EObject) notification.getNewValue(), notifier);
		}
		else if (notification.getEventType() == Notification.REMOVE) {
			handleReferenceRemove(ref, (EObject) notification.getOldValue(), (EObject) notification.getNewValue(), notifier);
		}
		else if (notification.getEventType() == Notification.SET) {
			EObject oldValue = (EObject) notification.getOldValue();
			EObject newValue = (EObject) notification.getNewValue();
			
			if (oldValue != null) {
				removeFeatureTuple(ref, oldValue, notifier);
				removeInstanceTuple(oldValue.eClass(), oldValue);

				if (ref.isContainment())
					navigationHelper.getVisitor().visitObjectForEAttribute(oldValue, navigationHelper.getObservedFeatures(), false);
			}
			if (newValue != null) {
				handleReferenceAdd(ref, newValue, notifier);
			}
		}
	}

	public void handleReferenceAdd(EReference ref, EObject newValue, EObject notifier) {
		insertFeatureTuple(ref, newValue, notifier);
		insertInstanceTuple(newValue.eClass(), newValue);
		if (ref.isContainment()) {
			navigationHelper.getVisitor().visitObjectForEAttribute(newValue, navigationHelper.getObservedFeatures(), true);
		}
	}

	public void handleReferenceRemove(EReference ref, EObject oldValue,
			EObject newValue, EObject notifier) {
		removeFeatureTuple(ref, oldValue, notifier);
		removeInstanceTuple(oldValue.eClass(), oldValue);

		if (ref.isContainment()) {
			navigationHelper.getVisitor().visitObjectForEAttribute(oldValue, navigationHelper.getObservedFeatures(), false);
		}

		if (newValue != null) {
			handleReferenceAdd(ref, newValue, notifier);
		}
	}

	public void handleAttributeChange(Notification notification, EAttribute attribute) {
		Object oldValue = notification.getOldValue();
		Object newValue = notification.getNewValue();
		EObject notifier = (EObject) notification.getNotifier();
		
		if (notification.getEventType() == Notification.SET) {
			if (oldValue != null) {
				removeFeatureTuple(attribute, oldValue, notifier);
			}
			if (newValue != null) {
				insertFeatureTuple(attribute, newValue, notifier);
			}
		}
	}
	
	private void addToFeatureMap(EStructuralFeature feature, Object value, EObject holder) {
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
	
	private void addToReversedFeatureMap(EStructuralFeature feature, EObject holder) {
		Set<EObject> setVal = reversedFeatureMap.get(feature);
		
		if (setVal == null) {
			setVal = new HashSet<EObject>();
		}
		setVal.add(holder);
		reversedFeatureMap.put(feature, setVal);
	}
	private void removeFromReversedFeatureMap(EStructuralFeature feature, EObject holder) {
		if (reversedFeatureMap.containsKey(feature)) {
			reversedFeatureMap.get(feature).remove(holder);
			
			if (reversedFeatureMap.get(feature).size() == 0) {
				reversedFeatureMap.remove(feature);
			}
		}
	}
	
	private void removeFromFeatureMap(EStructuralFeature feature, Object value, EObject holder) {
		if (featureMap.containsKey(value) && featureMap.get(value).containsKey(feature)) {
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
		}
		else {
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
				}
				else {
					valMap.put(value, count);
				}
			}
			if (valMap.size() == 0) {
				dataTypeMap.remove(type);
			}
		}
	}

	public void insertFeatureTuple(EStructuralFeature feature, Object value, EObject holder) {
		if ((navigationHelper.getType() == NavigationHelperType.ALL) || navigationHelper.getObservedFeatures().contains(feature)) {
			addToFeatureMap(feature, value, holder);
			
			if (reversedFeatureMap != null) {
				addToReversedFeatureMap(feature, holder);
			}
			
			//data type cache
			if (feature instanceof EAttribute) {
				EDataType type = ((EAttribute) feature).getEAttributeType();
				addToDataTypeMap(type, value);
				notifyDataTypeListeners(type, value, true);
			}
			
			notifyFeatureListeners(holder, feature, value, true);
		}
	}

	public void removeFeatureTuple(EStructuralFeature feature, Object value, EObject holder) {
		if ((navigationHelper.getType() == NavigationHelperType.ALL) || navigationHelper.getObservedFeatures().contains(feature)) {
			removeFromFeatureMap(feature, value, holder);
			
			if (reversedFeatureMap != null) {
				removeFromReversedFeatureMap(feature, holder);
			}
			
			//data type cache
			if (feature instanceof EAttribute) {
				EDataType type = ((EAttribute) feature).getEAttributeType();
				removeFromDataTypeMmap(type, value);
				notifyDataTypeListeners(type, value, false);
			}
			
			notifyFeatureListeners(holder, feature, value, false);
		}
	}

	public void insertInstanceTuple(EClass key, EObject value) {
		if (navigationHelper.getType() == NavigationHelperType.ALL || navigationHelper.getObservedClasses().contains(key)) {
			if (instanceMap.containsKey(key)) {
				instanceMap.get(key).add(value);
			} else {
				HashSet<EObject> set = new HashSet<EObject>();
				set.add(value);
				instanceMap.put(key, set);
			}
			
			//put subtype information into cache
			if (!visitedClasses.contains(key)) {
				visitedClasses.add(key);
				
				for (EClass superType : key.getEAllSuperTypes()) {
					Set<EClass> set = subTypeMap.get(superType);
					if (set == null) {
						set = new HashSet<EClass>();
						subTypeMap.put(superType, set);
					}
					set.add(key);
				}
			}
			
			notifyInstanceListeners(key, value, true);
		}
	}
	
	/**
	 * Returns true if sup is a supertype of sub.
	 * 
	 * @param sub subtype
	 * @param sup supertype
	 * @return
	 */
	private boolean isSubTypeOf(EClass sub, EClass sup) {
		Set<EClass> set = subTypeMap.get(sup); 
		if (set != null) {
			return set.contains(sub);
		}
		else {
			return false;
		}
	}

	private void removeInstanceTuple(EClass key, EObject value) {
		if (navigationHelper.getType() == NavigationHelperType.ALL || navigationHelper.getObservedClasses().contains(key)) {
			if (instanceMap.containsKey(key)) {
				instanceMap.get(key).remove(value);
				if (instanceMap.get(key).size() == 0) {
					instanceMap.remove(key);
				}
			}
			
			notifyInstanceListeners(key, value, false);
		}
	}
	
	private void notifyDataTypeListeners(EDataType type, Object value, boolean isInsertion) {
		for (Entry<DataTypeListener, Collection<EDataType>> entry : navigationHelper.getDataTypeListeners().entrySet()) {
			if (entry.getValue().contains(type)) {
				if (isInsertion) {
					entry.getKey().dataTypeInstanceInserted(type, value);
				}
				else {
					entry.getKey().dataTypeInstanceDeleted(type, value);
				}
			}
		}
	}
	
	private void notifyFeatureListeners(EObject host, EStructuralFeature feature, Object value, boolean isInsertion) {
		for (Entry<FeatureListener, Collection<EStructuralFeature>> entry : navigationHelper.getFeatureListeners().entrySet()) {
			if (entry.getValue().contains(feature)) {
				if (isInsertion) {
					entry.getKey().featureInserted(host, feature, value);
				}
				else {
					entry.getKey().featureDeleted(host, feature, value);
				}
			}
		}
	}
	
	private void notifyInstanceListeners(EClass clazz, EObject instance, boolean isInsertion) {
		for (Entry<InstanceListener, Collection<EClass>> entry : navigationHelper.getInstanceListeners().entrySet()) {
			for (EClass sup : entry.getValue()) {
				if (isSubTypeOf(clazz, sup) || clazz.equals(sup)) {
					if (isInsertion) {
						entry.getKey().instanceInserted(clazz, instance);
					}
					else {
						entry.getKey().instanceDeleted(clazz, instance);
					}
				}
			}
		}
	}
	
	private void initReversedFeatureMap() {
		for (Entry<EStructuralFeature, Set<EObject>> entry : reversedFeatureMap.entrySet()) {
			for (EObject holder : entry.getValue()) {
				addToReversedFeatureMap(entry.getKey(), holder);
			}
		}
	}
	
}
