package org.eclipse.viatra2.emf.incquery.base.itc.test.relations;

import java.math.BigInteger;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.counting2.TcRelation;


public class Counting2TcRelation3 extends TcRelation<Integer> {
	
	public Counting2TcRelation3() {
		BigInteger one = BigInteger.valueOf(1);
		
		this.addTuple(1, 2, one);
    	this.addTuple(1, 3, one);
    	this.addTuple(1, 4, one);

    	this.addTuple(2, 3, one);
    	this.addTuple(2, 4, one);

    	this.addTuple(4, 3, one);

    	this.addTuple(5, 6, one);
	}
}
