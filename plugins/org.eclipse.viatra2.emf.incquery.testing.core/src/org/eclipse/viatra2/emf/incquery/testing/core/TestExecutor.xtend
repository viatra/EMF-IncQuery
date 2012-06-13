package org.eclipse.viatra2.emf.incquery.testing.core

import java.util.Date
import java.util.HashSet
import java.util.Set
import org.eclipse.core.resources.IFile
import org.eclipse.emf.common.notify.Notifier
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EEnumLiteral
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.URIConverter
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.MatcherFactoryRegistry
import org.eclipse.viatra2.emf.incquery.runtime.util.XmiModelUtil
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotFactory
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.IncQuerySnapshot
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord
import org.eclipse.viatra2.emf.incquery.testing.queries.notfoundmatchrecord.NotFoundMatchRecordMatcher
import org.eclipse.viatra2.emf.incquery.testing.queries.unexpectedmatchrecord.UnexpectedMatchRecordMatcher
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel

import static org.eclipse.viatra2.emf.incquery.testing.core.TestExecutor.*
import static org.junit.Assert.*
/**
 * Primitive methods for setting up and evaluating a functional test for EMF-IncQuery
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
	
	/**
	 * Load an instance EMF model from the given file to a new resource set
	 */
	def loadModelFromFile(IFile file) {
		loadModelFromUri(file.locationURI.path);
	}
	
	/**
	 * Load an instance EMF model from the given platform URI to a new resource set
	 */
	def loadModelFromUri(String platformUri){
		val resourceSet = new ResourceSetImpl()
		resourceSet.loadAdditionalResourceFromUri(platformUri)
	}
	
	/**
	 * Try to resolve a given platform URI first as a resource than as a plugin URI
	 */
	def private resolvePlatformUri(String platformUri){
		val resourceURI = URI::createPlatformResourceURI(platformUri, true)
		if (URIConverter::INSTANCE.exists(resourceURI, null)) {
			return resourceURI
		}
		val pluginURI = URI::createPlatformPluginURI(platformUri, true)
		if (URIConverter::INSTANCE.exists(pluginURI, null)) {
			return pluginURI
		}
	}
	
	/**
	 * Load an additional resource into the resource set from a given file.
	 * Works for both pattern and target model resource sets.
	 */
	def loadAdditionalResourceFromFile(ResourceSet resourceSet, IFile file){
		resourceSet.loadAdditionalResourceFromUri(file.locationURI.path)
	}
	
	/**
	 * Load an additional resource into the resource set from a given platform URI.
	 * Works for both pattern and target model resource sets.
	 */
	def loadAdditionalResourceFromUri(ResourceSet resourceSet, String platformUri){
		val modelURI = platformUri.resolvePlatformUri
		if(modelURI != null){
			resourceSet.getResource(modelURI, true)
		}
	}
	
	/**
	 * Load a pattern model from the given file into a new resource set
	 */
	def loadPatternModelFromFile(IFile file){
		file.locationURI.path.loadPatternModelFromUri
	}
	
	/**
	 * Load a pattern model from the given platform URI into a new resource set
	 */
	def loadPatternModelFromUri(String platformUri){
		XmiModelUtil::prepareXtextResource.loadAdditionalResourceFromUri(platformUri)		
	}
	
	/**
	 * Initialize a matcher for the pattern with the given name from the pattern model on the selected EMF root
	 */
	def initializeMatcherFromModel(PatternModel model, Notifier emfRoot, String patternName){
		val patterns = model.patterns.filter[name.equals(patternName)]
		if(patterns.size == 1){
			MatcherFactoryRegistry::getOrCreateMatcherFactory(patterns.iterator.next).getMatcher(emfRoot)
		}
	}
	
	/**
	 * Initialize a registered matcher for the pattern FQN on the selected EMF root
	 */
	def initializeMatcherFromRegistry(Notifier emfRoot, String patternFQN){
		val factory = MatcherFactoryRegistry::getMatcherFactory(patternFQN)
		factory.getMatcher(emfRoot)
	}
	
	/**
	 * Load the recorded match set into an existing resource set form the given file
	 */
	def loadExpectedResultsFromFile(ResourceSet resourceSet, IFile file){
		resourceSet.loadExpectedResultsFromUri(file.locationURI.path)
	}
	
	/**
	 * Load the recorded match set into an existing resource set form the given platform URI
	 */
	def loadExpectedResultsFromUri(ResourceSet resourceSet, String platformUri){
		val resource = resourceSet.loadAdditionalResourceFromUri(platformUri);
		if(resource != null){
			if(resource.contents.size > 0){
				if(resource.contents.get(0) instanceof IncQuerySnapshot){
					resource.contents.get(0)
				}
			}	
		}
	}
	
	/**
	 * Into new resource set
	 */
	def loadExpectedResultsFromUri(String platformUri){
		
	}
	
	def loadExpectedResultsForPatternFromFile(ResourceSet resourceSet, IFile file, String patternFQN){
		resourceSet.loadExpectedResultsForPatternFromUri(file.locationURI.path,patternFQN)
	}
	
	def loadExpectedResultsForPatternFromUri(ResourceSet resourceSet, String platformUri, String patternFQN){
		val snapshot = resourceSet.loadAdditionalResourceFromUri(platformUri) as IncQuerySnapshot
		val matchsetrecord = snapshot.matchSetRecords.filter[patternQualifiedName.equals(patternFQN)]
		if(matchsetrecord.size == 1){
			return matchsetrecord.iterator.next
		}
	}
	
	// TODO maybe create a match set record from the matcher and use queries to evaluate their equality (e.g. neg find MatchRecordWithCorrespondingRecord(){all substitutions are equal} 
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
		val unexpectedMatcher = UnexpectedMatchRecordMatcher::FACTORY.getMatcher(expected.eContainer)
		val notFoundMatcher = NotFoundMatchRecordMatcher::FACTORY.getMatcher(expected.eContainer)
		
		matcher.saveMatchesToSnapshot(expected.eContainer as IncQuerySnapshot)
		
		// 5. run matchers
		if(unexpectedMatcher.countMatches == 0 && notFoundMatcher.countMatches == 0){
			diff.add(CORRECTRESULTS)
		}
		
		unexpectedMatcher.forEachMatch() [
			diff.add(UNEXPECTED_MATCH + " ("+it+")")
		]
		notFoundMatcher.forEachMatch() [
			diff.add(EXPECTED_NOT_FOUND + " ("+it+")")
		]
		
		
	}

	def saveMatchesToSnapshot(IncQueryMatcher matcher, IncQuerySnapshot snapshot){
		val patternFQN = matcher.patternName
		val actualRecord = EIQSnapshotFactory::eINSTANCE.createMatchSetRecord
		actualRecord.patternQualifiedName = patternFQN
		// 2. put actual match set record in the same model with the expected
		snapshot.matchSetRecords.add(actualRecord)
		// 4. create matchset records
		matcher.forEachMatch()[match | 
			val matchRecord = EIQSnapshotFactory::eINSTANCE.createMatchRecord
			match.parameterNames.forEach()[param | 
				matchRecord.substitutions.add(param.createSubstitution(match.get(param)))
			]
			actualRecord.matches.add(matchRecord)
		]
		return actualRecord
	}
	
	/**
	 * Compares the match set of a given matcher with the given match record using the records as partial matches on the matcher.
	 * Therefore the comparison does not depend on correct EMF-IncQuery query evaluation.
	 */
	def compareResultSets(IncQueryMatcher matcher, MatchSetRecord expected){
		//var notCompatible = false
		val diff = newHashSet
		
		//val MatchRecord first = expected.matches.get(0)
		
		// 1. Validate match set record against matcher
		var correctResults = matcher.validateMatcherBeforeCompare(expected, diff)
		if(!correctResults){
			return diff
		}
		//val substitutionMatcher = CorrectRecordSubstitutionValueMatcher::FACTORY.getMatcher(expected.eResource.resourceSet)
		
		// 3. Matches of patterns with at least one parameter are handled in two phases
		// 3/a. expected match records are used as partial matches 
		val foundMatches = newArrayList()
		for(MatchRecord matchRecord : expected.matches){
			val partialMatch = matcher.newEmptyMatch()
			matchRecord.substitutions.forEach()[
				partialMatch.set(parameterName,derivedValue)				
			]
			val numMatches = matcher.countMatches(partialMatch)
			if(numMatches == 0){
				diff.add(EXPECTED_NOT_FOUND + " ("+matchRecord+")")
				correctResults = false
			} else if(numMatches == 1){
				// partialMatch is equal to actual match
				foundMatches.add(partialMatch)
			} else {
				diff.add(MULTIPLE_FOR_EXPECTED + " ("+matchRecord+")")
				correctResults = false
			}
		}
		
		// 3/b. check for unexpected matches
		val notFoundMatches = newArrayList()
		matcher.forEachMatch() [
			if(!foundMatches.contains(it)){
				notFoundMatches.add(it)
				diff.add(UNEXPECTED_MATCH + " ("+it+")")
			}
		]
		
		// 4. return results
		if(correctResults && notFoundMatches.empty){
			diff.add(CORRECTRESULTS)
		}
		return diff
		/*for(IPatternMatch match : matcher.allMatches as Iterable<IPatternMatch>){
			if(!foundMatches.contains(match)){
				diff.add("Unexpected match ("+match+")")
			}
		}*/
		
		// ensure that the expected pattern matches are equal to the actual pattern matches
		/*for(IPatternMatch match : matcher.allMatches as Iterable<IPatternMatch>){
			val sub = match.get(0);
			val records = substitutionMatcher.getAllMatches(null,match.parameterNames.get(0),sub)
			if(match.parameterNames.size == 1){
				if(records.size == 1){
					foundMatches.add(records.iterator.next)
				}
			} else {
				for(CorrectRecordSubstitutionValueMatch r : records as Iterable<CorrectRecordSubstitutionValueMatch>){
					for(String parameters : match.parameterNames){
						
					}
				}
			}
		}*/
		
		/*for(String parameter : matcher.parameterNames){
			val values = matcher.getAllValues(parameter)
			val expectedValues = new HashMap<String, IPatternMatch>()
			
		}*/
	}
	
	def assertMatchResults(PatternModel patternModel, IncQuerySnapshot snapshot){
		val diff = newHashSet
		
		snapshot.matchSetRecords.forEach() [matchSet |
			val input = matchSet.modelRoot
			val matcher = patternModel.initializeMatcherFromModel(input,matchSet.patternQualifiedName)
			val result = matcher.compareResultSets(matchSet)
			if(!newHashSet(CORRECTRESULTS).equals(result)){
				diff.addAll(result)
			}
		]
		
		if(diff.size == 0){
			diff.add(CORRECTRESULTS)
		}
	}
	
	/**
	 * Checks the pattern name of the matcher against the one stored in the record and checks parameterless patterns as well
	 * 
	 * Returns true if further comparison is allowed, false otherwise
	 */
	def validateMatcherBeforeCompare(IncQueryMatcher matcher, MatchSetRecord expected, Set diff){
		//val MatchRecord first = expected.matches.get(0)
		
		// 1. Check match set record pattern name against matcher pattern name
		if(!matcher.patternName.equals(expected.patternQualifiedName)){
			diff.add(PATTERNNAME_DIFFERENT + " ("+expected.patternQualifiedName+"!="+matcher.patternName+")")
			return false
		}
		/*if(matcher.parameterNames.size != first.parameterNames.size){
			diff.add("Expected parameter size ("+first.parameterNames.size+") different from actual ("+matcher.parameterNames.size+")")
			notCompatible = true
		}*/
		/*for(String parameter : first.parameterNames){
			if(!matcher.parameterNames.contains(parameter)){
				diff.add("Expected parameter ("+parameter+") does not exist in actual ("+matcher.parameterNames+")")
				notCompatible = true
			}
		}
		if(notCompatible){
			return diff
		}*/
		/*if(expected.matches.size > 0){
			if(matcher.countMatches != expected.matches.size){
				diff.add("Expected result set size ("+expected.matches.size+") different from actual ("+matcher.countMatches+")")
			} else*/ 
			
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
	
	def createSubstitution(String parameterName, Object value){
		if(value instanceof EObject){
			val sub = EIQSnapshotFactory::eINSTANCE.createEMFSubstitution
			sub.setValue(value as EObject)
			sub.setParameterName(parameterName)
			return sub
		} else if(value instanceof Integer){
			val sub = EIQSnapshotFactory::eINSTANCE.createIntSubstitution
			sub.setValue(value as Integer)
			sub.setParameterName(parameterName)
			return sub
		} else  if(value instanceof Long){
			val sub = EIQSnapshotFactory::eINSTANCE.createLongSubstitution
			sub.setValue(value as Long)
			sub.setParameterName(parameterName)
			return sub
		} else  if(value instanceof Double){
			val sub = EIQSnapshotFactory::eINSTANCE.createDoubleSubstitution
			sub.setValue(value as Double)
			sub.setParameterName(parameterName)
			return sub
		} else  if(value instanceof Float){
			val sub = EIQSnapshotFactory::eINSTANCE.createFloatSubstitution
			sub.setValue(value as Float)
			sub.setParameterName(parameterName)
			return sub
		} else  if(value instanceof Boolean){
			val sub = EIQSnapshotFactory::eINSTANCE.createBooleanSubstitution
			sub.setValue(value as Boolean)
			sub.setParameterName(parameterName)
			return sub
		} else  if(value instanceof String){
			val sub = EIQSnapshotFactory::eINSTANCE.createStringSubstitution
			sub.setValue(value as String)
			sub.setParameterName(parameterName)
			return sub
		} else  if(value instanceof Date){
			val sub = EIQSnapshotFactory::eINSTANCE.createDateSubstitution
			sub.setValue(value as Date)
			sub.setParameterName(parameterName)
			return sub
		} else  if(value instanceof EEnumLiteral){
			val sub = EIQSnapshotFactory::eINSTANCE.createEnumSubstitution
			sub.setValueLiteral((value as EEnumLiteral).literal)
			sub.setEnumType((value as EEnumLiteral).EEnum)
			sub.setParameterName(parameterName)
			return sub
		} else {
			val sub = EIQSnapshotFactory::eINSTANCE.createMiscellaneousSubstitution
			sub.setValue(value)
			sub.setParameterName(parameterName)
			return sub
		}
	}
}