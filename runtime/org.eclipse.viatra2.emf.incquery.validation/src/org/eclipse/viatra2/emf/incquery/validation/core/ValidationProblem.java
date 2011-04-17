package org.eclipse.viatra2.emf.incquery.validation.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature;

public class ValidationProblem<Signature extends IPatternSignature> {

	Constraint<Signature> kind;
	
	Signature affectedElements;
	
	public ValidationProblem(Constraint<Signature> _kind, Signature _affectedElements) 
	{
		this.kind = _kind;
		this.affectedElements = _affectedElements;
	}
	
	public IMarker createMarker(IFile file) throws CoreException {
		IMarker marker = file.createMarker(IMarker.PROBLEM);
		marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		marker.setAttribute(IMarker.TRANSIENT, true);
		marker.setAttribute(IMarker.MESSAGE, kind.getMessage());    
		marker.setAttribute(IMarker.LOCATION, kind.prettyPrintSignature(affectedElements));
		return marker;
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
}
