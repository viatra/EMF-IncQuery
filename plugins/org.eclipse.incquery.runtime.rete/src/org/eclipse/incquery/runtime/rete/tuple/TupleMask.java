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

import java.util.LinkedList;
import java.util.List;

/**
 * @author Gabor Bergmann
 * 
 *         Specifies select indices of a tuple. If viewed through this
 *         mask, the signature of the pattern will consist of its individual
 *         substitutions at the given positions, in the exact same order as they
 *         appear in indices[].
 * 
 */
public class TupleMask {
	/**
	 * indices[i] specifies the index of the substitution in the original
	 * tuple that occupies the i-th place in the masked signature.
	 */
	public final int[] indices;
	/**
	 * indicesSorted is indices, sorted in ascending order.
	 */
	public int[] indicesSorted;
	/**
	 * the size of the tuple this mask is applied to
	 */
	public int sourceWidth;

	/**
	 * Creates a TupleMask instance with the given indices array
	 */
	public TupleMask(int[] indices, int sourceWidth) {
		this.sourceWidth = sourceWidth;
		this.indices = indices;
		indicesSorted = null;
	}
	
	/**
	 * Creates a TupleMask instance of the given size that maps the first
	 * 'size' elements intact
	 */
	public static TupleMask linear(int size, int sourceWidth) {
		int[] indices = new int[size];
		for (int i=0; i<size; i++) indices[i]=i;
		return new TupleMask(indices, sourceWidth);
	}

	/**
	 * Creates a TupleMask instance of the given size that maps every single
	 * element intact
	 */
	public static TupleMask identity(int size) {
		return linear(size, size);
	}
	
	/**
	 * Creates a TupleMask instance of the given size that does not emit output.
	 */
	public static TupleMask empty(int sourceWidth) {
		return linear(0, sourceWidth);
	}
	
	/**
	 * Creates a TupleMask instance that maps the tuple intact save for a single element at the specified index which is omitted 
	 */
	public static TupleMask omit(int omission, int sourceWidth) {
		int size = sourceWidth-1;
		int[] indices = new int[size];
		for (int i=0; i<omission; i++) indices[i]=i;
		for (int i=omission; i<size; i++) indices[i]=i+1;
		return new TupleMask(indices, sourceWidth);
	}

	/**
	 * Creates a TupleMask instance that discards positions where keep is true
	 */
	public TupleMask(boolean[] keep) {
		this.sourceWidth = keep.length;
		int size = 0;
		for (int k = 0; k < keep.length; ++k)
			if (keep[k])
				size++;
		this.indices = new int[size];
		int l = 0;
		for (int k = 0; k < keep.length; ++k)
			if (keep[k])
				indices[l++] = k;
		indicesSorted = null;
	}
	
	/**
	 * Creates a TupleMask instance that moves an element from one index to other, shifting the others if neccessary.
	 */
	public static TupleMask displace(int from, int to, int sourceWidth) {
		int[] indices = new int[sourceWidth];
		for (int i=0; i<sourceWidth; i++) 
			if (i==to) indices[i]=from;
			else if (i>=from && i<to) indices[i]=i+1;
			else if (i>to && i<=from) indices[i]=i-1;
			else indices[i]=i;
		return new TupleMask(indices, sourceWidth);
	}
	/**
	 * Creates a TupleMask instance that selects a single element of the tuple.
	 */
	public static TupleMask selectSingle(int selected, int sourceWidth) {
		int[] indices = {selected};
		return new TupleMask(indices, sourceWidth);
	}
	/**
	 * Creates a TupleMask instance that selects whatever is selected by left, and appends whatever is selected by right.
	 * PRE: left and right have the same sourcewidth
	 */
	public static TupleMask append(TupleMask left, TupleMask right) {
		int leftLength = left.indices.length;
		int rightLength = right.indices.length;
		int[] indices = new int[leftLength + rightLength];
		for (int i=0; i<leftLength; ++i) indices[i] = left.indices[i];
		for (int i=0; i<rightLength; ++i) indices[i+leftLength] = right.indices[i];
		return new TupleMask(indices, left.sourceWidth);
	}

	/**
	 * Generates indicesSorted from indices
	 * 
	 */
	public void sort() {
		indicesSorted = new int[indices.length];
		List<Integer> list = new LinkedList<Integer>();
		for (int i = 0; i < indices.length; ++i)
			list.add(indices[i]);
		java.util.Collections.sort(list);
		int i = 0;
		for (Integer a : list)
			indicesSorted[i++] = a;
	}

	/**
	 * Generates a masked view of the original pattern.
	 */
	public Tuple transform(Tuple original) {
		Object signature[] = new Object[indices.length];
		for (int i = 0; i < indices.length; ++i)
			signature[i] = original.get(indices[i]);
		return new FlatTuple(signature);
	}
	
	/**
	 * Transforms a given mask directly, instead of transforming tuples that were transformed by the other mask.
	 * @return a mask that cascades the effects this mask after the mask provided as parameter.
	 */
	public TupleMask transform(TupleMask mask) {
		int[] cascadeIndices = new int[indices.length];
		for (int i = 0; i < indices.length; ++i)
			cascadeIndices[i] = mask.indices[indices[i]];
		return new TupleMask(cascadeIndices, mask.sourceWidth);
	}	

	// /**
	// * Generates a complementer mask that maps those elements that were
	// untouched by the original mask.
	// * Ordering is left intact.
	// * A Tuple is used for reference concerning possible equalities among
	// elements.
	// */
	// public TupleMask complementer(Tuple reference)
	// {
	// HashSet<Object> touched = new HashSet<Object>();
	// LinkedList<Integer> untouched = new LinkedList<Integer>();
	//		
	// for (int index : indices) touched.add(reference.get(index));
	// for (int index=0; index<reference.getSize(); ++index)
	// {
	// if (touched.add(reference.get(index))) untouched.addLast(index);
	// }
	//		
	// int[] complementer = new int[untouched.size()];
	// int k = 0;
	// for (Integer integer : untouched) complementer[k++] = integer;
	// return new TupleMask(complementer, reference.getSize());
	// }

	/**
	 * Combines two substitutions. The new pattern will contain all
	 * substitutions of masked and unmasked, assuming that the elements of
	 * masked indicated by this mask are already matched against unmasked.
	 * 
	 * POST: the result will start with an exact copy of unmasked
	 * 
	 * @param unmasked
	 *            primary pattern substitution that is left intact.
	 * @param masked
	 *            secondary pattern substitution that is transformed to the end
	 *            of the result.
	 * @param useInheritance
	 *            whether to use inheritance or copy umasked into result
	 *            instead.
	 * @param asComplementer
	 *            whether this mask maps from the masked Tuple to the tail of
	 *            the result or to the unmasked one.
	 * @return new pattern that is a combination of unmasked and masked.
	 */
	public Tuple combine(Tuple unmasked, Tuple masked, boolean useInheritance, boolean asComplementer) {

		int combinedLength = asComplementer ? indices.length : masked.getSize()
				- indices.length;
		if (!useInheritance)
			combinedLength += unmasked.getSize();
		Object combined[] = new Object[combinedLength];

		int cPos = 0;
		if (!useInheritance) {
			for (int i = 0; i < unmasked.getSize(); ++i)
				combined[cPos++] = unmasked.get(i);
		}

		if (asComplementer) {
			for (int i = 0; i < indices.length; ++i)
				combined[cPos++] = masked.get(indices[i]);
		} else {
			if (indicesSorted == null)
				sort();
			int mPos = 0;
			for (int i = 0; i < masked.getSize(); ++i)
				if (mPos < indicesSorted.length && i == indicesSorted[mPos])
					mPos++;
				else
					combined[cPos++] = masked.get(i);
		}

		return useInheritance ? new LeftInheritanceTuple(unmasked, combined)
				: new FlatTuple(combined);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = sourceWidth;
		for (int i : indices)
			result = PRIME * result + i;
		return result;
	}

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
		if (getClass() != obj.getClass())
			return false;
		final TupleMask other = (TupleMask) obj;
		if (sourceWidth != other.sourceWidth)
			return false;
		if (indices.length != other.indices.length)
			return false;
		for (int k = 0; k < indices.length; k++)
			if (indices[k] != other.indices[k])
				return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("M("+sourceWidth+"->");
		for (int i : indices) {
			s.append(i);
			s.append(',');
		}
		s.append(')');
		return s.toString();
	}







}
