package org.eclipse.viatra2.emf.incquery.base.core;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.viatra2.emf.incquery.base.api.TransitiveClosureHelper;
import org.eclipse.viatra2.emf.incquery.base.exception.IncQueryBaseException;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.incscc.IncSCCAlg;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphObserver;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.ITcObserver;

/**
 * Implementation class for the transitive closure.
 * The class is a wrapper for the tc algorithms on an emf model.
 * 
 * @author Tamas Szabo
 *
 */
public class TransitiveClosureHelperImpl extends EContentAdapter implements
		TransitiveClosureHelper, ITcObserver<EObject> {

	private IncSCCAlg<EObject> sccAlg;
	private Set<EReference> refToObserv;
	private IGraphDataSource<EObject> dataSource;
	private ArrayList<ITcObserver<EObject>> observers;
	private Notifier notifier;
	
	public TransitiveClosureHelperImpl(Notifier emfRoot,
			Set<EReference> refToObserv) throws IncQueryBaseException {
		this.refToObserv = refToObserv;
		this.notifier = emfRoot;
		this.observers = new ArrayList<ITcObserver<EObject>>();
		
		if (emfRoot instanceof EObject)
			dataSource = new EMFDataSource.ForEObject((EObject) emfRoot,
					refToObserv);
		else if (emfRoot instanceof Resource)
			dataSource = new EMFDataSource.ForResource((Resource) emfRoot,
					refToObserv);
		else if (emfRoot instanceof ResourceSet)
			dataSource = new EMFDataSource.ForResourceSet((ResourceSet) emfRoot, refToObserv);
		else
			throw new IncQueryBaseException(
					IncQueryBaseException.INVALID_EMFROOT);

		this.sccAlg = new IncSCCAlg<EObject>(dataSource);
		emfRoot.eAdapters().add(this);
	}

	private void visitObjectForEReference(EObject obj, boolean isInsert) {
		for (EReference ref : obj.eClass().getEReferences()) {
			if (refToObserv.contains(ref)) {
				Object o = obj.eGet(ref);

				if (o instanceof EObjectEList<?>) {
					@SuppressWarnings("unchecked")
					EObjectEList<EObject> list = (EObjectEList<EObject>) o;
					Iterator<EObject> it = list.iterator();

					while (it.hasNext()) {
						EObject target = it.next();
						if (isInsert) {
							nodeInserted(target);
							edgeInserted(obj, target);
						} else {
							edgeDeleted(obj, target);
							nodeDeleted(target);
						}
					}
				} else {
					EObject target = (EObject) o;
					if (isInsert) {
						nodeInserted(target);
						edgeInserted(obj, target);
					} else {
						edgeDeleted(obj, target);
						nodeDeleted(target);
					}
				}
			}
		}
	}

	@Override
	public void notifyChanged(Notification notification) {
		super.notifyChanged(notification);
		Object feature = notification.getFeature();
		//System.out.println(notification);
		if (feature instanceof EReference) {

			EReference ref = (EReference) feature;
			EObject oldValue = (EObject) notification.getOldValue();
			EObject newValue = (EObject) notification.getNewValue();
			EObject notifier = (EObject) notification.getNotifier();

			if (ref.isContainment()) {
				// Inserting nodes
				if (notification.getEventType() == Notification.ADD
						&& oldValue == null && newValue != null) {
					nodeInserted(newValue);
					visitObjectForEReference(newValue, true);
				}
				if (notification.getEventType() == Notification.REMOVE
						&& newValue == null && oldValue != null) {
					visitObjectForEReference(oldValue, false);
					nodeDeleted(oldValue);
				}
			} else // Inserting edges (excusively -> edge or node modification)
			if (refToObserv.contains(ref)) {

				if (notification.getEventType() == Notification.ADD
						&& newValue != null) {
					edgeInserted(notifier, newValue);
				}
				if (notification.getEventType() == Notification.REMOVE
						&& oldValue != null) {
					edgeDeleted(notifier, oldValue);
				}
				if (notification.getEventType() == Notification.SET) {
					if (oldValue != null) {
						edgeDeleted(notifier, oldValue);
					}

					if (newValue != null) {
						edgeInserted(notifier, newValue);
					}
				}
			}
		}
	}

	private void edgeInserted(EObject source, EObject target) {
		for (IGraphObserver<EObject> o : EMFDataSource.observers) {
			o.edgeInserted(source, target);
		}
	}

	private void edgeDeleted(EObject source, EObject target) {
		for (IGraphObserver<EObject> o : EMFDataSource.observers) {
			o.edgeDeleted(source, target);
		}
	}

	private void nodeInserted(EObject node) {
		for (IGraphObserver<EObject> o : EMFDataSource.observers) {
			o.nodeInserted(node);
		}
	}

	private void nodeDeleted(EObject node) {
		for (IGraphObserver<EObject> o : EMFDataSource.observers) {
			o.nodeDeleted(node);
		}
	}

	@Override
	public void attachObserver(ITcObserver<EObject> to) {
		this.observers.add(to);
	}

	@Override
	public void detachObserver(ITcObserver<EObject> to) {
		this.observers.remove(to);
	}

	@Override
	public Set<EObject> getAllReachableTargets(EObject source) {
		return this.sccAlg.getAllReachableTargets(source);
	}

	@Override
	public Set<EObject> getAllReachableSources(EObject target) {
		return this.sccAlg.getAllReachableSources(target);
	}

	@Override
	public boolean isReachable(EObject source, EObject target) {
		return this.sccAlg.isReachable(source, target);
	}

	@Override
	public void tupleInserted(EObject source, EObject target) {
		for (ITcObserver<EObject> to : observers) {
			to.tupleInserted(source, target);
		}
	}

	@Override
	public void tupleDeleted(EObject source, EObject target) {
		for (ITcObserver<EObject> to : observers) {
			to.tupleDeleted(source, target);
		}
	}

	@Override
	public void dispose() {
		this.sccAlg.dispose();
		this.notifier.eAdapters().remove(this);
	}
}
