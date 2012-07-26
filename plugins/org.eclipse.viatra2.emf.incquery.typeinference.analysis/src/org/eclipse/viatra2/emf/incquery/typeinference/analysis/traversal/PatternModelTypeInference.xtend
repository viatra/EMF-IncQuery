package org.eclipse.viatra2.emf.incquery.typeinference.analysis.traversal

import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import org.eclipse.viatra2.emf.incquery.typeinference.analysis.TypeAnalysis
import org.eclipse.viatra2.emf.incquery.typeinference.analysis.TypeAnalysisErrorReason
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.viatra2.emf.incquery.typeinference.analysis.TypeReason
import java.util.List
import java.util.Set

abstract class PatternModelTypeInference extends PatternModelTraverser{
	TypeAnalysis typeAnalysis;
	TypeAnalysisErrorReason typeErrorReason;
	
	override initPatternModel(PatternModel patternModel) {
		this.typeAnalysis = new TypeAnalysis(patternModel)
		this.typeErrorReason = new TypeAnalysisErrorReason(patternModel);
		System::out.println("Type inference in " + patternModel.eResource.URI)
	}
	
	override traverseVariableAndParameter(PatternModel patternModel, Pattern pattern, PatternBody body, Variable variable) {
		val classifier = this.typeAnalysis.getTypeOfVariableInBody(body,variable);
		if(classifier!=null)
		{
			writeType(patternModel,variable,classifier);
		}
		else{
			val isUnsat = this.typeAnalysis.isUnsatisfiableTypeOfVariableInBody(body,variable);
			if(isUnsat != null)
			{
				writeUnsatTypeInBody(body,variable)
			}
			else
			{
				val isTooGeneral = typeAnalysis.isTooGeneralTypeOfVariableInBody(body,variable);
				if(isTooGeneral != null)
				{
					writeTooGeneralTypeInBody(variable);
				}
			}
		}	
	}
	
	def void writeType(PatternModel patternModel, Variable variable, EClassifier classifier)
	
	def void writeUnsatTypeInBody(PatternBody body, Variable variable)
	{
		val List<TypeReason<Object>> reason2 = typeErrorReason.get2ReasonOftUnsatisfiabilityOfVariableInBody(body,variable);
		if(reason2 != null)
		{
			writeUnsatTypeInBody3Reason(reason2,variable);
			return
		}
		val List<TypeReason<Object>> reason3 = typeErrorReason.get3ReasonOftUnsatisfiabilityOfVariableInBody(body,variable);
		if(reason2 != null)
		{
			writeUnsatTypeInBody3Reason(reason3,variable);
			return
		}
		val Set<TypeReason<Object>> reasonN = typeErrorReason.getNReasonOftUnsatisfiabilityOfVariableInBody(body,variable);
		if(reasonN != null)
		{
			writeUnsatTypeInBodyNReason(reasonN,variable);
			return
		}
	}
	
	def void writeUnsatTypeInBody2Reason(List<TypeReason<Object>> reasons, Variable variable)
	def void writeUnsatTypeInBody3Reason(List<TypeReason<Object>> reasons, Variable variable)
	def void writeUnsatTypeInBodyNReason(Set<TypeReason<Object>> reasons, Variable variable)
	
	def void writeTooGeneralTypeInBody(Variable variable)
	
	override traverseParameter(PatternModel patternModel, Pattern pattern, Variable parameter) {
		val classifier = typeAnalysis.getTypeOfParameter(parameter);
		if(classifier!=null)
		{
			writeType(patternModel,parameter,classifier);
		}
		else{
			val isUnsat = typeAnalysis.isUnsatisfiableTypeOfParameter(parameter)
			if(isUnsat != null)
			{
				writeUnsatTypeInParameter(parameter)
			}
			else
			{
				val isTooGeneral = typeAnalysis.isTooGeneralTypeOfParameter(parameter)
				if(isTooGeneral != null)
				{
					writeTooGeneralTypeInParameter(pattern, parameter);
				}
				else System::err.println("\t\t\tSomething is wrong, type not inferred.")
			
			}
		}
	}
	
	def void writeUnsatTypeInParameter(Variable variable)
	
	def void writeTooGeneralTypeInParameter(Pattern pattern, Variable variable)
	{
		val result = this.typeErrorReason.getReasonOfTooGeneralParameter(pattern, variable)
		if(result!=null) writeTooGeneralTypeInParameter(result,pattern,variable)
		else writeTooGeneralTypeInParameterNoConstraint(pattern, variable)
	}
	
	def void writeTooGeneralTypeInParameterNoConstraint(Pattern pattern, Variable variable)
	
	def void writeTooGeneralTypeInParameter(List<TypeReason<PatternBody>> reasons, Pattern pattern, Variable variable)
}