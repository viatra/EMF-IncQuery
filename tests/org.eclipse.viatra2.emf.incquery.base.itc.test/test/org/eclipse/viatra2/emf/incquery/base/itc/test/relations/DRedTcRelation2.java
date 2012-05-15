package org.eclipse.viatra2.emf.incquery.base.itc.test.relations;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.dred.TcRelation;

public class DRedTcRelation2 extends TcRelation<Integer>{

	private static final long serialVersionUID = -9211874694848138868L;
	
	public DRedTcRelation2() {
		this.addTuple(1, 2);
    	this.addTuple(1, 6);
    	
    	this.addTuple(2, 6);
    	
    	this.addTuple(3, 4);
    	this.addTuple(3, 5);
    	
    	this.addTuple(4, 5);
    	this.addTuple(4, 3);
    	
    	this.addTuple(5, 3);
    	this.addTuple(5, 4);
	}
}
