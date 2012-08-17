package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.xtext.xbase.resource.XbaseResource;

public abstract class QueryAnalysis<Target extends EObject> extends EContentAdapter{
	protected Target target;
	protected ResourceSet resourceSet;
	private boolean cacheValid;
	private boolean cacheMatching;
	
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
		this.cacheMatching = false;
		//this.bugHunter(patternModel);
	}
	
	protected abstract boolean IsChanged(Notification notification);
	
	@Override
	public synchronized void notifyChanged(Notification notification) {
		if(this.cacheValid && !this.cacheMatching && IsChanged(notification))
		{
			System.out.println("[x] ("+Thread.currentThread().getId()+") invalidate: " + notification);
			this.cacheValid=false;	
		}
		else
			System.out.println("["+(this.isCacheValid()?" ":".")+"] ("+Thread.currentThread().getId()+") not interesting: " + notification);
		super.notifyChanged(notification);
	}
	
	protected abstract void initMatchers() throws TypeAnalysisException;
	protected abstract void getMaches();
	protected abstract void releaseMatchers();
	
	protected void beforeValidation(ResourceSet resourceSet2) {};
	
	int validationRequests = 0;
	
	protected synchronized boolean validateCache(EObject object)
			throws TypeAnalysisException {
		int thisRequest = ++validationRequests;
		if (!cacheMatching) {
			// ResourceSet resourceSet = object.eResource().getResourceSet();
			System.out.println("Call " + thisRequest
					+ ": Thread asks to validate: "
					+ Thread.currentThread().getId()
					+ " where tha cache is valid: " + this.cacheValid);

			if (!cacheValid/* && !computing */) {
				this.beforeValidation(resourceSet);
				if (!cacheValid/* && !computing */) {
					System.out.println("Getting matches!!!");
					this.cacheMatching = true;
					initMatchers();
					this.cacheMatching = false;
					if (!cacheValid) {
						this.cacheValid = true;
						getMaches();
						releaseMatchers();
						System.out.println("Matches are recalculated!!!");
						if (!this.cacheValid) {
							System.out.println("!!! Inefficiency");
							this.validateCache(object);
						}
					}
				} else {
					System.out
							.println("The beforevalidation catched the inefficient case :D");
				}
			}

			System.out.println("Call " + thisRequest
					+ ": Thread got answer to the validation requivest: "
					+ Thread.currentThread().getId()
					+ " where tha cache is valid: " + this.cacheValid);
			return true;
		} else
		{
			System.out.println("Call " + thisRequest
				+ ": Validation denied.");
			
			return false;
		}
	}

	public boolean isCacheValid() {
		return cacheValid;
	}
}
