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

package org.eclipse.incquery.runtime.base.itc.alg.misc.scc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.incquery.runtime.base.itc.igraph.IGraphDataSource;

/**
 * Efficient algorithms to compute the Strongly Connected Components in a directed graph.
 * 
 * @author Tamas Szabo
 * 
 * @param <V> the type parameter of the nodes in the graph
 */
public class SCC<V> {
	
	public static long sccId = 0;
	
	/**
	 * Computes the SCCs for the given graph and returns them as a multiset. 
	 * (Iterative version of Tarjan's algorithm)
	 * 
	 * @param g the directed graph data source
	 * @return the set of SCCs
	 */
	public static <V> SCCResult<V> computeSCC(IGraphDataSource<V> g) {
		int index = 0;
		Set<Set<V>> ret = new HashSet<Set<V>>();
		Map<V, SCCProperty> nodeMap = new HashMap<V, SCCProperty>();
		Map<V, List<V>> targetNodeMap = new HashMap<V, List<V>>();
		Map<V, Set<V>> notVisitedMap = new HashMap<V, Set<V>>();
		
		//stores the nodes during the traversal
		Stack<V> nodeStack = new Stack<V>();
		//stores the nodes that are in the same strongly connected component
		Stack<V> sccStack = new Stack<V>();

		boolean sink = false, finishedTraversal = true;
		
		//initialize all nodes with 0 index and 0 lowlink 
		Set<V> allNodes = g.getAllNodes();
		for (V n : allNodes) {
			nodeMap.put(n, new SCCProperty(0, 0));
		}

		for (V n : allNodes) {
			if (nodeMap.get(n).getIndex() == 0) {	
				
				index++;
				sccStack.push(n);
				nodeMap.get(n).setIndex(index);
				nodeMap.get(n).setLowlink(index);
				
				notVisitedMap.put(n, new HashSet<V>());
				
				List<V> targetNodes = g.getTargetNodes(n);
				
				if (targetNodes != null) {
					targetNodeMap.put(n, new ArrayList<V>(targetNodes));
				}
				
				nodeStack.push(n);
				
				while(!nodeStack.isEmpty()) {
				
					V currentNode = nodeStack.peek();
					sink = false; finishedTraversal = false;
					SCCProperty prop = nodeMap.get(currentNode);
					
					//if node is not visited yet
					if (nodeMap.get(currentNode).getIndex() == 0) {
						index++;
						sccStack.push(currentNode);
						prop.setIndex(index);
						prop.setLowlink(index);
						
						notVisitedMap.put(currentNode, new HashSet<V>());
						
						//storing the target nodes of the actual node
						if (g.getTargetNodes(currentNode) != null) {
							targetNodeMap.put(currentNode, new ArrayList<V>(g.getTargetNodes(currentNode)));
						}
					}
					
					if (targetNodeMap.get(currentNode) != null) {
						
						if (targetNodeMap.get(currentNode).size() == 0) {
							targetNodeMap.remove(currentNode);
							
							//remove node from stack, the exploration of its children has finished
							nodeStack.pop();
							
							List<V> targets = g.getTargetNodes(currentNode);
							if (targets != null) {
								for (V targetNode : g.getTargetNodes(currentNode)) {
									if (notVisitedMap.get(currentNode).contains(targetNode)) {
										prop.setLowlink(Math.min(prop.getLowlink(),	nodeMap.get(targetNode).getLowlink()));
									} else if (sccStack.contains(targetNode)) {
										prop.setLowlink(Math.min(prop.getLowlink(),	nodeMap.get(targetNode).getIndex()));
									}
								}
							}
							
							finishedTraversal = true;
						}
						else {
							//push next node to stack
							V targetNode = targetNodeMap.get(currentNode).remove(0);
							//if _t has not yet been visited
							if (nodeMap.get(targetNode).getIndex() == 0) {
								notVisitedMap.get(currentNode).add(targetNode);
								nodeStack.add(targetNode);
							}
						}
					}
					//if _node has no target nodes
					else {
						nodeStack.pop();
						sink = true;
					}
					
					//create scc if node is a sink or an scc has been found
					if ((sink || finishedTraversal) && (prop.getLowlink() == prop.getIndex())) {
						Set<V> sc = new HashSet<V>();
						V targetNode = null;

						do {
							targetNode = sccStack.pop();
							sc.add(targetNode);
						} while (!targetNode.equals(currentNode));

						ret.add(sc);
					}
				}
			}
		}
		
		return new SCCResult<V>(ret, g);
	}
	
//	static class SCCComputationTask<V> extends RecursiveTask<Set<Set<V>>> {
//
//		private static final long serialVersionUID = -7247372604863927633L;
//		private IBiDirectionalGraphDataSource<V> graph;
//		
//		public SCCComputationTask(IBiDirectionalGraphDataSource<V> graph) {
//			this.graph = graph;
//		}
//		
//		@Override
//		protected Set<Set<V>> compute() {
//			Set<Set<V>> multiSet = null;
//			//System.out.println(Thread.currentThread().getName()+" nodes: "+graph.getAllNodes());
//			V node = graph.getAllNodes().iterator().next();
//			
//			Set<V> allNodes = new HashSet<V>();
//			allNodes.addAll(graph.getAllNodes());
//			Set<V> sourceNodes = BFS.reachableSources(graph, node);
//			Set<V> targetNodes = BFS.reachableTargets(graph, node);
//			
//			Set<V> intersection = setIntersection(sourceNodes, targetNodes);
//			//System.out.println(Thread.currentThread().getName()+" isect "+intersection);
//			if (intersection.size() == graph.getAllNodes().size()) {
//				//Recursion ends
//				multiSet = new HashSet<Set<V>>();
//				multiSet.add(intersection);
//			}
//			else {
//				allNodes.removeAll(sourceNodes);
//				allNodes.removeAll(targetNodes);
//				sourceNodes.removeAll(intersection);
//				targetNodes.removeAll(intersection);
//				
//				SCCComputationTask<V> task1 = null;
//				Set<Set<V>> task1Result = null;
//				
//				SCCComputationTask<V> task2 = null;
//				Set<Set<V>> task2Result = null;
//				
//				SCCComputationTask<V> task3 = null;
//				Set<Set<V>> task3Result = null;
//				
//				//DCSC(Pred(G, v) \ SCC)
//				if (!sourceNodes.isEmpty()) {
//					task1 = new SCCComputationTask<V>(getGraphOfSCC(graph, sourceNodes));
//					task1.fork();
//				}
//				//DCSC(Desc(G, v) \ SCC)
//				if (!targetNodes.isEmpty()) {
//					task2 = new SCCComputationTask<V>(getGraphOfSCC(graph, targetNodes));
//					task2.fork();
//				}
//				//DCSC(Rem(G, v))
//				if (!allNodes.isEmpty()) {
//					task3 = new SCCComputationTask<V>(getGraphOfSCC(graph, allNodes));
//					task3.fork();
//				}
//				
//				if (task1 != null) task1Result = task1.join();
//				if (task2 != null) task2Result = task2.join();
//				if (task3 != null) task3Result = task3.join();			
//				
//				multiSet = multisetUnion(task1Result, multisetUnion(task2Result, task3Result));
//				multiSet.add(intersection);
//			}
//			
//			return multiSet;
//		}
//	
//	}
//	
//	private static<V> Set<V> setIntersection(Set<V> sourceNodes, Set<V> targetNodes) {
//		HashSet<V> retSet = new HashSet<V>();
//		
//		for (V e : sourceNodes) {
//			if (targetNodes.contains(e)) {
//				retSet.add(e);
//			}
//		}
//		
//		return retSet;
//	}
//	
//	private static<V> Set<Set<V>> multisetUnion(Set<Set<V>> m1, Set<Set<V>> m2) {
//		Set<Set<V>> multiSet = new HashSet<Set<V>>();
//		
//		if (m1 != null) {
//			for (Set<V> s : m1) {
//				multiSet.add(s);
//			}
//		}
//		if (m2 != null) {
//			for (Set<V> s : m2) {
//				multiSet.add(s);
//			}
//		}
//		
//		return multiSet;
//	}
//	
//	private static<V> IBiDirectionalGraphDataSource<V> getGraphOfSCC(IBiDirectionalGraphDataSource<V> graph, Set<V> scc) {
//		Graph<V> g = new Graph<V>();
//		
//		for (V node : scc) {
//			g.insertNode(node);
//		}
//		
//		for (V src : graph.getAllNodes()) {
//			for (V trg : graph.getTargetNodes(src)) {
//				if (scc.contains(src) && scc.contains(trg)) {
//					g.insertEdge(src, trg);
//				}
//			}
//		}
//		
//		IBiDirectionalGraphDataSource<V> bG = new IBiDirectionalWrapper<V>(g);
//		return bG;
//	}
//	
//	/**
//	 * Parallel algorithm to compute the Strongly Connected Components in a directed graph.
//	 * The implementation is based on the Fork Join Framework.
//	 * 
//	 * Note that the implementation relies on the correct implementation of the equals method of the type paramter's class.
//	 * 
//	 * @param g the directed graph datasource
//	 * @return the set of SCCs
//	 */
//	public static <V> SCCResult<V> parallelSCCComputation(IBiDirectionalGraphDataSource<V> graph) {
//		ForkJoinPool pool = new ForkJoinPool();
//		SCCComputationTask<V> task = new SCCComputationTask<V>(graph);
//		pool.invoke(task);
//		
//		return new SCCResult<V>(task.join(), graph);
//	}
	
	
}
