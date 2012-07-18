package org.eclipse.viatra2.emf.incquery.typeinference.analysis

import com.google.inject.Inject
import org.eclipse.viatra2.emf.incquery.testing.core.ModelLoadHelper
import org.eclipse.viatra2.emf.incquery.testing.core.SnapshotHelper
import org.eclipse.viatra2.emf.incquery.testing.core.TestExecutor
import org.eclipse.viatra2.emf.incquery.testing.core.injector.EMFPatternLanguageInjectorProvider
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class HelloWorld {
	
	@Inject extension TestExecutor
	@Inject extension ModelLoadHelper
	@Inject extension SnapshotHelper
	
	@Inject	ParseHelper parseHelper
	
	/*def typeEIQ() { // Creates new resource set
		return "org.eclipse.viatra2.emf.incquery.typeinference/src/org/eclipse/viatra2/emf/incquery/typeinference/constraints.eiq".loadPatternModelFromUri as PatternModel
	}*/
	
	def patternModelInput() {
		return "schoolPatterns/src/testpatterns/testpatterns.eiq".loadPatternModelFromUri as PatternModel
	}
	
	@Test
	def firstTest() {
		//val teiq = typeEIQ
		//val matcher = teiq.initializeMatcherFromModel(patternModelInput,"org.eclipse.viatra2.emf.incquery.typeinference.nextTailOfPathExpression")
		//System::out.println(matcher.countMatches)
		
		val patternInput = patternModelInput
		val t = new TypeAnalysis(patternInput);
		System::out.println("New TypeAnalysis object on the pattern model: " + patternInput.toString)
	}
}
