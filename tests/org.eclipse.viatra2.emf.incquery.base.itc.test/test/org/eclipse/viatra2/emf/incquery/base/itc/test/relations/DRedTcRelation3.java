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
