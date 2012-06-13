/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

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
