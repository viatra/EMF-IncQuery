package org.eclipse.viatra2.emf.incquery.typeinference.queryanalysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

public abstract class QueryAnalysisProvider<Target extends EObject, Analysis extends QueryAnalysis<Target>, TargetRepresentation>{

	QueryAnalysisFactory<Target, Analysis> analysisFactory;
	Map<TargetRepresentation, Analysis> map = new HashMap<TargetRepresentation, Analysis>();
	
	public QueryAnalysisProvider(QueryAnalysisFactory<Target, Analysis> analysisFactory) {
		this.analysisFactory=analysisFactory;
	}
	
	protected abstract TargetRepresentation getRepresentation(Target target);
	
	public Analysis getQueryAnalysis(Target target)
	{
		TargetRepresentation representation = getRepresentation(target);
		Analysis old = map.get(representation);
		if (old != null) return old;
		else {
			Analysis analysis = analysisFactory.instance(target);
			map.put(representation,analysis);
			return analysis;
		}
	}
	
	public void freeQueryAnalysis(Target target)
	{
		this.map.remove(getRepresentation(target));
	}
}
