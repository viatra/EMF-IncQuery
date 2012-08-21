package org.eclipse.viatra2.emf.incquery.typeinference.typeerrors;

import java.util.Collection;
import org.eclipse.viatra2.emf.incquery.typeinference.queryanalysis.QueryAnalysisProviderOnPattern;
import org.eclipse.viatra2.emf.incquery.typeinference.typeanalysis.TypeAnalysisException;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;

public class ErrorReasonProvider extends QueryAnalysisProviderOnPattern<TypeAnalysisErrorReason>{

	public ErrorReasonProvider() {
		super(new ErrorReasonFactory());
	}
	
	public Collection<TypeReason<Object>> getReasonOfUnsat(PatternBody body, Variable variable)
	{
		TypeAnalysisErrorReason errorReasoner = this.getQueryAnalysis(QueryAnalysisProviderOnPattern.getPatternModel(variable));
		Collection<TypeReason<Object>> reasons;
		try {
			reasons = errorReasoner.get2ReasonOftUnsatisfiabilityOfVariableInBody(body, variable);
			if(reasons!=null) return reasons;
			reasons = errorReasoner.get3ReasonOftUnsatisfiabilityOfVariableInBody(body, variable);
			if(reasons!=null) return reasons;
			reasons = errorReasoner.getNReasonOftUnsatisfiabilityOfVariableInBody(body, variable);
			if(reasons!=null) return reasons;
		} catch (TypeAnalysisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	public Collection<TypeReason<PatternBody>> getReasonOfGenerality(Variable variable)
	{
		TypeAnalysisErrorReason errorReasoner = this.getQueryAnalysis(QueryAnalysisProviderOnPattern.getPatternModel(variable));
		Collection<TypeReason<PatternBody>> reasons;
		try {
			reasons = errorReasoner.getReasonOfTooGeneralParameter(variable);
		} catch (TypeAnalysisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return reasons;
	}
}
