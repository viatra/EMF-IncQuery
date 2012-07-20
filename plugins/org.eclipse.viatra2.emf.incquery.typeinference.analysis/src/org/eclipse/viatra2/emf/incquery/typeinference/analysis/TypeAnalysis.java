package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.viatra2.emf.incquery.typeinference.toogeneraltypeofpatternparameter.TooGeneralTypeOfPatternParameterMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.toogeneraltypeofvariableinbody.TooGeneralTypeOfVariableInBodyMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.typeofparameterofpattern.TypeOfParameterOfPatternMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.typeofvariableinbody.TypeOfVariableInBodyMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.unsatisfiabletypeconstraininpatternbody.UnsatisfiableTypeConstrainInPatternBodyMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.unsatisfiabletypeofpatternparameter.UnsatisfiableTypeOfPatternParameterMatcher;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

public class TypeAnalysis extends QueryAnalisys{
	
	TypeOfVariableInBodyMatcher typeOfVariableInBodyMatcher;
	UnsatisfiableTypeConstrainInPatternBodyMatcher unsatisfiableTypeConstrainInPatternBodyMatcher;
	TooGeneralTypeOfVariableInBodyMatcher tooGeneralTypeOfVariableInBodyMatcher;
	
	TypeOfParameterOfPatternMatcher typeOfParameterOfPatternMatcher;
	UnsatisfiableTypeOfPatternParameterMatcher unsatisfiableTypeOfPatternParameterMatcher;
	TooGeneralTypeOfPatternParameterMatcher tooGeneralTypeOfPatternParameterMatcher;
	
	public TypeAnalysis(PatternModel patternModel) {
		super(patternModel);
		
		this.typeOfVariableInBodyMatcher = new TypeOfVariableInBodyMatcher(resourceSet);
		this.unsatisfiableTypeConstrainInPatternBodyMatcher = new UnsatisfiableTypeConstrainInPatternBodyMatcher(resourceSet);
		this.tooGeneralTypeOfVariableInBodyMatcher = new TooGeneralTypeOfVariableInBodyMatcher(resourceSet);
		
		this.typeOfParameterOfPatternMatcher = new TypeOfParameterOfPatternMatcher(resourceSet);
		this.unsatisfiableTypeOfPatternParameterMatcher = new UnsatisfiableTypeOfPatternParameterMatcher(resourceSet);
		this.tooGeneralTypeOfPatternParameterMatcher = new TooGeneralTypeOfPatternParameterMatcher(resourceSet);
	}
	
	public EClassifier getTypeOfVariableInBody(PatternBody body, Variable variable) throws TypeAnalisysException {
		return handleMatchResult(this.typeOfVariableInBodyMatcher.getAllValuesOfclass(null,body,variable));
	}
	
	public boolean isUnsatisfiableTypeOfVariableInBody(PatternBody body, Variable variable) throws TypeAnalisysException {
		return hasMatchResult(this.unsatisfiableTypeConstrainInPatternBodyMatcher.getAllMatches(null,body,variable));
	}
	
	public boolean isTooGeneralTypeOfVariableInBody(PatternBody body, Variable variable) throws TypeAnalisysException {
		return hasMatchResult(this.tooGeneralTypeOfVariableInBodyMatcher.getAllMatches(null,body,variable));
	}
	
	public EClassifier getTypeOfParameter(Variable parameter) throws TypeAnalisysException {
		return handleMatchResult(this.typeOfParameterOfPatternMatcher.getAllValuesOfclass(null, parameter));
	}
	
	public boolean isUnsatisfiableTypeOfParameter(Variable parameter) throws TypeAnalisysException {
		return hasMatchResult(this.unsatisfiableTypeOfPatternParameterMatcher.getAllMatches());
	}
	
	public boolean isTooGeneralTypeOfParameter(Variable parameter) throws TypeAnalisysException {
		return hasMatchResult(this.tooGeneralTypeOfPatternParameterMatcher.getAllMatches());
	}

	@Override
	public String toString() {
		return "TypeAnalysis [patternModel=" + patternModel.eResource().getURI() + "]";
	}
}
