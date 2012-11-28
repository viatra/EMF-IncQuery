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

package org.eclipse.incquery.runtime.api.impl;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.api.IPatternMatch;

/**
 * Base implementation of IPatternMatch.
 * @author Bergmann Gábor
 *
 */
public abstract class BasePatternMatch implements IPatternMatch {

	public static String prettyPrintValue(Object o) {
		if (o == null) return "(null)";
		String name = prettyPrintFeature(o, "name");
		if(name != null){
			return name;
		}
		/*if (o instanceof EObject) {
			EStructuralFeature feature = ((EObject)o).eClass().getEStructuralFeature("name");
			if (feature != null) {
				Object name = ((EObject)o).eGet(feature);
				if (name != null) return name.toString();
			}
		}*/
		return o.toString();
	}
	
	public static String prettyPrintFeature(Object o, String featureName) {
		if (o != null && o instanceof EObject) {
			EStructuralFeature feature = ((EObject)o).eClass().getEStructuralFeature(featureName);
			if (feature != null) {
				Object value = ((EObject)o).eGet(feature);
				if (value != null) return value.toString();
			}
		}
		return null;
	}
	
	// TODO performance can be improved here somewhat
	
	@Override
	public Object get(int position) {
		if (position >= 0 && position < parameterNames().length)
			return get(parameterNames()[position]);
		else return null;
	}

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
		return "Match<" + patternName() + ">{" + prettyPrint() + "}";
	}

}
