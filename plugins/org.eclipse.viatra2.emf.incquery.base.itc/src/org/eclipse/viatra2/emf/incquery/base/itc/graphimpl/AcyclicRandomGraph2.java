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

package org.eclipse.viatra2.emf.incquery.base.itc.graphimpl;

import java.util.ArrayList;
import java.util.Collections;

public class AcyclicRandomGraph2 extends Graph<Integer> {

	private static final long serialVersionUID = 8652340594254839785L;
	private ArrayList<Integer> nodes;
	private int nodeCount;
	
	public AcyclicRandomGraph2(int nodeCount) {
		this.nodeCount = nodeCount;
	}
	
	public void buildGraph() {
		
		nodes = new ArrayList<Integer>();
		int s = 0;
		int eInserted = 0;
		
		for (int i=0;i<nodeCount;i++) {
			this.insertNode(i);
			if (i > 0) {
				Collections.shuffle(nodes);
				s = nodes.get(0);
				this.insertEdge(s, i);
				eInserted++;
			}
			nodes.add(i);
		}
		
		//System.out.println("Edges inserted: "+eInserted);
	}

	@Override
	public Integer[] deleteRandomEdge() {
		Integer[] r = new Integer[2];
		Collections.shuffle(nodes);
		r[0] = nodes.get(0);
		for (int delT : this.getTargetNodes(r[0])) {
			r[1] = delT;
			break;
		}
		return r;
	}
	
	@Override
	public Integer[] insertRandomEdge() {
		Integer[] r = new Integer[2];
		int i1, i2;
		Collections.shuffle(nodes);
		i1 = nodes.get(0);
		i2 = nodes.get(1);
		
		if (i1 < i2) {
			r[0] = i1;
			r[1] = i2;
		}
		else {
			r[0] = i2;
			r[1] = i1;
		}
		return r;
	}
}
