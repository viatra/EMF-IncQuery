package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import org.eclipse.viatra2.emf.incquery.typeinference.typeofvariableinbody.TypeOfVariableInBodyMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.typeofvariableinbody.TypeOfVariableInBodyMatcherFactory;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;

public class TypeAnalysis {
	PatternModel patternModel;
	
	TypeOfVariableInBodyMatcher typeOfVariableInBodyMatcher;
	
	public TypeAnalysis(PatternModel patternModel) {
		this.patternModel = patternModel;
		TypeOfVariableInBodyMatcherFactory typeOfVariableInBodyMatcherFactory = new TypeOfVariableInBodyMatcherFactory();
		this.typeOfVariableInBodyMatcher = typeOfVariableInBodyMatcherFactory.getMatcher(patternModel);
	}

	@Override
	public String toString() {
		return "TypeAnalysis [patternModel=" + patternModel + "]";
	}
}
