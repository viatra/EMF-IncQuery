package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.typeinference.toogeneraltypeofpatternparameter.TooGeneralTypeOfPatternParameterMatch;
import org.eclipse.viatra2.emf.incquery.typeinference.toogeneraltypeofpatternparameter.TooGeneralTypeOfPatternParameterMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.toogeneraltypeofvariableinbody.TooGeneralTypeOfVariableInBodyMatch;
import org.eclipse.viatra2.emf.incquery.typeinference.toogeneraltypeofvariableinbody.TooGeneralTypeOfVariableInBodyMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.typeofparameterofpattern.TypeOfParameterOfPatternMatch;
import org.eclipse.viatra2.emf.incquery.typeinference.typeofparameterofpattern.TypeOfParameterOfPatternMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.typeofparameterofpattern.TypeOfParameterOfPatternProcessor;
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

public class TypeAnalysis extends QueryAnalysisOnPattern{
	
	Map<String,EClassifier> typeOfVariableInBody = new HashMap<String, EClassifier>();
	Set<String> unsatisfiableTypeConstrainInPatternBody = new HashSet<String>();
	Set<String> tooGeneralTypeOfVariableInBody = new HashSet<String>();
	Map<String,EClassifier> typeOfParameterOfPattern = new HashMap<String, EClassifier>();
	Set<String> unsatisfiableTypeOfPatternParameter = new HashSet<String>();
	Set<String> tooGeneralTypeOfPatternParameter = new HashSet<String>();
	
	TypeOfVariableInBodyMatcher typeOfVariableInBodyMatcher;
	UnsatisfiableTypeConstrainInPatternBodyMatcher unsatisfiableTypeConstrainInPatternBodyMatcher;
	TooGeneralTypeOfVariableInBodyMatcher tooGeneralTypeOfVariableInBodyMatcher;
	TypeOfParameterOfPatternMatcher typeOfParameterOfPatternMatcher;
	UnsatisfiableTypeOfPatternParameterMatcher unsatisfiableTypeOfPatternParameterMatcher;
	TooGeneralTypeOfPatternParameterMatcher tooGeneralTypeOfPatternParameterMatcher;

	public TypeAnalysis(PatternModel patternModel) throws TypeAnalysisException {
		super(patternModel);
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
			this.typeOfVariableInBody.put(getVariableInBodyID(result.getVariable(), result.getBody()), result.getValueOfClass());
		for(UnsatisfiableTypeConstrainInPatternBodyMatch result : unsatisfiableTypeConstrainInPatternBodyMatcher.getAllMatches())
			this.unsatisfiableTypeConstrainInPatternBody.add(getVariableInBodyID(result.getVariable(), result.getBody()));
		for(TooGeneralTypeOfVariableInBodyMatch result : tooGeneralTypeOfVariableInBodyMatcher.getAllMatches())
			this.tooGeneralTypeOfVariableInBody.add(getVariableInBodyID(result.getVariable(), result.getBody()));
		for(TypeOfParameterOfPatternMatch result : typeOfParameterOfPatternMatcher.getAllMatches())
			this.typeOfParameterOfPattern.put(getParameterID(result.getVariable()), result.getValueOfClass());
		for(UnsatisfiableTypeOfPatternParameterMatch result : unsatisfiableTypeOfPatternParameterMatcher.getAllMatches())
			this.unsatisfiableTypeOfPatternParameter.add(getParameterID(result.getVariable()));
		for(TooGeneralTypeOfPatternParameterMatch result : tooGeneralTypeOfPatternParameterMatcher.getAllMatches())
			this.tooGeneralTypeOfPatternParameter.add(getParameterID(result.getVariable()));
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
	
	public synchronized EClassifier getTypeOfVariableInBody(PatternBody body, Variable variable) throws TypeAnalysisException{
		this.validateCache();
		return this.typeOfVariableInBody.get(getVariableInBodyID(variable, body));
	}
	
	public synchronized boolean isUnsatisfiableTypeOfVariableInBody(PatternBody body, Variable variable) throws TypeAnalysisException{
		this.validateCache();
		return this.unsatisfiableTypeConstrainInPatternBody.contains(getVariableInBodyID(variable, body));
	}
	
	public synchronized boolean isTooGeneralTypeOfVariableInBody(PatternBody body, Variable variable) throws TypeAnalysisException{
		this.validateCache();
		return this.tooGeneralTypeOfVariableInBody.contains(getVariableInBodyID(variable, body));
	}
	
	public synchronized EClassifier getTypeOfParameter(Variable parameter) throws TypeAnalysisException{
		this.validateCache();
		return this.typeOfParameterOfPattern.get(getParameterID(parameter));
	}
	
	public synchronized boolean isUnsatisfiableTypeOfParameter(Variable parameter) throws TypeAnalysisException {
		this.validateCache();
		return this.unsatisfiableTypeOfPatternParameter.contains(getParameterID(parameter));
	}
	
	public synchronized boolean isTooGeneralTypeOfParameter(Variable parameter) throws TypeAnalysisException {
		this.validateCache();
		return this.tooGeneralTypeOfPatternParameter.contains(getParameterID(parameter));
	}
}
