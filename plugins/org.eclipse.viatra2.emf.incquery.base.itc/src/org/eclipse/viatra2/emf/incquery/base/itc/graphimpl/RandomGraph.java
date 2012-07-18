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
import java.util.List;

public class RandomGraph extends Graph<Integer> {

	private static final long serialVersionUID = -8888373780155316788L;
	private List<Integer> numbers = null;
	private int K;
	private int nodeCount;
	
	public RandomGraph(int nodeCount, int K) {
		this.nodeCount = nodeCount;
		this.K = K;
		
	}
	
	public void buildGraph() {
		numbers = new ArrayList<Integer>();
		
		for (int i = 0;i< nodeCount;i++) {
			numbers.add(i);
			this.insertNode(i);
		}
		
		for (int i = 0;i< nodeCount;i++) {
			numbers.remove(i);
			
			Collections.shuffle(numbers);
			
			for (int j=0;j<K;j++) {
				this.insertEdge(i, numbers.get(j));
			}
			
			numbers.add(i);
		}
	}

	@Override
	public Integer[] deleteRandomEdge() {
		Integer[] r = new Integer[2];
		Collections.shuffle(numbers);
		r[0] = numbers.get(0);
		for (int delT : this.getTargetNodes(r[0])) {
			r[1] = delT;
			break;
		}
		return r;
	}
	
	@Override
	public Integer[] insertRandomEdge() {
		Integer[] r = new Integer[2];
		Collections.shuffle(numbers);
		r[0] = numbers.get(0);
		r[1] = numbers.get(1);
		return r;
	}
	
}
