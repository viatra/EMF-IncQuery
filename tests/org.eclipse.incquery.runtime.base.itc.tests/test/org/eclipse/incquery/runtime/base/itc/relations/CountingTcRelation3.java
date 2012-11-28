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

package org.eclipse.incquery.runtime.base.itc.relations;

import org.eclipse.incquery.runtime.base.itc.alg.counting.CountingTcRelation;

public class CountingTcRelation3 extends CountingTcRelation<Integer> {
	
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
