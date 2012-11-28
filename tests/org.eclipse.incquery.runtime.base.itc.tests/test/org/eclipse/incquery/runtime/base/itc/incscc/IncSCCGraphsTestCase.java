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

package org.eclipse.incquery.runtime.base.itc.incscc;

import static org.junit.Assert.assertTrue;

import org.eclipse.incquery.runtime.base.itc.BaseTransitiveClosureAlgorithmTest;
import org.eclipse.incquery.runtime.base.itc.alg.fw.FloydWarshallAlg;
import org.eclipse.incquery.runtime.base.itc.alg.incscc.IncSCCAlg;
import org.eclipse.incquery.runtime.base.itc.graphs.TestGraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class IncSCCGraphsTestCase extends BaseTransitiveClosureAlgorithmTest {

	protected TestGraph<Integer> testGraph;
	
	public IncSCCGraphsTestCase(TestGraph<Integer> testGraph) {
		this.testGraph = testGraph;
	}
	
	@Test
	public void testResult() {
    	FloydWarshallAlg<Integer> fwa = new FloydWarshallAlg<Integer>(testGraph);
    	IncSCCAlg<Integer> alg = new IncSCCAlg<Integer>(testGraph);
    	if (testGraph.getObserver() != null) {
    		alg.attachObserver(testGraph.getObserver());
    	}
		testGraph.modify();	
		assertTrue(alg.checkTcRelation(fwa.getTcRelation()));
	}
}