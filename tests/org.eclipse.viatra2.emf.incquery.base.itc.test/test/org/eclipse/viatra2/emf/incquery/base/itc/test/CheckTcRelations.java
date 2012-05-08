package org.eclipse.viatra2.emf.incquery.base.itc.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CheckTcRelations {

	public static Test suite() {
		TestSuite suite = new TestSuite(CheckTcRelations.class.getName());

		suite.addTestSuite(DRedTestCase.class);
		suite.addTestSuite(DFSTestCase.class);
		suite.addTestSuite(CountingTestCase.class);
		suite.addTestSuite(Counting2TestCase.class);
		suite.addTestSuite(KingOptTestCase.class);
		suite.addTestSuite(IncSCCTestCase.class);
		
		return suite;
	}

}
