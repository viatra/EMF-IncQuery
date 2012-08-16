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

import org.eclipse.viatra2.emf.incquery.base.itc.test.counting.CountingCompleteGraphTestCase;
import org.eclipse.viatra2.emf.incquery.base.itc.test.counting2.Counting2CompleteGraphTestCase;
import org.eclipse.viatra2.emf.incquery.base.itc.test.dfs.DFSCompleteGraphTestCase;
import org.eclipse.viatra2.emf.incquery.base.itc.test.dred.DRedCompleteGraphTestCase;
import org.eclipse.viatra2.emf.incquery.base.itc.test.dred.DRedGraphsTestCase;
import org.eclipse.viatra2.emf.incquery.base.itc.test.incscc.IncSCCCompleteGraphTestCase;
import org.eclipse.viatra2.emf.incquery.base.itc.test.incscc.IncSCCGraphsTestCase;
import org.eclipse.viatra2.emf.incquery.base.itc.test.kingopt.KingOptCompleteGraphTestCase;
import org.eclipse.viatra2.emf.incquery.base.itc.test.kingopt.KingOptGraphsTestCase;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
		DRedGraphsTestCase.class,
		DRedCompleteGraphTestCase.class, 
		DFSCompleteGraphTestCase.class, 
		CountingCompleteGraphTestCase.class,
		Counting2CompleteGraphTestCase.class, 
		KingOptGraphsTestCase.class,
		KingOptCompleteGraphTestCase.class,
		//KingGraphsTestCase.class,
		//KingCompleteGraphTestCase.class,
		IncSCCGraphsTestCase.class,
		IncSCCCompleteGraphTestCase.class
})
public class TransitiveClosureAlgorithmTestSuite {

}
