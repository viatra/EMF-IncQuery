package org.eclipse.viatra2.emf.incquery.validation.runtime;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

public abstract class Constraint<T extends IPatternMatch> {

	public abstract String getMessage();

	public abstract EObject getLocationObject(T signature);

	public String prettyPrintSignature(T signature) {
		return signature.prettyPrint();
	}

	public Object[] extractAffectedElements(T signature) {
		return signature.toArray();
	}
	
	public abstract int getSeverity();
}
