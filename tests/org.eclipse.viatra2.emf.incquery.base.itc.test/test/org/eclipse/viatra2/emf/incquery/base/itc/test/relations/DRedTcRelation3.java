package org.eclipse.viatra2.emf.incquery.base.itc.test.relations;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.dred.TcRelation;

public class DRedTcRelation3 extends TcRelation<Integer>{

	private static final long serialVersionUID = -9211874694848138868L;
	
	public DRedTcRelation3() {
		this.addTuple(1, 2);
    	this.addTuple(1, 3);
    	this.addTuple(1, 4);

    	this.addTuple(2, 3);
    	this.addTuple(2, 4);

    	this.addTuple(4, 3);

    	this.addTuple(5, 6);
	}
}
