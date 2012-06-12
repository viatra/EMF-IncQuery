package org.eclipse.viatra2.emf.incquery.base.core;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
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
	
	public void visitModel(Notifier emfRoot, Set<EStructuralFeature> features, Set<EClass> classes) {
		if (emfRoot instanceof EObject) {
			visitEObjectRoot((EObject) emfRoot, features, classes);
		}
		else if (emfRoot instanceof Resource) {
			visitResourceRoot((Resource) emfRoot, features, classes);
		}
		else if (emfRoot instanceof ResourceSet) {
			visitResourceSetRoot((ResourceSet) emfRoot, features, classes);
		}
	}

	private void visitResourceSetRoot(ResourceSet resourceSet, Set<EStructuralFeature> features, Set<EClass> classes) {
		for (Resource r : resourceSet.getResources()) {
			visitResourceRoot(r, features, classes);
		}
	}

	private void visitResourceRoot(Resource resource, Set<EStructuralFeature> features, Set<EClass> classes) {
		for (EObject obj : resource.getContents()) {
			visitEObjectRoot(obj, features, classes);
		}
	}

	private void visitEObjectRoot(EObject root, Set<EStructuralFeature> features, Set<EClass> classes) {
		visitObject(root, features, classes);
		TreeIterator<EObject> it = root.eAllContents();

		while (it.hasNext()) {
			visitObject(it.next(), features, classes);
		}
	}
	
	private void visitObject(EObject obj, Set<EStructuralFeature> features, Set<EClass> classes) {
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
									navigationHelper.getContentAdapter().insertRefTuple(ref, it.next(), obj);
								}
							} else {
								navigationHelper.getContentAdapter().insertRefTuple(ref, (EObject) o, obj);
							}
						}
					}
				}

				visitObjectForEAttributeInsert(obj, features);
			}
		}
	}

	// visit all the attributes of the given EObject and insert them to the
	// cache
	public void visitObjectForEAttributeInsert(EObject obj, Set<EStructuralFeature> features) {
		if (obj != null) {
			for (EAttribute attr : obj.eClass().getEAllAttributes()) {
				if ((navigationHelper.getType() == NavigationHelperType.ALL) || features.contains(attr)) {
					Object o = obj.eGet(attr);
					if (o != null) {
						navigationHelper.getContentAdapter().insertAttrTuple(attr, o, obj);
					}
				}
			}
		}
	}

	// visit all the attributes of the given EObject and delete them from the
	// cache
	public void visitObjectForEAttributeDelete(EObject obj, Set<EStructuralFeature> features) {
		if (obj != null) {
			for (EAttribute attr : obj.eClass().getEAllAttributes()) {
				if ((navigationHelper.getType() == NavigationHelperType.ALL) || features.contains(attr)) {
					Object o = obj.eGet(attr);
					if (o != null) {
						navigationHelper.getContentAdapter().removeAttrTuple(attr, o, obj);
					}
				}
			}
		}
	}
	
}
