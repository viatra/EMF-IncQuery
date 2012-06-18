package org.eclipse.viatra2.emf.incquery.base.api;

import org.eclipse.emf.ecore.EStructuralFeature.Setting;

public interface FeatureListener {

	public void featureInserted(Setting setting);

	public void featureDeleted(Setting setting);
}
