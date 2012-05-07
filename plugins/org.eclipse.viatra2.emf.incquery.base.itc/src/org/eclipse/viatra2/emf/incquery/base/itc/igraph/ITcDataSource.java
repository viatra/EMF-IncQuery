package org.eclipse.viatra2.emf.incquery.base.itc.igraph;

import java.util.Set;

/**
 * Defines those methods that are necessary to observ a tc relation provider. 
 * 
 * @author Tamás Szabó
 *
 * @param <V> the type parameter of the node
 */
public interface ITcDataSource<V> {
	
	/**
	 * Attach a tc relation observer.
	 * 
	 * @param to the observer object
	 */
	public void attachObserver(ITcObserver<V> to);
	
	/**
	 * Detach a tc relation observer.
	 * 
	 * @param to the observer object
	 */
	public void detachObserver(ITcObserver<V> to);
	
	/**
	 * Returns all nodes which are reachable from the source node.
	 * 
	 * @param source the source node
	 * @return the set of target nodes
	 */
	public Set<V> getAllReachableTargets(V source);
	
	/**
	 * Returns all nodes from which the target node is reachable.
	 * 
	 * @param target the target node
	 * @return the set of source nodes
	 */
	public Set<V> getAllReachableSources(V target);
	
	/**
	 * Returns true if the target node is reachable from the source node.
	 * 
	 * @param source the source node
	 * @param target the target node
	 * @return true if target is reachable from source, false otherwise
	 */
	public boolean isReachable(V source, V target);
	
	/**
	 * Call this method to properly dispose the data strucutres of a transitive closure algorithm.
	 */
	public void dispose();
}
