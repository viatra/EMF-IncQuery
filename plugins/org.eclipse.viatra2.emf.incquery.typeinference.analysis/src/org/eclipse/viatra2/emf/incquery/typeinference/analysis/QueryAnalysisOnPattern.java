package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

public abstract class QueryAnalysisOnPattern extends QueryAnalysis<PatternModel> {
	
	public QueryAnalysisOnPattern(PatternModel target) {
		super(target);
	}

	protected String getParameterID(Variable parameter)
	{
		Pattern p = (Pattern) parameter.eContainer();
		PatternModel pm = (PatternModel) p.eContainer();
		Resource r = pm.eResource();
		return r.getURI()+"/"+pm.getPackageName()+"."+p.getName()+"."+parameter.getName();
	}
	
	protected String getVariableInBodyID(Variable variable,PatternBody body)
	{
		Pattern p = (Pattern) body.eContainer();
		int bodyCout = p.getBodies().indexOf(body)+1;
		PatternModel pm = (PatternModel) p.eContainer();
		Resource r = pm.eResource();
		return r.getURI()+"/"+pm.getPackageName()+"."+p.getName() + "." +bodyCout + "." +variable.getName();
	}
}
