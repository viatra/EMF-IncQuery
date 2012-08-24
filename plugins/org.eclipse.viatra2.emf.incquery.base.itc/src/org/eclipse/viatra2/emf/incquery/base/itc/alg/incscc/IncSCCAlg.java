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

package org.eclipse.viatra2.emf.incquery.base.itc.alg.incscc;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.counting.CountingAlg;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.dred.DRedTcRelation;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.Tuple;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.bfs.BFS;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.scc.SCC;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.scc.SCCResult;
import org.eclipse.viatra2.emf.incquery.base.itc.graphimpl.Graph;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IBiDirectionalGraphDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IBiDirectionalWrapper;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphObserver;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.ITcDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.ITcObserver;

/**
 * Incremental SCC maintenance + counting algorithm.
 * 
 * @author Tamas Szabo
 *
 * @param <V> the type parameter of the nodes in the graph data source
 */
public class IncSCCAlg<V> implements IGraphObserver<V>, ITcDataSource<V> {

	private static final long serialVersionUID = 6207002106223444807L;

	private UnionFind<V> sccs;
	private IBiDirectionalGraphDataSource<V> gds;
	private CountingAlg<V> countingAlg;
	private Graph<V> reducedGraph;
	private IBiDirectionalGraphDataSource<V> reducedGraphIndexer;
	private List<ITcObserver<V>> observers;
		
	public IncSCCAlg(IGraphDataSource<V> gds) {
		
		if (gds instanceof IBiDirectionalGraphDataSource<?>) {
			this.gds = (IBiDirectionalGraphDataSource<V>) gds;
		}
		else {
			this.gds = new IBiDirectionalWrapper<V>(gds);
		}	
		this.observers = new ArrayList<ITcObserver<V>>();
		this.sccs = new UnionFind<V>();
		this.reducedGraph = new Graph<V>();
		this.reducedGraphIndexer = new IBiDirectionalWrapper<V>(reducedGraph);
		init();
		this.gds.attachObserver(this);
	}
	
	@SuppressWarnings("unchecked")
	private void init() {	
		SCCResult<V> _sccres = SCC.computeSCC(gds);
		Set<Set<V>> _sccs = _sccres.getSccs();
		
		for (Set<V> _set : _sccs) {
			sccs.makeSet((V[]) _set.toArray());
		}

		//init reduced graph
		for (V n : sccs.setMap.keySet()) {
			reducedGraph.insertNode(n);
		}

		for (V source : gds.getAllNodes()) {
			final List<V> targetNodes = gds.getTargetNodes(source);
			if (targetNodes != null) for (V target : targetNodes) {
				V sourceRoot = sccs.find(source);
				V targetRoot = sccs.find(target);
				
				if (!sourceRoot.equals(targetRoot) && !sourceRoot.equals(targetRoot))
					reducedGraph.insertEdge(sourceRoot, targetRoot);
			}
		}

		this.countingAlg = new CountingAlg<V>(reducedGraph);
	}
	
	@Override
	public void edgeInserted(V source, V target) {
		V sourceRoot = sccs.find(source);
		V targetRoot = sccs.find(target);
		
		//Different SCC
		if (!sourceRoot.equals(targetRoot)) {
			
			//source is reachable from target?
			if (countingAlg.isReachable(targetRoot, sourceRoot)) {

				Set<V> predecessorRoots = countingAlg.getAllReachableSources(sourceRoot);
				Set<V> successorRoots = countingAlg.getAllReachableTargets(targetRoot);
				
				//1. intersection
				List<V> isectRoots = setIntersection(predecessorRoots, successorRoots);
				isectRoots.add(sourceRoot);
				isectRoots.add(targetRoot);
				
				//must notfiy before the actual relation modification because some sets will be deleted
				if (observers.size() > 0) {
					Set<V> sourcesOfNotification = new HashSet<V>();
					Set<V> targetsOfNotification = new HashSet<V>();

					Set<V> sources = new HashSet<V>();
					sources.add(sourceRoot);
					
					Set<V> targets = new HashSet<V>();
					targets.add(targetRoot);
					
					Set<V> reachableTargetsOfSourceRoot = this.countingAlg.getAllReachableTargets(sourceRoot);
					Set<V> reachableTargetsOfTargetRoot = this.countingAlg.getAllReachableTargets(targetRoot);
					Set<V> reachableSourcesOfSourceRoot = this.countingAlg.getAllReachableSources(sourceRoot);
					
					if (reachableSourcesOfSourceRoot != null) sources.addAll(reachableSourcesOfSourceRoot);
					if (reachableTargetsOfTargetRoot != null) targets.addAll(reachableTargetsOfTargetRoot);
					
					for (V sRoot : sources) {
						for (V tRoot : targets) {
							if (!tRoot.equals(sRoot) && 
									((reachableTargetsOfSourceRoot == null) || 
											!reachableTargetsOfSourceRoot.contains(tRoot))) {
								sourcesOfNotification.clear();
								targetsOfNotification.clear();
								sourcesOfNotification.addAll(sccs.setMap.get(sRoot));
								targetsOfNotification.addAll(sccs.setMap.get(tRoot));
								notifyTcObservers(sourcesOfNotification, targetsOfNotification, 1);
							}
						}
					}
				}
				
				//2. delete edges, nodes
				List<V> sources = new ArrayList<V>();
				List<V> targets = new ArrayList<V>();
				
				for (V r : isectRoots) {
					List<V> _srcList = getSourceNodesToSCC(r);
					List<V> _trgList = getTargetNodesToSCC(r);
					
					for (V _source : _srcList) {
						if (!_source.equals(r)) {
							reducedGraph.deleteEdge(_source, r);
						}
					}
					
					for (V _target : _trgList) {
						if (!isectRoots.contains(_target) && !r.equals(_target)) {
							reducedGraph.deleteEdge(r, _target);
						}
					}
					
					sources.addAll(_srcList);
					targets.addAll(_trgList);
				}
				
				for (V r : isectRoots) {
					reducedGraph.deleteNode(r);
				}
				
				//3. union
				V newRoot = isectRoots.get(0);
				for (int i = 1;i<isectRoots.size();i++) {
					newRoot = sccs.union(newRoot, isectRoots.get(i));
				}
				
				//4. add new node
				reducedGraph.insertNode(newRoot);

				//5. add edges
				Set<V> containedNodes = sccs.setMap.get(newRoot);
				
				for (V _s : sources) {
					if (!containedNodes.contains(_s) && !_s.equals(newRoot)) {
						reducedGraph.insertEdge(_s, newRoot);
					}
				}
				for (V _t : targets) {
					if (!containedNodes.contains(_t) && !_t.equals(newRoot)) {
						reducedGraph.insertEdge(newRoot, _t);
					}
				}
			}
			else {
				
				if (!sourceRoot.equals(targetRoot)) {
										
					//if there is at least one observer
					if (observers.size() > 0) {
					
						Set<V> notifiedSources = new HashSet<V>();
						notifiedSources.addAll(sccs.setMap.get(sourceRoot));
						Set<V> notifiedTargets = new HashSet<V>();
						notifiedTargets.addAll(sccs.setMap.get(targetRoot));
						
						Set<V> sourceRoots = this.countingAlg.getAllReachableSources(sourceRoot);
						Set<V> targetRoots = this.countingAlg.getAllReachableTargets(targetRoot);
						
						if (sourceRoots != null) {
							for (V s : sourceRoots) {
								notifiedSources.addAll(sccs.setMap.get(s));
							}
						}
						
						if (targetRoots != null) {
							for (V t : targetRoots) {
								notifiedTargets.addAll(sccs.setMap.get(t));
							}
						}
						
						notifyTcObservers(notifiedSources, notifiedTargets, 1);
					}
					
					reducedGraph.insertEdge(sourceRoot, targetRoot);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void edgeDeleted(V source, V target) {		
		V sourceRoot = sccs.find(source);
		V targetRoot = sccs.find(target);
		
		if (!sourceRoot.equals(targetRoot)) {
			reducedGraph.deleteEdge(sourceRoot, targetRoot);
			
			if (observers.size() > 0) {
				
				Set<V> sourcesOfNotification = new HashSet<V>();
				sourcesOfNotification.addAll(sccs.setMap.get(sourceRoot));
				Set<V> targetsOfNotification = new HashSet<V>();
				targetsOfNotification.addAll(sccs.setMap.get(targetRoot));
				
				Set<V> sourceRoots = this.countingAlg.getAllReachableSources(sourceRoot);
				Set<V> targetRoots = this.countingAlg.getAllReachableTargets(targetRoot);
				
				if (sourceRoots != null) {
					for (V s : sourceRoots) {
						sourcesOfNotification.addAll(sccs.setMap.get(s));
					}
				}
				
				if (targetRoots != null) {
					for (V t : targetRoots) {
						targetsOfNotification.addAll(sccs.setMap.get(t));
					}
				}
				
				notifyTcObservers(sourcesOfNotification, targetsOfNotification, -1);
			}
		}
		else {
			//sourceRoot equals targetRoot
			Graph<V> g = getGraphOfSCC(sourceRoot);

			//if source is not reachable from target anymore
			if (!BFS.isReachable(source, target, g)) {
				List<V> reachableSources = null;
				List<V> reachableTargets = null;
				
				//get the graph for the scc whose root is sourceRoot
				SCCResult<V> _newSccs = SCC.computeSCC(g);
				
				//delete scc node (and with this edges too)
				reachableSources = reducedGraphIndexer.getSourceNodes(sourceRoot);
				reachableTargets = reducedGraphIndexer.getTargetNodes(sourceRoot);
				
				if (reachableSources != null) {
					Set<V> tmp = new HashSet<V>(reachableSources);
					for (V s : tmp) {
						reducedGraph.deleteEdge(s, sourceRoot);
					}
				}
				if (reachableTargets != null) {
					Set<V> tmp = new HashSet<V>(reachableTargets);
					for (V t : tmp) {
						reducedGraph.deleteEdge(sourceRoot, t);
					}
				}
				sccs.deleteSet(sourceRoot);
				reducedGraph.deleteNode(sourceRoot);
				
				Set<Set<V>> newSccs = _newSccs.getSccs();
				Set<V> roots = new HashSet<V>();
				
				//add new nodes and edges to the reduced graph
				for (Set<V> _scc : newSccs) {
					V newRoot = sccs.makeSet((V[]) _scc.toArray());
					reducedGraph.insertNode(newRoot);
					roots.add(newRoot);
				}
				for (V _root : roots) {
					List<V> sourceNodes = getSourceNodesToSCC(_root);
					List<V> targetNodes = getTargetNodesToSCC(_root);

					for (V _s : sourceNodes) {
						V _sR = sccs.find(_s);
						if (!_sR.equals(_root))
							reducedGraph.insertEdge(sccs.find(_s), _root);
					}
					for (V _t : targetNodes) {
						V _tR = sccs.find(_t);
						if (!roots.contains(_t) && !_tR.equals(_root))
							reducedGraph.insertEdge(_root, _tR);
					}
				}
				
				//Must be after the union-find modifications
				if (observers.size() > 0) {
					V newSourceRoot = sccs.find(source);
					V newTargetRoot = sccs.find(target);
					Set<V> reachableSourcesTmp = countingAlg.getAllReachableSources(newSourceRoot);
					Set<V> reachableTargetsTmp = countingAlg.getAllReachableTargets(newTargetRoot);
					Set<V> sourceRoots = (Set<V>) ((reachableSourcesTmp == null) ? Collections.emptySet() : new HashSet<V>(reachableSourcesTmp));
					sourceRoots.add(newSourceRoot);
					Set<V> targetRoots = (Set<V>) ((reachableTargetsTmp == null) ? Collections.emptySet() : new HashSet<V>(reachableTargetsTmp));
					targetRoots.add(newTargetRoot);
					
					for (V sRoot : sourceRoots) {						
						for (V tRoot : targetRoots) {	
							if (!sRoot.equals(tRoot) && !countingAlg.isReachable(sRoot, tRoot)) {
								Set<V> sourcesOfNotification = new HashSet<V>();
								sourcesOfNotification.addAll(sccs.setMap.get(sRoot));

								Set<V> targetsOfNotification = new HashSet<V>();
								targetsOfNotification.addAll(sccs.setMap.get(tRoot));

								notifyTcObservers(sourcesOfNotification, targetsOfNotification, -1);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void nodeInserted(V n) {		
		sccs.makeSet(n);
		reducedGraph.insertNode(n);
	}

	@Override
	public void nodeDeleted(V n) {		
		List<V> sources = gds.getSourceNodes(n);
		List<V> targets = gds.getTargetNodes(n);
		if (sources != null)
			for (V source : sources) {
				edgeDeleted(source, n);
			}
		
		if (targets != null)
			for (V target : gds.getTargetNodes(n)) {
				edgeDeleted(n, target);
			}
		
		sccs.deleteSet(n);
	}

	@Override
	public void attachObserver(ITcObserver<V> to) {
		this.observers.add(to);
	}

	@Override
	public void detachObserver(ITcObserver<V> to) {
		this.observers.remove(to);
	}

	@Override
	public Set<V> getAllReachableTargets(V source) {
		V sourceRoot = sccs.find(source);
		Set<V> targets = new HashSet<V>(sccs.setMap.get(sourceRoot));
		Set<V> rootSet = countingAlg.getAllReachableTargets(sourceRoot);

		if (rootSet != null) {
			for (V _root : rootSet) {
				targets.addAll(sccs.setMap.get(_root));
			}
		}
		//targets.remove(source);
		return targets;
	}

	@Override
	public Set<V> getAllReachableSources(V target) {
		V targetRoot = sccs.find(target);
		Set<V> sources = new HashSet<V>(sccs.setMap.get(targetRoot));
		Set<V> rootSet = countingAlg.getAllReachableSources(targetRoot);
		
		if (rootSet != null) {
			for (V _root : rootSet) {
				sources.addAll(sccs.setMap.get(_root));
			}
		}
		//sources.remove(target);
		return sources;
	}

	@Override
	public boolean isReachable(V source, V target) {
		V sourceRoot = sccs.find(source);
		V targetRoot = sccs.find(target);
		
		if (sourceRoot.equals(targetRoot)) return true;
		else return countingAlg.isReachable(sourceRoot, targetRoot);
	}

	private List<V> getSourceNodesToSCC(V root) {
		List<V> tmp = new ArrayList<V>();
		
		for (V containedNode : sccs.setMap.get(root)) {
			List<V> tmpNodes = gds.getSourceNodes(containedNode);
			
			if (tmpNodes != null) {
				for (V source : tmpNodes) {
					tmp.add(sccs.find(source));
				}
			}
		}

		return tmp;
	}
	
	/**
	 * Return the SCCs which are reachable from the SCC represented by the root node. 
	 * Note that an SCC can be present multiple times in the returned list (multiple edges between the two SCCs).
	 * 
	 * @param root
	 * @return the list of reachable target SCCs
	 */
	private List<V> getTargetNodesToSCC(V root) {

		List<V> tmp = new ArrayList<V>();
		
		for (V containedNode : sccs.setMap.get(root)) {
			List<V> tmpNodes = gds.getTargetNodes(containedNode);
			
			if (tmpNodes != null) {		
				for (V target : tmpNodes) {
					tmp.add(sccs.find(target));
				}
			}
		}

		return tmp;
	}
	
	/**
	 * Returns the intersection of two sets as a List.
	 * 
	 * @param set1 the first set
	 * @param set2 the second set
	 * @return the intersection of the sets
	 */
	private List<V> setIntersection(Set<V> set1, Set<V> set2) {
		
		List<V> tmp = new ArrayList<V>();
		
		for (V n : set1) {
			if (set2.contains(n)) {
				tmp.add(n);
			}
		}

		return tmp;
	}
	
	/**
	 * Returns the Graph for the given root node in the union find structure.
	 * 
	 * @param root the root node
	 * @return the graph for the subtree
	 */
	private Graph<V> getGraphOfSCC(V root) {
		
		Graph<V> g = new Graph<V>();
		Set<V> nodeSet = sccs.setMap.get(root);
		
		if (nodeSet != null) {
			for (V node : nodeSet) {
				g.insertNode(node);
			}
			for (V node : nodeSet) {
				
				ArrayList<V> sources = (gds.getSourceNodes(node) == null) ? null : new ArrayList<V>(gds.getSourceNodes(node));

				if (sources != null) {
					for (V _s : sources) {
						if (nodeSet.contains(_s)) {
							g.insertEdge(_s, node);
						}
					}
				}
			}
		}

		return g;
	}
	
	// for JUnit
	public boolean checkTcRelation(DRedTcRelation<V> tc) {
		
		for (V s : tc.getTupleStarts()) {
			for (V t : tc.getTupleEnds(s)) {
				if (!isReachable(s, t)) return false;
			}
		}
		
		for (V root : this.countingAlg.getTcRelation().getTupleStarts()) {
			for (V end : this.countingAlg.getTcRelation().getTupleEnds(root)) {
				for (V s : this.sccs.setMap.get(root)) {
					for (V t : this.sccs.setMap.get(end)) {
						if (!tc.containsTuple(s, t)) return false;
					}
				}
			}
		}
		
		return true;
	}

	@Override
	public void dispose() {
		this.gds.detachObserver(this);
		countingAlg.dispose();
	}
	
	/**
	 * Call this method to notify the observers of the transitive closure relation.
	 * The tuples used in the notification will be the Descartes product of the two sets given.
	 * 
	 * @param sources the source nodes
	 * @param targets the target nodes
	 * @param dir 1 if tuple insertion, -1 if tuple deletion occured
	 */
	private void notifyTcObservers(Set<V> sources, Set<V> targets, int dir) {
		for (ITcObserver<V> o : observers) {
			for (V s : sources) {
				for (V t : targets) {
					if (dir == 1) {
						if (!this.isReachable(s, t)) {
							o.tupleInserted(s, t);
						}
					}
					if (dir == -1) {
						if (!this.isReachable(s, t)) {
							o.tupleDeleted(s, t);
						}
					}
				}
			}
			//new NotifierThread<V>(sources, targets, o, dir).start();
		}
	}

	public Set<Tuple<V>> getTcRelation() {
		Set<Tuple<V>> retSet = new HashSet<Tuple<V>>();
		
		for (V sourceRoot : this.sccs.setMap.keySet()) {
			
			for (V nS : this.sccs.setMap.get(sourceRoot)) {
				for (V nT : this.sccs.setMap.get(sourceRoot)) {
					//if (!nS.equals(nT)) {  - loops are allowed
						retSet.add(new Tuple<V>(nS, nT));
					//}
				}
			}
			Set<V> reachableTargets = this.countingAlg.getAllReachableTargets(sourceRoot);
			
			if (reachableTargets != null) {
				for (V targetRoot : reachableTargets) {
					for (V sN : this.sccs.setMap.get(sourceRoot)) {
						for (V tN : this.sccs.setMap.get(targetRoot)) {
							retSet.add(new Tuple<V>(sN, tN));
						}
					}
				}
			}
		}
		
		return retSet;
	}
	
	public boolean isIsolated(V node) {
		List<V> targets = gds.getTargetNodes(node);
		List<V> sources = gds.getSourceNodes(node);	
		
		if (((targets == null) || (targets.isEmpty())) && ((sources == null) || (sources.isEmpty()))) {
			return true;
		}
		else {
			return false;
		}
	}
}
