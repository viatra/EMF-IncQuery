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

package org.eclipse.viatra2.emf.incquery.base.itc.alg.counting2;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

public class TcRelation<V> {
	
	private HashMap<V, HashMap<V, BigInteger>> tuplesForward = null;
	private HashMap<V, HashMap<V, BigInteger>> tuplesBackward = null;
	
	public TcRelation() {
		tuplesForward = new HashMap<V, HashMap<V, BigInteger>>();
		tuplesBackward = new HashMap<V, HashMap<V,BigInteger>>();
	}
	
	public void normalize() {
			
		HashSet<V> sources = new HashSet<V>(); 
		sources.addAll(tuplesForward.keySet());
		HashSet<V> targets = new HashSet<V>();
		BigInteger zeroBI = BigInteger.valueOf(0);
		
		//To avoid ConcurrentModificationException
		for (V s : sources) {
			
			targets.clear();
			targets.addAll(tuplesForward.get(s).keySet());
			
			for (V t : targets) {
				if (tuplesForward.get(s).get(t).compareTo(zeroBI) <= 0) {
					tuplesForward.get(s).remove(t);
				}
				if (tuplesForward.get(s).size() == 0) {
					tuplesForward.remove(s);
				}
			}
		}
		
		sources.clear();
		targets.clear();
		targets.addAll(tuplesBackward.keySet());
		
		for (V t : targets) {
			sources.clear();
			sources.addAll(tuplesBackward.get(t).keySet());
			
			for (V s : sources) {
				if (tuplesBackward.get(t).get(s).compareTo(zeroBI)  <= 0) {
					tuplesBackward.get(t).remove(s);
				}
				if (tuplesBackward.get(t).size() == 0) {
					tuplesBackward.remove(t);
				}
			}
		}
	}
	
	public boolean isEmpty() {
		return this.tuplesForward.isEmpty();
	}
	
	public void clear() {
		this.tuplesForward.clear();
		this.tuplesBackward.clear();
	}
	
	public void union(TcRelation<V> rA) {	
		/*for (V source : rA.tuplesForward.keySet()) {
			for (V target : rA.tuplesForward.get(source).keySet()) {
				this.addTuple(source, target, rA.tuplesForward.get(source).get(target));
			}
		}*/
	}
	
	public BigInteger getCount(V source, V target) {
		
		if (tuplesForward.containsKey(source)) {
			if (tuplesForward.get(source).containsKey(target)) {
				return tuplesForward.get(source).get(target);
			}
		}
		return BigInteger.valueOf(1);
	}
	
	public void addTuple(V source, V target, BigInteger count) {
		
		HashMap<V, BigInteger> sMap = null;
		HashMap<V, BigInteger> tMap = null;
		
		sMap = tuplesForward.get(source);
		
		if (sMap == null) {
			tMap = new HashMap<V, BigInteger>();
			tMap.put(target, count);
			tuplesForward.put(source, tMap);
		}
		else {
			if (sMap.containsKey(target)) {
				sMap.put(target, sMap.get(target).add(count));
			}
			else {
				sMap.put(target, count);
			}
		}
		
		tMap = tuplesBackward.get(target);
		
		if (tMap == null) {
			sMap = new HashMap<V, BigInteger>();
			sMap.put(source, count);
			tuplesBackward.put(target, sMap);
		}
		else {
			if (tMap.containsKey(source)) {
				tMap.put(source, tMap.get(source).add(count));
			}
			else {
				tMap.put(source, count);
			}
		}
	}

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
	
	public Set<V> getTupleEnds(V source) {
		HashMap<V, BigInteger> tupEnds = tuplesForward.get(source);	
		if (tupEnds == null) return null;
		return tupEnds.keySet();
	}
	
	public Set<V> getTupleStarts(V target) {
		HashMap<V, BigInteger> tupStarts = tuplesBackward.get(target);
		if (tupStarts == null) return null;
		return tupStarts.keySet();
	}
	
	public boolean containsTuple(V source, V target) {
		if (tuplesForward.containsKey(source)) {
			if (tuplesForward.get(source).containsKey(target)) 
				return true;
		}
		return false;
	}

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
			TcRelation<V> aTR = (TcRelation<V>) obj;
			
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
		
		for (Entry<V, HashMap<V, BigInteger>> entry : this.tuplesForward.entrySet()) {
			hash += 31 * hash + entry.hashCode();
		}
		
		for (Entry<V, HashMap<V, BigInteger>> entry : this.tuplesBackward.entrySet()) {
			hash += 31 * hash + entry.hashCode();
		}
		
		return hash;
	}
}
