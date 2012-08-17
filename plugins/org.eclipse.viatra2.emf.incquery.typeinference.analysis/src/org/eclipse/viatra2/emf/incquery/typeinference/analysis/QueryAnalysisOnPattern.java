package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CheckConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.resource.XbaseResource;

public abstract class QueryAnalysisOnPattern extends QueryAnalysis<PatternModel> {
	
	public QueryAnalysisOnPattern(PatternModel target) {
		super(target);
	}

	protected String getParameterID(Variable parameter)
	{
		Pattern p = (Pattern) parameter.eContainer();
		PatternModel pm = (PatternModel) p.eContainer();
		Resource r = pm.eResource();
		return r.getURI()+"/"+pm.getPackageName()+"."+p.getName()+"."+parameter.getName();
	}
	
	protected String getVariableInBodyID(Variable variable,PatternBody body)
	{
		Pattern p = (Pattern) body.eContainer();
		int bodyCout = p.getBodies().indexOf(body)+1;
		PatternModel pm = (PatternModel) p.eContainer();
		Resource r = pm.eResource();
		return r.getURI()+"/"+pm.getPackageName()+"."+p.getName() + "." +bodyCout + "." +variable.getName();
	}
	
	

	@Override
	protected boolean IsChanged(Notification notification) {
		return notification.getOldValue() != notification.getNewValue() && //An idempotent change shouldn't invalidate the cache.
			   notification.getEventType() != Notification.RESOLVE && // ?
				   (!(notification.getNotifier() instanceof XbaseResource));
	}

	@Override
	public synchronized void notifyChanged(Notification notification) {
		// TODO Auto-generated method stub
		super.notifyChanged(notification);
	}

	@Override
	protected void initMatchers() throws TypeAnalysisException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void getMaches() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void releaseMatchers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeValidation(ResourceSet resourceSet2) {
		TreeIterator<Notifier> iterator = resourceSet2.getAllContents();
		while(iterator.hasNext())
		{
			Notifier element = iterator.next();
			if(element instanceof PatternBody)
			{
				System.out.println(">:( Getting variables of " + element);
				((PatternBody)element).getVariables();
			}
			else if(element instanceof PackageImport)
			{
				System.out.println(">:( Getting the imported package of " + element);
				((PackageImport)element).getEPackage();
			}
			else if(element instanceof ClassType)
			{
				System.out.println(">:( classname of " + element);
				((ClassType)element).getClassname();
			}
			if(element instanceof CheckConstraint)
			{
				System.out.println(">:( check expression " + ((CheckConstraint)element).getExpression().getClass());
				((CheckConstraint)element).getExpression().eAllContents();
				if(((CheckConstraint)element).getExpression() instanceof XAbstractFeatureCall)
				{
					((XAbstractFeatureCall)((CheckConstraint)element).getExpression()).getTypeArguments();
				}
			}
		}
		System.out.println(" :) I hope the bests...");
	}
}
