package org.eclipse.viatra2.emf.incquery.base.itc.test.relations;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.counting.TcRelation;

public class CountingTcRelation3 extends TcRelation<Integer> {
	
	public CountingTcRelation3() {
		super(true);
		this.addTuple(1, 2, 1);
    	this.addTuple(1, 3, 1);
    	this.addTuple(1, 4, 1);

    	this.addTuple(2, 3, 1);
    	this.addTuple(2, 4, 1);

    	this.addTuple(4, 3, 1);

    	this.addTuple(5, 6, 1);
	}
}
