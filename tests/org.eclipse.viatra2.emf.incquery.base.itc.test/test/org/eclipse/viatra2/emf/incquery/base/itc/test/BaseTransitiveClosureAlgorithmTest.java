package org.eclipse.viatra2.emf.incquery.base.itc.test;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.viatra2.emf.incquery.base.itc.test.graphs.Graph1;
import org.eclipse.viatra2.emf.incquery.base.itc.test.graphs.Graph2;
import org.eclipse.viatra2.emf.incquery.base.itc.test.graphs.Graph3;
import org.eclipse.viatra2.emf.incquery.base.itc.test.graphs.Graph4;
import org.junit.runners.Parameterized.Parameters;

public class BaseTransitiveClosureAlgorithmTest {

	@Parameters
	public static Collection<Object[]> getGraphs() {
		return Arrays.asList(new Object[][] {
			             { new Graph1() },
			             { new Graph2() },
			             { new Graph3() },
			             { new Graph4() }
		});
	}
	
}
