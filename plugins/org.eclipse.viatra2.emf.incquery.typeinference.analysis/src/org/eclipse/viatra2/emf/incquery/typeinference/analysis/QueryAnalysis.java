package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;

public abstract class QueryAnalysis<Target extends EObject> extends EContentAdapter{
	protected Target target;
	protected ResourceSet resourceSet;
	private boolean cacheValid;
	
	private ResourceSet createResourceOfQuery(Target patternModel)
	{
		return patternModel.eResource().getResourceSet();
	}
	
	public QueryAnalysis(Target target) {
		super();
		this.target = target;
		resourceSet = this.createResourceOfQuery(target);
		this.setTarget(target.eResource().getResourceSet());
		this.cacheValid = false;
		//this.bugHunter(patternModel);
	}
	
	/*protected <T> T getOne(Collection<T> collection)
	{
		for(T element : collection)
		{
			return element;
		}
		return null;
	}*/

	@Override
	public synchronized void notifyChanged(Notification notification) {
		if(this.cacheValid && notification.getOldValue() != notification.getNewValue() && notification.getEventType()!=Notification.RESOLVE)
		{
			System.out.println("Notification to invalidate: " + notification);
			this.cacheValid=false;	
		}
		else
			System.out.println("Notification not interesting: " + notification);
		super.notifyChanged(notification);
	}
	
	
	
	protected abstract void initMatchers() throws TypeAnalysisException;
	protected abstract void getMaches();
	protected abstract void releaseMatchers();
	
	protected void beforeValidation() {};
	
	boolean computing = false;
	int validationRequests = 0;
	
	protected synchronized void validateCache() throws TypeAnalysisException
	{
		synchronized (resourceSet) {
			int thisRequest = ++validationRequests;
			System.out.println(thisRequest+" Thread asks to validate: "
					+ Thread.currentThread().getId()
					+ " where tha cache is valid: " + this.cacheValid);
			this.beforeValidation();
			if (!cacheValid && !computing) {
				computing = true;
				initMatchers();
				computing = false;
				getMaches();
				releaseMatchers();
				this.cacheValid = true;
				System.out.println("Matches are recalculated!!!");
			}
			System.out.println(thisRequest+" Thread is answered: "
					+ Thread.currentThread().getId()
					+ " where tha cache is valid: " + this.cacheValid);
		}
	}

	public boolean isCacheValid() {
		return cacheValid;
	}
}
