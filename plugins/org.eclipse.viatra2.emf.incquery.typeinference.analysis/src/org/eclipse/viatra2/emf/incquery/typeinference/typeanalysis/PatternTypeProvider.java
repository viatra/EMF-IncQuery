package org.eclipse.viatra2.emf.incquery.typeinference.typeanalysis;

import org.eclipse.viatra2.emf.incquery.typeinference.queryanalysis.QueryAnalysisProviderOnPattern;

public class PatternTypeProvider extends QueryAnalysisProviderOnPattern<TypeAnalysis>{

	public PatternTypeProvider()
	{
		super(new TypeAnalysisFactory());
	}
}
