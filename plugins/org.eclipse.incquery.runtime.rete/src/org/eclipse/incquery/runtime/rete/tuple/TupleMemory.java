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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Multiset for tuples. Can contain duplicate occurrences of the same matching.
 * 
 * @author Gabor Bergmann.
 * 
 */
public class TupleMemory implements Clearable, Collection<Tuple> {
	/**
	 * Counts the number of occurences of each pattern. Element is deleted if #
	 * of occurences drops to 0.
	 */
	protected Map<Tuple, Integer> occurences;

	/**
	 * 
	 */
	public TupleMemory() {
		super();
		occurences = new HashMap<Tuple, Integer>();
	}

	/**
	 * Adds a pattern occurence to the memory
	 * 
	 * @return true if a new pattern is entered
	 */
	@Override
    public boolean add(Tuple ps) {
		boolean exists = occurences.containsKey(ps);

		if (exists)
			occurences.put(ps, occurences.get(ps) + 1);
		else
			occurences.put(ps, 1);

		return !exists;
	}

	/**
	 * Removes a pattern occurence from the memory
	 * 
	 * @return true if this was the the last occurence of pattern
	 */
	public boolean remove(Tuple ps) {
		int rest = occurences.get(ps) - 1;
		boolean empty = rest == 0;

		if (!empty)
			occurences.put(ps, rest);
		else
			occurences.remove(ps);

		return empty;
	}

	@Override
    public void clear() {
		occurences.clear();

	}

	@Override
    public Iterator<Tuple> iterator() {
		return occurences.keySet().iterator();
	}

	@Override
    public boolean addAll(Collection<? extends Tuple> arg0) {
		boolean change = false;
		for (Tuple ps : arg0)
			change |= add(ps);
		return change;
	}

	@Override
    public boolean contains(Object arg0) {
		return occurences.containsKey(arg0);
	}

	@Override
    public boolean containsAll(Collection<?> arg0) {
		return occurences.keySet().containsAll(arg0);
//		for (Object o : arg0)
//			if (!occurences.containsKey(o))
//				return false;
//		return true;
	}

	@Override
    public boolean isEmpty() {
		return occurences.isEmpty();
	}

	@Override
    public boolean remove(Object arg0) {
		return remove((Tuple) arg0);
	}

	@Override
    public boolean removeAll(Collection<?> arg0) {
		boolean change = false;
		for (Object o : arg0)
			change |= remove(o);
		return change;
	}

	@Override
    public boolean retainAll(Collection<?> arg0) {
		return occurences.keySet().retainAll(arg0);
//		HashSet<Tuple> obsolete = new HashSet<Tuple>();
//		for (Tuple key : occurences.keySet())
//			if (!arg0.contains(key))
//				obsolete.add(key);
//		for (Tuple key : obsolete)
//			occurences.remove(key);
//		return !obsolete.isEmpty();
	}

	@Override
    public int size() {
//		int sum = 0;
//		for (Integer count : occurences.values())
//			sum += count;
//		return sum;
		return occurences.size();
	}

	@Override
    public Object[] toArray() {
		return toArray(new Object[0]);
	}

//	@SuppressWarnings("unchecked")
	@Override
    public <T> T[] toArray(T[] arg0) {
		return occurences.keySet().toArray(arg0);
//		int length = size();
//		T[] result = (T[]) java.lang.reflect.Array.newInstance(arg0.getClass()
//				.getComponentType(), length);
//		int next = 0;
//		for (Tuple key : occurences.keySet()) {
//			for (int counter = occurences.get(key); counter > 0; --counter)
//				result[next++] = (T) key;
//		}
//		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TM"+occurences.keySet();
	}

}
