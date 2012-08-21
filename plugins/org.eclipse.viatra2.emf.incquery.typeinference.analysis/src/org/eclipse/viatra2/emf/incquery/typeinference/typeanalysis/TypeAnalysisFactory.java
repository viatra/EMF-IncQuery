package org.eclipse.viatra2.emf.incquery.typeinference.typeanalysis;

import org.eclipse.viatra2.emf.incquery.typeinference.queryanalysis.QueryAnalysisFactory;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

public class TypeAnalysisFactory implements QueryAnalysisFactory<PatternModel, TypeAnalysis>
{
	@Override
	public TypeAnalysis instance(PatternModel target) {
		try {
			return new TypeAnalysis(target);
		} catch (TypeAnalysisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
