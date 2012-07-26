package org.eclipse.viatra2.emf.incquery.typeinference.analysis.traversal

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
	
	def patternModelInput() {
		return "schoolPatterns/src/testpatterns/testpatterns.eiq".loadPatternModelFromUri as PatternModel
	}
	
	@Test def firstTest() {		
		val patternInput = patternModelInput
		
		val printer = new PatternModelTypePrinter()
		printer.traverse(patternInput)
	}
}
