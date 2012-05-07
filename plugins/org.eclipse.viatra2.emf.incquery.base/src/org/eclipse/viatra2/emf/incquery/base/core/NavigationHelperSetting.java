package org.eclipse.viatra2.emf.incquery.base.core;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;

/**
 * EStructuralFeature.Setting implementation for the NavigationHelper. 
 * 
 * @author Tamás Szabó
 *
 */
public class NavigationHelperSetting implements Setting {

	private EStructuralFeature feature;
	private EObject holder;
	private Object value;
	
	public NavigationHelperSetting() {
		super();
	}
	
	public NavigationHelperSetting(EStructuralFeature feature, EObject holder,
			Object value) {
		super();
		this.feature = feature;
		this.holder = holder;
		this.value = value;
	}

	@Override
	public EObject getEObject() {
		return holder;
	}

	@Override
	public EStructuralFeature getEStructuralFeature() {
		return feature;
	}

	@Override
	public Object get(boolean resolve) {
		return value;
	}

	@Override
	public void set(Object newValue) {
		this.value = newValue;		
	}

	@Override
	public boolean isSet() {
		if (value != null)
			return true;
		return false;
	}

	@Override
	public void unset() {
		this.value = null;		
	}

	@Override
	public String toString() {
		return "feature = "+feature+" holder = "+holder+" value = "+value;
	}
}
