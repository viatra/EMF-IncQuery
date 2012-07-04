package org.eclipse.viatra2.emf.incquery.base.core;

import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra2.emf.incquery.base.comprehension.EMFVisitor;

public class NavigationHelperVisitor extends EMFVisitor {

	private NavigationHelperImpl navigationHelper;
	Set<EStructuralFeature> features; 
	Set<EClass> classes; 
	Set<EDataType> dataTypes;
	boolean isInsertion;
	boolean descendHierarchy;
	
	
	
	/**
	 * Creates a visitor for processing a single change event. Does not traverse the model. Uses all the observed types.
	 */
	public static NavigationHelperVisitor newChangeVisitor(NavigationHelperImpl navigationHelper, boolean isInsertion) {
		return new NavigationHelperVisitor(
				navigationHelper, 
				navigationHelper.getObservedFeatures(), 
				navigationHelper.getObservedClasses(), 
				navigationHelper.getObservedDataTypes(), 
				isInsertion, false);
	}	
	
	/**
	 * Creates a visitor for a single-pass traversal of the whole model, processing only the given types and inserting them.
	 */
	public static NavigationHelperVisitor newTraversingVisitor(NavigationHelperImpl navigationHelper,
			Set<EStructuralFeature> features, Set<EClass> classes, Set<EDataType> dataTypes) {
		return new NavigationHelperVisitor(navigationHelper, features, classes, dataTypes, true, true);
	}


	NavigationHelperVisitor(NavigationHelperImpl navigationHelper,
			Set<EStructuralFeature> features, Set<EClass> classes,
			Set<EDataType> dataTypes, boolean isInsertion, boolean descendHierarchy) {
		super();
		this.navigationHelper = navigationHelper;
		this.features = features;
		this.classes = classes;
		this.dataTypes = dataTypes;
		this.isInsertion = isInsertion;
		this.descendHierarchy = descendHierarchy;
	}
	
//	public void visitModel(Notifier emfRoot, Set<EStructuralFeature> features, Set<EClass> classes, Set<EDataType> dataTypes) {
//		if (emfRoot instanceof EObject) {
//			visitEObjectRoot((EObject) emfRoot, features, classes, dataTypes);
//		}
//		else if (emfRoot instanceof Resource) {
//			visitResourceRoot((Resource) emfRoot, features, classes, dataTypes);
//		}
//		else if (emfRoot instanceof ResourceSet) {
//			visitResourceSetRoot((ResourceSet) emfRoot, features, classes, dataTypes);
//		}
//	}
//
//	private void visitResourceSetRoot(ResourceSet resourceSet, Set<EStructuralFeature> features, Set<EClass> classes, Set<EDataType> dataTypes) {
//		for (Resource r : resourceSet.getResources()) {
//			visitResourceRoot(r, features, classes, dataTypes);
//		}
//	}
//
//	private void visitResourceRoot(Resource resource, Set<EStructuralFeature> features, Set<EClass> classes, Set<EDataType> dataTypes) {
//		for (EObject obj : resource.getContents()) {
//			visitEObjectRoot(obj, features, classes, dataTypes);
//		}
//	}
//
//	private void visitEObjectRoot(EObject root, Set<EStructuralFeature> features, Set<EClass> classes, Set<EDataType> dataTypes) {
//		visitObject(root, features, classes, dataTypes);
//		TreeIterator<EObject> it = root.eAllContents();
//
//		while (it.hasNext()) {
//			visitObject(it.next(), features, classes, dataTypes);
//		}
//	}
	
	@Override
	public boolean pruneSubtrees(EObject source) {
		return !descendHierarchy;
	}
	
	@Override
	public boolean pruneSubtrees(Resource source) {
		return !descendHierarchy;
	}
	
	@Override
	public boolean pruneFeature(EStructuralFeature feature) {
		if ((navigationHelper.getType() == NavigationHelperType.ALL) || features.contains(feature)) return false; 
		if (feature instanceof EAttribute && dataTypes.contains(((EAttribute)feature).getEAttributeType())) return false;
		return true;
	}
	
	@Override
	public void visitElement(EObject source) {
		final EClass eClass = source.eClass();
		if (isInsertion) {
			navigationHelper.getContentAdapter().insertInstanceTuple(eClass, source);
		} else {
			navigationHelper.getContentAdapter().removeInstanceTuple(eClass, source);
		}
	}
	
	@Override
	public void visitAttribute(EObject source, EAttribute feature, Object target) {
		final EDataType eAttributeType = feature.getEAttributeType();
		if (isInsertion) {
			navigationHelper.getContentAdapter().insertFeatureTuple(feature, target, source);
		}
		else {
			navigationHelper.getContentAdapter().removeFeatureTuple(feature, target, source);
		}
		navigationHelper.getContentAdapter().dataTypeInstanceUpdate(eAttributeType, target, isInsertion);
	};
	
	@Override
	public void visitInternalContainment(EObject source, EReference feature, EObject target) {
		visitReference(source, feature, target);
	}
	
	@Override
	public void visitNonContainmentReference(EObject source, EReference feature, EObject target) {
		visitReference(source, feature, target);
//		if (isInsertion) navigationHelper.considerForExpansion(target);
	};
	
	private void visitReference(EObject source, EReference feature,
			EObject target) {
		if (isInsertion)
			navigationHelper.getContentAdapter().insertFeatureTuple(feature, target, source);
		else
			navigationHelper.getContentAdapter().removeFeatureTuple(feature, target, source);
	}
	

//	private void visitObject(EObject obj, Set<EStructuralFeature> features, Set<EClass> classes, Set<EDataType> dataTypes) {
//		if (obj != null) {
//
//			if (classes != null) {
//				if ((navigationHelper.getType() == NavigationHelperType.ALL) || classes.contains(obj.eClass())) {
//					navigationHelper.getContentAdapter().insertInstanceTuple(obj.eClass(), obj);
//				}
//			}
//
//			if (features != null) {
//				for (EReference ref : obj.eClass().getEAllReferences()) {
//					if ((navigationHelper.getType() == NavigationHelperType.ALL) || features.contains(ref)) {
//						Object o = obj.eGet(ref);
//
//						if (o != null) {
//							if (o instanceof EObjectEList<?>) {
//								@SuppressWarnings("unchecked")
//								EObjectEList<EObject> list = (EObjectEList<EObject>) o;
//								Iterator<EObject> it = list.iterator();
//								while (it.hasNext()) {
//									navigationHelper.getContentAdapter().insertFeatureTuple(ref, it.next(), obj);
//								}
//							} else {
//								navigationHelper.getContentAdapter().insertFeatureTuple(ref, o, obj);
//							}
//						}
//					}
//				}
//
//				visitObjectForEAttribute(obj, features, dataTypes, true);
//			}
//		}
//	}

//	// visit all the attributes of the given EObject and insert them to the
//	// cache
//	public void visitObjectForEAttribute(EObject obj, Set<EStructuralFeature> features, Set<EDataType> dataTypes, boolean isInsertion) {
//		if (obj != null) {
//			for (EAttribute attr : obj.eClass().getEAllAttributes()) {
//				final EDataType eAttributeType = attr.getEAttributeType();
//				if ((navigationHelper.getType() == NavigationHelperType.ALL) || features.contains(attr) || dataTypes.contains(eAttributeType)) {
//					if (attr.isMany()) {
//						Collection<Object> values = (Collection<Object>) obj.eGet(attr);
//						for (Object value : values) {
//							visitAttributeValue(obj, features, dataTypes,
//									isInsertion, attr, eAttributeType, value);
//						}
//					} else {
//						Object value = obj.eGet(attr);
//						if (value != null) {
//							visitAttributeValue(obj, features, dataTypes,
//									isInsertion, attr, eAttributeType, value);
//						}
//					}
//				}
//			}
//		}
//	}

}
