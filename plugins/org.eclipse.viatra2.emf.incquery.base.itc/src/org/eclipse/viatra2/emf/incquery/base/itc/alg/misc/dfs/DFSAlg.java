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

package org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.dfs;

import java.util.HashMap;
import java.util.List;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.dred.DRedTcRelation;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphObserver;


public class DFSAlg<V> implements IGraphObserver<V> {

	private static final long serialVersionUID = 7397186805581323071L;
	private IGraphDataSource<V> gds;
	private DRedTcRelation<V> tc;
	private int[] visited;
	private HashMap<V, Integer> nodeMap;
	
	public DFSAlg(IGraphDataSource<V> gds) {
		this.gds = gds;
		this.tc = new DRedTcRelation<V>();
		gds.attachObserver(this);
		deriveTc();
	}
	
	private void deriveTc() {
		tc.clear();
		
		this.visited = new int[gds.getAllNodes().size()];
		nodeMap = new HashMap<V, Integer>();
		
		int j = 0;
		for (V n : gds.getAllNodes()) {
			nodeMap.put(n, j);
			j++;
		}
		
		for (V n : gds.getAllNodes()) {
			oneDFS(n, n);
			initVisitedArray();
		}
	}
	
	private void initVisitedArray() {
		for (int i=0;i<visited.length;i++)
			visited[i] = 0;
	}
	
	private void oneDFS(V act, V source) {
	
		if (!act.equals(source)) {
			tc.addTuple(source, act);
		}
		
		visited[nodeMap.get(act)] = 1;
	
		List<V> targets = gds.getTargetNodes(act);
		if (targets != null) {
			for (V t : targets)	{
				if (visited[nodeMap.get(t)] == 0) {
					oneDFS(t, source);
				}
			}
		}
	}
	
	public DRedTcRelation<V> getTcRelation() {
		return this.tc;
	}
	
	@Override
	public void edgeInserted(V source, V target) {
		deriveTc();
	}

	@Override
	public void edgeDeleted(V source, V target) {
		deriveTc();
	}

	@Override
	public void nodeInserted(V n) {
		deriveTc();
	}

	@Override
	public void nodeDeleted(V n) {
		deriveTc();
	}
}
