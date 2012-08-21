package org.eclipse.viatra2.emf.incquery.typeinference.queryanalysis;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

public abstract class QueryAnalysisProviderOnPattern<PatternAnalysis extends QueryAnalysisOnPattern> extends QueryAnalysisProvider<PatternModel, PatternAnalysis, String>{

	public QueryAnalysisProviderOnPattern(
			QueryAnalysisFactory<PatternModel, PatternAnalysis> analysisFactory) {
		super(analysisFactory);
	}

	@Override
	protected String getRepresentation(PatternModel target) {
		return target.eResource().getURI().toString();
	}
	
	public static PatternModel getPatternModel(EObject object)
	{
		do {
			object = object.eContainer();
		} while (!(object instanceof PatternModel));
		return (PatternModel) object;
	}
	
	public static PatternBody getPatternBody(EObject object)
	{
		while(!(object instanceof PatternBody))
		{	
			EObject object2 = object.eContainer();
			//System.out.println("\t >" +object + "\t> " +object2);
			if(object2 == null) return null;
			else object = object2;
		}
		return (PatternBody) object;
	}
}
