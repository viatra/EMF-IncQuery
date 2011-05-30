/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.runtime.api.impl;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature;

/**
 * Base implementation of IPatternSignature.
 * @author Bergmann Gábor
 *
 */
public abstract class BasePatternSignature implements IPatternSignature {

	public static String prettyPrintValue(Object o) {
		if (o == null) return "(null)";
		if (o instanceof EObject) {
			EStructuralFeature feature = ((EObject)o).eClass().getEStructuralFeature("name");
			if (feature != null) {
				Object name = ((EObject)o).eGet(feature);
				if (name != null) return name.toString();
			}
		}
		return o.toString();
	}
	
	// TODO performance can be improved here somewhat
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature#get(int)
	 */
	@Override
	public Object get(int position) {
		if (position >= 0 && position < parameterNames().length)
			return get(parameterNames()[position]);
		else return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature#set(int, java.lang.Object)
	 */
	@Override
	public boolean set(int position, Object newValue) {
		if (position >= 0 && position < parameterNames().length)
			return set(parameterNames()[position], newValue);
		else return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Sig<" + patternName() + ">{" + prettyPrint() + "}";
	}

}
