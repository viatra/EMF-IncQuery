package org.eclipse.viatra2.emf.incquery.typeinference.analysis.traversal

import java.util.List
import java.util.Set
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.viatra2.emf.incquery.typeinference.analysis.TypeReason
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel

class PatternModelTypeSilent extends PatternModelTypeInference{
	

	override writeType(PatternModel patternModel, Variable variable, EClassifier classifier) {
	}
	
	override writeUnsatTypeInBody2Reason(List<TypeReason<Object>> reasons, Variable variable) {
	}
	
	override writeUnsatTypeInBody3Reason(List<TypeReason<Object>> reasons, Variable variable) {
	}
	
	override writeUnsatTypeInBodyNReason(Set<TypeReason<Object>> reasons, Variable variable) {
	}
	
	override writeTooGeneralTypeInBody(Variable variable) {
	}
	
	override writeUnsatTypeInParameter(Variable variable) {
	}
	
	override writeTooGeneralTypeInParameterNoConstraint(Pattern pattern, Variable variable) {
	}
	
	override writeTooGeneralTypeInParameter(List<TypeReason<PatternBody>> reasons, Pattern pattern, Variable variable) {
	}
	
	override traversePattern(PatternModel patternModel, Pattern pattern) {
	}
	
	override traverseBody(PatternModel patternModel, Pattern pattern, PatternBody body) {
	}
	
}