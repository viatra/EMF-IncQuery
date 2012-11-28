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

import java.util.Arrays;

/**
 * 
 * Tuple that inherits another tuple on the left.
 * 
 * @author Gabor Bergmann
 * 
 */
public class LeftInheritanceTuple extends Tuple {
	/**
	 * The number of elements that aren't stored locally, but inherited from an
	 * ancestor Tuple instead.
	 */
	private final int inheritedIndex;

	/**
	 * This object contains the same elements as the ancestor on the first
	 * inheritedIndex positions
	 */
	private final Tuple ancestor;

	/**
	 * Array of substituted values above inheritedIndex. DO NOT MODIFY! Use
	 * Constructor to build a new instance instead.
	 */
	private final Object[] localElements;

	//
	// /**
	// * Creates a Tuple instance, fills it with the given array.
	// * @pre: no elements are null
	// * @param elements array of substitution values
	// */
	// public Tuple(Object[] elements)
	// {
	// this.localElements = elements;
	// this.ancestor=null;
	// this.inheritedIndex = 0;
	// calcHash();
	// }

	/**
	 * Creates a Tuple instance, lets it inherit from an ancestor, extends it
	 * with a given array. @pre: no elements are null
	 * 
	 * @param elements
	 *            array of substitution values
	 */
	public LeftInheritanceTuple(Tuple ancestor, Object[] localElements) {
		this.localElements = localElements;
		this.ancestor = ancestor;
		this.inheritedIndex = ancestor.getSize();
		calcHash();
	}

	//	
	// /**
	// * Creates a Tuple instance of size one, fills it with the given object.
	// * @pre: o!=null
	// * @param o the single substitution
	// */
	// public Tuple(Object o)
	// {
	// localElements = new Object [1];
	// localElements[0] = o;
	// this.ancestor=null;
	// this.inheritedIndex = 0;
	// calcHash();
	// }
	//	
	// /**
	// * Creates a Tuple instance of size two, fills it with the given objects.
	// * @pre: o1!=null, o2!=null
	// */
	// public Tuple(Object o1, Object o2)
	// {
	// localElements = new Object [2];
	// localElements[0] = o1;
	// localElements[1] = o2;
	// this.ancestor=null;
	// this.inheritedIndex = 0;
	// calcHash();
	// }
	//	
	// /**
	// * Creates a Tuple instance of size three, fills it with the given
	// objects.
	// * @pre: o1!=null, o2!=null, o3!=null
	// */
	// public Tuple(Object o1, Object o2, Object o3)
	// {
	// localElements = new Object [3];
	// localElements[0] = o1;
	// localElements[1] = o2;
	// localElements[2] = o3;
	// this.ancestor=null;
	// this.inheritedIndex = 0;
	// calcHash();
	// }

	/**
	 * @return number of elements
	 */
	public int getSize() {
		return inheritedIndex + localElements.length;
	}

	/**
	 * @pre: 0 <= index < getSize()
	 * 
	 * @return the element at the specified index
	 */
	public Object get(int index) {
		return (index < inheritedIndex) ? ancestor.get(index)
				: localElements[index - inheritedIndex];
	}

	/**
	 * Optimized hash calculation
	 */
	@Override
	void calcHash() {
		final int PRIME = 31;
		cachedHash = ancestor.hashCode();
		for (int i = 0; i < localElements.length; i++) {
			cachedHash = PRIME * cachedHash;
			Object element = localElements[i];
			if (element != null)
				cachedHash += element.hashCode();
		}
	}

	/**
	 * Optimized equals calculation (prediction: true, since hash values match)
	 */
	@Override
	protected boolean internalEquals(Tuple other) {
		if (other instanceof LeftInheritanceTuple) {
			LeftInheritanceTuple lit = (LeftInheritanceTuple) other;
			if (lit.inheritedIndex == this.inheritedIndex
					&& this.ancestor.equals(lit.ancestor))
				return Arrays.equals(this.localElements, lit.localElements);
		}
		return super.internalEquals(other);
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

}
