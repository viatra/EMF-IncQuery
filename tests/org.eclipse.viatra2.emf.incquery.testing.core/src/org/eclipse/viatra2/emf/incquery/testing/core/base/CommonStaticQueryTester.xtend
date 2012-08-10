package org.eclipse.viatra2.emf.incquery.testing.core.base

import org.eclipse.viatra2.emf.incquery.testing.core.ModelLoadHelper
import com.google.inject.Inject
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.IncQuerySnapshot
import org.eclipse.viatra2.emf.incquery.testing.core.SnapshotHelper
import org.eclipse.viatra2.emf.incquery.testing.core.TestExecutor
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory

import static org.junit.Assert.*

abstract class CommonStaticQueryTester {
	@Inject extension ModelLoadHelper
	@Inject extension TestExecutor
	@Inject extension SnapshotHelper
		
	def testQuery(String queryFQN){
		val sns = snapshot
		val matcher = queryInputXMI.initializeMatcherFromModel(sns.EMFRootForSnapshot, queryFQN)
		val results = matcher.compareResultSets(sns.getMatchSetRecordForPattern(queryFQN))
		assertArrayEquals(newHashSet,results)
	}
	
	def testQuery(IMatcherFactory queryMF){
		val sns = snapshot
		val matcher = queryMF.getMatcher(sns.EMFRootForSnapshot)//queryInputXMI.initializeMatcherFromModel(sns.EMFRootForSnapshot, queryFQN)
		val results = matcher.compareResultSets(sns.getMatchSetRecordForPattern(queryMF.patternFullyQualifiedName))
		assertArrayEquals(newHashSet,results)
	}
		
	def snapshot() { // Creates new resource set
		return snapshotURI.loadExpectedResultsFromUri as IncQuerySnapshot
	}
	def queryInputXMI() { // Creates new resource set
		return queryInputXMIURI.loadPatternModelFromUri as PatternModel
	}
	
	def String snapshotURI() // abstract
	def String queryInputXMIURI() // abstract
}