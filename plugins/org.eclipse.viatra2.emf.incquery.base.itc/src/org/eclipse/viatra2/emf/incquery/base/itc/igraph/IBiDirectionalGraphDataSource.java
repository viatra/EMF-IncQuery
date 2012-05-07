package org.eclipse.viatra2.emf.incquery.base.itc.igraph;

import java.util.List;

/**
 * This interface extends the functionality of IGraphDataSource with an extra method.
 * One can query the source nodes of those edges which have a given target node.
 * 
 * @author Tamas Szabo
 *
 * @param <V> the type of the nodes in the graph
 */
public interface IBiDirectionalGraphDataSource<V> extends IGraphDataSource<V>{
	
	/**
	 * Get those nodes that are the source of an edge ending with target.
	 * The list is necessary because there can be more edges between two nodes.
	 * If no such edge can be found than the method should return null.
	 * 
	 * @param target the target node
	 * @return the list of source nodes or null if no sources can be found
	 */
	public List<V> getSourceNodes(V target);
}
