package org.eclipse.viatra2.emf.incquery.base.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EObjectEList;

public class NavigationHelperVisitor {

	private NavigationHelperImpl navigationHelper;
	
	public NavigationHelperVisitor(NavigationHelperImpl navigationHelper) {
		this.navigationHelper = navigationHelper;
	}
	
	public void visitModel(Notifier emfRoot, Set<EStructuralFeature> features, Set<EClass> classes, Set<EDataType> dataTypes) {
		if (emfRoot instanceof EObject) {
			visitEObjectRoot((EObject) emfRoot, features, classes, dataTypes);
		}
		else if (emfRoot instanceof Resource) {
			visitResourceRoot((Resource) emfRoot, features, classes, dataTypes);
		}
		else if (emfRoot instanceof ResourceSet) {
			visitResourceSetRoot((ResourceSet) emfRoot, features, classes, dataTypes);
		}
	}

	private void visitResourceSetRoot(ResourceSet resourceSet, Set<EStructuralFeature> features, Set<EClass> classes, Set<EDataType> dataTypes) {
		for (Resource r : resourceSet.getResources()) {
			visitResourceRoot(r, features, classes, dataTypes);
		}
	}

	private void visitResourceRoot(Resource resource, Set<EStructuralFeature> features, Set<EClass> classes, Set<EDataType> dataTypes) {
		for (EObject obj : resource.getContents()) {
			visitEObjectRoot(obj, features, classes, dataTypes);
		}
	}

	private void visitEObjectRoot(EObject root, Set<EStructuralFeature> features, Set<EClass> classes, Set<EDataType> dataTypes) {
		visitObject(root, features, classes, dataTypes);
		TreeIterator<EObject> it = root.eAllContents();

		while (it.hasNext()) {
			visitObject(it.next(), features, classes, dataTypes);
		}
	}
	
	private void visitObject(EObject obj, Set<EStructuralFeature> features, Set<EClass> classes, Set<EDataType> dataTypes) {
		if (obj != null) {

			if (classes != null) {
				if ((navigationHelper.getType() == NavigationHelperType.ALL) || classes.contains(obj.eClass())) {
					navigationHelper.getContentAdapter().insertInstanceTuple(obj.eClass(), obj);
				}
			}

			if (features != null) {
				for (EReference ref : obj.eClass().getEAllReferences()) {
					if ((navigationHelper.getType() == NavigationHelperType.ALL) || features.contains(ref)) {
						Object o = obj.eGet(ref);

						if (o != null) {
							if (o instanceof EObjectEList<?>) {
								@SuppressWarnings("unchecked")
								EObjectEList<EObject> list = (EObjectEList<EObject>) o;
								Iterator<EObject> it = list.iterator();
								while (it.hasNext()) {
									navigationHelper.getContentAdapter().insertFeatureTuple(ref, it.next(), obj);
								}
							} else {
								navigationHelper.getContentAdapter().insertFeatureTuple(ref, o, obj);
							}
						}
					}
				}

				visitObjectForEAttribute(obj, features, dataTypes, true);
			}
		}
	}

	// visit all the attributes of the given EObject and insert them to the
	// cache
	public void visitObjectForEAttribute(EObject obj, Set<EStructuralFeature> features, Set<EDataType> dataTypes, boolean isInsertion) {
		if (obj != null) {
			for (EAttribute attr : obj.eClass().getEAllAttributes()) {
				final EDataType eAttributeType = attr.getEAttributeType();
				if ((navigationHelper.getType() == NavigationHelperType.ALL) || features.contains(attr) || dataTypes.contains(eAttributeType)) {
					if (attr.isMany()) {
						Collection<Object> values = (Collection<Object>) obj.eGet(attr);
						for (Object value : values) {
							visitAttributeValue(obj, features, dataTypes,
									isInsertion, attr, eAttributeType, value);
						}
					} else {
						Object value = obj.eGet(attr);
						if (value != null) {
							visitAttributeValue(obj, features, dataTypes,
									isInsertion, attr, eAttributeType, value);
						}
					}
				}
			}
		}
	}

	/**
	 * @param obj
	 * @param features
	 * @param dataTypes
	 * @param isInsertion
	 * @param attr
	 * @param eAttributeType
	 * @param value
	 */
	private void visitAttributeValue(EObject obj,
			Set<EStructuralFeature> features, Set<EDataType> dataTypes,
			boolean isInsertion, EAttribute attr,
			final EDataType eAttributeType, Object value) 
	{
			if (isInsertion) {
				navigationHelper.getContentAdapter().insertFeatureTuple(attr, value, obj);
			}
			else {
				navigationHelper.getContentAdapter().removeFeatureTuple(attr, value, obj);
			}
			navigationHelper.getContentAdapter().dataTypeInstanceUpdate(eAttributeType, value, isInsertion);
	}
}
