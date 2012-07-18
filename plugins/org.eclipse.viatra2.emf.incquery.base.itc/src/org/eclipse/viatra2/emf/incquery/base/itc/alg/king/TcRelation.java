/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.base.itc.alg.king;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * 	forwardTuples means E'
 *	dE means dE
 *	original edges means E
 * 
 * @author Tamas Szabo
 *
 * @param <V>
 */
public class TcRelation<V> {

	private HashMap<V, HashMap<V, Integer>> forwardTuples = null;
	private HashMap<V, HashMap<V, Integer>> backwardTuples = null;
	private HashMap<V, HashSet<V>> dE = null;

	public TcRelation() {
		forwardTuples = new HashMap<V, HashMap<V, Integer>>();
		backwardTuples = new HashMap<V, HashMap<V, Integer>>();
		dE = new HashMap<V, HashSet<V>>();
	}

	public boolean isEmpty() {
		return this.forwardTuples.isEmpty();
	}

	public void clearAll() {
		this.forwardTuples.clear();
		this.backwardTuples.clear();
		this.dE.clear();
	}

	public void clearModEdges() {
		this.dE.clear();
	}

	public void union(TcRelation<V> rA) {
		for (V source : rA.forwardTuples.keySet()) {
			for (V target : rA.forwardTuples.get(source).keySet()) {
				this.addTuple(source, target,
						rA.forwardTuples.get(source).get(target));
			}
		}
	}

	public HashMap<V, HashMap<V, Integer>> getTuplesForward() {
		return forwardTuples;
	}

	public HashMap<V, HashSet<V>> getOriginalEdges(int dCount) {
		HashMap<V, HashSet<V>> E = new HashMap<V, HashSet<V>>();
		
		for (V s : forwardTuples.keySet()) {
			for (V t : forwardTuples.get(s).keySet()) {
				if (dCount == 1) {
					//new edges were inserted so it must not include the ones in dE
					if (!(dE.containsKey(s) && (dE.get(s).contains(t)))) {
						if (E.containsKey(s)) {
							E.get(s).add(t);
						}
						else {
							HashSet<V> set = new HashSet<V>();
							set.add(t);
							E.put(s, set);
						}
					}

				}
				else {
					if (E.containsKey(s)) {
						E.get(s).add(t);
					}
					else {
						HashSet<V> set = new HashSet<V>();
						set.add(t);
						E.put(s, set);
					}
				}
				
			}
		}
		
		if (dCount == -1) {
			//edges were deleted so it must include the ones in dE
			for (V s : dE.keySet()) {
				for (V t : dE.get(s)) {
					if (E.containsKey(s)) {
						E.get(s).add(t);
					}
					else {
						HashSet<V> set = new HashSet<V>();
						set.add(t);
						E.put(s, set);
					}
				}
			}
		}
		
		return E;
	}

	/**
	 * Adds a new tuple to the relation and maintains the set of mod edges.
	 * 
	 * @param source the source of the tuple
	 * @param target the target of the tuple
	 * @param count  the count of the tuple
	 * @return true if the relation did not contain previously the tuple
	 */
	public void addTuple(V source, V target, int count) {

		boolean addTodE = false;
		HashMap<V, Integer> sMap = null;
		HashMap<V, Integer> tMap = null;

		sMap = forwardTuples.get(source);

		if (sMap == null) {

			if (count > 0) {
				tMap = new HashMap<V, Integer>();
				tMap.put(target, count);
				forwardTuples.put(source, tMap);
			}

			// the tuple appears here first
			addTodE = true;

		} else {
			if (sMap.containsKey(target)) {
				sMap.put(target, sMap.get(target) + count);
				if (sMap.get(target) == 0) {
					sMap.remove(target);
					if (sMap.size() == 0)
						forwardTuples.remove(source);

					// will be removed
					addTodE = true;
				}
			} else {
				if (count > 0)
					sMap.put(target, count);

				// here appears first
				addTodE = true;
			}
		}

		if (addTodE) {
			if (dE.containsKey(source)) {
				dE.get(source).add(target);
			} else {
				HashSet<V> s = new HashSet<V>();
				s.add(target);
				dE.put(source, s);
			}
		}

		tMap = backwardTuples.get(target);

		if (tMap == null) {

			if (count > 0) {
				sMap = new HashMap<V, Integer>();
				sMap.put(source, count);
				backwardTuples.put(target, sMap);
			}
		} else {
			if (tMap.containsKey(source)) {

				tMap.put(source, tMap.get(source) + count);

				if (tMap.get(source) == 0) {
					tMap.remove(source);
					if (tMap.size() == 0)
						backwardTuples.remove(target);
				}
			} else {
				if (count > 0)
					tMap.put(source, count);
			}
		}
	}

	@Override
	public String toString() {
		String s = "TcRelation = ";

		for (V source : this.forwardTuples.keySet()) {
			for (V target : this.forwardTuples.get(source).keySet()) {
				s += "{(" + source + "," + target + "),"
						+ this.forwardTuples.get(source).get(target) + "} ";
			}
		}
		return s;
	}

	public Set<V> getTupleEnds(V source) {
		HashMap<V, Integer> tupEnds = forwardTuples.get(source);
		if (tupEnds == null)
			return null;
		return forwardTuples.get(source).keySet();
	}

	public Set<V> getTupleStarts(V target) {
		HashMap<V, Integer> tupStarts = backwardTuples.get(target);
		if (tupStarts == null)
			return null;
		return backwardTuples.get(target).keySet();
	}

	public Set<V> getTupleStarts() {
		HashSet<V> nodes = new HashSet<V>();
		nodes.addAll(forwardTuples.keySet());

		return nodes;
	}

	public boolean containsTuple(V source, V target) {
		if (forwardTuples.containsKey(source)) {
			if (forwardTuples.get(source).containsKey(target))
				return true;
		}
		return false;
	}

	public HashMap<V, HashSet<V>> getModEdges() {
		return dE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TcRelation) {
			TcRelation<V> aTR = (TcRelation<V>) obj;

			for (V source : aTR.forwardTuples.keySet()) {
				for (V target : aTR.forwardTuples.get(source).keySet()) {
					if (!this.containsTuple(source, target))
						return false;
				}
			}

			for (V source : this.forwardTuples.keySet()) {
				for (V target : this.forwardTuples.get(source).keySet()) {
					if (!aTR.containsTuple(source, target))
						return false;
				}
			}

			return true;
		}
		return false;
	}
}
