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
import java.util.HashSet;
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
import org.eclipse.viatra2.emf.incquery.base.api.NavigationHelper;
import org.eclipse.viatra2.emf.incquery.base.exception.IncQueryBaseException;

public class NavigationHelperImpl implements NavigationHelper {

	protected HashSet<EClass> observedClasses;
	protected HashSet<EStructuralFeature> observedFeatures;
	protected Notifier notifier;
	protected NavigationHelperType navigationHelperType;
	protected NavigationHelperVisitor visitor;
	protected NavigationHelperContentAdapter contentAdapter;
	
	public NavigationHelperImpl(Notifier emfRoot, NavigationHelperType type) throws IncQueryBaseException {

		if (!((emfRoot instanceof EObject) || (emfRoot instanceof Resource) || (emfRoot instanceof ResourceSet))) {
			throw new IncQueryBaseException(IncQueryBaseException.INVALID_EMFROOT);
		}

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

		if (contentAdapter.getAttrMap().get(value) != null) {
			for (EAttribute attr : contentAdapter.getAttrMap().get(value).keySet()) {
				for (EObject holder : contentAdapter.getAttrMap().get(value).get(attr)) {
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
			if (contentAdapter.getAttrMap().get(value) != null
					&& contentAdapter.getAttrMap().get(value).get(attr) != null) {
				for (EObject holder : contentAdapter.getAttrMap().get(value).get(attr)) {
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
		if (contentAdapter.getAttrMap().get(value) == null)
			return null;
		return contentAdapter.getAttrMap().get(value).get(attribute);
	}

	@Override
	public Collection<Setting> findAllAttributeValuesByType(Class<?> clazz) {
		HashSet<Setting> retSet = new HashSet<Setting>();

		for (Object value : contentAdapter.getAttrMap().keySet()) {
			if (value.getClass().equals(clazz)) {
				for (EAttribute attr : contentAdapter.getAttrMap().get(value).keySet()) {
					for (EObject holder : contentAdapter.getAttrMap().get(value).get(attr)) {
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

		if (contentAdapter.getRefMap().get(target) != null) {
			for (EReference ref : contentAdapter.getRefMap().get(target).keySet()) {
				for (EObject source : contentAdapter.getRefMap().get(target).get(ref)) {
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
			if (contentAdapter.getRefMap().get(target) != null
					&& contentAdapter.getRefMap().get(target).get(ref) != null) {
				for (EObject source : contentAdapter.getRefMap().get(target).get(ref)) {
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
		if (contentAdapter.getRefMap().get(target) == null)
			return null;
		return contentAdapter.getRefMap().get(target).get(reference);
	}

	@Override
	public Set<EObject> getDirectInstances(EClass type) {
		return contentAdapter.getInstanceMap().get(type);
	}

	@Override
	public Set<EObject> getAllInstances(EClass type) {
		HashSet<EObject> retSet = new HashSet<EObject>();

		for (EClass c : contentAdapter.getInstanceMap().keySet()) {
			if (type.isSuperTypeOf(c))
				retSet.addAll(contentAdapter.getInstanceMap().get(c));
		}

		if (retSet.isEmpty())
			return null;
		return retSet;
	}
}
