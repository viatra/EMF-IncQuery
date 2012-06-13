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

public class AcyclicRandomGraph extends Graph<Integer> {

	private static final long serialVersionUID = 8652340594254839785L;
	private ArrayList<ArrayList<Integer>> numbers;
	private int L;
	private int K;
	private int M;
	private int nodeCount;
	
	public AcyclicRandomGraph(int M, int K, int L) {
		this.nodeCount = M * L;
		this.K = K;
		this.L = L;
		this.M = M;
	}
	
	public void buildGraph() {
		numbers = new ArrayList<ArrayList<Integer>>(L);
		int s = 0;
		int t = 0;
		int eInserted = 0;
		
		for (int i=0;i<L;i++)
			numbers.add(new ArrayList<Integer>());
		
		for (int i = 0;i< nodeCount;i++) {
			numbers.get((int) i/M).add(i);
			this.insertNode(i);
		}
		
		for (int i = 0;i<L-1;i++) {
			for (int j=0;j<M;j++) {
				//from
				s = numbers.get(i).get(j);
				
				for (int l=0;l<K;l++) {
					t = numbers.get(i+1).get(l);
					this.insertEdge(s, t);
					eInserted++;
				}
			}
		}
		
		//System.out.println("Edges inserted: "+eInserted);
	}

	@Override
	public Integer[] deleteRandomEdge() {
		Integer[] r = new Integer[2];
		int r1 = (int) (Math.random()*L - 1);
		Collections.shuffle(numbers.get(r1));
		r[0] = numbers.get(r1).get(0);
		
		for (int delT : this.getTargetNodes(r[0])) {
			r[1] = delT;
			break;
		}
		
		return r;
	}

	@Override
	public Integer[] insertRandomEdge() {
		Integer[] r = new Integer[2];
		int r1 = (int) (Math.random()*L - 1);
		Collections.shuffle(numbers.get(r1));
		Collections.shuffle(numbers.get(r1+1));
		
		r[0] = numbers.get(r1).get(0);
		r[1] = numbers.get(r1+1).get(0);
		
		return r;
	}
	
	
}
