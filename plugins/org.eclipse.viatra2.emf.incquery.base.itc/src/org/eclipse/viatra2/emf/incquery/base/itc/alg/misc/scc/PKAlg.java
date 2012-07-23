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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IBiDirectionalGraphDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IBiDirectionalWrapper;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphObserver;

public class PKAlg<V> implements IGraphObserver<V> {

	private static final long serialVersionUID = -4382533946686076317L;
	
	/**
	 * Maps the nodes to their indicies.
	 */
	private HashMap<V, Integer> node2index;
	private HashMap<Integer, V> index2node;
	private HashMap<V, Boolean> node2mark;
	
	/**
	 * Maps the index of a node to the index in the topsort.
	 */
	private HashMap<Integer, Integer> index2topsort;
	private HashMap<Integer, Integer> topsort2index;
	
	/**
	 * Index associated to the inserted nodes (incrementing with every insertion).
	 */
	private int index;
	
	/**
	 * Index within the topsort for the target node when edge insertion occurs.
	 */
	private int lower_bound;
	
	/**
	 * Index within the topsort for the source node when edge insertion occurs.
	 */
	private int upper_bound;
	
	private ArrayList<V> RF;
	private ArrayList<V> RB;
	private IBiDirectionalGraphDataSource<V> gds;
	
	public PKAlg(IGraphDataSource<V> gds) {
		if (gds instanceof IBiDirectionalGraphDataSource<?>) {
			this.gds = (IBiDirectionalGraphDataSource<V>) gds;
		}
		else {
			this.gds = new IBiDirectionalWrapper<V>(gds);
		}	
		
		node2mark = new HashMap<V, Boolean>();
		node2index = new HashMap<V, Integer>();
		index2node = new HashMap<Integer, V>();
		index2topsort = new HashMap<Integer, Integer>();
		topsort2index = new HashMap<Integer, Integer>();
		index = 0;
		
		gds.attachObserver(this);
	}
	
	
	@Override
	public void edgeInserted(V source, V target) {
	
		RF = new ArrayList<V>();
		RB = new ArrayList<V>();
		
		lower_bound = index2topsort.get(node2index.get(target));
		upper_bound = index2topsort.get(node2index.get(source));
		
		if (lower_bound < upper_bound) {
			dfsForward(target);
			dfsBackward(source);
			reorder();
		}
	}
	
	private ArrayList<Integer> getIndicies(ArrayList<V> list) {
		ArrayList<Integer> indicies = new ArrayList<Integer>();
		
		for (V n : list)
			indicies.add(index2topsort.get(node2index.get(n)));
		
		return indicies;
	}

	private void reorder() {
	
		Collections.reverse(RB);
		
		//azon csomopontok indexei amelyek sorrendje nem jo
		ArrayList<Integer> L = getIndicies(RF);
		L.addAll(getIndicies(RB));
		Collections.sort(L);
		
		for (int i = 0;i<RB.size();i++) {
			index2topsort.put(node2index.get(RB.get(i)), L.get(i));
			topsort2index.put(L.get(i), node2index.get(RB.get(i)));
		}
		
		for (int i = 0;i<RF.size();i++) {
			index2topsort.put(node2index.get(RF.get(i)), L.get(i+RB.size()));
			topsort2index.put(L.get(i+RB.size()), node2index.get(RF.get(i)));
		}
	}

	@SuppressWarnings("unused")
	private List<V> getTopSort() {
		List<V> topsort = new ArrayList<V>();
		
		for (int i : topsort2index.values()) {
			topsort.add(index2node.get(i));
		}
		
		return topsort;
	}

	private void dfsBackward(V node) {
		node2mark.put(node, true);
		RB.add(node);
		
		List<V> sources = gds.getSourceNodes(node);
		
		if (sources != null)
			for (V sn : sources) {
				int top_id = index2topsort.get(node2index.get(sn));
				
				if (!node2mark.get(sn) && lower_bound < top_id) 
					dfsBackward(sn);
			}
	}


	private void dfsForward(V node) {
		node2mark.put(node, true);
		RF.add(node);
		
		List<V> targets = gds.getTargetNodes(node);
		
		if (targets != null)
			for (V tn : targets) {
				int top_id = index2topsort.get(node2index.get(tn));
				
				if (top_id == upper_bound)
					System.out.println("!!!Cycle detected!!!");
				else if (!node2mark.get(tn) && top_id < upper_bound)
					dfsForward(tn);
			}
	}

	@Override
	public void edgeDeleted(V source, V target) {
		//Edge deletion does not affect topsort
	}

	@Override
	public void nodeInserted(V n) {
		node2mark.put(n, false);
		node2index.put(n, index);
		index2node.put(index, n);
		index2topsort.put(index, index);
		topsort2index.put(index, index);
		index++;
	}

	@Override
	public void nodeDeleted(V n) {
		node2mark.remove(n);
		int node_id = node2index.remove(n);
		index2node.remove(node_id);
		int top_id = index2topsort.remove(node_id);
		topsort2index.remove(top_id);
	}
}
