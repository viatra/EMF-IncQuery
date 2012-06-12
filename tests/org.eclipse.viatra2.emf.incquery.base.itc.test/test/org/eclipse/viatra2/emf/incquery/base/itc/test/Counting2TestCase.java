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

package org.eclipse.viatra2.emf.incquery.base.itc.test;

import junit.framework.TestCase;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.counting2.CountingAlg2;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.TcRelationGenerator;
import org.eclipse.viatra2.emf.incquery.base.itc.graphimpl.Graph;
import org.junit.Test;

public class Counting2TestCase extends TestCase {

	public Counting2TestCase() {
	}

	@Test
	public void testResult() {

		TcRelationGenerator<Integer> gen = null;
		int nodeCount = 10;
		Graph<Integer> g = new Graph<Integer>();
		CountingAlg2<Integer> ca = new CountingAlg2<Integer>(g);

		for (int i = 0; i < nodeCount; i++) {
			g.insertNode(i);
		}

		// inserting edges
		for (int i = 0; i < nodeCount; i++) {
			for (int j = 0; j < nodeCount; j++) {
				if (i < j) {
					g.insertEdge(i, j);

					gen = new TcRelationGenerator<Integer>(g);

					assertEquals(gen.getCounting2TcRelation(),
							ca.getTcRelation());
				}
			}
		}

		for (int i = 0; i < nodeCount; i++) {
			for (int j = 0; j < nodeCount; j++) {
				if (i < j) {
					g.deleteEdge(i, j);

					gen = new TcRelationGenerator<Integer>(g);

					assertEquals(gen.getCounting2TcRelation(),
							ca.getTcRelation());
				}
			}
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(Counting2TestCase.class);
	}
}