package org.eclipse.viatra2.emf.incquery.base.itc.test;

import junit.framework.TestCase;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.fw.FloydWarshallAlg;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.king.KingAlg;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.dfs.DFSAlg;
import org.eclipse.viatra2.emf.incquery.base.itc.graphimpl.Graph;
import org.eclipse.viatra2.emf.incquery.base.itc.test.graphs.Graph1;
import org.eclipse.viatra2.emf.incquery.base.itc.test.graphs.Graph2;
import org.eclipse.viatra2.emf.incquery.base.itc.test.graphs.Graph3;
import org.junit.Test;

public class KingTestCase extends TestCase {
    public KingTestCase () {
    }
    
    @Test
    public void testResult() {
    	
    	Graph1 g1 = new Graph1();
    	FloydWarshallAlg<Integer> fwa = new FloydWarshallAlg<Integer>(g1);
    	KingAlg<Integer> ka = new KingAlg<Integer>(g1);
		g1.modify();	
    	
        assertEquals(ka.getMergedRelation(), fwa.getTcRelation());
        
        Graph2 g2 = new Graph2();
    	fwa = new FloydWarshallAlg<Integer>(g2);
    	ka = new KingAlg<Integer>(g2);
		g2.modify();	
    	
		assertEquals(ka.getMergedRelation(), fwa.getTcRelation());
		
        Graph3 g3 = new Graph3();
    	fwa = new FloydWarshallAlg<Integer>(g3);
    	ka = new KingAlg<Integer>(g3);
		g3.modify();	
    	
		assertEquals(ka.getMergedRelation(), fwa.getTcRelation());
		   	
		int nodeCount = 8;
		Graph<Integer> g = new Graph<Integer>();
		DFSAlg<Integer> da = new DFSAlg<Integer>(g);
		ka = new KingAlg<Integer>(g);

		for (int i = 0; i < nodeCount; i++) {
			g.insertNode(i);
		}

		System.out.println("insert");
		for (int i = 0; i < nodeCount; i++) {
			for (int j = 0; j < nodeCount; j++) {
				if (i != j) {
					g.insertEdge(i, j);
					
					assertEquals(da.getTcRelation(), ka.getMergedRelation());
				}
			}
		}

		System.out.println("delete");
		for (int i = 0; i < nodeCount; i++) {
			for (int j = 0; j < nodeCount; j++) {
				if (i != j) {
					g.deleteEdge(i, j);

					assertEquals(da.getTcRelation(), ka.getMergedRelation());
				}
			}
		}
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(KingTestCase.class);
    }
}
 
