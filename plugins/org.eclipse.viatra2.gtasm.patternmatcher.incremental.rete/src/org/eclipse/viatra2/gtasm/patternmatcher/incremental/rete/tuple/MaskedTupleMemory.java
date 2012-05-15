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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Gabor Bergmann
 *
 *         Indexes a collection of Tuples according to their masks.
 */
public class MaskedTupleMemory implements Clearable, Iterable<Tuple> {
	/**
	 * Counts the number of occurences of each pattern. Element is deleted if #
	 * of occurences drops to 0.
	 */
	protected Map<Tuple, Collection<Tuple>> matchings;

	/**
	 * The mask used to index the matchings
	 */
	protected TupleMask mask;

	/**
	 * @param mask
	 *            The mask used to index the matchings
	 */
	public MaskedTupleMemory(TupleMask mask) {
		super();
		this.mask = mask;
		matchings = new HashMap<Tuple, Collection<Tuple>>();
	}

	/**
	 * Adds a pattern occurence to the memory
	 * @param ps
	 *
	 * @return true if new signature encountered
	 */
	public boolean add(Tuple ps) {
		Tuple signature = mask.transform(ps);
		return add(ps, signature);
	}

	/**
	 * Adds a pattern occurence to the memory, with given signature
	 * @param ps
	 * @param signature
	 *
	 * @return true if new signature encountered
	 */
	public boolean add(Tuple ps, Tuple signature) {
		Collection<Tuple> coll = matchings.get(signature);
		boolean change = (coll == null);

		if (change) {
			coll = new TupleMemory();
			matchings.put(signature, coll);
		}
		if (!coll.add(ps)) {
			throw new IllegalStateException();
		}

		return change;
	}

	/**
	 * Removes a pattern occurence from the memory
	 *
	 * @return true if this was the the last occurence of the signature
	 */
	public boolean remove(Tuple ps) {
		Tuple signature = mask.transform(ps);
		return remove(ps, signature);
	}

	/**
	 * Removes a pattern occurence from the memory, with given signature
	 *
	 * @return true if this was the the last occurence of the signature
	 */
	public boolean remove(Tuple ps, Tuple signature) {
		Collection<Tuple> coll = matchings.get(signature);
		if (!coll.remove(ps)) {
			throw new IllegalStateException();
		}

		boolean change = coll.isEmpty();
		if (change)
			matchings.remove(signature);

		return change;
	}

	/**
	 * Retrieves entries that have the specified signature
	 *
	 * @return collection of matchings found
	 */
	public Collection<Tuple> get(Tuple signature) {
		return matchings.get(signature);
	}

	public void clear() {
		matchings.clear();
	}
	/**
	 * Retrieves a read-only collection of exactly those signatures for which at least one tuple is stored
	 *
	 * @return collection of significant signatures
	 */
	public Collection<Tuple> getSignatures() {
		return matchings.keySet();
	}
	public Iterator<Tuple> iterator() {
		return new MaskedPatternIterator(this);
	}

	class MaskedPatternIterator implements Iterator<Tuple> {
		// private MaskedTupleMemory memory;
		Iterator<Collection<Tuple>> signatureGroup;
		Iterator<Tuple> element;

		public MaskedPatternIterator(MaskedTupleMemory memory) {
			// this.memory = memory;
			signatureGroup = memory.matchings.values().iterator();
			Set<Tuple> emptySet = Collections.emptySet();
			element = emptySet.iterator();
		}

		public boolean hasNext() {
			return (element.hasNext() || signatureGroup.hasNext());
		}

		public Tuple next() throws NoSuchElementException {
			if (element.hasNext())
				return element.next();
			else if (signatureGroup.hasNext()) {
				element = signatureGroup.next().iterator();
				return element.next();
			} else
				throw new NoSuchElementException();
		}

		/**
		 * Not implemented
		 */
		public void remove() {

		}

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MTM<"+mask+"|"+matchings+">";
	}



}
