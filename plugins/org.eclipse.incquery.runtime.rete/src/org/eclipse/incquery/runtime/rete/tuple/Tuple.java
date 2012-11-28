/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Gabor Bergmann
 * 
 */
public abstract class Tuple {

	/**
	 * Caches precalculated hash value
	 */
	protected int cachedHash;

	/**
	 * Creates a Tuple instance Derivatives should call calcHash()
	 */
	protected Tuple() {
		// calcHash();
	}

	/**
	 * @return number of elements
	 */
	public abstract int getSize();

	/**
	 * @pre: 0 <= index < getSize()
	 * 
	 * @return the element at the specified index
	 */
	public abstract Object get(int index);

	/**
	 * @return the array containing all elements of this Tuple
	 */
	public Object[] getElements() {
		Object[] allElements = new Object[getSize()];
		for (int i = 0; i < allElements.length; ++i)
			allElements[i] = get(i);
		return allElements;
	}
	
	/**
	 * @return the set containing all distinct elements of this Tuple, cast as type T
	 */
	@SuppressWarnings("unchecked")
	public <T> Set<T> getDistinctElements() {
		Set<T> result = new HashSet<T>();
		Object[] elements = getElements();
		for (Object object : elements) {
			result.add((T) object);
		}
		return result;
	}
	/**
	 * Hash calculation. Overrides should keep semantics.
	 */
	void calcHash() {
		final int PRIME = 31;
		cachedHash = 1;
		for (int i = 0; i < getSize(); i++) {
			cachedHash = PRIME * cachedHash;
			Object element = get(i);
			if (element != null)
				cachedHash += element.hashCode();
		}
	}

	/**
	 * Calculates an inverted index of the elements of this pattern. For each
	 * element, the index of the (last) occurrence is calculated.
	 * 
	 * @return the inverted index mapping each element of this pattern to its
	 *         index in the array
	 */
	public Map<Object, Integer> invertIndex() {
		Map<Object, Integer> result = new HashMap<Object, Integer>();
		for (int i = 0; i < getSize(); i++)
			result.put(get(i), i);
		return result;
	}
	
	/**
	 * Calculates an inverted index of the elements of this pattern. For each
	 * element, the index of all of its occurrences is calculated.
	 * 
	 * @return the inverted index mapping each element of this pattern to its
	 *         index in the array
	 */
	public Map<Object, List<Integer>> invertIndexWithMupliplicity() {
		Map<Object, List<Integer>> result = new HashMap<Object, List<Integer>>();
		for (int i = 0; i < getSize(); i++) {
			Object value = get(i);
			List<Integer> indices = result.get(value);
			if (indices == null) {
				indices = new ArrayList<Integer>();
				result.put(value, indices);
			}
			indices.add(i);
		}
		return result;
	}
	
	// public int compareTo(Object arg0) {
	// Tuple other = (Tuple) arg0;
	//		
	// int retVal = cachedHash - other.cachedHash;
	// if (retVal==0) retVal = elements.length - other.elements.length;
	// for (int i=0; retVal==0 && i<elements.length; ++i)
	// {
	// if (elements[i] == null && other.elements[i] != null) retVal = -1;
	// else if (other.elements[i] == null) retVal = 1;
	// else retVal = elements[i].compareTo(other.elements[i]);
	// }
	// return retVal;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Tuple))
			return false;
		final Tuple other = (Tuple) obj;
		if (cachedHash != other.cachedHash)
			return false;
		return internalEquals(other);
	}

	protected boolean internalEquals(Tuple other) {
		if (getSize() != other.getSize())
			return false;
		for (int i = 0; i < getSize(); ++i) {
			Object ours = get(i);
			Object theirs = other.get(i);

			if (ours == null) {
				if (theirs != null)
					return false;
			} else {
				if (!ours.equals(theirs))
					return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		/*
		 * final int PRIME = 31; int result = 1; result = PRIME result +
		 * Arrays.hashCode(elements); return result;
		 */
		return cachedHash;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("T(");
		for (Object o : getElements()) {
			s.append(o == null ? "null" : o.toString());
			s.append(';');
		}
		s.append(')');
		return s.toString();
	}

	/**
	 * @param obsolete
	 * @param replacement
	 * @return
	 */
	public Tuple replaceAll(Object obsolete, Object replacement) {
		Object[] oldElements = getElements();
		Object[] newElements = new Object[oldElements.length];
		for (int i=0; i<oldElements.length; ++i) {
			newElements[i] = obsolete.equals(oldElements[i]) ?
					replacement :
					oldElements[i];
		}
		return new FlatTuple(newElements);
	}

}
