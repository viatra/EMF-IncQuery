package org.eclipse.viatra2.emf.incquery.base.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.viatra2.emf.incquery.base.api.NavigationHelper;
import org.eclipse.viatra2.emf.incquery.base.exception.IncQueryBaseException;

public class NavigationHelperImpl extends EContentAdapter implements
		NavigationHelper {

	// target -> ref -> source
	protected HashMap<EObject, HashMap<EReference, HashSet<EObject>>> refMap;

	// class -> value -> attribute -> holder
	protected HashMap<Object, HashMap<EAttribute, HashSet<EObject>>> attrMap;
	protected HashMap<EClass, HashSet<EObject>> instanceMap;
	protected HashSet<EClass> observedClasses;
	protected HashSet<EStructuralFeature> observedFeatures;
	protected Notifier notifier;
	protected NavigationHelperType navigationHelperType;

	public NavigationHelperImpl(Notifier emfRoot, NavigationHelperType type)
			throws IncQueryBaseException {

		if (!((emfRoot instanceof EObject) || (emfRoot instanceof Resource) || (emfRoot instanceof ResourceSet))) {
			throw new IncQueryBaseException(
					IncQueryBaseException.INVALID_EMFROOT);
		}

		this.refMap = new HashMap<EObject, HashMap<EReference, HashSet<EObject>>>();
		this.attrMap = new HashMap<Object, HashMap<EAttribute, HashSet<EObject>>>();
		this.instanceMap = new HashMap<EClass, HashSet<EObject>>();
		this.observedClasses = new HashSet<EClass>();
		this.observedFeatures = new HashSet<EStructuralFeature>();
		this.notifier = emfRoot;
		this.navigationHelperType = type;

		if (this.navigationHelperType == NavigationHelperType.ALL) {
			visitModel(notifier, observedFeatures, observedClasses);
		}
		this.notifier.eAdapters().add(this);
	}

	protected void visitModel(Notifier emfRoot,
			Set<EStructuralFeature> features, Set<EClass> classes) {
		if (emfRoot instanceof EObject)
			visitEObjectRoot((EObject) emfRoot, features, classes);
		else if (emfRoot instanceof Resource)
			visitResourceRoot((Resource) emfRoot, features, classes);
		else if (emfRoot instanceof ResourceSet)
			visitResourceSetRoot((ResourceSet) emfRoot, features, classes);
	}

	private void visitResourceSetRoot(ResourceSet resourceSet,
			Set<EStructuralFeature> features, Set<EClass> classes) {
		for (Resource r : resourceSet.getResources()) {
			visitResourceRoot(r, features, classes);
		}
	}

	private void visitResourceRoot(Resource resource,
			Set<EStructuralFeature> features, Set<EClass> classes) {
		for (EObject obj : resource.getContents()) {
			visitEObjectRoot(obj, features, classes);
		}
	}

	private void visitEObjectRoot(EObject root,
			Set<EStructuralFeature> features, Set<EClass> classes) {
		visitObject(root, features, classes);
		TreeIterator<EObject> it = root.eAllContents();

		while (it.hasNext()) {
			visitObject(it.next(), features, classes);
		}
	}

	// visit all the attributes of the given EObject and insert them to the
	// cache
	private void visitObjectForEAttributeInsert(EObject obj,
			Set<EStructuralFeature> features) {
		if (obj != null) {
			for (EAttribute attr : obj.eClass().getEAllAttributes()) {
				if ((navigationHelperType == NavigationHelperType.ALL)
						|| features.contains(attr)) {
					Object o = obj.eGet(attr);
					if (o != null) {
						insertAttrTuple(attr, o, obj);
					}
				}
			}
		}
	}

	// visit all the attributes of the given EObject and delete them from the
	// cache
	private void visitObjectForEAttributeDelete(EObject obj,
			Set<EStructuralFeature> features) {
		if (obj != null) {
			for (EAttribute attr : obj.eClass().getEAllAttributes()) {
				if ((navigationHelperType == NavigationHelperType.ALL)
						|| features.contains(attr)) {
					Object o = obj.eGet(attr);
					if (o != null) {
						removeAttrTuple(attr, o, obj);
					}
				}
			}
		}
	}

	private void visitObject(EObject obj, Set<EStructuralFeature> features,
			Set<EClass> classes) {
		if (obj != null) {

			if (classes != null)
				if ((navigationHelperType == NavigationHelperType.ALL)
						|| classes.contains(obj.eClass()))
					insertInstanceTuple(obj.eClass(), obj);

			if (features != null) {
				for (EReference ref : obj.eClass().getEAllReferences()) {
					if ((navigationHelperType == NavigationHelperType.ALL)
							|| features.contains(ref)) {
						Object o = obj.eGet(ref);

						if (o != null) {
							if (o instanceof EObjectEList<?>) {
								@SuppressWarnings("unchecked")
								EObjectEList<EObject> list = (EObjectEList<EObject>) o;
								Iterator<EObject> it = list.iterator();

								while (it.hasNext()) {
									insertRefTuple(ref, it.next(), obj);
								}
							} else {
								insertRefTuple(ref, (EObject) o, obj);
							}
						}
					}
				}

				visitObjectForEAttributeInsert(obj, features);
			}
		}
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
	private void handleRefChange(Notification notification, EReference ref) {
		EObject notifier = (EObject) notification.getNotifier();
		
		if (notification.getEventType() == Notification.REMOVE_MANY) {
			for (EObject oldValue: (Collection<? extends EObject>)notification.getOldValue()) 
				handleRefRemove(ref, oldValue, null, notifier);
				
			return;
		}
		if (notification.getEventType() == Notification.ADD_MANY) {
			for (EObject newValue: (Collection<? extends EObject>)notification.getNewValue()) 
				handleRefAdd(ref, newValue, notifier);
				
			return;
		}

		EObject oldValue = (EObject) notification.getOldValue();
		EObject newValue = (EObject) notification.getNewValue();

		if (notification.getEventType() == Notification.ADD) {
			handleRefAdd(ref, newValue, notifier);
		}
		if (notification.getEventType() == Notification.SET) {
			if (oldValue != null) {
				removeRefTuple(ref, oldValue, notifier);
				removeInstanceTuple(oldValue.eClass(), oldValue);

				if (ref.isContainment())
					visitObjectForEAttributeDelete(oldValue, observedFeatures);
			}
			if (newValue != null) {
				handleRefAdd(ref, newValue, notifier);
			}
		}
		if (notification.getEventType() == Notification.REMOVE) {

			handleRefRemove(ref, oldValue, newValue, notifier);
		}
	}

	/**
	 * @param ref
	 * @param newValue
	 * @param notifier
	 */
	private void handleRefAdd(EReference ref, EObject newValue, EObject notifier) {
		insertRefTuple(ref, newValue, notifier);
		insertInstanceTuple(newValue.eClass(), newValue);
		if (ref.isContainment())
			visitObjectForEAttributeInsert(newValue, observedFeatures);
	}

	/**
	 * @param ref
	 * @param oldValue
	 * @param newValue
	 * @param notifier
	 */
	private void handleRefRemove(EReference ref, EObject oldValue,
			EObject newValue, EObject notifier) {
		removeRefTuple(ref, oldValue, notifier);
		removeInstanceTuple(oldValue.eClass(), oldValue);

		if (ref.isContainment())
			visitObjectForEAttributeDelete(oldValue, observedFeatures);

		if (newValue != null) {
			handleRefAdd(ref, newValue, notifier);
		}
	}

	private void handleAttrChange(Notification notification, EAttribute feature) {
		Object oldValue = notification.getOldValue();
		Object newValue = notification.getNewValue();
		EObject notifier = (EObject) notification.getNotifier();

		if (notification.getEventType() == Notification.SET) {
			if (oldValue != null) {
				removeAttrTuple(feature, oldValue, notifier);
			}
			if (newValue != null) {
				insertAttrTuple(feature, newValue, notifier);
			}
		}
	}

	private void insertAttrTuple(EAttribute attr, Object value, EObject holder) {
		if ((navigationHelperType == NavigationHelperType.ALL)
				|| observedFeatures.contains(attr)) {
			if (attrMap.containsKey(value)) {
				if (attrMap.get(value).containsKey(attr)) {
					attrMap.get(value).get(attr).add(holder);
				} else {
					HashSet<EObject> set = new HashSet<EObject>();
					set.add(holder);
					attrMap.get(value).put(attr, set);
				}
			} else {
				HashMap<EAttribute, HashSet<EObject>> map = new HashMap<EAttribute, HashSet<EObject>>();
				HashSet<EObject> set = new HashSet<EObject>();
				set.add(holder);
				map.put(attr, set);
				attrMap.put(value, map);
			}
		}
	}

	private void removeAttrTuple(EAttribute attr, Object value, EObject holder) {

		if ((navigationHelperType == NavigationHelperType.ALL)
				|| observedFeatures.contains(attr)) {
			if (attrMap.containsKey(value)
					&& attrMap.get(value).containsKey(attr)) {
				attrMap.get(value).get(attr).remove(holder);

				if (attrMap.get(value).get(attr).size() == 0) {
					attrMap.get(value).remove(attr);
				}

				if (attrMap.get(value).size() == 0) {
					attrMap.remove(value);
				}
			}
		}

	}

	private void insertRefTuple(EReference ref, EObject target, EObject source) {
		if ((navigationHelperType == NavigationHelperType.ALL)
				|| observedFeatures.contains(ref)) {
			if (refMap.containsKey(target)) {
				if (refMap.get(target).containsKey(ref)) {
					refMap.get(target).get(ref).add(source);
				} else {
					HashSet<EObject> set = new HashSet<EObject>();
					set.add(source);
					refMap.get(target).put(ref, set);
				}
			} else {
				HashMap<EReference, HashSet<EObject>> map = new HashMap<EReference, HashSet<EObject>>();
				HashSet<EObject> set = new HashSet<EObject>();
				set.add(source);
				map.put(ref, set);
				refMap.put(target, map);
			}
		}
	}

	private void removeRefTuple(EReference ref, EObject target, EObject source) {
		if ((navigationHelperType == NavigationHelperType.ALL)
				|| observedFeatures.contains(ref)) {
			if (refMap.containsKey(target)
					&& refMap.get(target).containsKey(ref)) {
				refMap.get(target).get(ref).remove(source);

				if (refMap.get(target).get(ref).size() == 0)
					refMap.get(target).remove(ref);

				if (refMap.get(target).size() == 0)
					refMap.remove(target);
			}
		}
	}

	private void insertInstanceTuple(EClass key, EObject value) {
		if (navigationHelperType == NavigationHelperType.ALL
				|| observedClasses.contains(key)) {
			if (instanceMap.containsKey(key)) {
				instanceMap.get(key).add(value);
			} else {
				HashSet<EObject> set = new HashSet<EObject>();
				set.add(value);
				instanceMap.put(key, set);
			}
		}
	}

	private void removeInstanceTuple(EClass key, EObject value) {
		if (navigationHelperType == NavigationHelperType.ALL
				|| observedClasses.contains(key)) {
			if (instanceMap.containsKey(key)) {
				instanceMap.get(key).remove(value);

				if (instanceMap.get(key).size() == 0)
					instanceMap.remove(key);
			}
		}
	}

	@Override
	public void dispose() {
		notifier.eAdapters().remove(this);
	}

	@Override
	public Collection<Setting> findByAttributeValue(Object value) {
		HashSet<Setting> retSet = new HashSet<Setting>();

		if (attrMap.get(value) != null) {
			for (EAttribute attr : attrMap.get(value).keySet()) {
				for (EObject holder : attrMap.get(value).get(attr)) {
					retSet.add(new NavigationHelperSetting(attr, holder, value));
				}
			}
		}

		if (retSet.isEmpty())
			return null;
		return retSet;
	}

	@Override
	public Collection<Setting> findByAttributeValue(Object value,
			Set<EAttribute> attributes) {
		HashSet<Setting> retSet = new HashSet<Setting>();

		for (EAttribute attr : attributes) {
			if (attrMap.get(value) != null
					&& attrMap.get(value).get(attr) != null) {
				for (EObject holder : attrMap.get(value).get(attr)) {
					retSet.add(new NavigationHelperSetting(attr, holder, value));
				}
			}
		}

		if (retSet.isEmpty())
			return null;
		return retSet;
	}

	@Override
	public Set<EObject> findByAttributeValue(Object value, EAttribute attribute) {
		if (attrMap.get(value) == null)
			return null;
		return attrMap.get(value).get(attribute);
	}

	@Override
	public Collection<Setting> findAllAttributeValuesByType(Class<?> clazz) {
		HashSet<Setting> retSet = new HashSet<Setting>();

		for (Object value : attrMap.keySet()) {
			if (value.getClass().equals(clazz)) {
				for (EAttribute attr : attrMap.get(value).keySet()) {
					for (EObject holder : attrMap.get(value).get(attr)) {
						retSet.add(new NavigationHelperSetting(attr, holder,
								value));
					}
				}
			}
		}

		if (retSet.isEmpty())
			return null;
		return retSet;
	}

	@Override
	public Collection<Setting> getInverseReferences(EObject target) {
		HashSet<Setting> retSet = new HashSet<Setting>();

		if (refMap.get(target) != null) {
			for (EReference ref : refMap.get(target).keySet()) {
				for (EObject source : refMap.get(target).get(ref)) {
					retSet.add(new NavigationHelperSetting(ref, target, source));
				}
			}
		}

		if (retSet.isEmpty())
			return null;
		return retSet;
	}

	@Override
	public Collection<Setting> getInverseReferences(EObject target,
			Set<EReference> references) {
		HashSet<Setting> retSet = new HashSet<Setting>();

		for (EReference ref : references) {
			if (refMap.get(target) != null
					&& refMap.get(target).get(ref) != null) {
				for (EObject source : refMap.get(target).get(ref)) {
					retSet.add(new NavigationHelperSetting(ref, target, source));
				}
			}
		}

		if (retSet.isEmpty())
			return null;
		return retSet;
	}

	@Override
	public Set<EObject> getInverseReferences(EObject target,
			EReference reference) {
		if (refMap.get(target) == null)
			return null;
		return refMap.get(target).get(reference);
	}

	@Override
	public Set<EObject> getDirectInstances(EClass type) {
		return instanceMap.get(type);
	}

	@Override
	public Set<EObject> getAllInstances(EClass type) {
		HashSet<EObject> retSet = new HashSet<EObject>();

		for (EClass c : instanceMap.keySet()) {
			if (type.isSuperTypeOf(c))
				retSet.addAll(instanceMap.get(c));
		}

		if (retSet.isEmpty())
			return null;
		return retSet;
	}
}
