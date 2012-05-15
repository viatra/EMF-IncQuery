package org.eclipse.viatra2.emf.incquery.base.itc.alg.dred;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TcRelation<V> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// tc(a,b) means that b is transitively reachable from a
	private HashMap<V, HashSet<V>> tuplesForward;
	
	// data structure to efficiently get those nodes from which a given node is reachable
	// symmetric to tuplesForward
	private HashMap<V, HashSet<V>> tuplesBackward;
	
	public TcRelation() {
		this.tuplesForward = new HashMap<V, HashSet<V>>();
		this.tuplesBackward = new HashMap<V, HashSet<V>>();
	}
		
	public void clear() {
		this.tuplesForward.clear();
		this.tuplesBackward.clear();
	}
	
	public boolean isEmpty() {
		return tuplesForward.isEmpty();
	}
	
	public void removeTuple(V source, V target) {
		
		//removing tuple from 'forward' tc relation
		HashSet<V> sSet = tuplesForward.get(source);
		if (sSet != null) {
			sSet.remove(target);
			if (sSet.size() == 0) 
				tuplesForward.remove(source);
		}
		
		//removing tuple from 'backward' tc relation
		HashSet<V> tSet = tuplesBackward.get(target);
		if (tSet != null) {
			tSet.remove(source);
			if (tSet.size() == 0) 
				tuplesBackward.remove(target);
		}
	}
	
	/**
	 * Returns true if the tc relation did not contain previously such a tuple that is defined by (source,target),
	 * false otherwise.
	 * 
	 * @param source the source of the tuple
	 * @param target the target of the tuple
	 * @return true if the relation did not contain previously the tuple
	 */
	public boolean addTuple(V source, V target) {
		
		//symmetric modification, it is sufficient to check the return value in one collection
		//adding tuple to 'forward' tc relation
		HashSet<V> sSet = tuplesForward.get(source);
		if (sSet == null) {
			HashSet<V> newSet = new HashSet<V>();
			newSet.add(target);
			tuplesForward.put(source, newSet);
		}
		else {	
			sSet.add(target);
		}
		
		//adding tuple to 'backward' tc relation
		HashSet<V> tSet = tuplesBackward.get(target);
		if (tSet == null) {
			HashSet<V> newSet = new HashSet<V>();
			newSet.add(source);
			tuplesBackward.put(target, newSet);
			return true;
		}
		else {
			boolean ret = tSet.add(source);
			return ret;
		}
		
	}
	
	/**
	 * Union operation of two tc realtions.
	 * 
	 * @param rA the other tc relation
	 */
	public void union(TcRelation<V> rA) {	
		for (V source : rA.tuplesForward.keySet()) {
			for (V target : rA.tuplesForward.get(source)) {
				this.addTuple(source, target);
			}
		}
	}
	
	/**
	 * Computes the difference of this tc relation and the given rA parameter. 
	 * 
	 * @param rA the subtrahend relation
	 */
	public void difference(TcRelation<V> rA) {	
		for (V source : rA.tuplesForward.keySet()) {
			for (V target : rA.tuplesForward.get(source)) {
				this.removeTuple(source, target);
			}
		}
	}
	
	/**
	 * Returns the set of nodes that are reachable from the given source node.
	 * 
	 * @param source the source node
	 * @return the set of target nodes
	 */
	public Set<V> getTupleEnds(V source) {
		Set<V> t = tuplesForward.get(source);
		return (t == null) ? new HashSet<V>() : new HashSet<V>(t);
	}
	
	/**
	 * Returns the set of nodes from which the target node is reachable.
	 * 
	 * @param target the target node
	 * @return the set of source nodes
	 */
	public Set<V> getTupleStarts(V target) {
		Set<V> t = tuplesBackward.get(target);
		return (t == null) ? new HashSet<V>() : new HashSet<V>(t);
	}
	
	/**
	 * Returns the set of nodes that are present on the left side of a transitive closure tuple.
	 * 
	 * @return the set of nodes
	 */
	public Set<V> getTupleStarts() {
		Set<V> t = tuplesForward.keySet();
		return (t == null) ? new HashSet<V>() : new HashSet<V>(t);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String s = "TcRelation = ";
		
		for (V source : this.tuplesForward.keySet()) {
			for (V target : this.tuplesForward.get(source)) {
				s+="("+source+","+target+") ";
			}
		}
		return s;
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
			if (tuplesForward.get(source).contains(target)) 
				return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TcRelation) {
			TcRelation<V> aTR = (TcRelation<V>) obj;
			
			for (V source : aTR.tuplesForward.keySet()) {
				for (V target : aTR.tuplesForward.get(source)) {
					if (!this.containsTuple(source, target)) return false;
				}
			}
			
			for (V source : this.tuplesForward.keySet()) {
				for (V target : this.tuplesForward.get(source)) {
					if (!aTR.containsTuple(source, target)) return false;
				}
			}
			
			return true;
		}
		return false;
	}
	
	public void marshall(String path) {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try	{
			fos = new FileOutputStream(path);
			out = new ObjectOutputStream(fos);
			out.writeObject(this);
			out.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static TcRelation unmarshall(String path) {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		TcRelation tc = null;
		try {
			fis = new FileInputStream(path);
			in = new ObjectInputStream(fis);
			tc = (TcRelation) in.readObject();
			in.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return tc;
	}

	public HashMap<V, HashSet<V>> getTuplesForward() {
		return tuplesForward;
	}
}
