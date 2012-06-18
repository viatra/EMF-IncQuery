package org.eclipse.viatra2.emf.incquery.base.api;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

public interface InstanceListener {

	public void instanceInserted(EClass clazz, EObject instance);

	public void instanceDeleted(EClass clazz, EObject instance);
}
