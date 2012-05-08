package org.eclipse.viatra2.emf.incquery.base.itc.test;

import junit.framework.TestCase;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.fw.FloydWarshallAlg;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.dfs.DFSAlg;
import org.eclipse.viatra2.emf.incquery.base.itc.graphimpl.Graph;
import org.junit.Test;

public class DFSTestCase extends TestCase {
    public DFSTestCase () {
    }
    
    @Test
    public void testResult() {
    	
    	FloydWarshallAlg<Integer> fwa = null;
		int nodeCount = 10;
		Graph<Integer> g = new Graph<Integer>();
		DFSAlg<Integer> da = new DFSAlg<Integer>(g);

		for (int i = 0; i < nodeCount; i++) {
			g.insertNode(i);
		}

		// inserting edges
		for (int i = 0; i < nodeCount; i++) {
			for (int j = 0; j < nodeCount; j++) {
				if (i != j) {
					g.insertEdge(i, j);

					fwa = new FloydWarshallAlg<Integer>(g);

					assertEquals(da.getTcRelation(), fwa.getTcRelation());
				}
			}
		}

		for (int i = 0; i < nodeCount; i++) {
			for (int j = 0; j < nodeCount; j++) {
				if (i != j) {
					g.deleteEdge(i, j);

					fwa = new FloydWarshallAlg<Integer>(g);

					assertEquals(da.getTcRelation(), fwa.getTcRelation());
				}
			}
		}
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(DFSTestCase.class);
    }
}
 
