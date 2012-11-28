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

package org.eclipse.incquery.runtime.base.itc.graphs;

import org.eclipse.incquery.runtime.base.itc.alg.misc.Tuple;
import org.eclipse.incquery.runtime.base.itc.misc.TestObserver;


public class Graph7 extends TestGraph<Integer> {

	private static final long serialVersionUID = -3807323812221410872L;
	
	public Graph7() {
		super(new TestObserver<Integer>());
	}
	
	public void modify() {
		Integer n1 = Integer.valueOf(1);
		Integer n2 = Integer.valueOf(2);
		Integer n3 = Integer.valueOf(3);
		
		this.insertNode(n1);
		this.insertNode(n2);
		this.insertNode(n3);

		this.observer.clearTuples();
		this.observer.addInsertedTuple(new Tuple<Integer>(n1, n2));
		this.insertEdge(n1, n2);
		
		this.observer.clearTuples();
		this.observer.addInsertedTuple(new Tuple<Integer>(n2, n1));
		this.observer.addInsertedTuple(new Tuple<Integer>(n1, n1));
		this.observer.addInsertedTuple(new Tuple<Integer>(n2, n2));
		this.insertEdge(n2, n1);
		
		this.observer.clearTuples();
		this.observer.addInsertedTuple(new Tuple<Integer>(n3, n1));
		this.observer.addInsertedTuple(new Tuple<Integer>(n3, n2));
		this.insertEdge(n3, n2);

		this.observer.clearTuples();
		this.observer.addInsertedTuple(new Tuple<Integer>(n1, n3));
		this.observer.addInsertedTuple(new Tuple<Integer>(n2, n3));
		this.observer.addInsertedTuple(new Tuple<Integer>(n3, n3));
		this.insertEdge(n2, n3);
		
		this.observer.clearTuples();
		this.observer.addDeletedTuple(new Tuple<Integer>(n1, n1));
		this.observer.addDeletedTuple(new Tuple<Integer>(n1, n2));
		this.observer.addDeletedTuple(new Tuple<Integer>(n1, n3));
		this.deleteEdge(n1, n2);
		
	}
}
