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

package org.eclipse.viatra2.emf.incquery.base.itc.graphimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IBiDirectionalGraphDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphObserver;

public class Graph<V> implements IGraphDataSource<V>, IBiDirectionalGraphDataSource<V> {

	private static final long serialVersionUID = 1L;
	private Map<V,List<V>> edgeList;
	private Map<V,List<V>> edgeListReversed;
	private ArrayList<IGraphObserver<V>> observers;
	
	public Graph() {
		this.edgeList = new HashMap<V, List<V>>();
		this.edgeListReversed = new HashMap<V, List<V>>();
		this.observers = new ArrayList<IGraphObserver<V>>();
	}
	
	public void insertEdge(V source, V target) {
		//insert nodes if necessary
//		if (!edgeList.containsKey(source)) {
//			this.insertNode(source);
//		}
//		if (!edgeList.containsKey(target)) {
//			this.insertNode(target);
//		}
		
		List<V> outgoingEdges = edgeList.get(source);
		if (outgoingEdges == null) {
			outgoingEdges = new ArrayList<V>();
			outgoingEdges.add(target);
			edgeList.put(source, outgoingEdges);
		}
		else {
			outgoingEdges.add(target);
		}
		
		List<V> incomingEdges = edgeListReversed.get(target);
		if (incomingEdges == null) {
			incomingEdges = new ArrayList<V>();
			incomingEdges.add(source);
			edgeListReversed.put(target, incomingEdges);
		}
		else {
			incomingEdges.add(source);
		}

		for (IGraphObserver<V> go : this.observers) {
			go.edgeInserted(source, target);
		}
	}
	
	public void deleteEdge(V source, V target) {
		
		boolean containedEdge = false;
		List<V> incomingEdges = edgeListReversed.get(target);
		if (incomingEdges != null) {
			if (incomingEdges.remove(source)) {
				containedEdge = true;
			}
		}
		
		List<V> outgoingEdges = edgeList.get(source);
		if (outgoingEdges != null) {
			outgoingEdges.remove(target);
		}
		
		if (containedEdge) {
			for (IGraphObserver<V> go : this.observers) {
				go.edgeDeleted(source, target);
			}
		}
	}
	
	public void insertNode(V node) {
		if (!edgeList.containsKey(node)) {
			this.edgeList.put(node, null);
		}
		if (!edgeListReversed.containsKey(node)) {
			this.edgeListReversed.put(node, null);
		}
		
		for (IGraphObserver<V> go : this.observers) {
			go.nodeInserted(node);
		}
	}
	
	public void deleteNode(V node) {
		boolean containedNode = edgeList.containsKey(node);
		List<V> incomingEdges = edgeListReversed.get(node);
		List<V> outgoingEdges = edgeList.get(node);
		
		if (incomingEdges != null) {
			List<V> tmp = new ArrayList<V>(incomingEdges);
			
			for (V s : tmp) {
				this.deleteEdge(s, node);
			}
		}
		if (outgoingEdges != null) {
			List<V> tmp = new ArrayList<V>(outgoingEdges);
			
			for (V t : tmp) {
				this.deleteEdge(node, t);
			}
		}
		
		if (containedNode) {
			for (IGraphObserver<V> go : this.observers) {
				go.nodeDeleted(node);
			}
		}
	}
	
	public void attachObserver(IGraphObserver<V> go) {
		this.observers.add(go);
	}
	
	public void detachObserver(IGraphObserver<V> go) {
		this.observers.remove(go);
	}

	@Override
	public Set<V> getAllNodes() {
		return this.edgeList.keySet();
	}

	@Override
	public List<V> getTargetNodes(V source) {
		return this.edgeList.get(source);
	}
	
	@Override
	public List<V> getSourceNodes(V target) {
		return this.edgeListReversed.get(target);
	}
	
	@Override
	public String toString() {
		String s = "nodes = ";
		for (V n : this.edgeList.keySet()) {
			s += n.toString()+" ";
		}
		s += " edges = ";
		for (V source : this.edgeList.keySet()) {
			if (edgeList.get(source) != null) {
				for (V target : this.edgeList.get(source)) {
					s += "("+source+","+target+") ";
				}
			}
		}
		return s;
	}

	public Integer[] deleteRandomEdge() {
		return null;
	}

	public Integer[] insertRandomEdge() {
		return null;
	}



}
