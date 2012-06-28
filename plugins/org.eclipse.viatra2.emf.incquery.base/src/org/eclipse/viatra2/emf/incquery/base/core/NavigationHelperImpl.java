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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import org.eclipse.viatra2.emf.incquery.base.api.DataTypeListener;
import org.eclipse.viatra2.emf.incquery.base.api.FeatureListener;
import org.eclipse.viatra2.emf.incquery.base.api.InstanceListener;
import org.eclipse.viatra2.emf.incquery.base.api.NavigationHelper;
import org.eclipse.viatra2.emf.incquery.base.exception.IncQueryBaseException;

public class NavigationHelperImpl implements NavigationHelper {

	protected HashSet<EClass> observedClasses;
	protected HashSet<EDataType> observedDataTypes;
	protected HashSet<EStructuralFeature> observedFeatures;
	
	protected Notifier notifier;
	protected NavigationHelperType navigationHelperType;
	protected NavigationHelperVisitor visitor;
	protected NavigationHelperContentAdapter contentAdapter;
	
	private Map<InstanceListener, Collection<EClass>> instanceListeners;
	private Map<FeatureListener, Collection<EStructuralFeature>> featureListeners;
	private Map<DataTypeListener, Collection<EDataType>> dataTypeListeners;
	
	public NavigationHelperImpl(Notifier emfRoot, NavigationHelperType type) throws IncQueryBaseException {

		if (!((emfRoot instanceof EObject) || (emfRoot instanceof Resource) || (emfRoot instanceof ResourceSet))) {
			throw new IncQueryBaseException(IncQueryBaseException.INVALID_EMFROOT);
		}
		
		this.instanceListeners = new HashMap<InstanceListener, Collection<EClass>>();
		this.featureListeners = new HashMap<FeatureListener, Collection<EStructuralFeature>>();
		this.dataTypeListeners = new HashMap<DataTypeListener, Collection<EDataType>>();
		this.observedClasses = new HashSet<EClass>();
		this.observedFeatures = new HashSet<EStructuralFeature>();
		this.contentAdapter = new NavigationHelperContentAdapter(this);
		this.visitor = new NavigationHelperVisitor(this);

		this.notifier = emfRoot;
		this.navigationHelperType = type;

		if (this.navigationHelperType == NavigationHelperType.ALL) {
			visitor.visitModel(notifier, observedFeatures, observedClasses, observedDataTypes);
		}
		this.notifier.eAdapters().add(contentAdapter);
	}
	
	public NavigationHelperType getType() {
		return navigationHelperType;
	}
	
	public NavigationHelperContentAdapter getContentAdapter() {
		return contentAdapter;
	}
	
	public HashSet<EClass> getObservedClasses() {
		return observedClasses;
	}
	
	public HashSet<EStructuralFeature> getObservedFeatures() {
		return observedFeatures;
	}
	
	public NavigationHelperVisitor getVisitor() {
		return visitor;
	}

	@Override
	public void dispose() {
		notifier.eAdapters().remove(contentAdapter);
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

	@Override
	public Collection<Setting> findAllAttributeValuesByType(Class<?> clazz) {
		Set<Setting> retSet = new HashSet<Setting>();

		for (Object value : contentAdapter.featureMap.keySet()) {
			if (value.getClass().equals(clazz)) {
				for (EStructuralFeature attr : contentAdapter.featureMap.get(value).keySet()) {
					for (EObject holder : contentAdapter.featureMap.get(value).get(attr)) {
						retSet.add(new NavigationHelperSetting(attr, holder, value));
					}
				}
			}
		}

		return retSet;
	}

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
				retSet.addAll(contentAdapter.instanceMap.get(c));
			}
		}
		retSet.addAll(contentAdapter.instanceMap.get(type));
		
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
		this.instanceListeners.put(listener, classes);		
	}

	@Override
	public void unregisterInstanceListener(Collection<EClass> classes, InstanceListener listener) {
		Collection<EClass> restriction = this.instanceListeners.get(listener);
		restriction.removeAll(classes);
		if (restriction.size() == 0) {
			this.instanceListeners.remove(listener);
		}		
	}
	
	@Override
	public void registerFeatureListener(Collection<EStructuralFeature> features, FeatureListener listener) {
		this.featureListeners.put(listener, features);
	}

	@Override
	public void unregisterFeatureListener(Collection<EStructuralFeature> features, FeatureListener listener) {
		Collection<EStructuralFeature> restriction = this.featureListeners.get(listener);
		restriction.removeAll(features);
		if (restriction.size() == 0) {
			this.featureListeners.remove(listener);
		}
	}
	
	@Override
	public void registerDataTypeListener(Collection<EDataType> types, DataTypeListener listener) {
		this.dataTypeListeners.put(listener, types);
	}
	
	@Override
	public void unregisterDataTypeListener(Collection<EDataType> types,	DataTypeListener listener) {
		Collection<EDataType> restriction = this.dataTypeListeners.get(listener);
		restriction.removeAll(types);
		if (restriction.size() == 0) {
			this.dataTypeListeners.remove(listener);
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
}
