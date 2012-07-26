package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import java.util.Collection;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

public abstract class QueryAnalisys {
	protected PatternModel patternModel;
	protected ResourceSet resourceSet;
	
	private ResourceSet createResourceOfQuery(PatternModel patternModel)
	{
		return patternModel.eResource().getResourceSet();
	}
	
	public QueryAnalisys(PatternModel patternModel) {
		this.patternModel = patternModel;
		resourceSet = this.createResourceOfQuery(patternModel);
	}
	
	protected <T> T getOne(Collection<T> collection)
	{
		for(T element : collection)
		{
			return element;
		}
		return null;
	}
	
	protected <T> T handleMatchResult(Collection<T> resultSet) throws TypeAnalysisException
	{
		if(resultSet == null) throw new TypeAnalysisException("Matcher resulted with null value.");
		else if(resultSet.isEmpty()) return null;
		else if (resultSet.size() == 1) return this.getOne(resultSet);
		else
		{
			System.err.println(resourceSet);
			throw new TypeAnalysisException("Matcher incorrectly resulted with multiple match.", resultSet);
		}
	}
	
	protected <T> Collection<T> hasMatchResult(Collection<T> resultSet) throws TypeAnalysisException
	{
		if(resultSet == null) throw new TypeAnalysisException("Matcher resulted with null value.");
		else if(resultSet.isEmpty()) return null;
		else return resultSet;
	}
}
