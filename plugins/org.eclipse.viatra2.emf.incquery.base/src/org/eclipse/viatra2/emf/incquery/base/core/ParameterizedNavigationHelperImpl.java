package org.eclipse.viatra2.emf.incquery.base.core;


import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.emf.incquery.base.api.ParameterizedNavigationHelper;
import org.eclipse.viatra2.emf.incquery.base.exception.IncQueryBaseException;

public class ParameterizedNavigationHelperImpl extends NavigationHelperImpl implements ParameterizedNavigationHelper {

	public ParameterizedNavigationHelperImpl(Notifier notifier) throws IncQueryBaseException {
		super(notifier, NavigationHelperType.REGISTER);
	}
	
	@Override
	public void registerEStructuralFeatures(Set<EStructuralFeature> features) {
		if (features != null) {
			observedFeatures.addAll(features);
			
			visitModel(notifier, features, null);
		}
	}

	@Override
	public void unregisterEStructuralFeatures(Set<EStructuralFeature> features) {
		if (features != null) {
			observedFeatures.removeAll(features);

			for (EStructuralFeature f : features) {
				if (f instanceof EAttribute) {
					for (Object key : attrMap.keySet()) {
						attrMap.get(key).remove(f);
					}
				}
				if (f instanceof EReference) {
					for (EObject key : refMap.keySet()) {
						refMap.get(key).remove(f);
					}
				}
			}
		}
	}

	@Override
	public void registerEClasses(Set<EClass> classes) {
		if (classes != null) {
			observedClasses.addAll(classes);
			
			visitModel(notifier, null, classes);
		}
	}

	@Override
	public void unregisterEClasses(Set<EClass> classes) {
		if (classes != null) {
			observedClasses.removeAll(classes);
		 
			for (EClass c : classes)
				instanceMap.remove(c);
		}
	}
}
