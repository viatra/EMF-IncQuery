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

package org.eclipse.incquery.runtime.base.itc;

import org.eclipse.incquery.runtime.base.itc.counting.CountingCompleteGraphTestCase;
import org.eclipse.incquery.runtime.base.itc.dfs.DFSCompleteGraphTestCase;
import org.eclipse.incquery.runtime.base.itc.dred.DRedCompleteGraphTestCase;
import org.eclipse.incquery.runtime.base.itc.dred.DRedGraphsTestCase;
import org.eclipse.incquery.runtime.base.itc.incscc.IncSCCCompleteGraphTestCase;
import org.eclipse.incquery.runtime.base.itc.incscc.IncSCCGraphsTestCase;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
		DRedGraphsTestCase.class,
		DRedCompleteGraphTestCase.class, 
		DFSCompleteGraphTestCase.class, 
		CountingCompleteGraphTestCase.class,
		IncSCCGraphsTestCase.class,
		IncSCCCompleteGraphTestCase.class
})
public class TransitiveClosureAlgorithmTestSuite {

}
