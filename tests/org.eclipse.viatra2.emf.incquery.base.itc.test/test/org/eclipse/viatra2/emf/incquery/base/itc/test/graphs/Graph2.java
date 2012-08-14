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

package org.eclipse.viatra2.emf.incquery.base.itc.test.graphs;

import org.eclipse.viatra2.emf.incquery.base.itc.graphimpl.Graph;

public class Graph2 extends Graph<Integer> {

	private static final long serialVersionUID = -970373253476554580L;
	
	public void modify() {
		Integer n1 = Integer.valueOf(1);
		Integer n2 = Integer.valueOf(2);
		Integer n3 = Integer.valueOf(3);
		Integer n4 = Integer.valueOf(4);
		Integer n5 = Integer.valueOf(5);
		Integer n6 = Integer.valueOf(6);
			
		this.insertNode(n1);
		this.insertNode(n2);
		this.insertNode(n3);
		this.insertNode(n4);
		this.insertNode(n5);
		this.insertNode(n6);
		
		this.insertEdge(n1, n2);
		this.insertEdge(n2, n6);
		this.insertEdge(n1, n6);
		this.insertEdge(n2, n3);
		this.insertEdge(n3, n4);
		this.insertEdge(n4, n5);
		this.insertEdge(n5, n3);
		
		this.deleteEdge(n2, n3);
	}
}
