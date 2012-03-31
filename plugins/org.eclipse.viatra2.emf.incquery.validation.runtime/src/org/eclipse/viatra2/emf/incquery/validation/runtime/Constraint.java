package org.eclipse.viatra2.emf.incquery.validation.runtime;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

public abstract class Constraint<T extends IPatternMatch> {

	/**
	 * Will be printed to the "message" field of the problem marker.
	 * 
	 * @return a user-friendly message to be displayed in the problem marker
	 */
	public abstract String getMessage(T signature);

	/**
	 * Override! Will be printed to the location field of the problem marker.
	 * 
	 * @return an {@link EObject} which is the most important context of the
	 *         validation
	 */
	public abstract EObject getLocationObject(T signature);

	public String prettyPrintSignature(T signature) {
		return signature.prettyPrint();
	}

	public Object[] extractAffectedElements(T signature) {
		return signature.toArray();
	}
}
