package org.eclipse.viatra2.emf.incquery.typeinference.typeerrors;

import org.eclipse.viatra2.emf.incquery.typeinference.queryanalysis.QueryAnalysisFactory;
import org.eclipse.viatra2.emf.incquery.typeinference.typeanalysis.TypeAnalysisException;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

public class ErrorReasonFactory implements QueryAnalysisFactory<PatternModel, TypeAnalysisErrorReason>
{
	@Override
	public TypeAnalysisErrorReason instance(PatternModel target) {
		try {
			return new TypeAnalysisErrorReason(target);
		} catch (TypeAnalysisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
