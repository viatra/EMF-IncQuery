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

package org.eclipse.viatra2.emf.incquery.base.itc.test.king;

import static org.junit.Assert.assertEquals;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.fw.FloydWarshallAlg;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.king.KingAlg;
import org.eclipse.viatra2.emf.incquery.base.itc.test.BaseTransitiveClosureAlgorithmTest;
import org.eclipse.viatra2.emf.incquery.base.itc.test.graphs.TestGraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class KingGraphsTestCase extends BaseTransitiveClosureAlgorithmTest {
    
	private TestGraph<Integer> testGraph;
	
	public KingGraphsTestCase(TestGraph<Integer> testGraph) {
		this.testGraph = testGraph;
	}
	
    @Test
    public void testResult() {
    	FloydWarshallAlg<Integer> fwa = new FloydWarshallAlg<Integer>(testGraph);
    	KingAlg<Integer> ka = new KingAlg<Integer>(testGraph);
    	testGraph.modify();	
        assertEquals(fwa.getTcRelation(), ka.getMergedRelation());
    }
}
 
