package org.eclipse.viatra2.emf.incquery.typeinference.queryanalysis;

import org.eclipse.emf.ecore.EObject;

public interface QueryAnalysisFactory <Target extends EObject, Analysis extends QueryAnalysis<Target>>{
	public Analysis instance(Target target);
}
