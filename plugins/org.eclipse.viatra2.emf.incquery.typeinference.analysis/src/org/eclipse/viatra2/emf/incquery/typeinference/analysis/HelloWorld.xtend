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
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody

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
	
	def writeType(PatternModel patternModel, Variable variable, EClassifier classifier)
	{
		System::out.println("\t\t\tType: " + classifier.name);
		if(patternModel.importPackages.contains(classifier.EPackage))
			System::out.println("\t\t\tWarning: The type '" + classifier.name + "' of the variable '" + variable +"' is not in the domain of the imported packages.");
	}
	
	def writeUnsatType(Variable variable)
	{
		System::out.println("\t\t\tError: The variable '"+variable.name +"' has unsatisfiable type constrains.");
	}
	
	def writeTooGeneralType(Variable variable)
	{
		System::out.println("\t\t\tWarning: The variable '"+variable.name+"' has more general type than any existing classifier.");
	}
	
	def writeVariableInBody(TypeAnalysis typeAnalisys, PatternModel patternModel, PatternBody body, Variable variable)
	{
		System::out.println("\t\tVariable: " + variable.name);
		val classifier = typeAnalisys.getTypeOfVariableInBody(body,variable);
		if(classifier!=null)
		{
			writeType(patternModel,variable,classifier);
		}
		else{
			val isUnsat = typeAnalisys.isUnsatisfiableTypeOfVariableInBody(body,variable);
			if(isUnsat == true)
			{
				writeUnsatType(variable)
			}
			else
			{
				val isTooGeneral = typeAnalisys.isTooGeneralTypeOfVariableInBody(body,variable);
				if(isTooGeneral == true)
				{
					writeTooGeneralType(variable);
				}
				else System::err.println("\t\t\tSomething is wrong, type not inferred.")
			}
		}		
	}
	
	def writeParameter(TypeAnalysis typeAnalisys, PatternModel patternModel, Variable variable)
	{
		System::out.println("\t\tParameter: " + variable.name);
		val classifier = typeAnalisys.getTypeOfParameter(variable);
		if(classifier!=null)
		{
			writeType(patternModel,variable,classifier);
		}
		else{
			val isUnsat = typeAnalisys.isUnsatisfiableTypeOfParameter(variable)
			if(isUnsat == true)
			{
				writeUnsatType(variable)
			}
			else
			{
				val isTooGeneral = typeAnalisys.isTooGeneralTypeOfParameter(variable)
				if(isTooGeneral == true)
				{
					writeTooGeneralType(variable);
				}
				else System::err.println("\t\t\tSomething is wrong, type not inferred.")
			}
		}	
	}
	
	@Test def firstTest() {
		/*val teiq = typeEIQ
		System::out.println("*")
		val matcher = teiq.initializeMatcherFromModel(patternModelInput,"org.eclipse.viatra2.emf.incquery.typeinference.supertype")
		System::out.println(matcher.countMatches)*/
		
		val patternInput = patternModelInput
		val t = new TypeAnalysis(patternInput);
		System::out.println("New TypeAnalysis object on the pattern model: " + t)
		for(pattern : patternInput.patterns)
		{
			System::out.println("Pattern: " + pattern.name)
			var bodyCount = 1;
			for(body : pattern.bodies)
			{
				System::out.println("\tBody: #" + bodyCount);
				for(variable : pattern.parameters) writeVariableInBody(t,patternInput,body,variable);
				for(variable : body.variables) writeVariableInBody(t,patternInput,body,variable);
				bodyCount = bodyCount+1;
			}
			System::out.println("\tParameters:");
			for(variable : pattern.parameters) writeParameter(t,patternInput,variable);
		}
	}
}
