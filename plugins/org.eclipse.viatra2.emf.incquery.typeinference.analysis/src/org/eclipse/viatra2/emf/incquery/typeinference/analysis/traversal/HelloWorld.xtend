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
	
	/*def typeEIQ() { // Creates new resource set
		return "org.eclipse.viatra2.emf.incquery.typeinference/src/org/eclipse/viatra2/emf/incquery/typeinference/type.eiq".loadPatternModelFromUri as PatternModel
	}*/
	
	def patternModelInput() {
		return "schoolPatterns/src/testpatterns/testpatterns.eiq".loadPatternModelFromUri as PatternModel
	}
	
/*	def writeType(PatternModel patternModel, Variable variable, EClassifier classifier)
	{
		System::out.println("\t\t\tType: " + classifier.name);
		if(patternModel.importPackages.contains(classifier.EPackage))
			System::out.println("\t\t\tWarning: The type '" + classifier.name + "' of the variable '" + variable +"' is not in the domain of the imported packages.");
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
	
	def writeUnsatTypeInBody(TypeAnalysisErrorReason typeAnalysisErrorReason, PatternBody body, Variable variable)
	{
		val List<TypeReason<Object>> reason2 = typeAnalysisErrorReason.get2ReasonOftUnsatisfiabilityOfVariableInBody(body,variable);
		if(reason2 != null)
		{
			System::out.println("\t\t\tError: The variable '"+variable.name +"' is unsatisfiable: " +
				reason2.get(0).resolveReason + " and "+ reason2.get(1).resolveReason +
				", but " + reason2.get(0).getType + " and " +reason2.get(0).getType +" are disjoint types."
			)
			return
		}
		val List<TypeReason<Object>> reason3 = typeAnalysisErrorReason.get3ReasonOftUnsatisfiabilityOfVariableInBody(body,variable);
		if(reason2 != null)
		{
			System::out.println("\t\t\tError: The variable '"+variable.name +"' is unsatisfiable: " +
				reason3.get(0).resolveReason + ", "+ reason3.get(1).resolveReason + " and "+ reason3.get(2).resolveReason +
				", but " + reason3.get(0).getType +", "+ reason3.get(1).getType + " and " +reason3.get(2).getType +" are three disjoint type."
			)
			return
		}
		val Set<TypeReason<Object>> reasonN = typeAnalysisErrorReason.getNReasonOftUnsatisfiabilityOfVariableInBody(body,variable);
		if(reasonN != null)
		{
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
			return
		}
		System::out.println("\t\t\tError: The variable '"+variable.name +"' is unsatisfiable.")
	}
	
	def writeTooGeneralTypeInBody(TypeAnalysisErrorReason typeAnalysisErrorReason, Variable variable)
	{
		System::out.println("\t\t\tWarning: The variable '"+variable.name+"' has more general type than any existing classifier.");
	}
	
	def writeUnsatTypeInParameter(TypeAnalysisErrorReason typeAnalysisErrorReason, Variable variable)
	{
		System::out.println("\t\t\tError: The variable '"+variable.name +"' has unsatisfiable type constrains.");
	}
	
	def writeTooGeneralTypeInParameter(TypeAnalysisErrorReason typeAnalysisErrorReason, Variable variable)
	{
		System::out.println("\t\t\tWarning: The variable '"+variable.name+"' has more general type than any existing classifier.");
	}
	
	def writeVariableInBody(TypeAnalysis typeAnalisys, TypeAnalysisErrorReason typeAnalysisErrorReason, PatternModel patternModel, PatternBody body, Variable variable)
	{
		System::out.println("\t\tVariable: " + variable.name);
		val classifier = typeAnalisys.getTypeOfVariableInBody(body,variable);
		if(classifier!=null)
		{
			writeType(patternModel,variable,classifier);
		}
		else{
			val isUnsat = typeAnalisys.isUnsatisfiableTypeOfVariableInBody(body,variable);
			if(isUnsat != null)
			{
				writeUnsatTypeInBody(typeAnalysisErrorReason,body,variable)
			}
			else
			{
				val isTooGeneral = typeAnalisys.isTooGeneralTypeOfVariableInBody(body,variable);
				if(isTooGeneral != null)
				{
					writeTooGeneralTypeInBody(typeAnalysisErrorReason,variable);
				}
				else System::out.println("\t\t\tSomething is wrong, type not inferred.")
			}
		}		
	}
	
	def writeParameter(TypeAnalysis typeAnalisys, TypeAnalysisErrorReason typeAnalysisErrorReason, PatternModel patternModel, Variable variable)
	{
		System::out.println("\t\tParameter: " + variable.name);
		val classifier = typeAnalisys.getTypeOfParameter(variable);
		if(classifier!=null)
		{
			writeType(patternModel,variable,classifier);
		}
		else{
			val isUnsat = typeAnalisys.isUnsatisfiableTypeOfParameter(variable)
			if(isUnsat != null)
			{
				writeUnsatTypeInParameter(typeAnalysisErrorReason,variable)
			}
			else
			{
				val isTooGeneral = typeAnalisys.isTooGeneralTypeOfParameter(variable)
				if(isTooGeneral != null)
				{
					writeTooGeneralTypeInParameter(typeAnalysisErrorReason,variable);
				}
				else System::err.println("\t\t\tSomething is wrong, type not inferred.")
			}
		}	
	}
	
	def writeUnsatisfiableCompare(CompareConstraint constraint) {
		System::out.println("The left and right operand of "+
			constraint.leftOperand.toString +" == " + constraint.rightOperand.toString +
			" has different type so the constraint will always fail.");
	}
	
	def writeTautologycCompare(CompareConstraint constraint) {
		System::out.println("The left and right operand of "+
			constraint.leftOperand.toString +" != " + constraint.rightOperand.toString +
			" has different type so the constraint will always satisfied.");
	}*/
	
	@Test def firstTest() {		
		val patternInput = patternModelInput
		
		val printer = new PatternModelTypePrinter()
		printer.traverse(patternInput)
	}
}
