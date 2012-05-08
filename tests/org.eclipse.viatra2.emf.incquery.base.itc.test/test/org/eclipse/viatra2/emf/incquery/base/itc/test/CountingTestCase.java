package org.eclipse.viatra2.emf.incquery.base.itc.test;

import junit.framework.TestCase;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.counting.CountingAlg;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.TcRelationGenerator;
import org.eclipse.viatra2.emf.incquery.base.itc.graphimpl.Graph;
import org.junit.Test;

public class CountingTestCase extends TestCase {
	
    public CountingTestCase () {
    }
    
    @Test
    public void testResult() {
    	
    	TcRelationGenerator<Integer> gen = null;
		int nodeCount = 4;
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
					
					gen = new TcRelationGenerator<Integer>(g);
					
					assertEquals(gen.getCounting1TcRelation(), ca.getTcRelation());
				}
			}
		}
		
		for (int i = 0;i<nodeCount;i++) {
			for (int j = 0;j<nodeCount;j++) {
				if (i < j) {
					g.deleteEdge(i, j);
					
					gen = new TcRelationGenerator<Integer>(g);
					
					assertEquals(gen.getCounting1TcRelation(), ca.getTcRelation());
				}
			}
		} 
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(CountingTestCase.class);
    }
}
