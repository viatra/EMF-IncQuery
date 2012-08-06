package org.eclipse.viatra2.emf.incquery.typeinference.analysis.traversal

import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable

abstract class PatternModelTraverser {
	
	def traverse(PatternModel patternModel) {
		initPatternModel(patternModel)
		for(pattern : patternModel.patterns) {
			for(body:pattern.bodies)body.variables
						
			traversePattern(patternModel, pattern);
			for(variable : pattern.parameters) traverseParameter(patternModel, pattern, variable)
			for(body : pattern.bodies)
			{
				
				traverseBody(patternModel, pattern, body)
				for(variable : pattern.parameters) traverseVariableAndParameter(patternModel, pattern, body, variable)
				for(variable : body.variables) traverseVariableAndParameter(patternModel, pattern, body, variable)
			}
		}
	}
	
	def void initPatternModel(PatternModel patternModel)
	
	def void traversePattern(PatternModel patternModel, Pattern pattern)
	
	def void traverseBody(PatternModel patternModel, Pattern pattern, PatternBody body)
	
	def void traverseVariableAndParameter(PatternModel patternModel, Pattern pattern, PatternBody body, Variable variable)
	
	def void traverseParameter(PatternModel patternModel, Pattern pattern, Variable parameter)
}
