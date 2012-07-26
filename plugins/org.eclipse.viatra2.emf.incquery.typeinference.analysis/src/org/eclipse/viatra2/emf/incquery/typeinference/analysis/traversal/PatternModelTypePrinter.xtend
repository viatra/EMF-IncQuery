package org.eclipse.viatra2.emf.incquery.typeinference.analysis.traversal

import java.util.List
import java.util.Set
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.viatra2.emf.incquery.typeinference.analysis.TypeReason
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassifierConstraint
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionConstraint
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionHead
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CompareConstraint

class PatternModelTypePrinter extends PatternModelTypeInference{

	override writeType(PatternModel patternModel, Variable variable, EClassifier classifier) {
		System::out.println("\t\t\tType: " + classifier.name);
		if(patternModel.importPackages.contains(classifier.EPackage))
			System::out.println("\t\t\tWarning: The type '" + classifier.name +
			"' of the variable '" + variable +"' is not in the domain of the imported packages.");
	}
	
	def String resolveReason(TypeReason<Object> typeReason)
	{
		val reason = typeReason.getReason();
		if(reason instanceof ClassType)
			return "the type "+typeReason.getType.name+" defined in the parameter list"
		else if(reason instanceof EClassifierConstraint)
			return "a classifier constraint defines it is an "+typeReason.getType.name
		else if(reason instanceof PathExpressionConstraint)
		{
			if(reason instanceof PathExpressionHead)
			{
				return "it's the head of a path expression with type " + typeReason.getType
			}
			else
			{
				return "it's the tail of a path expression with type " + typeReason.getType
			}
		}
		else if(reason instanceof CompareConstraint)
			return "it's equivalent with an "+typeReason.type+"typed literal"
		else if(reason instanceof Pattern)
			return "it satisfies the " +(reason as Pattern).name +" pattern as a "+typeReason.getType+" typed parameter";
	}
	
	override writeUnsatTypeInBody2Reason(List<TypeReason<Object>> reason2, Variable variable) {
		System::out.println("\t\t\tError: The variable '"+variable.name +"' is unsatisfiable: " +
				reason2.get(0).resolveReason + " and "+ reason2.get(1).resolveReason +
				", but " + reason2.get(0).getType + " and " +reason2.get(0).getType +" are disjoint types.");
	}
	
	override writeUnsatTypeInBody3Reason(List<TypeReason<Object>> reason3, Variable variable) {
		System::out.println("\t\t\tError: The variable '"+variable.name +"' is unsatisfiable: " +
				reason3.get(0).resolveReason + ", "+ reason3.get(1).resolveReason + " and "+ reason3.get(2).resolveReason +
				", but " + reason3.get(0).getType +", "+ reason3.get(1).getType + " and " +reason3.get(2).getType +" are three disjoint type.")
	}
	
	override writeUnsatTypeInBodyNReason(Set<TypeReason<Object>> reasonN, Variable variable) {
		var reasonPrinted = false
		var ret = "\t\t\tError: The variable '"+variable.name +"' is unsatisfiable because the following constrains: "
		for(reason : reasonN)
		{
			if(reasonPrinted) ret = ret + ", ";
			reasonPrinted = true;
			ret = ret + reason.resolveReason;
		}
		ret = ret + ".";
		System::out.println(ret)
	}
	
	override writeTooGeneralTypeInBody(Variable variable) {
		System::out.println(
			"\t\t\tWarning: The variable '"+variable.name+"' is more general type than any existing classifier.");
	}
	
	override writeUnsatTypeInParameter(Variable variable) {
		System::out.println("\t\t\tError: The variable '"+variable.name +"' has unsatisfiable type constrains.");
	}
	
	override writeTooGeneralTypeInParameterNoConstraint(Pattern pattern, Variable variable) {
		System::out.println("\t\t\tWarning: The parameter '"+variable.name+"' is more general type than any existing classifier.");
	}
	
	override writeTooGeneralTypeInParameter(List<TypeReason<PatternBody>> reasons, Pattern pattern, Variable variable) {
		System::out.println("\t\t\tWarning: The parameter '"+variable.name+"' is more general type than any existing classifier, "+
		"because "+reasons.get(0).type+" and "+reasons.get(1).type +"doesn't have common supertype.")
	}
	
	override traversePattern(PatternModel patternModel, Pattern pattern) {
		System::out.println("\tPattern: " + pattern.name);
		bodyCount = 1
	}
	
	var bodyCount = 1
	
	override traverseBody(PatternModel patternModel, Pattern pattern, PatternBody body) {
		System::out.println("\t\tBody: #" + bodyCount)
		bodyCount = bodyCount +1
	}
	
}