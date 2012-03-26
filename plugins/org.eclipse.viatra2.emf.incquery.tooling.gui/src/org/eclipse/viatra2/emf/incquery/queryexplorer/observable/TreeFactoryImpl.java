package org.eclipse.viatra2.emf.incquery.queryexplorer.observable;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanListProperty;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;

/**
 * Custom tree factory implementation.
 * Observed lists are:
 *  - the matches list of the PatternMatcher class
 *  - the matchers list of the PatternMatcherRoot class
 *  
 * @author Tamas Szabo
 *
 */
public class TreeFactoryImpl implements IObservableFactory {

	private IBeanListProperty matchesListProp;
	private IBeanListProperty matcherListProp;
	
	public TreeFactoryImpl() {
		matchesListProp = BeanProperties.list(PatternMatcher.class, PatternMatcher.MATCHES_ID, PatternMatch.class);
		matcherListProp = BeanProperties.list(PatternMatcherRoot.class, PatternMatcherRoot.MATCHERS_ID, PatternMatcher.class);
	}
	
	@Override
	public IObservable createObservable(Object target) {
		if (target instanceof PatternMatcher) {
			return matchesListProp.observe(target);
		}
		if (target instanceof PatternMatcherRoot) {
			return matcherListProp.observe(target);
		}
		if (target instanceof IObservable) {
			return (IObservable) target;
		}
		return null;
	}
	
	
}
