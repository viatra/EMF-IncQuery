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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra2.emf.incquery.base.api.DataTypeListener;
import org.eclipse.viatra2.emf.incquery.base.api.FeatureListener;
import org.eclipse.viatra2.emf.incquery.base.api.InstanceListener;
import org.eclipse.viatra2.emf.incquery.base.api.NavigationHelper;
import org.eclipse.viatra2.emf.incquery.base.comprehension.EMFModelComprehension;
import org.eclipse.viatra2.emf.incquery.base.exception.IncQueryBaseException;

public class NavigationHelperImpl implements NavigationHelper {

	protected boolean inWildcardMode;
	protected HashSet<EClass> directlyObservedClasses;
	protected HashSet<EClass> allObservedClasses = null; // including subclasses
	protected HashSet<EDataType> observedDataTypes;
	protected HashSet<EStructuralFeature> observedFeatures;
	
	protected Notifier notifier;
	protected Set<Notifier> modelRoots;
	private boolean expansionAllowed;
//	protected NavigationHelperVisitor visitor;
	protected NavigationHelperContentAdapter contentAdapter;
	
	private Logger logger;
	
	/**
	 * These global listeners will be called after updates.
	 */
	protected Set<Runnable> afterUpdateCallbacks;
	
	private Map<InstanceListener, Collection<EClass>> instanceListeners;
	private Map<FeatureListener, Collection<EStructuralFeature>> featureListeners;
	private Map<DataTypeListener, Collection<EDataType>> dataTypeListeners;
	
	/**
	 * Feature registration and model traversal is delayed while true
	 */
	protected boolean delayTraversals = false;
	/**
	 * Classes to be registered once the coalescing period is over
	 */
	protected Set<EClass> delayedClasses;
	/**
	 * EStructuralFeatures to be registered once the coalescing period is over
	 */
	protected Set<EStructuralFeature> delayedFeatures;
	/**
	 * EDataTypes to be registered once the coalescing period is over
	 */
	protected Set<EDataType> delayedDataTypes;
	
	private Set<EClass> noClass() { return Collections.emptySet(); };
	private Set<EDataType> noDataType() { return Collections.emptySet(); };
	private Set<EStructuralFeature> noFeature() { return Collections.emptySet(); };
	
	
	<T> Set<T> setMinus(Set<T> a, Set<T> b) {
		Set<T> result = new HashSet<T>(a);
		result.removeAll(b);
		return result;
	}
	
	<T extends EObject> Set<T> resolveAll(Set<T> a) {
		Set<T> result = new HashSet<T>(a);
		for (T t : a) {
			if (t.eIsProxy())
				result.add((T) EcoreUtil.resolve(t, (ResourceSet)null));				
			else
				result.add(t);
		}
		return result;
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.base.api.NavigationHelper#isInWildcardMode()
	 */
	@Override
	public boolean isInWildcardMode() {
		return inWildcardMode;
	}
//	@Override
//	public void setInWildcardMode(boolean newWildcardMode) {
//		if (inWildcardMode && !newWildcardMode)
//			throw new UnsupportedOperationException();
//		if (!inWildcardMode && newWildcardMode) {
//			this.inWildcardMode = true;
//			
//			this.allObservedClasses = null;
//			this.directlyObservedClasses = null;
//			this.observedDataTypes = null;
//			this.observedFeatures = null;
//			
//			this.contentAdapter. // TODO lot of work because need to send proper notifications
//		}
//			
//	}

	
	public NavigationHelperImpl(Notifier emfRoot, boolean wildcardMode, Logger logger) throws IncQueryBaseException {
		this.logger = logger;
		assert(logger!=null);
		
		
		this.instanceListeners = new HashMap<InstanceListener, Collection<EClass>>();
		this.featureListeners = new HashMap<FeatureListener, Collection<EStructuralFeature>>();
		this.dataTypeListeners = new HashMap<DataTypeListener, Collection<EDataType>>();
		this.directlyObservedClasses = new HashSet<EClass>();
		this.observedFeatures = new HashSet<EStructuralFeature>();
		this.observedDataTypes = new HashSet<EDataType>();
		this.contentAdapter = new NavigationHelperContentAdapter(this);
//		this.visitor = new NavigationHelperVisitor(this);
		this.afterUpdateCallbacks = new HashSet<Runnable>();

		this.notifier = emfRoot;
		this.modelRoots = new HashSet<Notifier>();
		this.expansionAllowed = notifier instanceof ResourceSet;
		this.inWildcardMode = wildcardMode;

//		if (this.navigationHelperType == NavigationHelperType.ALL) {
//			visitor.visitModel(notifier, observedFeatures, observedClasses, observedDataTypes);
//		}
		if (emfRoot != null) addRootInternal(emfRoot);
	}
	
	
	
	public NavigationHelperContentAdapter getContentAdapter() {
		return contentAdapter;
	}
	
	public HashSet<EStructuralFeature> getObservedFeatures() {
		return observedFeatures;
	}
	
//	public NavigationHelperVisitor getVisitor() {
//		return visitor;
//	}

	@Override
	public void dispose() {
		for (Notifier root : modelRoots) {
			contentAdapter.removeAdapter(root);		
		}
	}
	
	@Override
	public Collection<Object> getDataTypeInstances(EDataType type) {
		Map<Object, Integer> valMap = contentAdapter.dataTypeMap.get(type);
		if (valMap != null) {
			return Collections.unmodifiableSet(valMap.keySet());
		}
		else {
			return Collections.emptySet();
		}
	}

	@Override
	public Collection<Setting> findByAttributeValue(Object value) {
		Set<Setting> retSet = new HashSet<Setting>();
		Map<EStructuralFeature, Set<EObject>> valMap = contentAdapter.featureMap.get(value);
		
		if (valMap != null) {
			for (Entry<EStructuralFeature, Set<EObject>> entry : valMap.entrySet()) {
				for (EObject holder : entry.getValue()) {
					retSet.add(new NavigationHelperSetting(entry.getKey(), holder, value));
				}
			}
		}

		return retSet;
	}

	@Override
	public Collection<Setting> findByAttributeValue(Object value, Collection<EAttribute> attributes) {
		Set<Setting> retSet = new HashSet<Setting>();
		Map<EStructuralFeature, Set<EObject>> valMap = contentAdapter.featureMap.get(value);
		
		if (valMap != null) {
			for (EAttribute attr : attributes) {
				if (valMap.get(attr) != null) {
					for (EObject holder : valMap.get(attr)) {
						retSet.add(new NavigationHelperSetting(attr, holder, value));
					}
				}
			}
		}

		return retSet;
	}

	@Override
	public Collection<EObject> findByAttributeValue(Object value, EAttribute attribute) {
		Map<EStructuralFeature, Set<EObject>> valMap = contentAdapter.featureMap.get(value);
		if (valMap == null || valMap.get(attribute) == null) {
			return Collections.emptySet();
		}
		else {
			return Collections.unmodifiableSet(valMap.get(attribute));
		}
	}

//	@Override
//	public Collection<Setting> findAllAttributeValuesByType(Class<?> clazz) {
//		Set<Setting> retSet = new HashSet<Setting>();
//
//		for (Object value : contentAdapter.featureMap.keySet()) {
//			if (value.getClass().equals(clazz)) {
//				for (EStructuralFeature attr : contentAdapter.featureMap.get(value).keySet()) {
//					for (EObject holder : contentAdapter.featureMap.get(value).get(attr)) {
//						retSet.add(new NavigationHelperSetting(attr, holder, value));
//					}
//				}
//			}
//		}
//
//		return retSet;
//	}

	@Override
	public Collection<Setting> getInverseReferences(EObject target) {
		Set<Setting> retSet = new HashSet<Setting>();
		Map<EStructuralFeature, Set<EObject>> valMap = contentAdapter.featureMap.get(target);
		
		if (valMap != null) {
			for (Entry<EStructuralFeature, Set<EObject>> entry : valMap.entrySet()) {
				for (EObject source : entry.getValue()) {
					retSet.add(new NavigationHelperSetting(entry.getKey(), target, source));
				}
			}
		}

		return retSet;
	}

	@Override
	public Collection<Setting> getInverseReferences(EObject target, Collection<EReference> references) {
		Set<Setting> retSet = new HashSet<Setting>();
		Map<EStructuralFeature, Set<EObject>> valMap = contentAdapter.featureMap.get(target);
		
		if (valMap != null) {
			for (EReference ref : references) {
				if (valMap.get(ref) != null) {
					for (EObject source : valMap.get(ref)) {
						retSet.add(new NavigationHelperSetting(ref, target, source));
					}
				}
			}
		}

		return retSet;
	}

	@Override
	public Collection<EObject> getInverseReferences(EObject target, EReference reference) {
		Map<EStructuralFeature, Set<EObject>> valMap = contentAdapter.featureMap.get(target);
		if (valMap == null || valMap.get(reference) == null) {
			return Collections.emptySet();
		}
		else {
			return Collections.unmodifiableSet(valMap.get(reference));
		}
	}

	@Override
	public Collection<EObject> getDirectInstances(EClass type) {
		Set<EObject> valSet = contentAdapter.instanceMap.get(type);
		if (valSet == null) {
			return Collections.emptySet();
		}
		else {
			return Collections.unmodifiableSet(valSet);
		}
	}

	@Override
	public Collection<EObject> getAllInstances(EClass type) {
		Set<EObject> retSet = new HashSet<EObject>();
		
		Set<EClass> valSet = contentAdapter.subTypeMap.get(type);
		if (valSet != null) {
			for (EClass c : valSet) {
				final Set<EObject> instances = contentAdapter.instanceMap.get(c);
				if (instances != null) retSet.addAll(instances);
			}
		}
		final Set<EObject> instances = contentAdapter.instanceMap.get(type);
		if (instances != null) retSet.addAll(instances);
		
		return retSet;
	}
	
	@Override
	public Collection<EObject> findByFeatureValue(Object value, EStructuralFeature feature) {
		Set<EObject> retSet = new HashSet<EObject>();
		Map<EStructuralFeature, Set<EObject>> valMap = contentAdapter.featureMap.get(value);
		if (valMap != null && valMap.get(feature) != null) {
			retSet.addAll(valMap.get(feature));
		}
		return retSet;
	}
	
	@Override
	public Collection<EObject> getHoldersOfFeature(EStructuralFeature feature) {
		if (contentAdapter.getReversedFeatureMap().get(feature) == null) {
			return Collections.emptySet();
		}
		else {
			return Collections.unmodifiableSet(contentAdapter.getReversedFeatureMap().get(feature));
		}
	}

	@Override
	public void registerInstanceListener(Collection<EClass> classes, InstanceListener listener) {
		Collection<EClass> registered = this.instanceListeners.get(listener);
		if (registered == null) {
			registered = new HashSet<EClass>();
			this.instanceListeners.put(listener, registered);	
		}
		registered.addAll(classes);
	}

	@Override
	public void unregisterInstanceListener(Collection<EClass> classes, InstanceListener listener) {
		Collection<EClass> restriction = this.instanceListeners.get(listener);
		if (restriction != null) {
			restriction.removeAll(classes);
			if (restriction.size() == 0) {
				this.instanceListeners.remove(listener);
			}		
		}
	}
	
	@Override
	public void registerFeatureListener(Collection<EStructuralFeature> features, FeatureListener listener) {
		Collection<EStructuralFeature> registered = this.featureListeners.get(listener);
		if (registered == null) {
			registered = new HashSet<EStructuralFeature>();
			this.featureListeners.put(listener, registered);	
		}
		registered.addAll(features);
	}

	@Override
	public void unregisterFeatureListener(Collection<EStructuralFeature> features, FeatureListener listener) {
		Collection<EStructuralFeature> restriction = this.featureListeners.get(listener);
		if (restriction != null) {
			restriction.removeAll(features);
			if (restriction.size() == 0) {
				this.featureListeners.remove(listener);
			}
		}
	}
	
	@Override
	public void registerDataTypeListener(Collection<EDataType> types, DataTypeListener listener) {
		Collection<EDataType> registered = this.dataTypeListeners.get(listener);
		if (registered == null) {
			registered = new HashSet<EDataType>();
			this.dataTypeListeners.put(listener, registered);	
		}
		registered.addAll(types);
	}
	
	@Override
	public void unregisterDataTypeListener(Collection<EDataType> types,	DataTypeListener listener) {
		Collection<EDataType> restriction = this.dataTypeListeners.get(listener);
		if (restriction != null) {
			restriction.removeAll(types);
			if (restriction.size() == 0) {
				this.dataTypeListeners.remove(listener);
			}
		}
	}
	
	public Map<InstanceListener, Collection<EClass>> getInstanceListeners() {
		return instanceListeners;
	}
	
	public Map<FeatureListener, Collection<EStructuralFeature>> getFeatureListeners() {
		return featureListeners;
	}
	
	public Map<DataTypeListener, Collection<EDataType>> getDataTypeListeners() {
		return dataTypeListeners;
	}

	/**
	 * @return the observedDataTypes
	 */
	public HashSet<EDataType> getObservedDataTypes() {
		return observedDataTypes;
	}

	/**
	 * These runnables will be called after updates by the manipulationListener at its own discretion.
	 * Can be used e.g. to check delta monitors.
	 */
	@Override
	public Set<Runnable> getAfterUpdateCallbacks() {
		return afterUpdateCallbacks;
	}	
	/**
	 * This will run after updates.
	 */
//	 * If there are any such, updates are settled before they are run. 
	public void runAfterUpdateCallbacks() {
		try {
			if (!afterUpdateCallbacks.isEmpty()) {
				//settle();
				for (Runnable runnable : new ArrayList<Runnable>(afterUpdateCallbacks)) {
					runnable.run();
				}
			}
		} catch (Exception ex) {
			logger.fatal(
					"EMF-IncQuery Base encountered an error in delivering notifications about changes. " , ex);
			//throw new IncQueryRuntimeException(IncQueryRuntimeException.EMF_MODEL_PROCESSING_ERROR, ex);
		}
	}
	
	protected void considerForExpansion(EObject obj) {
		if (expansionAllowed) {
			Resource eResource = obj.eResource();
			if (eResource != null && eResource.getResourceSet() == null) {
				expandToAdditionalRoot(eResource);
			}
		}
	}
	
	protected void expandToAdditionalRoot(Notifier root) {
		if (modelRoots.add(root)) {
			contentAdapter.addAdapter(root);
		}
	}

	/**
	 * @return the expansionAllowed
	 */
	public boolean isExpansionAllowed() {
		return expansionAllowed;
	}

	/**
	 * @return the directlyObservedClasses
	 */
	public HashSet<EClass> getDirectlyObservedClasses() {
		return directlyObservedClasses;
	}
	
	public boolean isObserved(EClass clazz) {
		return inWildcardMode || getAllObservedClasses().contains(clazz);
	}

	/**
	 * not just the directly observed classes, but also their known subtypes
	 */
	public HashSet<EClass> getAllObservedClasses() {
		if (allObservedClasses == null) {
			allObservedClasses = new HashSet<EClass>();
			for (EClass eClass : directlyObservedClasses) {
				allObservedClasses.add(eClass);
				final Set<EClass> subTypes = NavigationHelperContentAdapter.subTypeMap.get(eClass);
				if (subTypes != null) {
					allObservedClasses.addAll(subTypes);
				}
			}
		}
		return allObservedClasses;
	}
	
	
	@Override
	public void registerEStructuralFeatures(Set<EStructuralFeature> features) {
		if (inWildcardMode) throw new IllegalStateException();
		if (features != null) {
			features = resolveAll(features);
			if (delayTraversals) {
				delayedFeatures.addAll(features);
			} else {
				features = setMinus(features, observedFeatures);

				observedFeatures.addAll(features);
				final NavigationHelperVisitor visitor = 
						new NavigationHelperVisitor.TraversingVisitor(this, features, noClass(), noClass(), noDataType());
				traverse(visitor);
			}
		}
	}



	@Override
	public void unregisterEStructuralFeatures(Set<EStructuralFeature> features) {
		if (inWildcardMode) throw new IllegalStateException();
		if (features != null) {
			features = resolveAll(features);
			observedFeatures.removeAll(features);
			delayedFeatures.removeAll(features);
			for (EStructuralFeature f : features) {
				for (Object key : contentAdapter.featureMap.keySet()) {
					contentAdapter.featureMap.get(key).remove(f);
					// TODO proper notification
				}
			}
		}
	}
	
	@Override
	public void registerEClasses(Set<EClass> classes) {
		if (inWildcardMode) throw new IllegalStateException();
		if (classes != null) {
			classes = resolveAll(classes);
			if (delayTraversals) {
				delayedClasses.addAll(classes);
			} else {
				classes = setMinus(classes, directlyObservedClasses);
				
				final HashSet<EClass> oldClasses = new HashSet<EClass>(directlyObservedClasses);
				startObservingClasses(classes);		
				final NavigationHelperVisitor visitor = 
						new NavigationHelperVisitor.TraversingVisitor(this, noFeature(), classes, oldClasses, noDataType());
				traverse(visitor);
			}
		}
	}
	/**
	 * @param classes
	 */
	protected void startObservingClasses(Set<EClass> classes) {
		directlyObservedClasses.addAll(classes);
		getAllObservedClasses().addAll(classes);
		for (EClass eClass : classes) {
			final Set<EClass> subTypes = NavigationHelperContentAdapter.subTypeMap.get(eClass);
			if (subTypes != null) {
				allObservedClasses.addAll(subTypes);
			}					
		}
	}

	@Override
	public void unregisterEClasses(Set<EClass> classes) {
		if (inWildcardMode) throw new IllegalStateException();
		if (classes != null) {
			classes = resolveAll(classes);
			directlyObservedClasses.removeAll(classes);
			allObservedClasses = null;
			delayedClasses.removeAll(classes);
			for (EClass c : classes) {
				contentAdapter.instanceMap.remove(c);
			}
		}
	}

	@Override
	public void registerEDataTypes(Set<EDataType> dataTypes) {
		if (inWildcardMode) throw new IllegalStateException();
		if (dataTypes != null) {
			dataTypes = resolveAll(dataTypes);
			if (delayTraversals) {
				delayedDataTypes.addAll(dataTypes);
			} else {
				dataTypes = setMinus(dataTypes, observedDataTypes);
				
				observedDataTypes.addAll(dataTypes);
				final NavigationHelperVisitor visitor = 
						new NavigationHelperVisitor.TraversingVisitor(this, noFeature(), noClass(), noClass(), dataTypes);
				traverse(visitor);

			}
		}
	}

	@Override
	public void unregisterEDataTypes(Set<EDataType> dataTypes) {
		if (inWildcardMode) throw new IllegalStateException();
		if (dataTypes != null) {
			dataTypes = resolveAll(dataTypes);
			observedDataTypes.removeAll(dataTypes);
			delayedDataTypes.removeAll(dataTypes);
			for (EDataType dt : dataTypes) {
				contentAdapter.dataTypeMap.remove(dt);
			}
		}
	}
	
	@Override
	public <V> V coalesceTraversals(Callable<V> callable) throws InvocationTargetException {
		if(delayTraversals) { // reentrant case, no special action needed
			V result = null;
			try {
				result = callable.call();
			} catch (Exception e) {
				throw new InvocationTargetException(e);
			}
			return result;
		}
			
		delayedClasses = new HashSet<EClass>();
		delayedFeatures = new HashSet<EStructuralFeature>();
		delayedDataTypes = new HashSet<EDataType>(); 
		
		V result = null;
		try {
			try {
				delayTraversals = true;
				result = callable.call();
			} finally {
				delayTraversals = false;
				
				delayedFeatures = setMinus(delayedFeatures, observedFeatures);
				delayedClasses = setMinus(delayedClasses, directlyObservedClasses);
				delayedDataTypes = setMinus(delayedDataTypes, observedDataTypes);
				
				boolean classesWarrantTraversal = !setMinus(delayedClasses, getAllObservedClasses()).isEmpty();

				if (!delayedClasses.isEmpty() || !delayedFeatures.isEmpty() || !delayedDataTypes.isEmpty()) {
					final HashSet<EClass> oldClasses = new HashSet<EClass>(directlyObservedClasses);
					startObservingClasses(delayedClasses);
					observedFeatures.addAll(delayedFeatures);
					observedDataTypes.addAll(delayedDataTypes);
					
					// make copies and clean original accumulators, for the rare case that a coalesced 
					// 	traversal is invoked during visitation, e.g. by a derived feature implementation
					final HashSet<EClass> toGatherClasses = new HashSet<EClass>(delayedClasses);
					final HashSet<EStructuralFeature> toGatherFeatures = new HashSet<EStructuralFeature>(delayedFeatures);
					final HashSet<EDataType> toGatherDataTypes = new HashSet<EDataType>(delayedDataTypes);
					delayedFeatures.clear();
					delayedClasses.clear();
					delayedDataTypes.clear();
					
					if (classesWarrantTraversal || !toGatherFeatures.isEmpty() || !toGatherDataTypes.isEmpty()) {
						final NavigationHelperVisitor visitor = 
								new NavigationHelperVisitor.TraversingVisitor(this, toGatherFeatures, toGatherClasses, oldClasses, toGatherDataTypes);
						traverse(visitor);
					}
				}
			}
		} catch (Exception e) {
			getLogger().fatal("EMF-IncQuery Base encountered an error while traversing the EMF model to gather new information. " , e);
			throw new InvocationTargetException(e);
		}
		return result;
	}
	
	private void traverse(final NavigationHelperVisitor visitor) {
		for (Notifier root : modelRoots) {
			EMFModelComprehension.visitModel(visitor, root);		
		}
		runAfterUpdateCallbacks();
	}
	/**
	 * @return the logger
	 */
	public Logger getLogger() {
		return logger;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.base.api.NavigationHelper#addRoot(org.eclipse.emf.common.notify.Notifier)
	 */
	@Override
	public void addRoot(Notifier emfRoot) throws IncQueryBaseException {
    addRootInternal(emfRoot);
	}
  /**
   * @param emfRoot
   * @throws IncQueryBaseException
   */
  private void addRootInternal(Notifier emfRoot) throws IncQueryBaseException {
    if (!((emfRoot instanceof EObject) || (emfRoot instanceof Resource) || (emfRoot instanceof ResourceSet))) {
      throw new IncQueryBaseException(IncQueryBaseException.INVALID_EMFROOT);
    }
    expandToAdditionalRoot(emfRoot);
  }

}
