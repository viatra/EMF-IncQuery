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

package org.eclipse.viatra2.emf.incquery.base.itc.test.kingopt;

import static org.junit.Assert.assertEquals;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.kingopt.KingOptAlg;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.dfs.DFSAlg;
import org.eclipse.viatra2.emf.incquery.base.itc.graphimpl.Graph;
import org.junit.Test;

public class KingOptCompleteGraphTestCase {

	@Test
	public void testResult() {
		int nodeCount = 10;
		Graph<Integer> g = new Graph<Integer>();
		DFSAlg<Integer> da = new DFSAlg<Integer>(g);
		KingOptAlg<Integer> koa = new KingOptAlg<Integer>(g);
		g.detachObserver(koa);
		
		for (int i = 0; i < nodeCount; i++) {
			g.insertNode(i);
		}

		for (int i = 0; i < nodeCount; i++) {
			for (int j = 0; j < nodeCount; j++) {
				if (i != j) {
					g.insertEdge(i, j);
					koa.fullGen();
					assertEquals(koa.getTcRelation(), da.getTcRelation());
				}
			}
		}

		for (int i = 0; i < nodeCount; i++) {
			for (int j = 0; j < nodeCount; j++) {
				if (i != j) {
					g.deleteEdge(i, j);
					koa.fullGen();
					assertEquals(koa.getTcRelation(), da.getTcRelation());
				}
			}
		}
	}
}