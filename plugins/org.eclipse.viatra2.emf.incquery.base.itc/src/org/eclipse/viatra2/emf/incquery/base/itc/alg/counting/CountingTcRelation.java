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

package org.eclipse.viatra2.emf.incquery.base.itc.alg.counting;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.ITcRelation;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.topsort.TopSort;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IBiDirectionalGraphDataSource;

/**
 * Transitive closure relation implementation for the Counting algorithm. 
 * 
 * @author Tamas Szabo
 *
 * @param <V>
 */
public class CountingTcRelation<V> implements ITcRelation<V> {
	
	private Map<V, Map<V, Integer>> tuplesForward = null;
	private Map<V, Map<V, Integer>> tuplesBackward = null;
	
	protected CountingTcRelation(boolean backwardIndexing) {
		tuplesForward = new HashMap<V, Map<V,Integer>>();
		if (backwardIndexing) tuplesBackward = new HashMap<V, Map<V,Integer>>();
	}
	
	protected boolean isEmpty() {
		return this.tuplesForward.isEmpty();
	}
	
	protected void clear() {
		this.tuplesForward.clear();
		
		if (tuplesBackward != null) {
			this.tuplesBackward.clear();
		}
	}
	
	protected void union(CountingTcRelation<V> rA) {	
		for (V source : rA.tuplesForward.keySet()) {
			for (V target : rA.tuplesForward.get(source).keySet()) {
				this.addTuple(source, target, rA.tuplesForward.get(source).get(target));
			}
		}
	}
	
	public int getCount(V source, V target) {
		if (tuplesForward.containsKey(source) && tuplesForward.get(source).containsKey(target)) {
			return tuplesForward.get(source).get(target);
		}
		else {
			return 0;
		}
	}
	
	/**
	 * Returns true if the tc relation did not contain previously such a tuple that is defined by (source,target),
	 * false otherwise (in this case count is incremented with the given count parameter).
	 * 
	 * @param source the source of the tuple
	 * @param target the target of the tuple
	 * @param count the count of the tuple
	 * @return true if the relation did not contain previously the tuple
	 */
	public boolean addTuple(V source, V target, int count) {
		
		Map<V, Integer> sMap = null;
		Map<V, Integer> tMap = null;
		
		if (tuplesBackward != null) {
			tMap = tuplesBackward.get(target);
			
			if (tMap == null) {
				sMap = new HashMap<V, Integer>();
				sMap.put(source, count);
				tuplesBackward.put(target, sMap);
			}
			else {
				if (tMap.containsKey(source)) {
					tMap.put(source, tMap.get(source)+count);
					if (tMap.get(source) == 0) {
						tMap.remove(source);		
						if (tMap.size() == 0)
							tuplesBackward.remove(target);
					}	
				}
				else {
					tMap.put(source, count);
				}
			}
		}
		
		sMap = tuplesForward.get(source);
		
		if (sMap == null) {
			tMap = new HashMap<V, Integer>();
			tMap.put(target, count);
			tuplesForward.put(source, tMap);
			return true;
		}
		else {
			if (sMap.containsKey(target)) {
				sMap.put(target, sMap.get(target)+count);
				if (sMap.get(target) == 0) {
					sMap.remove(target);		
					if (sMap.size() == 0)
						tuplesForward.remove(source);

					return true;
				}	
					
				return false;
			}
			else {
				sMap.put(target, count);
				return true;
			}
		}
	}
	
	public void deleteTupleEnd(V tupleEnd) {
		this.tuplesForward.remove(tupleEnd);
		
		if (tuplesForward.keySet() != null) {
			Set<V> tmp = new HashSet<V>(tuplesForward.keySet());
			
			for (V key : tmp) {
				this.tuplesForward.get(key).remove(tupleEnd);
				if (this.tuplesForward.get(key).size() == 0)
					this.tuplesForward.remove(key);
			}
		}
		
		if (tuplesBackward != null) {
			this.tuplesBackward.remove(tupleEnd);
			
			if (tuplesBackward.keySet() != null) {
				Set<V> tmp = new HashSet<V>(tuplesBackward.keySet());
				
				for (V key : tmp) {
					this.tuplesBackward.get(key).remove(tupleEnd);
					if (this.tuplesBackward.get(key).size() == 0) {
						this.tuplesBackward.remove(key);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("TcRelation = ");
		
		for (V source : this.tuplesForward.keySet()) {
			for (V target : this.tuplesForward.get(source).keySet()) {
				sb.append("{("+source+","+target+"),"+this.tuplesForward.get(source).get(target)+"} ");
			}
		}
		return sb.toString();
	}
	
	@Override
	public Set<V> getTupleEnds(V source) {
		Map<V, Integer> tupEnds = tuplesForward.get(source);
		if (tupEnds == null) return null;
		return new HashSet<V>(tuplesForward.get(source).keySet());
	}
	
	/**
	 * Returns the set of nodes from which the target node is reachable.
	 * 
	 * @param target the target node
	 * @return the set of source nodes
	 */
	public Set<V> getTupleStarts(V target) {
		if (tuplesBackward != null) {
			Map<V, Integer> tupStarts = tuplesBackward.get(target);
			if (tupStarts == null) return null;
			return tuplesBackward.get(target).keySet();
		}
		else {
			return null;
		}
	}
	
	@Override
	public Set<V> getTupleStarts() {
		Set<V> nodes = new HashSet<V>();
		nodes.addAll(tuplesForward.keySet());
		return nodes;
	}
	
	/**
	 * Returns true if a (source, target) node is present in the transitive closure relation, false otherwise. 
	 * 
	 * @param source the source node
	 * @param target the target node
	 * @return true if tuple is present, false otherwise
	 */
	public boolean containsTuple(V source, V target) {
		if (tuplesForward.containsKey(source)) {
			if (tuplesForward.get(source).containsKey(target)) 
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		else {
			CountingTcRelation<V> aTR = (CountingTcRelation<V>) obj;
			
			for (V source : aTR.tuplesForward.keySet()) {
				for (V target : aTR.tuplesForward.get(source).keySet()) {
					if (!this.containsTuple(source, target)) {
						return false;
					}
				}
			}	
			
			for (V source : this.tuplesForward.keySet()) {
				for (V target : this.tuplesForward.get(source).keySet()) {
					if (!aTR.containsTuple(source, target)) {
						return false;
					}
				}
			}	
			
			return true;
		}
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		
		for (Entry<V, Map<V, Integer>> entry : this.tuplesForward.entrySet()) {
			hash += 31 * hash + entry.hashCode();
		}
		
		for (Entry<V, Map<V, Integer>> entry : this.tuplesBackward.entrySet()) {
			hash += 31 * hash + entry.hashCode();
		}
		
		return hash;
	}
	
	@SuppressWarnings("unchecked")
	public static <V> CountingTcRelation<V> createFrom(IBiDirectionalGraphDataSource<V> gds) {
		List<V> topologicalSorting = (List<V>) TopSort.getTopologicalSorting(gds);
		CountingTcRelation<V> tc = new CountingTcRelation<V>(true);
		Collections.reverse(topologicalSorting);
		for (V n : topologicalSorting) {			
			List<V> sourceNodes = gds.getSourceNodes(n);		
			if (sourceNodes != null) {		
				Set<V> tupEnds = tc.getTupleEnds(n);
				for (V s : sourceNodes) {
					tc.addTuple(s, n, 1);
					if (tupEnds != null) {
						for (V t : tupEnds) {
							tc.addTuple(s, t, 1);
						}
					}
				}
			}
		}
		
		return tc;
	}
}
