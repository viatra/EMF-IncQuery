package org.eclipse.viatra2.emf.incquery.base.core;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
	
	public static class ForEObject implements IGraphDataSource<EObject> {

		private static final long serialVersionUID = 5213298003205081695L;
		private EObject root;
		
		public ForEObject(EObject root, Set<EReference> _refToObserv) {
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
		public Set<EObject> getAllNodes() {
			HashSet<EObject> set = new HashSet<EObject>();
			set.add(root);
			TreeIterator<EObject> iterator = root.eAllContents();
			
			while (iterator.hasNext()) {
				set.add(iterator.next());
			}
			
			return set;
		}

		@Override
		public ArrayList<EObject> getTargetNodes(EObject source) {
			ArrayList<EObject> a = new ArrayList<EObject>();
			
			for (EReference ref : source.eClass().getEAllReferences()) {
				if (refToObserv.contains(ref)) {
					Object o = source.eGet(ref);
					
					if (o instanceof EObjectEList<?>) {
						@SuppressWarnings("unchecked")
						EObjectEList<EObject> list = (EObjectEList<EObject>) o;
						Iterator<EObject> it = list.iterator();
						
						while (it.hasNext()) {
							a.add(it.next());
						}
					}
//					else if (o instanceof EObjectResolvingEList<?>) {
//						@SuppressWarnings("unchecked")
//						EObjectResolvingEList<EObject> list = (EObjectResolvingEList<EObject>) o;
//						Iterator<EObject> it = list.iterator();
//						
//						while (it.hasNext()) {
//							a.add(it.next());
//						}
//					}
//					else if (o instanceof EObjectWithInverseResolvingEList<?>) {
//						@SuppressWarnings("unchecked")
//						EObjectWithInverseResolvingEList<EObject> list = (EObjectWithInverseResolvingEList<EObject>) o;
//						Iterator<EObject> it = list.iterator();
//						
//						while (it.hasNext()) {
//							a.add(it.next());
//						}
//					}
					else if (o instanceof EObject)
						a.add((EObject) o);
				}
			}
			return a;
		}
	}
	
	public static class ForResource implements IGraphDataSource<EObject> {

		private static final long serialVersionUID = 7372972311144593543L;
		private Resource root;
		
		public ForResource(Resource root, Set<EReference> _refToObserv) {
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
		public Set<EObject> getAllNodes() {
			HashSet<EObject> set = new HashSet<EObject>();

			TreeIterator<EObject> iterator = root.getAllContents();
			
			while (iterator.hasNext()) {
				set.add(iterator.next());
			}
			
			return set;
		}

		@Override
		public ArrayList<EObject> getTargetNodes(EObject source) {
			ArrayList<EObject> a = new ArrayList<EObject>();
			
			for (EReference ref : source.eClass().getEAllReferences()) {
				if (refToObserv.contains(ref)) {
					Object o = source.eGet(ref);
					
					if (o instanceof EObjectEList<?>) {
						@SuppressWarnings("unchecked")
						EObjectEList<EObject> list = (EObjectEList<EObject>) o;
						Iterator<EObject> it = list.iterator();
						
						while (it.hasNext()) {
							a.add(it.next());
						}
					} else if (o instanceof EObject)
						a.add((EObject) o);
				}
			}
			return a;	
		}
	}
	
	public static class ForResourceSet implements IGraphDataSource<EObject> {

		private static final long serialVersionUID = 4619478637173366088L;
		private ResourceSet root;
		
		public ForResourceSet(ResourceSet root, Set<EReference> _refToObserv) {
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
		public Set<EObject> getAllNodes() {
			HashSet<EObject> set = new HashSet<EObject>();
			
			for (Resource res : root.getResources()) {
				TreeIterator<EObject> iterator = res.getAllContents();				
				while (iterator.hasNext()) {
					set.add(iterator.next());
				}
			}
			
			return set;
		}

		@Override
		public ArrayList<EObject> getTargetNodes(EObject source) {
			ArrayList<EObject> a = new ArrayList<EObject>();
			
			for (EReference ref : source.eClass().getEAllReferences()) {
				if (refToObserv.contains(ref)) {
					Object o = source.eGet(ref);
					
					if (o instanceof EObjectEList<?>) {
						@SuppressWarnings("unchecked")
						EObjectEList<EObject> list = (EObjectEList<EObject>) o;
						Iterator<EObject> it = list.iterator();
						
						while (it.hasNext()) {
							a.add(it.next());
						}
					} else if (o instanceof EObject)
						a.add((EObject) o);
				}
			}
			return a;
		}
		
	}
}
