package org.eclipse.viatra2.emf.incquery.base.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.viatra2.emf.incquery.base.api.FeatureListener;
import org.eclipse.viatra2.emf.incquery.base.api.InstanceListener;

public class NavigationHelperContentAdapter extends EContentAdapter {

	// value -> feature (attr or ref) -> holders
	protected Map<Object, Map<EStructuralFeature, Set<EObject>>> featureMap;
	protected Map<EClass, Set<EObject>> instanceMap;
	private NavigationHelperImpl navigationHelper;
	
	public NavigationHelperContentAdapter(NavigationHelperImpl navigationHelper) {
		this.navigationHelper = navigationHelper;
		this.featureMap = new HashMap<Object, Map<EStructuralFeature, Set<EObject>>>();
		this.instanceMap = new HashMap<EClass, Set<EObject>>();
	}

	public Map<EClass, Set<EObject>> getInstanceMap() {
		return instanceMap;
	}
	
	public Map<Object, Map<EStructuralFeature, Set<EObject>>> getFeatureMap() {
		return featureMap;
	}
	
	@Override
	public void notifyChanged(Notification notification) {
		super.notifyChanged(notification);

		Object feature = notification.getFeature();

		if (feature instanceof EReference) {
			handleRefChange(notification, (EReferenceImpl) feature);
		}
		if (feature instanceof EAttribute) {
			handleAttrChange(notification, (EAttribute) feature);
		}
	}

	@SuppressWarnings("unchecked")
	public void handleRefChange(Notification notification, EReference ref) {
		EObject notifier = (EObject) notification.getNotifier();
		
		if (notification.getEventType() == Notification.REMOVE_MANY) {
			for (EObject oldValue: (Collection<? extends EObject>) notification.getOldValue()) {
				handleRefRemove(ref, oldValue, null, notifier);
			}
		}
		else if (notification.getEventType() == Notification.ADD_MANY) {
			for (EObject newValue: (Collection<? extends EObject>) notification.getNewValue()) {
				handleRefAdd(ref, newValue, notifier);
			}
		}
		else if (notification.getEventType() == Notification.ADD) {
			handleRefAdd(ref, (EObject) notification.getNewValue(), notifier);
		}
		else if (notification.getEventType() == Notification.REMOVE) {
			handleRefRemove(ref, (EObject) notification.getOldValue(), (EObject) notification.getNewValue(), notifier);
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
				handleRefAdd(ref, newValue, notifier);
			}
		}
	}

	public void handleRefAdd(EReference ref, EObject newValue, EObject notifier) {
		insertFeatureTuple(ref, newValue, notifier);
		insertInstanceTuple(newValue.eClass(), newValue);
		if (ref.isContainment()) {
			navigationHelper.getVisitor().visitObjectForEAttribute(newValue, navigationHelper.getObservedFeatures(), true);
		}
	}

	public void handleRefRemove(EReference ref, EObject oldValue,
			EObject newValue, EObject notifier) {
		removeFeatureTuple(ref, oldValue, notifier);
		removeInstanceTuple(oldValue.eClass(), oldValue);

		if (ref.isContainment()) {
			navigationHelper.getVisitor().visitObjectForEAttribute(oldValue, navigationHelper.getObservedFeatures(), false);
		}

		if (newValue != null) {
			handleRefAdd(ref, newValue, notifier);
		}
	}

	public void handleAttrChange(Notification notification, EAttribute feature) {
		Object oldValue = notification.getOldValue();
		Object newValue = notification.getNewValue();
		EObject notifier = (EObject) notification.getNotifier();

		if (notification.getEventType() == Notification.SET) {
			if (oldValue != null) {
				removeFeatureTuple(feature, oldValue, notifier);
			}
			if (newValue != null) {
				insertFeatureTuple(feature, newValue, notifier);
			}
		}
	}

	public void insertFeatureTuple(EStructuralFeature feature, Object value, EObject holder) {
		if ((navigationHelper.getType() == NavigationHelperType.ALL) || navigationHelper.getObservedFeatures().contains(feature)) {
			if (featureMap.containsKey(value)) {
				if (featureMap.get(value).containsKey(feature)) {
					featureMap.get(value).get(feature).add(holder);
				} else {
					HashSet<EObject> set = new HashSet<EObject>();
					set.add(holder);
					featureMap.get(value).put(feature, set);
				}
			} else {
				Map<EStructuralFeature, Set<EObject>> map = new HashMap<EStructuralFeature, Set<EObject>>();
				Set<EObject> set = new HashSet<EObject>();
				set.add(holder);
				map.put(feature, set);
				featureMap.put(value, map);
			}
			
			notifyFeatureListeners(holder, feature, value, true);
		}
	}

	public void removeFeatureTuple(EStructuralFeature feature, Object target, EObject source) {
		if ((navigationHelper.getType() == NavigationHelperType.ALL) || navigationHelper.getObservedFeatures().contains(feature)) {
			if (featureMap.containsKey(target) && featureMap.get(target).containsKey(feature)) {
				featureMap.get(target).get(feature).remove(source);

				if (featureMap.get(target).get(feature).size() == 0) {
					featureMap.get(target).remove(feature);
				}

				if (featureMap.get(target).size() == 0) {
					featureMap.remove(target);
				}
			}
			
			notifyFeatureListeners(source, feature, target, false);
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
			
			notifyInstanceListeners(key, value, true);
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
	
	private void notifyFeatureListeners(EObject host, EStructuralFeature feature, Object value, boolean isInsertion) {
		for (FeatureListener listener : navigationHelper.getFeatureListeners().keySet()) {
			if (navigationHelper.getFeatureListeners().get(listener).contains(feature)) {
				if (isInsertion) {
					listener.featureInserted(host, feature, value);
				}
				else {
					listener.featureDeleted(host, feature, value);
				}
			}
		}
	}
	
	private void notifyInstanceListeners(EClass clazz, EObject instance, boolean isInsertion) {
		for (InstanceListener listener : navigationHelper.getInstanceListeners().keySet()) {
			if (navigationHelper.getInstanceListeners().get(listener).contains(clazz)) {
				if (isInsertion) {
					listener.instanceInserted(clazz, instance);
				}
				else {
					listener.instanceInserted(clazz, instance);
				}
			}
		}
	}
	
}
