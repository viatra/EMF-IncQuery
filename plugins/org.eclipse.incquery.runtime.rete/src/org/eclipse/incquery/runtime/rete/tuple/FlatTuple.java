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
 * @author Gabor Bergmann Default Tuple implementation
 */
public class FlatTuple extends Tuple {

	/**
	 * Array of substituted values. DO NOT MODIFY! Use Constructor to build a
	 * new instance instead.
	 */
	private final Object[] elements;

	/**
	 * Creates a Tuple instance, fills it with the given array. @pre: no elements
	 * are null
	 * 
	 * @param elements
	 *            array of substitution values
	 */
	public FlatTuple(Object[] elements) {
		this.elements = elements;
		calcHash();
	}

	/**
	 * Creates a Tuple instance of size one, fills it with the given object.
	 * @pre: o!=null
	 * 
	 * @param o
	 *            the single substitution
	 */
	public FlatTuple(Object o) {
		elements = new Object[1];
		elements[0] = o;
		calcHash();
	}

	/**
	 * Creates a Tuple instance of size two, fills it with the given objects.
	 * @pre: o1!=null, o2!=null
	 */
	public FlatTuple(Object o1, Object o2) {
		elements = new Object[2];
		elements[0] = o1;
		elements[1] = o2;
		calcHash();
	}

	/**
	 * Creates a Tuple instance of size three, fills it with the given objects.
	 * @pre: o1!=null, o2!=null, o3!=null
	 */
	public FlatTuple(Object o1, Object o2, Object o3) {
		elements = new Object[3];
		elements[0] = o1;
		elements[1] = o2;
		elements[2] = o3;
		calcHash();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple#get(int)
	 */
	@Override
	public Object get(int index) {
		return elements[index];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple#getSize()
	 */
	@Override
	public int getSize() {
		return elements.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple#getElements()
	 */
	@Override
	public Object[] getElements() {
		return elements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple#internalEquals(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple)
	 */
	@Override
	protected boolean internalEquals(Tuple other) {
		if (other instanceof FlatTuple) {
			return Arrays.equals(elements, ((FlatTuple) other).elements);
		} else
			return super.internalEquals(other);
	}

}
