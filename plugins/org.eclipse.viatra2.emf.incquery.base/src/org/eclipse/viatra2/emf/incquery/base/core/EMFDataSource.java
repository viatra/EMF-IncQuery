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


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphObserver;

public abstract class EMFDataSource {

	public static ArrayList<IGraphObserver<EObject>> observers;
	public static Set<EReference> refToObserv;
	
	private static abstract class ForNotifier implements IGraphDataSource<EObject> {
		
		private static final long serialVersionUID = -1156614160439765303L;
		protected Notifier root;
		
		public ForNotifier(Notifier root, Set<EReference> _refToObserv) {
			this.root = root;
			observers = new ArrayList<IGraphObserver<EObject>>();
			refToObserv = _refToObserv;
		}
		
		@Override
		public void attachObserver(IGraphObserver<EObject> go) {
			observers.add(go);
		}

		@Override
		public void detachObserver(IGraphObserver<EObject> go) {
			observers.remove(go);
		}
		
		@Override
		public ArrayList<EObject> getTargetNodes(EObject source) {
			ArrayList<EObject> targetNodes = new ArrayList<EObject>();
			
			for (EReference ref : source.eClass().getEAllReferences()) {
				if (refToObserv.contains(ref)) {
					collectContents(source.eGet(ref), targetNodes);
				}
			}
			return targetNodes;
		}
	}
	
	public static class ForEObject extends ForNotifier {

		private static final long serialVersionUID = 5213298003205081695L;
		
		public ForEObject(EObject root, Set<EReference> _refToObserv) {
			super(root, _refToObserv);
		}
		
		@Override
		public Set<EObject> getAllNodes() {
			EObject rootObj = (EObject) root;
			HashSet<EObject> set = new HashSet<EObject>();
			set.add(rootObj);
			TreeIterator<EObject> iterator = rootObj.eAllContents();
			
			while (iterator.hasNext()) {
				set.add(iterator.next());
			}
			
			return set;
		}
	}
	
	public static class ForResource extends ForNotifier {

		private static final long serialVersionUID = 7372972311144593543L;
		
		public ForResource(Resource root, Set<EReference> _refToObserv) {
			super(root, _refToObserv);
		}

		@Override
		public Set<EObject> getAllNodes() {
			Resource rootResource = (Resource) root;
			HashSet<EObject> set = new HashSet<EObject>();
			TreeIterator<EObject> iterator = rootResource.getAllContents();
			while (iterator.hasNext()) {
				set.add(iterator.next());
			}
			return set;
		}
	}
	
	public static class ForResourceSet extends ForNotifier {

		private static final long serialVersionUID = 4619478637173366088L;
		
		public ForResourceSet(ResourceSet root, Set<EReference> _refToObserv) {
			super(root, _refToObserv);
		}

		@Override
		public Set<EObject> getAllNodes() {
			ResourceSet rootResourceSet = (ResourceSet) root;
			HashSet<EObject> set = new HashSet<EObject>();
			
			for (Resource res : rootResourceSet.getResources()) {
				TreeIterator<EObject> iterator = res.getAllContents();				
				while (iterator.hasNext()) {
					set.add(iterator.next());
				}
			}
			
			return set;
		}	
	}
	
	private static void collectContents(Object obj, List<EObject> contents) {
		if (obj instanceof EObjectEList<?>) {
			@SuppressWarnings("unchecked")
			EObjectEList<EObject> list = (EObjectEList<EObject>) obj;
			Iterator<EObject> it = list.iterator();
			
			while (it.hasNext()) {
				contents.add(it.next());
			}
		} 
		else if (obj instanceof EObject) {
			contents.add((EObject) obj);
		}
	}
}
