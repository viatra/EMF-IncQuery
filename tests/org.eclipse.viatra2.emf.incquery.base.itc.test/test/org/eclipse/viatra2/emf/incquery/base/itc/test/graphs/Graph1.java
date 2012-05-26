package org.eclipse.viatra2.emf.incquery.base.itc.test.graphs;

import org.eclipse.viatra2.emf.incquery.base.itc.graphimpl.Graph;

public class Graph1 extends Graph<Integer> {

	private static final long serialVersionUID = -3807323812221410872L;
	
	public void modify() {
		Integer n1 = Integer.valueOf(1);
		Integer n2 = Integer.valueOf(2);
		Integer n3 = Integer.valueOf(3);
		Integer n4 = Integer.valueOf(4);
			
		this.insertNode(n1);
		this.insertNode(n2);
		this.insertNode(n3);
		this.insertNode(n4);
		
		this.insertEdge(n1, n2);
		this.insertEdge(n2, n3);
		this.insertEdge(n3, n4);
		this.insertEdge(n4, n1);
		this.insertEdge(n1, n3);
		this.insertEdge(n2, n4);
		
		this.deleteEdge(n3, n4);
	}
}
