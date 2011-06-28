/*******************************************************************************
 * Copyright (c) 2004-2010 Akos Horvath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.runtime.term;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author Akos Horvath
 *
 */
public class Deleter {
	protected Vector<EObject> deletableEObjects = new Vector<EObject>();
	protected EObject root = null;
	protected static Resource topLevelResource= null;
	private Map<EObject, Collection<Setting>> crossReferences = null;

		/**
		 * Returns the topmost element of an EObject.
		 * @param currentObject object inside the containment hiearchy
		 * @return topmost element containment hiearchy
		 */
		protected EObject getRoot(EObject currentObject) {
			EObject parent = currentObject.eContainer();
			if (parent != null)
				return getRoot(parent);
			else
				return currentObject;
		}

		public EObject getRoot2(EObject curr){
			if(curr.eContainer() == null)
				return curr;
			else
				return EcoreUtil.getRootContainer(curr);
		}

		/**
		 *
		 * @param instanceNode
		 */
		public void setRoot(EObject instanceNode){
			this.root = getRoot(instanceNode);
		}

		/**
		 *
		 * @param instanceNode
		 * @param isRoot
		 */
		public void setRoot(EObject instanceNode, boolean isRoot){
			if(isRoot){
				this.root = instanceNode;
			}
			else{
				setRoot(instanceNode);
			}
		}


		/**
		 * Helper method
		 * @return Map containing all references to the deleted EObject
		 */
		private Map<EObject, Collection<Setting>> getReferringObjects(){
			//EObject root = getRoot(this.root);
			//Map test = EcoreUtil.UsageCrossReferencer.findAll(deletableEObjects, root);
			if(topLevelResource != null)
				{Map<EObject, Collection<Setting>> test = EcoreUtil.UsageCrossReferencer.findAll(deletableEObjects, topLevelResource);
				return test;}
			else
				System.out.println("***ERROR*** Does not have a ResourceSeT");

			return null;
		}

		/**
		 * Deletes the param EObject from containment hiearchy.
		 * @param eObject deleted EObject
		 */
		protected void remove(EObject eObject){
			deletableEObjects.addAll(getContainedElements(eObject));

			//deletes from the container
			EObject owner = eObject.eContainer();
			EReference contains = (EReference)eObject.eContainingFeature();
			if(contains.isMany())
				{((EList)owner.eGet(contains)).remove(eObject);}
			else
				{owner.eUnset(contains);}

			//have to set the root element to execute this operation
			deleteTreeRefs();
		}


		/**
		 * Collects all elements from the viatra.gtasm.compiled.emf.internal subtree of a deleted EObject.
		 * @param current tree element - deleted EObject or an element in its subtree
		 * @return subtree of the deleted EObject
		 */
		protected Vector<EObject> getContainedElements(EObject current){
			Vector<EObject> result = new Vector<EObject>();
			result.add(current);
			for (int i = current.eContents().size() - 1; i > -1; i--) {
				EObject currentObject = current.eContents().get(i);
				result.addAll(getContainedElements(currentObject));
			}
			return result;
		}

		/**
		 *
		 *
		 */
		protected void deleteTreeRefs(){
			if (deletableEObjects.size() > 0) {
				crossReferences = this.getReferringObjects();
				for(Iterator<EObject> mit = deletableEObjects.iterator();mit.hasNext();){
					EObject del = mit.next();
					Collection<Setting> refs = crossReferences.get(del);
					if(refs!=null){
						for(Iterator<Setting> it = refs.iterator(); it.hasNext();){
							EStructuralFeature.Setting set = it.next();
							EObject refSource = set.getEObject();
							EReference ref = (EReference)set.getEStructuralFeature();
							if(ref.isMany()){
								((EList)refSource.eGet(ref)).remove(del);
							}
							else{
								refSource.eUnset(ref);
							}
						}
					}
				}
			}
		}

		/**
		 * @return the topLevelResourceSet
		 */
		public static Resource getTopLevelResourceSet() {
			return topLevelResource;
		}

		/**
		 * @param topLevelResourceSet the topLevelResourceSet to set
		 */
		public static void setTopLevelResource(Resource topLevelResourceSet) {
			Deleter.topLevelResource = topLevelResource;
		}

//		/**
//		 * Restores cross references to deleted elements. This is used for undoing a rule application.
//		 */
//		protected void restoreTreeRefs(){
//			for(Iterator mit = deletableEObjects.iterator();mit.hasNext();){
//				EObject del = (EObject)mit.next();
//				Collection refs = (Collection) crossReferences.get(del);
//				if(refs!=null){
//					for(Iterator it = refs.iterator(); it.hasNext();){
//						EStructuralFeature.Setting set = (EStructuralFeature.Setting)it.next();
//						EObject refSource = set.getEObject();
//						EReference ref = (EReference)set.getEStructuralFeature();
//						if(ref.isMany()){
//							((EList)refSource.eGet(ref)).add(del);
//						}
//						else{
//							refSource.eSet(ref,del);
//						}
//					}
//				}
//			}
//		}


}


