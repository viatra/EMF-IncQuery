package org.eclipse.viatra2.emf.incquery.validation.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.viatra2.emf.incquery.validation.runtime.Constraint;

public class ConstraintViolation<MatchType extends IPatternMatch> {

	Constraint<MatchType> kind;
	
	MatchType affectedElements;
	
	private IMarker marker;
	
	Adapter eventHandler = new Adapter() {
		
		@Override
		public void setTarget(Notifier newTarget) {}
		
		@Override
		public void notifyChanged(Notification notification) {
			//If the marker does not exists, no update is necessary (or possible)
			if (!marker.exists()) return;
			try {
				/*switch(notification.getEventType()){
				case Notification.REMOVE:
				case Notification.REMOVE_MANY:
				case Notification.REMOVING_ADAPTER:
					break;
				default:*/
					if (notification.getNewValue() != null) {
						//This is needed because a deletion can cause  a set with null value
						updateMarker();
					}
				//}
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
	
	public ConstraintViolation(Constraint<MatchType> _kind, MatchType _affectedElements) 
	{
		this.kind = _kind;
		this.affectedElements = _affectedElements;
	}
	
	public IMarker createMarker(IFile file) throws CoreException {
		for (Object _o : affectedElements.toArray()) {
			if (_o instanceof EObject) {
				((EObject) _o).eAdapters().add(eventHandler);
			}
		}
		EObject location = kind.getLocationObject(affectedElements);
		URI uri = location.eResource().getURI();
		IResource markerLoc = null;
		if (uri.isPlatformResource()) {
			String platformString = uri.toPlatformString(true);
			 markerLoc = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);
		} else {
			markerLoc = file;
		}
		marker = markerLoc.createMarker("org.eclipse.emf.validation.problem");
		marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		marker.setAttribute(IMarker.TRANSIENT, true);
		
//		if (message==null) {
//			message = kind.getMessage();
//		}
		updateMarker();
		
		return marker;
	}
	
	private void updateMarker() throws CoreException {
		marker.setAttribute(IMarker.MESSAGE, kind.getMessage());    
		EObject location = kind.getLocationObject(affectedElements);
		if (location!=null) {
			/*
			 * Based on EMF Validation's MarkerUtil class inner attributes
			 */
			String uri = EcoreUtil.getURI(location).toString();
			marker.setAttribute(EValidator.URI_ATTRIBUTE, uri);
			marker.setAttribute(IMarker.LOCATION, location.eClass().getName() + " " + BasePatternMatch.prettyPrintValue(location));//TODO find other place for this
		}
	}
	
	/**
	 * Two validation problems are equal iff:
	 * - they have the same kind
	 * - they affect the same collection of elements
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConstraintViolation) {
			@SuppressWarnings("unchecked")
			ConstraintViolation<? extends IPatternMatch> vp2 = (ConstraintViolation<? extends IPatternMatch>)obj;
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
	 * @throws CoreException 
	 */
	public void dispose() throws CoreException {
		for (Object _o : affectedElements.toArray()) {
			if (_o instanceof EObject) {
				((EObject) _o).eAdapters().remove(eventHandler);
			}
		}
		if (marker != null) marker.delete();
	}
}
