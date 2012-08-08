package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.typeinference.toogeneraltypeofpatternparameter.TooGeneralTypeOfPatternParameterMatch;
import org.eclipse.viatra2.emf.incquery.typeinference.toogeneraltypeofpatternparameter.TooGeneralTypeOfPatternParameterMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.toogeneraltypeofvariableinbody.TooGeneralTypeOfVariableInBodyMatch;
import org.eclipse.viatra2.emf.incquery.typeinference.toogeneraltypeofvariableinbody.TooGeneralTypeOfVariableInBodyMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.typeofparameterofpattern.TypeOfParameterOfPatternMatch;
import org.eclipse.viatra2.emf.incquery.typeinference.typeofparameterofpattern.TypeOfParameterOfPatternMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.typeofvariableinbody.TypeOfVariableInBodyMatch;
import org.eclipse.viatra2.emf.incquery.typeinference.typeofvariableinbody.TypeOfVariableInBodyMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.unsatisfiabletypeconstraininpatternbody.UnsatisfiableTypeConstrainInPatternBodyMatch;
import org.eclipse.viatra2.emf.incquery.typeinference.unsatisfiabletypeconstraininpatternbody.UnsatisfiableTypeConstrainInPatternBodyMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.unsatisfiabletypeofpatternparameter.UnsatisfiableTypeOfPatternParameterMatch;
import org.eclipse.viatra2.emf.incquery.typeinference.unsatisfiabletypeofpatternparameter.UnsatisfiableTypeOfPatternParameterMatcher;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

public class TypeAnalysis extends QueryAnalisys{
	
	Map<VariableInBody,EClassifier> typeOfVariableInBody = new HashMap<VariableInBody, EClassifier>();
	Set<VariableInBody> unsatisfiableTypeConstrainInPatternBody = new HashSet<VariableInBody>();
	Set<VariableInBody> tooGeneralTypeOfVariableInBody = new HashSet<VariableInBody>();
	Map<Variable,EClassifier> typeOfParameterOfPattern = new HashMap<Variable, EClassifier>();
	Set<Variable> unsatisfiableTypeOfPatternParameter = new HashSet<Variable>();
	Set<Variable> tooGeneralTypeOfPatternParameter = new HashSet<Variable>();
	
	TypeOfVariableInBodyMatcher typeOfVariableInBodyMatcher;
	UnsatisfiableTypeConstrainInPatternBodyMatcher unsatisfiableTypeConstrainInPatternBodyMatcher;
	TooGeneralTypeOfVariableInBodyMatcher tooGeneralTypeOfVariableInBodyMatcher;
	TypeOfParameterOfPatternMatcher typeOfParameterOfPatternMatcher;
	UnsatisfiableTypeOfPatternParameterMatcher unsatisfiableTypeOfPatternParameterMatcher;
	TooGeneralTypeOfPatternParameterMatcher tooGeneralTypeOfPatternParameterMatcher;

	public TypeAnalysis(PatternModel patternModel) throws TypeAnalysisException {
		super(patternModel);
		//this.bugHunter(patternModel);
	}
	
	private void bugHunter(PatternModel patternModel)
	{
		for(Pattern pattern : patternModel.getPatterns())
			for(PatternBody body : pattern.getBodies())
				body.getVariables();
	}

	@Override
	protected void initMatchers() throws TypeAnalysisException {
		try {
			typeOfVariableInBodyMatcher = new TypeOfVariableInBodyMatcher(resourceSet);
			unsatisfiableTypeConstrainInPatternBodyMatcher = new UnsatisfiableTypeConstrainInPatternBodyMatcher(resourceSet);
			tooGeneralTypeOfVariableInBodyMatcher = new TooGeneralTypeOfVariableInBodyMatcher(resourceSet);
			typeOfParameterOfPatternMatcher = new TypeOfParameterOfPatternMatcher(resourceSet);
			unsatisfiableTypeOfPatternParameterMatcher = new UnsatisfiableTypeOfPatternParameterMatcher(resourceSet);
			tooGeneralTypeOfPatternParameterMatcher = new TooGeneralTypeOfPatternParameterMatcher(resourceSet);
		} catch (IncQueryException e) {
			throw new TypeAnalysisException("The matchers can not be created");
		}
	}

	@Override
	protected void getMaches() {
		this.typeOfVariableInBody.clear();
		this.unsatisfiableTypeConstrainInPatternBody.clear();
		this.tooGeneralTypeOfVariableInBody.clear();
		this.typeOfParameterOfPattern.clear();
		this.unsatisfiableTypeOfPatternParameter.clear();
		this.tooGeneralTypeOfPatternParameter.clear();
		
		for(TypeOfVariableInBodyMatch result : typeOfVariableInBodyMatcher.getAllMatches())
			this.typeOfVariableInBody.put(new VariableInBody(result.getVariable(), result.getBody()), result.getValueOfClass());
		for(UnsatisfiableTypeConstrainInPatternBodyMatch result : unsatisfiableTypeConstrainInPatternBodyMatcher.getAllMatches())
			this.unsatisfiableTypeConstrainInPatternBody.add(new VariableInBody(result.getVariable(), result.getBody()));
		for(TooGeneralTypeOfVariableInBodyMatch result : tooGeneralTypeOfVariableInBodyMatcher.getAllMatches())
			this.tooGeneralTypeOfVariableInBody.add(new VariableInBody(result.getVariable(), result.getBody()));
		for(TypeOfParameterOfPatternMatch result : typeOfParameterOfPatternMatcher.getAllMatches())
			this.typeOfParameterOfPattern.put(result.getVariable(), result.getValueOfClass());
		for(UnsatisfiableTypeOfPatternParameterMatch result : unsatisfiableTypeOfPatternParameterMatcher.getAllMatches())
			this.unsatisfiableTypeOfPatternParameter.add(result.getVariable());
		for(TooGeneralTypeOfPatternParameterMatch result : tooGeneralTypeOfPatternParameterMatcher.getAllMatches())
			this.tooGeneralTypeOfPatternParameter.add(result.getVariable());
	}

	@Override
	protected void releaseMatchers() {
		typeOfVariableInBodyMatcher.getEngine().dispose();
		unsatisfiableTypeConstrainInPatternBodyMatcher.getEngine().dispose();
		tooGeneralTypeOfVariableInBodyMatcher.getEngine().dispose();
		typeOfParameterOfPatternMatcher.getEngine().dispose();
		unsatisfiableTypeOfPatternParameterMatcher.getEngine().dispose();
		tooGeneralTypeOfPatternParameterMatcher.getEngine().dispose();
	}
	
	public EClassifier getTypeOfVariableInBody(PatternBody body, Variable variable) throws TypeAnalysisException{
		this.validateCache();
		return this.typeOfVariableInBody.get(new VariableInBody(variable, body));
	}
	
	public boolean isUnsatisfiableTypeOfVariableInBody(PatternBody body, Variable variable) throws TypeAnalysisException{
		this.validateCache();
		return this.unsatisfiableTypeConstrainInPatternBody.contains(new VariableInBody(variable, body));
	}
	
	public boolean isTooGeneralTypeOfVariableInBody(PatternBody body, Variable variable) throws TypeAnalysisException{
		this.validateCache();
		return this.tooGeneralTypeOfVariableInBody.contains((new VariableInBody(variable, body)));
	}
	
	public EClassifier getTypeOfParameter(Variable parameter) throws TypeAnalysisException{
		this.validateCache();
		return this.typeOfParameterOfPattern.get(parameter);
	}
	
	public boolean isUnsatisfiableTypeOfParameter(Variable parameter) throws TypeAnalysisException {
		this.validateCache();
		return this.unsatisfiableTypeOfPatternParameter.contains(parameter);
	}
	
	public boolean isTooGeneralTypeOfParameter(Variable parameter) throws TypeAnalysisException {
		this.validateCache();
		return this.tooGeneralTypeOfPatternParameter.contains(parameter);
	}
}
