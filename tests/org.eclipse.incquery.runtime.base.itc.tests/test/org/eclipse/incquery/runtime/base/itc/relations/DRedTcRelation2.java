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

import org.eclipse.incquery.runtime.base.itc.alg.dred.DRedTcRelation;

public class DRedTcRelation2 extends DRedTcRelation<Integer>{

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
