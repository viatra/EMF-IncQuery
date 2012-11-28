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

package org.eclipse.incquery.runtime.base.itc.counting;

import static org.junit.Assert.assertEquals;

import org.eclipse.incquery.runtime.base.itc.alg.counting.CountingAlg;
import org.eclipse.incquery.runtime.base.itc.alg.counting.CountingTcRelation;
import org.eclipse.incquery.runtime.base.itc.graphimpl.Graph;
import org.junit.Test;

public class CountingCompleteGraphTestCase {
    
    @Test
    public void testResult() {
		int nodeCount = 10;
		Graph<Integer> g = new Graph<Integer>();
		CountingAlg<Integer> ca = new CountingAlg<Integer>(g);
		
		for (int i = 0;i<nodeCount;i++) {
			g.insertNode(i);
		}
		
		//inserting edges
		for (int i = 0;i<nodeCount;i++) {
			for (int j = 0;j<nodeCount;j++) {
				if (i < j) {
					g.insertEdge(i, j);		
					assertEquals(CountingTcRelation.createFrom(g), ca.getTcRelation());
				}
			}
		}
		
		for (int i = 0;i<nodeCount;i++) {
			for (int j = 0;j<nodeCount;j++) {
				if (i < j) {
					g.deleteEdge(i, j);								
					assertEquals(CountingTcRelation.createFrom(g), ca.getTcRelation());
				}
			}
		} 
    }

}
