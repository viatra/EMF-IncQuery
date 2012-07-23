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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
	private TcRelation<V> tc;
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
		tc = new TcRelation<V>(calculateLevelCount(), observers);
		fullGen();
		gds.attachObserver(this);
	}

	/*
	 * (non-Javadoc)
	 * @see itc.igraph.IGraphObserver#edgeInserted(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void edgeInserted(V source, V target) {
		tc.deriveBaseTuple(source, target, 1);
	}

	/*
	 * (non-Javadoc)
	 * @see itc.igraph.IGraphObserver#edgeDeleted(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void edgeDeleted(V source, V target) {
		tc.deriveBaseTuple(source, target, -1);
	}

	/*
	 * (non-Javadoc)
	 * @see itc.igraph.IGraphObserver#nodeInserted(java.lang.Object)
	 */
	@Override
	public void nodeInserted(V n) {
		tc.setLevelCount(calculateLevelCount());
	}
	
	/*
	 * (non-Javadoc)
	 * @see itc.igraph.IGraphObserver#nodeDeleted(java.lang.Object)
	 */
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
			this.levelCount = (int) ((j == Math.floor(j)) ? j : j + 1);
		}
		return levelCount;
	}
	
	/**
	 * Generates the whole TC relation appropriate for the king opt algorithm.
	 */
	public void fullGen() {
		tc.clearTc();
		//System.out.println("Level count: "+calculateLevelCount());
		
		tc.setLevelCount(calculateLevelCount());
		
		//System.out.println(new Timestamp(System.currentTimeMillis()));
		for (V s : gds.getAllNodes()) {
			List<V> targets = gds.getTargetNodes(s);
			if (targets != null) {
				for (V t : gds.getTargetNodes(s)) {
					tc.addTuple(s, t, 0);
				}
			}
		}
		//System.out.println(new Timestamp(System.currentTimeMillis()));
		//we just need the starred tuples at level 0, so it is not necessary to spread the modification
		tc.clearQueue();
		
		HashMap<V, HashSet<V>> _targetSet = null;
		
		//E(i) = *E(i-1) X-> *U(i-1)
		for (int levelNumber = 1;levelNumber<=levelCount;levelNumber++) {
			HashMap<V, HashSet<V>> _sourceSet = tc.getStarredAtLevel(levelNumber-1);
			
			//System.out.println(levelNumber);
			if (_sourceSet != null) {
				
				for (Entry<V, HashSet<V>> entry : _sourceSet.entrySet()) {
					
					for (V _target : entry.getValue()) {
						
						for (int j = 0 ; j <= levelNumber ; j++) {
							_targetSet = tc.getStarredAtLevel(j);
							if (_targetSet != null && _targetSet.get(_target) != null) {
								
								for (V _targetTarget : _targetSet.get(_target)) {
									//if (!_targetTarget.equals(_source))
										tc.addTuple(entry.getKey(), _targetTarget, levelNumber);
								}
							}	
						}
					}
				}
			}
		}
		//System.out.println(new Timestamp(System.currentTimeMillis()));
		tc.doQueue();
		//System.out.println(new Timestamp(System.currentTimeMillis()));
		//System.out.println("Average count list size: "+getCountListAvg());
		
		//System.out.println(tc);
	}

	public TcRelation<V> getTcRelation() {
		return tc;
	}

	/*
	 * (non-Javadoc)
	 * @see itc.igraph.ITcDataSource#attachObserver(itc.igraph.ITcObserver)
	 */
	@Override
	public void attachObserver(ITcObserver<V> to) {
		observers.add(to);
	}

	/*
	 * (non-Javadoc)
	 * @see itc.igraph.ITcDataSource#detachObserver(itc.igraph.ITcObserver)
	 */
	@Override
	public void detachObserver(ITcObserver<V> to) {
		observers.remove(to);
	}

	/*
	 * (non-Javadoc)
	 * @see itc.igraph.ITcDataSource#getTargetNodes(java.lang.Object)
	 */
	@Override
	public Set<V> getAllReachableTargets(V source) {
		return tc.getTupleEnds(source);
	}

	/*
	 * (non-Javadoc)
	 * @see itc.igraph.ITcDataSource#getSourceNodes(java.lang.Object)
	 */
	@Override
	public Set<V> getAllReachableSources(V target) {
		return tc.getTupleStarts(target);
	}

	/*
	 * (non-Javadoc)
	 * @see itc.igraph.ITcDataSource#isReachable(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isReachable(V source, V target) {
		return tc.containsTuple(source, target);
	}

	@Override
	public void dispose() {
		tc = null;
	}
}
