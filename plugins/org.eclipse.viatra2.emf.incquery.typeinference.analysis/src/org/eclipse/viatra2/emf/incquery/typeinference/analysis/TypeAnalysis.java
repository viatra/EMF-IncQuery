package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import java.util.Collection;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.typeinference.toogeneraltypeofpatternparameter.TooGeneralTypeOfPatternParameterMatch;
import org.eclipse.viatra2.emf.incquery.typeinference.toogeneraltypeofpatternparameter.TooGeneralTypeOfPatternParameterMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.toogeneraltypeofvariableinbody.TooGeneralTypeOfVariableInBodyMatch;
import org.eclipse.viatra2.emf.incquery.typeinference.toogeneraltypeofvariableinbody.TooGeneralTypeOfVariableInBodyMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.typeofparameterofpattern.TypeOfParameterOfPatternMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.typeofvariableinbody.TypeOfVariableInBodyMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.unsatisfiabletypeconstraininpatternbody.UnsatisfiableTypeConstrainInPatternBodyMatch;
import org.eclipse.viatra2.emf.incquery.typeinference.unsatisfiabletypeconstraininpatternbody.UnsatisfiableTypeConstrainInPatternBodyMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.unsatisfiabletypeofpatternparameter.UnsatisfiableTypeOfPatternParameterMatch;
import org.eclipse.viatra2.emf.incquery.typeinference.unsatisfiabletypeofpatternparameter.UnsatisfiableTypeOfPatternParameterMatcher;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

public class TypeAnalysis extends QueryAnalisys{
	
	public static int instances = 0;
	
	TypeOfVariableInBodyMatcher typeOfVariableInBodyMatcher;
	UnsatisfiableTypeConstrainInPatternBodyMatcher unsatisfiableTypeConstrainInPatternBodyMatcher;
	TooGeneralTypeOfVariableInBodyMatcher tooGeneralTypeOfVariableInBodyMatcher;
	
	TypeOfParameterOfPatternMatcher typeOfParameterOfPatternMatcher;
	UnsatisfiableTypeOfPatternParameterMatcher unsatisfiableTypeOfPatternParameterMatcher;
	TooGeneralTypeOfPatternParameterMatcher tooGeneralTypeOfPatternParameterMatcher;
	
	public TypeAnalysis(PatternModel patternModel) throws TypeAnalysisException {
		super(patternModel);
		try {
			this.typeOfVariableInBodyMatcher = new TypeOfVariableInBodyMatcher(resourceSet);
			this.unsatisfiableTypeConstrainInPatternBodyMatcher = new UnsatisfiableTypeConstrainInPatternBodyMatcher(resourceSet);
			this.tooGeneralTypeOfVariableInBodyMatcher = new TooGeneralTypeOfVariableInBodyMatcher(resourceSet);
			
			this.typeOfParameterOfPatternMatcher = new TypeOfParameterOfPatternMatcher(resourceSet);
			this.unsatisfiableTypeOfPatternParameterMatcher = new UnsatisfiableTypeOfPatternParameterMatcher(resourceSet);
			this.tooGeneralTypeOfPatternParameterMatcher = new TooGeneralTypeOfPatternParameterMatcher(resourceSet);
		} catch (IncQueryException e) {
			throw new TypeAnalysisException("The matchers can not be created");
		}
		
		
		instances++;
	}
	
	public EClassifier getTypeOfVariableInBody(PatternBody body, Variable variable) throws TypeAnalysisException {
		return handleMatchResult(this.typeOfVariableInBodyMatcher.getAllValuesOfclass(null,body,variable));
	}
	
	public Collection<UnsatisfiableTypeConstrainInPatternBodyMatch> isUnsatisfiableTypeOfVariableInBody(PatternBody body, Variable variable) throws TypeAnalysisException {
		return hasMatchResult(this.unsatisfiableTypeConstrainInPatternBodyMatcher.getAllMatches(null,body,variable));
	}
	
	public Collection<TooGeneralTypeOfVariableInBodyMatch> isTooGeneralTypeOfVariableInBody(PatternBody body, Variable variable) throws TypeAnalysisException {
		return hasMatchResult(this.tooGeneralTypeOfVariableInBodyMatcher.getAllMatches(null,body,variable));
	}
	
	public EClassifier getTypeOfParameter(Variable parameter) throws TypeAnalysisException {
		return handleMatchResult(this.typeOfParameterOfPatternMatcher.getAllValuesOfclass(null, parameter));
	}
	
	public Collection<UnsatisfiableTypeOfPatternParameterMatch> isUnsatisfiableTypeOfParameter(Variable parameter) throws TypeAnalysisException {
		return hasMatchResult(this.unsatisfiableTypeOfPatternParameterMatcher.getAllMatches());
	}
	
	public Collection<TooGeneralTypeOfPatternParameterMatch> isTooGeneralTypeOfParameter(Variable parameter) throws TypeAnalysisException {
		return hasMatchResult(this.tooGeneralTypeOfPatternParameterMatcher.getAllMatches());
	}
}
