package org.eclipse.viatra2.emf.incquery.base.itc.igraph;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * This interface is used to provide information about the graph to the observers.
 * 
 * @author Tamas Szabo
 *
 * @param <V> the type of the nodes in the graph
 */
public interface IGraphDataSource<V> extends Serializable {
	
	/**
	 * Attach a new graph observer.
	 * 
	 * Note that the order of attaching the observers may be important (some observers will be notified earlier than others).
	 * 
	 * @param go the graph observer
	 */
	public void attachObserver(IGraphObserver<V> go);
	
	/**
	 * Detach an existing observer.
	 * 
	 * @param go the graph observer
	 */
	public void detachObserver(IGraphObserver<V> go);
	
	/**
	 * Get all nodes of the graph.
	 * 
	 * @return the set of all nodes
	 */
	public Set<V> getAllNodes();
	
	/**
	 * Get those nodes that are the target of an edge starting with source.
	 * The list is necessary because there can be more edges between two nodes.
	 * If no such edge can be found than the method returns null.
	 * 
	 * @param source the source node
	 * @return the list of target nodes or null if no targets can be found
	 */
	public List<V> getTargetNodes(V source);
}
