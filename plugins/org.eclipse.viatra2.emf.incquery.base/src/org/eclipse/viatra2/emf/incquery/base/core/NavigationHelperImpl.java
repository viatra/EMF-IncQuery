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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra2.emf.incquery.base.api.FeatureListener;
import org.eclipse.viatra2.emf.incquery.base.api.InstanceListener;
import org.eclipse.viatra2.emf.incquery.base.api.NavigationHelper;
import org.eclipse.viatra2.emf.incquery.base.exception.IncQueryBaseException;

public class NavigationHelperImpl implements NavigationHelper {

	protected HashSet<EClass> observedClasses;
	protected HashSet<EStructuralFeature> observedFeatures;
	protected Notifier notifier;
	protected NavigationHelperType navigationHelperType;
	protected NavigationHelperVisitor visitor;
	protected NavigationHelperContentAdapter contentAdapter;
	
	private Map<InstanceListener, Set<EClass>> instanceListeners;
	private Map<FeatureListener, Set<EStructuralFeature>> featureListeners;
	
	public NavigationHelperImpl(Notifier emfRoot, NavigationHelperType type) throws IncQueryBaseException {

		if (!((emfRoot instanceof EObject) || (emfRoot instanceof Resource) || (emfRoot instanceof ResourceSet))) {
			throw new IncQueryBaseException(IncQueryBaseException.INVALID_EMFROOT);
		}

		this.instanceListeners = new HashMap<InstanceListener, Set<EClass>>();
		this.featureListeners = new HashMap<FeatureListener, Set<EStructuralFeature>>();
		this.observedClasses = new HashSet<EClass>();
		this.observedFeatures = new HashSet<EStructuralFeature>();
		this.contentAdapter = new NavigationHelperContentAdapter(this);
		this.visitor = new NavigationHelperVisitor(this);

		this.notifier = emfRoot;
		this.navigationHelperType = type;

		if (this.navigationHelperType == NavigationHelperType.ALL) {
			visitor.visitModel(notifier, observedFeatures, observedClasses);
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
	public Collection<Setting> findByAttributeValue(Object value) {
		HashSet<Setting> retSet = new HashSet<Setting>();
		Map<EStructuralFeature, Set<EObject>> valMap = contentAdapter.getFeatureMap().get(value);
		
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
	public Collection<Setting> findByAttributeValue(Object value, Set<EAttribute> attributes) {
		HashSet<Setting> retSet = new HashSet<Setting>();

		for (EAttribute attr : attributes) {
			if (contentAdapter.getFeatureMap().get(value) != null
					&& contentAdapter.getFeatureMap().get(value).get(attr) != null) {
				for (EObject holder : contentAdapter.getFeatureMap().get(value).get(attr)) {
					retSet.add(new NavigationHelperSetting(attr, holder, value));
				}
			}
		}

		return retSet;
	}

	@Override
	public Set<EObject> findByAttributeValue(Object value, EAttribute attribute) {
		Map<EStructuralFeature, Set<EObject>> mapVal = contentAdapter.getFeatureMap().get(value);
		if (mapVal == null || mapVal.get(attribute) == null) {
			return Collections.emptySet();
		}
		else {
			return mapVal.get(attribute);
		}
	}

	@Override
	public Collection<Setting> findAllAttributeValuesByType(Class<?> clazz) {
		HashSet<Setting> retSet = new HashSet<Setting>();

		for (Object value : contentAdapter.getFeatureMap().keySet()) {
			if (value.getClass().equals(clazz)) {
				for (EStructuralFeature attr : contentAdapter.getFeatureMap().get(value).keySet()) {
					for (EObject holder : contentAdapter.getFeatureMap().get(value).get(attr)) {
						retSet.add(new NavigationHelperSetting(attr, holder,
								value));
					}
				}
			}
		}

		return retSet;
	}

	@Override
	public Collection<Setting> getInverseReferences(EObject target) {
		HashSet<Setting> retSet = new HashSet<Setting>();
		Map<EStructuralFeature, Set<EObject>> mapVal = contentAdapter.getFeatureMap().get(target);
		
		if (mapVal != null) {
			for (Entry<EStructuralFeature, Set<EObject>> entry : mapVal.entrySet()) {
				for (EObject source : entry.getValue()) {
					retSet.add(new NavigationHelperSetting(entry.getKey(), target, source));
				}
			}
		}

		return retSet;
	}

	@Override
	public Collection<Setting> getInverseReferences(EObject target, Set<EReference> references) {
		HashSet<Setting> retSet = new HashSet<Setting>();

		for (EReference ref : references) {
			if (contentAdapter.getFeatureMap().get(target) != null
					&& contentAdapter.getFeatureMap().get(target).get(ref) != null) {
				for (EObject source : contentAdapter.getFeatureMap().get(target).get(ref)) {
					retSet.add(new NavigationHelperSetting(ref, target, source));
				}
			}
		}

		return retSet;
	}

	@Override
	public Set<EObject> getInverseReferences(EObject target, EReference reference) {
		Map<EStructuralFeature, Set<EObject>> mapVal = contentAdapter.getFeatureMap().get(target);
		if (mapVal == null || mapVal.get(reference) == null) {
			return Collections.emptySet();
		}
		else {
			return mapVal.get(reference);
		}
	}

	@Override
	public Set<EObject> getDirectInstances(EClass type) {
		Set<EObject> setVal = contentAdapter.getInstanceMap().get(type);
		if (setVal == null) {
			return Collections.emptySet();
		}
		else {
			return setVal;
		}
	}

	@Override
	public Set<EObject> getAllInstances(EClass type) {
		HashSet<EObject> retSet = new HashSet<EObject>();
		
		Set<EClass> setVal = contentAdapter.getSubTypeMap().get(type);
		if (setVal != null) {
			for (EClass c : setVal) {
				retSet.addAll(contentAdapter.getInstanceMap().get(c));
			}
		}
		retSet.addAll(contentAdapter.getInstanceMap().get(type));
		
		return retSet;
	}

	@Override
	public void registerInstanceListener(Set<EClass> classes, InstanceListener listener) {
		this.instanceListeners.put(listener, classes);		
	}

	@Override
	public void unregisterInstanceListener(Set<EClass> classes, InstanceListener listener) {
		Set<EClass> restriction = this.instanceListeners.get(listener);
		restriction.removeAll(classes);
		if (restriction.size() == 0) {
			this.instanceListeners.remove(listener);
		}		
	}
	
	@Override
	public void registerFeatureListener(Set<EStructuralFeature> features, FeatureListener listener) {
		this.featureListeners.put(listener, features);
	}

	@Override
	public void unregisterFeatureListener(Set<EStructuralFeature> features, FeatureListener listener) {
		Set<EStructuralFeature> restriction = this.featureListeners.get(listener);
		restriction.removeAll(features);
		if (restriction.size() == 0) {
			this.featureListeners.remove(listener);
		}
	}
	
	public Map<InstanceListener, Set<EClass>> getInstanceListeners() {
		return instanceListeners;
	}
	
	public Map<FeatureListener, Set<EStructuralFeature>> getFeatureListeners() {
		return featureListeners;
	}
}
