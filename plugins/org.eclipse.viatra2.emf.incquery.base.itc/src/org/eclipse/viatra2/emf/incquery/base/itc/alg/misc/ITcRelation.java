package org.eclipse.viatra2.emf.incquery.base.itc.alg.misc;

import java.util.Set;

public interface ITcRelation<V> {

	/**
	 * Returns the starting nodes from a transitive closure relation.
	 * 
	 * @return the set of starting nodes
	 */
	public Set<V> getTupleStarts();
	
	/**
	 * Returns the set of nodes that are reachable from the given node.
	 * 
	 * @param start the starting node
	 * @return the set of reachable nodes
	 */
	public Set<V> getTupleEnds(V start);
}
