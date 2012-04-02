package org.eclipse.viatra2.emf.incquery.validation.runtime;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

public class ConstraintViolation<T extends IPatternMatch> {
	
	private IMarker marker;
	private T patternMatch;
	private ConstraintAdapter<T> adapter;
	
	public ConstraintViolation(ConstraintAdapter<T> adapter, T patternMatch) {
		this.patternMatch = patternMatch;
		this.adapter = adapter;
		updateMarker();
	}
	
	private void updateMarker() {
		EObject location = this.adapter.getConstraint().getLocationObject(patternMatch);
		URI uri = location.eResource().getURI();
		IResource markerLoc = null;
		String platformString = uri.toPlatformString(true);
		markerLoc = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);
		try {
			marker = markerLoc.createMarker("org.eclipse.emf.validation.problem");
			marker.setAttribute(IMarker.SEVERITY, this.adapter.getConstraint().getSeverity());
			marker.setAttribute(IMarker.TRANSIENT, true);
			marker.setAttribute(IMarker.MESSAGE, this.adapter.getConstraint().getMessage());
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public void dispose() {
		//dispose marker
	}
}
