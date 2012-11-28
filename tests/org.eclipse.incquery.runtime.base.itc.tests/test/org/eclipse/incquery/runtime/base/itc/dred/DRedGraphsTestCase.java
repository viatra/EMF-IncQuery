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

package org.eclipse.incquery.runtime.base.itc.dred;

import static org.junit.Assert.assertEquals;

import org.eclipse.incquery.runtime.base.itc.BaseTransitiveClosureAlgorithmTest;
import org.eclipse.incquery.runtime.base.itc.alg.dred.DRedAlg;
import org.eclipse.incquery.runtime.base.itc.alg.fw.FloydWarshallAlg;
import org.eclipse.incquery.runtime.base.itc.graphs.Graph4;
import org.eclipse.incquery.runtime.base.itc.graphs.TestGraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class DRedGraphsTestCase extends BaseTransitiveClosureAlgorithmTest {

	private TestGraph<Integer> testGraph;
	
	public DRedGraphsTestCase(TestGraph<Integer> testGraph) {
		this.testGraph = testGraph;
	}
	
	@Test
	public void testResult() {
		if (testGraph instanceof Graph4) {
	    	FloydWarshallAlg<Integer> fwa = new FloydWarshallAlg<Integer>(testGraph);
	    	DRedAlg<Integer> da = new DRedAlg<Integer>(testGraph);
	    	testGraph.modify();	
	        assertEquals(da.getTcRelation(), fwa.getTcRelation());
		}
	}
}
