package org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.bfs;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IBiDirectionalGraphDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphDataSource;

public class BFS<V> {

	/**
	 * Performs a breadth first search on the given graph to determine whether source is reachable from target.
	 * 
	 * @param <V> the type parameter of the nodes in the graph
	 * @param source the source node
	 * @param target the target node
	 * @param graph the graph data source
	 * @return true if source is reachable from target, false otherwise
	 */
	public static <V> boolean isReachable(V source, V target,
			IGraphDataSource<V> graph) {
		ArrayList<V> nodeQueue = new ArrayList<V>();
		HashMap<V, Boolean> visited = new HashMap<V, Boolean>();

		nodeQueue.add(source);
		visited.put(source, true);

		boolean ret =  _isReachable(target, graph, nodeQueue, visited);
		return ret;
	}

	private static <V> boolean _isReachable(V target, IGraphDataSource<V> graph, ArrayList<V> nodeQueue, HashMap<V, Boolean> visited) {

		while (!nodeQueue.isEmpty()) {
			V node = nodeQueue.remove(0);
			List<V> targets = graph.getTargetNodes(node);
			if (targets != null) {
				for (V _node : targets) {
					
					if (_node.equals(target)) return true;
					
					if (visited.get(_node) == null) {
						visited.put(_node, true);
						nodeQueue.add(_node);
					}
				}
			}
		}

		return false;
	}
	
	public static<V> Set<V> reachableSources(IBiDirectionalGraphDataSource<V> graph, V target) {
		HashSet<V> retSet = new HashSet<V>();
		retSet.add(target);
		ArrayList<V> nodeQueue = new ArrayList<V>();
		nodeQueue.add(target);
		
		_reachableSources(graph, nodeQueue, retSet);
		
		return retSet;
	}
	
	private static <V> void _reachableSources(IBiDirectionalGraphDataSource<V> graph, ArrayList<V> nodeQueue, HashSet<V> retSet) {
		while (!nodeQueue.isEmpty()) {
			V node = nodeQueue.remove(0);
			List<V> sourceNodes = graph.getSourceNodes(node);
			
			if (sourceNodes != null) {
				for (V _node : graph.getSourceNodes(node)) {
					
					if (!retSet.contains(_node)) {
						retSet.add(_node);
						nodeQueue.add(_node);
					}
				}
			}
		}
	}
	
	public static<V> Set<V> reachableTargets(IGraphDataSource<V> graph, V source) {
		HashSet<V> retSet = new HashSet<V>();
		retSet.add(source);
		ArrayList<V> nodeQueue = new ArrayList<V>();
		nodeQueue.add(source);
		
		_reachableTargets(graph, nodeQueue, retSet);
		
		return retSet;
	}
	
	private static <V> void _reachableTargets(IGraphDataSource<V> graph, ArrayList<V> nodeQueue, HashSet<V> retSet) {
		while (!nodeQueue.isEmpty()) {
			V node = nodeQueue.remove(0);

			for (V _node : graph.getTargetNodes(node)) {
				
				if (!retSet.contains(_node)) {
					retSet.add(_node);
					nodeQueue.add(_node);
				}
			}
		}
	}
	
	/**
	 * Performs a breadth first search on the given graph and collects all the nodes along the path from source to target if such path exists. 
	 * 
	 * @param <V> the type parameter of the nodes in the graph
	 * @param source the source node
	 * @param target the target node
	 * @param graph the graph data source
	 * @return the set of nodes along the path
	 */
	public static <V> Set<V> collectNodesAlongPath(V source, V target, IGraphDataSource<V> graph) {
		HashSet<V> path = new HashSet<V>();
		_collectNodesAlongPath(source, target, graph, path);
		return path;
	}
	
	private static <V> boolean _collectNodesAlongPath(V node, V target, IGraphDataSource<V> graph, HashSet<V> path) {
		
		boolean res = false;
		
		//end recursion
		if (node.equals(target)) {
			path.add(node);
			return true;
		}
		else {
			for (V _nodeT : graph.getTargetNodes(node)) {
				res = (_collectNodesAlongPath(_nodeT, target, graph, path)) || res;
			}
			if (res) path.add(node);
			return res;
		}
	}
}
