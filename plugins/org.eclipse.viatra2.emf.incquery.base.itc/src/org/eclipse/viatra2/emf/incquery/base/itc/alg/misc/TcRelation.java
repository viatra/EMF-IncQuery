package org.eclipse.viatra2.emf.incquery.base.itc.alg.misc;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IBiDirectionalGraphDataSource;

public class TcRelation<V> implements ITcRelation<V> {

	private Map<V, Set<V>> tupleMap;
	
	private TcRelation() {
		this.tupleMap = new HashMap<V, Set<V>>();
	}
	
	public TcRelation(Map<V, Set<V>> tupleMap) {
		this.tupleMap = tupleMap;
	}
	
	@Override
	public Set<V> getTupleStarts() {
		return tupleMap.keySet();
	}

	@Override
	public Set<V> getTupleEnds(V start) {
		return tupleMap.get(start);
	}
	
	public static <V> ITcRelation<V> createFrom(List<V> topologicalSorting, IBiDirectionalGraphDataSource<V> gds) {
		TcRelation<V> tc = new TcRelation<V>();
		Collections.reverse(topologicalSorting);
		for (V n : topologicalSorting) {			
			List<V> sourceNodes = gds.getSourceNodes(n);		
			if (sourceNodes != null) {		
				Set<V> tupEnds = tc.getTupleEnds(n);
				for (V s : sourceNodes) {
					tc.put(s, n);
					if (tupEnds != null) {
						for (V t : tupEnds) {
							tc.put(s, t);
						}
					}
				}
			}
		}
		
		return tc;
	}
		
	private void put(V s, V n) {
		Set<V> set = tupleMap.get(s);
		if (set == null) {
			set = new HashSet<V>();
		}
		set.add(n);
		tupleMap.put(s, set);
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		
		for (Entry<V, Set<V>> entry : this.tupleMap.entrySet()) {
			hash += 31 * hash + entry.hashCode();
		}
		
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (obj == null || (!(obj instanceof ITcRelation))) {
			return false;
		}
		
		@SuppressWarnings("unchecked")
		ITcRelation<V> other = (ITcRelation<V>) obj;
		
		for (V s : other.getTupleStarts()) {
			for (V t : other.getTupleEnds(s)) {
				if (!this.tupleMap.containsKey(s) || !this.tupleMap.get(s).contains(t)) {
					return false;
				}
			}
		}
		
		return true;
	}
}
