package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import org.eclipse.emf.ecore.EClassifier;

public class TypeReason<ReasonType> {
	ReasonType reason;
	EClassifier type;
	
	public TypeReason(ReasonType object, EClassifier type) {
		super();
		this.reason = object;
		this.type = type;
	}

	public ReasonType getReason() {
		return reason;
	}

	public EClassifier getType() {
		return type;
	}
}
