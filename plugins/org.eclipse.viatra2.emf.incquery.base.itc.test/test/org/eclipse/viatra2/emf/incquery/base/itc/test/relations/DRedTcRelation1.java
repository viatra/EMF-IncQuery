package org.eclipse.viatra2.emf.incquery.base.itc.test.relations;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.dred.TcRelation;

public class DRedTcRelation1 extends TcRelation<Integer>{

	private static final long serialVersionUID = -9211874694848138868L;
	
	public DRedTcRelation1() {
		this.addTuple(1, 2);
    	this.addTuple(1, 3);
    	this.addTuple(1, 4);
    	
    	this.addTuple(2, 1);
    	this.addTuple(2, 3);
    	this.addTuple(2, 4);
    	
    	this.addTuple(4, 1);
    	this.addTuple(4, 2);
    	this.addTuple(4, 3);
	}
}
