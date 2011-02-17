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

package org.eclipse.viatra2.emf.incquery.runtime.api;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternSignature;

/**
 * Generic signature object implementation. 
 * Please instantiate using GenericPatternMatcher.arrayToSignature().
 *  
 * See also the generated matcher and signature of the pattern, with pattern-specific API simplifications.
 *  
 * @author Bergmann Gábor
 *
 */
@SuppressWarnings("unused")
public class GenericPatternSignature extends BasePatternSignature implements IPatternSignature {

	private String patternName;
	private String[] parameterNames;
	private Map<String, Integer> posMapping;
	private Object[] array;

	/**
	 * @param posMapping
	 * @param array
	 */
	GenericPatternSignature(String patternName, String[] parameterNames, Map<String, Integer> posMapping, Object[] array) {
		super();
		this.patternName = patternName;
		this.parameterNames = parameterNames;
		this.posMapping = posMapping;
		this.array = array;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature#get(java.lang.String)
	 */
	@Override
	public Object get(String parameterName) {
		Integer index = posMapping.get(parameterName);
		return index == null? null : array[index];
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature#set(java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean set(String parameterName, Object newValue) {
		Integer index = posMapping.get(parameterName);
		if (index == null) return false;
		array[index] = newValue;
		return true;
	}
	
	@Override
	public Object[] toArray() {
		return array;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (int i=0; i<array.length; ++i)
			result = prime * result + ((array[i]==null) ? 0 : array[i].hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IPatternSignature))
			return false;
		IPatternSignature other = (IPatternSignature) obj;
		if (!patternName.equals(other.patternName()))
			return false;
		if (!Arrays.deepEquals(array, other.toArray()))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Sig<" + patternName + ">{");
		for (int i=0; i<array.length; ++i) {
			if (i!=0) result.append(", ");
			result.append("\"" + parameterNames[i] + "\"=" + printValue(array[i]));
		}
		result.append("}");
		return result.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature#patternName()
	 */
	@Override
	public String patternName() {
		return patternName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature#parameterNames()
	 */
	@Override
	public String[] parameterNames() {
		return parameterNames;
	}
	
}
