package org.eclipse.viatra2.emf.incquery.base.itc.test.graphs;

import org.eclipse.viatra2.emf.incquery.base.itc.graphimpl.Graph;

public class Graph1 extends Graph<Integer> {

	private static final long serialVersionUID = -3807323812221410872L;

	public Graph1() {
    	
	}
	
	public void modify() {
		Integer n1 = new Integer(1);
		Integer n2 = new Integer(2);
		Integer n3 = new Integer(3);
		Integer n4 = new Integer(4);
			
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
