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

package org.eclipse.incquery.ui.wizards.internal;

import org.eclipse.emf.ecore.EClassifier;

/**
 * Instances of this class represents the specification of a pattern parameter.
 * It has a parameter name and type specification as an {@link EClassifier} instance.
 * 
 * @author Tamas Szabo
 *
 */
public class ObjectParameter {

	private EClassifier object;
	private String parameterName;

	public ObjectParameter() {
		super();
		this.object = null;
		this.parameterName = "";
	}
	
	public ObjectParameter(EClassifier object, String parameterName) {
		super();
		this.object = object;
		this.parameterName = parameterName;
	}

	public EClassifier getObject() {
		return object;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setObject(EClassifier object) {
		this.object = object;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

}
