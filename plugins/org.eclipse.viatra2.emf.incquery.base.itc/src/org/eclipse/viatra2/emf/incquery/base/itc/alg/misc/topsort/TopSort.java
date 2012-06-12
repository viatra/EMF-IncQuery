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

package org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.topsort;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphDataSource;

public class TopSort<V> {

	private IGraphDataSource<V> gds;
	private HashMap<Integer, V> forwardNodeMap;
	private HashMap<V, Integer> backwardNodeMap;
	private int nodeCount;
	
	private int[] visited;
	private int[] finishNumber;
	private int[] depthNumber;
	private int[] sourceNumber;		
	
	private int depthCount = 0;
	private int finishCount = 0;
	
	private List<V> topologicalSorting = null;
	
	public TopSort(IGraphDataSource<V> g) {
		this.gds = g;
		nodeCount = gds.getAllNodes().size();
		
		this.topologicalSorting = new ArrayList<V>();
		
		visited = new int[nodeCount];
		finishNumber = new int[nodeCount];
		depthNumber = new int[nodeCount];
		sourceNumber = new int[nodeCount];	
		
		forwardNodeMap = new HashMap<Integer, V>();
		backwardNodeMap = new HashMap<V, Integer>();
		
		int j = 0;
		for (V n : gds.getAllNodes()) {
			forwardNodeMap.put(j, n);
			backwardNodeMap.put(n, j);
			j++;
		}
		
		for (int i=0;i<nodeCount;i++) {
			visited[i] = 0;
			finishNumber[i] = -1;
			depthNumber[i] = -1;
			sourceNumber[i] = -1;
		}
	}
	
	public void doDFS() {
		for (int i=0;i<nodeCount;i++) {
			if (visited[i] == 0) oneDFS(i);
		}
		
		Collections.reverse(topologicalSorting);
	}
	
	private void oneDFS(int v) {
		visited[v] = 1;
		depthNumber[v]=++depthCount;
		List<V> targets = gds.getTargetNodes(forwardNodeMap.get(v));
		if (targets != null) {
			for (V t : targets) {
				
				int u = backwardNodeMap.get(t);
				sourceNumber[u] = v;
				if (visited[u] == 0) oneDFS(u);
			}
		}
		finishNumber[v] = ++finishCount;
		topologicalSorting.add(forwardNodeMap.get(v));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<?> getTopologicalSorting(IGraphDataSource<?> g) {
		TopSort da = new TopSort(g);
		da.doDFS();
		return da.topologicalSorting;
	}
}

