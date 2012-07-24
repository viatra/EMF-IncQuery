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

package org.eclipse.viatra2.emf.incquery.base.itc.alg.kingopt;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphObserver;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.ITcDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.ITcObserver;

/**
 * This class is the optimized implementation of the King algorithm. 
 * The King algorithm works well both on cyclic and acyclic graphs.
 * 
 * @author Tamas Szabo
 *
 * @param <V> the type parameter of the nodes in the graph data source
 */
public class KingOptAlg<V> implements IGraphObserver<V>, ITcDataSource<V> {

	private static final long serialVersionUID = -748676749122336868L;
	private KingOptTcRelation<V> tc;
	private ArrayList<ITcObserver<V>> observers;
	private IGraphDataSource<V> gds;
	private int levelCount;
	
	/**
	 * Constructs a new King algorithm and initializes the transitive closure relation with the given graph data source.
	 * Attach itself on the graph data source as an observer. 
	 * 
	 * @param gds the graph data source instance
	 */
	public KingOptAlg(IGraphDataSource<V> gds) {
		this.gds = gds;		
		observers = new ArrayList<ITcObserver<V>>();
		tc = new KingOptTcRelation<V>(calculateLevelCount(), observers);
		fullGen();
		gds.attachObserver(this);
	}

	@Override
	public void edgeInserted(V source, V target) {
		tc.deriveBaseTuple(source, target, 1);
	}

	@Override
	public void edgeDeleted(V source, V target) {
		tc.deriveBaseTuple(source, target, -1);
	}

	@Override
	public void nodeInserted(V n) {
		tc.setLevelCount(calculateLevelCount());
	}
	
	@Override
	public void nodeDeleted(V n) {
		tc.setLevelCount(calculateLevelCount());
	}
	
	private int calculateLevelCount() {
		int nodeCount = this.gds.getAllNodes().size();
		if (nodeCount <= 1) {
			levelCount = 0;
		}
		else {
			double j = Math.log10(gds.getAllNodes().size()) / Math.log10(2);
			this.levelCount = (int) Math.ceil(j);
		}
		return levelCount;
	}
	
	/**
	 * Generates the whole TC relation appropriate for the king opt algorithm.
	 */
	public void fullGen() {
		tc.clearTc();
		tc.setLevelCount(calculateLevelCount());
		
		for (V s : gds.getAllNodes()) {
			List<V> targets = gds.getTargetNodes(s);
			if (targets != null) {
				for (V t : gds.getTargetNodes(s)) {
					tc.addTuple(s, t, 0);
				}
			}
		}
		
		//we just need the starred tuples at level 0, so it is not necessary to spread the modification
		tc.clearQueue();
		
		Map<V, Set<V>> _targetSet = null;
		
		//E(i) = *E(i-1) X-> *U(i-1)
		for (int levelNumber = 1;levelNumber<=levelCount;levelNumber++) {
			Map<V, Set<V>> _sourceSet = tc.getStarredAtLevel(levelNumber-1);

			if (_sourceSet != null) {
				
				for (Entry<V, Set<V>> entry : _sourceSet.entrySet()) {
					
					for (V _target : entry.getValue()) {
						
						for (int j = 0 ; j <= levelNumber ; j++) {
							_targetSet = tc.getStarredAtLevel(j);
							if (_targetSet != null && _targetSet.get(_target) != null) {
								
								for (V _targetTarget : _targetSet.get(_target)) {
									tc.addTuple(entry.getKey(), _targetTarget, levelNumber);
								}
							}	
						}
					}
				}
			}
		}

		tc.doQueue();
	}

	public KingOptTcRelation<V> getTcRelation() {
		return tc;
	}

	@Override
	public void attachObserver(ITcObserver<V> to) {
		observers.add(to);
	}

	@Override
	public void detachObserver(ITcObserver<V> to) {
		observers.remove(to);
	}

	@Override
	public Set<V> getAllReachableTargets(V source) {
		return tc.getTupleEnds(source);
	}

	@Override
	public Set<V> getAllReachableSources(V target) {
		return tc.getTupleStarts(target);
	}

	@Override
	public boolean isReachable(V source, V target) {
		return tc.containsTuple(source, target);
	}

	@Override
	public void dispose() {
		tc = null;
	}
}
