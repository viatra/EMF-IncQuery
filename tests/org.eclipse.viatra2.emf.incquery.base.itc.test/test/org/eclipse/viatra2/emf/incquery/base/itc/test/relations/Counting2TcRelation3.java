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
