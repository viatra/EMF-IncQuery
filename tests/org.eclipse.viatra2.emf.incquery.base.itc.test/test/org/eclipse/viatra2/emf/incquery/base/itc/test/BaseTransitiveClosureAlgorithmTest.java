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

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.viatra2.emf.incquery.base.itc.test.graphs.SelfLoopGraph;
import org.eclipse.viatra2.emf.incquery.base.itc.test.graphs.Graph1;
import org.eclipse.viatra2.emf.incquery.base.itc.test.graphs.Graph2;
import org.eclipse.viatra2.emf.incquery.base.itc.test.graphs.Graph3;
import org.eclipse.viatra2.emf.incquery.base.itc.test.graphs.Graph4;
import org.junit.runners.Parameterized.Parameters;

public class BaseTransitiveClosureAlgorithmTest {

	@Parameters
	public static Collection<Object[]> getGraphs() {
		return Arrays.asList(new Object[][] {
		                 { new SelfLoopGraph()},
			             { new Graph1() },
			             { new Graph2() },
			             { new Graph3() },
			             { new Graph4() }
		});
	}
	
}
