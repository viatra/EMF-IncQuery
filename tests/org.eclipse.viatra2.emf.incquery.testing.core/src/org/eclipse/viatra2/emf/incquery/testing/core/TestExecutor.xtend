/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.testing.core

import com.google.inject.Inject
import java.util.Set
import org.eclipse.viatra2.emf.incquery.runtime.api.EngineManager
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.IncQuerySnapshot
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord
import org.eclipse.viatra2.emf.incquery.testing.queries.unexpectedmatchrecord.UnexpectedMatchRecordMatcher
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel

import static org.eclipse.viatra2.emf.incquery.testing.core.TestExecutor.*
import static org.junit.Assert.*

/**
 * Primitive methods for executing a functional test for EMF-IncQuery.
 */
class TestExecutor {
	
	public static String CORRECTRESULTS = "Correct result set"
	public static String CORRECT_SINGLE = "Correct single match for parameterless pattern"
	public static String CORRECT_EMPTY = "Correct empty match set"
	public static String UNEXPECTED_MATCH = "Unexpected match"
	public static String EXPECTED_NOT_FOUND = "Expected match not found"
	public static String MULTIPLE_FOR_EXPECTED = "Multiple matches found for expected match"
	public static String MATCHSETRECORD_NOT_IN_SNAPSHOT = "Expected match set record is not part of snapshot"
	public static String PATTERNNAME_DIFFERENT = "Expected pattern qualified name different from actual"
	
	@Inject extension ModelLoadHelper
	@Inject extension SnapshotHelper
	
	/**
	 * Checks the pattern name of the matcher against the one stored in
	 *  the record and checks parameterless patterns as well.
	 * 
	 * Returns true if further comparison is allowed, false otherwise.
	 */
	def validateMatcherBeforeCompare(IncQueryMatcher matcher, MatchSetRecord expected, Set diff){
		
		// 1. Check match set record pattern name against matcher pattern name
		if(!matcher.patternName.equals(expected.patternQualifiedName)){
			diff.add(PATTERNNAME_DIFFERENT + " ("+expected.patternQualifiedName+"!="+matcher.patternName+")")
			return false
		}
			
		// 2. Parameter-less patterns have either zero or one matches
		if(matcher.parameterNames.size == 0){
			if(expected.matches.size == 1){
				if(matcher.countMatches == 1){
					diff.add(CORRECT_SINGLE)
					return true
				} else if(matcher.countMatches == 0){
					diff.add(CORRECT_EMPTY)
					return true
				}	
			}
		}
		return true
		
	}
	
	/**
	 * Compares the match set of a given matcher with the given match record
	 *  using EMF-IncQuery as a compare tool.
	 * Therefore the comparison depends on correct EMF-IncQuery query evaluation
	 *  (for a given limited pattern language feature set).
	 */
	def compareResultSetsAsRecords(IncQueryMatcher matcher, MatchSetRecord expected){
		val diff = newHashSet
		
		// 1. Validate match set record against matcher
		var correctResults = matcher.validateMatcherBeforeCompare(expected, diff)
		if(!correctResults){
			return diff
		}
		
		if(!(expected.eContainer instanceof IncQuerySnapshot)){
			diff.add(MATCHSETRECORD_NOT_IN_SNAPSHOT)
			return diff
		}
		val snapshot = expected.eContainer as IncQuerySnapshot
		
		// 2. Initialize matcher for comparison
		val unexpectedMatcher = UnexpectedMatchRecordMatcher::factory().getMatcher(snapshot.EMFRootForSnapshot)
		
		// 3. Save match results into snapshot
		val partialMatch = matcher.createMatchForMachRecord(expected.filter)
		val actual = matcher.saveMatchesToSnapshot(partialMatch,snapshot)
		
		// 4. run matchers
		unexpectedMatcher.forEachMatch(actual, expected, null) [
			diff.add(UNEXPECTED_MATCH + " ("+it.prettyPrint+")")
		]
		unexpectedMatcher.forEachMatch(expected, actual, null) [
			diff.add(EXPECTED_NOT_FOUND + " ("+it.prettyPrint+")")
		]
		return diff
	}

	
	
	/**
	 * Compares the match set of a given matcher with the given match record using the
	 *  records as partial matches on the matcher.
	 * Therefore the comparison does not depend on correct EMF-IncQuery query evaluation.
	 */
	def compareResultSets(IncQueryMatcher matcher, MatchSetRecord expected){
		val diff = newHashSet
		
		// 1. Validate match set record against matcher
		var correctResults = matcher.validateMatcherBeforeCompare(expected, diff)
		if(!correctResults){
			return diff
		}
		
		// 2. Matches of patterns with at least one parameter are handled in two phases
		// 2/a. expected match records are used as partial matches 
		val foundMatches = newArrayList()
		for(MatchRecord matchRecord : expected.matches){
			val partialMatch = 	matcher.createMatchForMachRecord(matchRecord)
			val numMatches = matcher.countMatches(partialMatch)
			if(numMatches == 0){
				diff.add(EXPECTED_NOT_FOUND + " ("+matchRecord.printMatchRecord+")")
				correctResults = false
			} else if(numMatches == 1){
				// partialMatch is equal to actual match
				foundMatches.add(partialMatch)
			} else {
				diff.add(MULTIPLE_FOR_EXPECTED + " ("+matchRecord.printMatchRecord+")")
				correctResults = false
			}
		}
		
		// 2/b. check for unexpected matches
		//val notFoundMatches = newArrayList()
		matcher.forEachMatch(matcher.createMatchForMachRecord(expected.filter)) [
			if(!foundMatches.contains(it)){
				//notFoundMatches.add(it)
				diff.add(UNEXPECTED_MATCH + " ("+it.prettyPrint+")")
			}
		]
		return diff
		
	}
	
  def printMatchRecord(MatchRecord record){
    val sb = new StringBuilder
    val matchSet = record.eContainer as MatchSetRecord
    record.substitutions.forEach[
      if(sb.length > 0){
        sb.append(",")
      }
      sb.append(it.parameterName).append("=").append(it.derivedValue)
    ]
    sb.insert(0,matchSet.patternQualifiedName+"(")
    sb.append(")")
  } 
	
	/**
	 * Compares match set of each matcher initialized from the given pattern model
	 *  based on the input specification of the snapshot.
	 * If any of the matchers return incorrect results, the assert fails.
	 */
	def assertMatchResults(PatternModel patternModel, IncQuerySnapshot snapshot){
		val diff = newHashSet
		val input = snapshot.EMFRootForSnapshot
		val engine = EngineManager::getInstance().getIncQueryEngine(input);
		engine.registerLogger
		snapshot.matchSetRecords.forEach() [matchSet |
			val matcher = patternModel.initializeMatcherFromModel(engine,matchSet.patternQualifiedName)
			if(matcher != null){
				val result = matcher.compareResultSets(matchSet)
				if(!(result == null
					|| newHashSet(CORRECT_EMPTY).equals(result)
					|| newHashSet(CORRECT_SINGLE).equals(result)
				)){
					diff.addAll(result)
				}
			}
		]
		
		//assertArrayEquals(diff.logDifference,newHashSet,diff)
		//assertSame(CORRECTRESULTS,if(diff.empty){CORRECTRESULTS}else{diff.logDifference})
		assertTrue(diff.logDifference(engine),diff.empty)
	}
	
	/**
	 * Compares match set of each matcher initialized from the given pattern model
	 *  based on the input specification of the snapshot (specified as a platform URI).
	 * If any of the matchers return incorrect results, the assert fails.
	 */
	def assertMatchResults(PatternModel patternModel, String snapshotUri){
		val snapshot = snapshotUri.loadExpectedResultsFromUri
		patternModel.assertMatchResults(snapshot)
	}
	
	/**
	 * Compares match set of each matcher initialized from the given pattern model (specified as a platform URI)
	 *  based on the input specification of the snapshot (specified as a platform URI).
	 * If any of the matchers return incorrect results, the assert fails.
	 */
	def assertMatchResults(String patternUri, String snapshotUri){
		val patternModel = patternUri.loadPatternModelFromUri
		patternModel.assertMatchResults(snapshotUri)
	}
	
	def registerLogger(IncQueryEngine engine){
		engine.logger.addAppender(new TestingLogAppender)
	}
	
	def retrieveLoggerOutput(IncQueryEngine engine){
		val logger = engine.getLogger
		
		val appers = logger.allAppenders
		while (appers.hasMoreElements) {
			val apper = appers.nextElement
			if(apper instanceof TestingLogAppender){
				(apper as TestingLogAppender).getOutput.toString
			} 
		}
		"Logger output not recorded"
	}
	
	def logDifference(Set<Object> diff){
		val stringBuilder = new StringBuilder()
		diff.logDifference(stringBuilder)
		stringBuilder.toString
	}
	
	def logDifference(Set<Object> diff, IncQueryEngine engine){
		val stringBuilder = new StringBuilder()
		diff.logDifference(stringBuilder)
		stringBuilder.append(engine.retrieveLoggerOutput)
		stringBuilder.toString
	}
	
	def private logDifference(Set<Object> diff, StringBuilder stringBuilder){
		diff.forEach()[
			stringBuilder.append("\n" + it)
		]
	}
}