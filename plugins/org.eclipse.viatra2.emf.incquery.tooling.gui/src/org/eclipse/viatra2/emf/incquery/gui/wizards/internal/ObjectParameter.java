package org.eclipse.viatra2.emf.incquery.gui.wizards.internal;

import org.eclipse.emf.ecore.EObject;

public class ObjectParameter {

	private EObject object;
	private String parameterName;

	public ObjectParameter() {
		super();
		this.object = null;
		this.parameterName = "";
	}
	
	public ObjectParameter(EObject object, String parameterName) {
		super();
		this.object = object;
		this.parameterName = parameterName;
	}

	public EObject getObject() {
		return object;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setObject(EObject object) {
		this.object = object;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

}
