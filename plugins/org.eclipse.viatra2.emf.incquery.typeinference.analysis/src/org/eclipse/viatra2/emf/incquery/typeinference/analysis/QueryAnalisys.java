package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

public abstract class QueryAnalisys extends EContentAdapter{
	protected PatternModel patternModel;
	protected ResourceSet resourceSet;
	private boolean cacheValid;
	
	private ResourceSet createResourceOfQuery(PatternModel patternModel)
	{
		return patternModel.eResource().getResourceSet();
	}
	
	public QueryAnalisys(PatternModel patternModel) {
		super();
		this.patternModel = patternModel;
		resourceSet = this.createResourceOfQuery(patternModel);
		this.setTarget(patternModel.eResource().getResourceSet());
		this.cacheValid = false;
		//this.bugHunter(patternModel);
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

	@Override
	public synchronized void notifyChanged(Notification notification) {
		//System.out.println("Notification: " + notification);
		if(notification.getOldValue() != notification.getNewValue() && this.cacheValid)
		{
			System.out.println("Nonidempotent");
			this.cacheValid=false;	
		}
		super.notifyChanged(notification);
	}
	
	protected abstract void initMatchers() throws TypeAnalysisException;
	protected abstract void getMaches();
	protected abstract void releaseMatchers();
	
	private void bugHunter(PatternModel patternModel)
	{
		for(Pattern pattern : patternModel.getPatterns())
			for(PatternBody body : pattern.getBodies())
				body.getVariables();
	}
	
	protected synchronized void validateCache() throws TypeAnalysisException
	{
		if (!cacheValid) {
			synchronized (resourceSet) {
				initMatchers();
				getMaches();
				releaseMatchers();
				this.cacheValid = true;
				System.out.println("Matches are recalculated!!!");
			}
		}
	}

	public boolean isCacheValid() {
		return cacheValid;
	}
}
