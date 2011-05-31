package org.eclipse.viatra2.emf.incquery.validation.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternSignature;

public class ValidationProblem<Signature extends IPatternSignature> {

	Constraint<Signature> kind;
	
	Signature affectedElements;
	
	IMarker marker;
	
	Adapter eventHandler = new Adapter() {
		
		@Override
		public void setTarget(Notifier newTarget) {}
		
		@Override
		public void notifyChanged(Notification notification) {
			try {
				updateMarker();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public boolean isAdapterForType(Object type) {
			return false;
		}
		
		@Override
		public Notifier getTarget() {
			return null;
		}
	};
	
	public ValidationProblem(Constraint<Signature> _kind, Signature _affectedElements) 
	{
		this.kind = _kind;
		this.affectedElements = _affectedElements;
	}
	
	public IMarker createMarker(IFile file) throws CoreException {
		marker = file.createMarker("org.eclipse.emf.validation.problem");
		marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		marker.setAttribute(IMarker.TRANSIENT, true);
		
		for (Object _o : affectedElements.toArray()) {
			if (_o instanceof EObject) {
				((EObject) _o).eAdapters().add(eventHandler);
			}
		}
//		if (message==null) {
//			message = kind.getMessage();
//		}
		updateMarker();
		
		return marker;
	}
	
	private void updateMarker() throws CoreException {
		marker.setAttribute(IMarker.MESSAGE, kind.getMessage(affectedElements));    
		EObject location = kind.getLocationObject(affectedElements);
		if (location!=null) {
			/*
			 * Based on EMF Validation's MarkerUtil class inner attributes
			 */
			marker.setAttribute(EValidator.URI_ATTRIBUTE, EcoreUtil.getURI(location).toString());
			marker.setAttribute(IMarker.LOCATION, location.eClass().getName() + " " + BasePatternSignature.prettyPrintValue(location));//TODO find other place for this
		}
	}
	
	/**
	 * Two validation problems are equal iff:
	 * - they have the same kind
	 * - they affect the same collection of elements
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ValidationProblem) {
			@SuppressWarnings("unchecked")
			ValidationProblem<? extends IPatternSignature> vp2 = (ValidationProblem<? extends IPatternSignature>)obj;
			return this.kind.equals(vp2.kind) && 
				affectedElements.equals(vp2.affectedElements);
		}
		return false;
	}
	
	
	@Override
	public int hashCode() {
		int hash = 31 + this.kind.hashCode();
		hash = 31*hash + affectedElements.hashCode();
		return hash;
	}
	
	/**
	 * A callback method when the validation problem is removed.
	 */
	public void dispose() {
		for (Object _o : affectedElements.toArray()) {
			if (_o instanceof EObject) {
				((EObject) _o).eAdapters().remove(eventHandler);
			}
		}
	}
}
