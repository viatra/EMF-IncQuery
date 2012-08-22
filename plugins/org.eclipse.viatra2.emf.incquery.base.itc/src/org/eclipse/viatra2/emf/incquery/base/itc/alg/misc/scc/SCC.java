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

package org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.scc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphDataSource;

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
	 * This method computes the SCCs for the given graph and returns them as a
	 * set of sets. (Iterative Tarjan algorithm)
	 * 
	 * Note that the implementation relies on the correct implementation of the equals method of the type paramter's class.
	 * 
	 * @param g the directed graph datasource
	 * @return the set of SCCs
	 */
	public static <V> SCCResult<V> computeSCC(IGraphDataSource<V> g) {
		
		int index = 0;
		Set<Set<V>> ret = new HashSet<Set<V>>();
		HashMap<V, SCCProperty> nodeMap = new HashMap<V, SCCProperty>();
		HashMap<V, ArrayList<V>> targetNodeMap = new HashMap<V, ArrayList<V>>();
		Stack<V> nodeStack = new Stack<V>();
		Stack<V> sccStack = new Stack<V>();
		
		//used to stores those nodes which have not been visited at the time when the key node is visited
		HashMap<V, HashSet<V>> notVisitedMap = new HashMap<V, HashSet<V>>();

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
				
					V _node = nodeStack.peek();
					SCCProperty prop = nodeMap.get(_node);
					
					//if node is not visited yet
					if (nodeMap.get(_node).getIndex() == 0) {
						index++;
						sccStack.push(_node);
						prop.setIndex(index);
						prop.setLowlink(index);
						
						notVisitedMap.put(_node, new HashSet<V>());
						
						//storing the taret nodes of the actual node
						if (g.getTargetNodes(_node) != null) {
							targetNodeMap.put(_node, new ArrayList<V>(g.getTargetNodes(_node)));
						}
					}
					
					if (targetNodeMap.get(_node) != null) {
						
						if (targetNodeMap.get(_node).size() == 0) {
							targetNodeMap.remove(_node);
							
							//remove node from stack, the exploration of its children has finished
							nodeStack.pop();
							
							List<V> targets = g.getTargetNodes(_node);
							if (targets != null) {
								for (V t : g.getTargetNodes(_node)) {
									if (notVisitedMap.get(_node).contains(t)) {
										prop.setLowlink(Math.min(prop.getLowlink(),	nodeMap.get(t).getLowlink()));
									} else if (sccStack.contains(t)) {
										prop.setLowlink(Math.min(prop.getLowlink(),	nodeMap.get(t).getIndex()));
									}
								}
							}
							
							if (prop.getLowlink() == prop.getIndex()) {
								HashSet<V> sc = new HashSet<V>();
								V targetNode = null;
			
								do {
									targetNode = sccStack.pop();
									sc.add(targetNode);
								} while (!targetNode.equals(_node));
			
								ret.add(sc);
							}
						}
						else {
							V _t = targetNodeMap.get(_node).remove(0);
							if (nodeMap.get(_t).getIndex() == 0) {
								notVisitedMap.get(_node).add(_t);
								nodeStack.add(_t);
							}
						}
					}
					else {
						nodeStack.pop();
						
						if (prop.getLowlink() == prop.getIndex()) {
							HashSet<V> sc = new HashSet<V>();
							V targetNode = null;
		
							do {
								targetNode = sccStack.pop();
								sc.add(targetNode);
							} while (!targetNode.equals(_node));
		
							ret.add(sc);
						}
						
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
