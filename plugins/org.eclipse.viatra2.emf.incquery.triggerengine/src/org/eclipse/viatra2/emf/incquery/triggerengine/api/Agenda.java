package org.eclipse.viatra2.emf.incquery.triggerengine.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.viatra2.emf.incquery.base.api.NavigationHelper;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.AutomaticFiringStrategy;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.IQBaseCallbackUpdateCompleteProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.IUpdateCompleteListener;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.IUpdateCompleteProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.TimedFiringStrategy;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.TransactionUpdateCompleteProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.ActivationNotificationProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationListener;

/**
 * An Agenda is associated to each EMF instance model (more precisely {@link IncQueryEngine}
 * or equivalently {@link Notifier} in the context of EMF-IncQuery) and it is 
 * responsible for creating, managing and disposing rules in the AbstractRule Engine. 
 * It provides an unmodifiable view for the collection of applicable activations.
 * 
 * <p>One must register an {@link IActivationNotificationListener} in order to receive 
 * notifications automatically about the changes in the collection of activations.
 * 
 * <p>The Trigger Engine is a collection of strategies which can be used to 
 * fire these activations with pre-defined timings. Such strategies include 
 * the {@link AutomaticFiringStrategy} and {@link TimedFiringStrategy} at the current 
 * state of development. 
 * 
 * <p>Note that, one may instantiate an {@link ActivationMonitor} in order to 
 * process the activations on an individual basis, because the {@link Agenda} always reflects 
 * the most up-to-date state of the activations. 
 * 
 * <p>One may define whether multiple firing of the same activation is allowed; that is, only 
 * the Appeared state will be used from the lifecycle of {@link Activation}s and consecutive 
 * firing of a previously applied {@link Activation} is possible. For more 
 * information on the lifecycle see {@link Activation}. Multiple firing is used 
 * for example in Design Space Exploration scenarios. 
 * 
 * @author Tamas Szabo
 *
 */
public class Agenda implements IAgenda {

	private IncQueryEngine iqEngine;
	private Set<IRule<? extends IPatternMatch>> rules;
	private Set<ActivationMonitor> monitors;
	private Notifier notifier;
	private TransactionalEditingDomain editingDomain;
	private boolean allowMultipleFiring;
	private IRuleFactory ruleFactory;
	private Collection<Activation<? extends IPatternMatch>> activations;
	private IUpdateCompleteProvider updateCompleteProvider;
	private IActivationNotificationListener activationListener;
	private ActivationNotificationProvider activationProvider;
	
	/**
	 * Instantiates a new Agenda instance with the given {@link IncQueryEngine}.
	 * Multiple firing of the same activation is not allowed.
	 * 
	 * @param iqEngine the {@link IncQueryEngine} instance
	 */
	protected Agenda(IncQueryEngine iqEngine) {
		this(iqEngine, false);
	}
	
	/**
	 * Instantiates a new Agenda instance with the given {@link IncQueryEngine} 
	 * and sets whether multiple allowing is allowed. 
	 * 
	 * @param iqEngine the {@link IncQueryEngine} instance
	 * @param allowMultipleFiring indicates whether multiple firing is allowed
	 */
	protected Agenda(IncQueryEngine iqEngine, boolean allowMultipleFiring) {
		this.iqEngine = iqEngine;
		this.rules = new HashSet<IRule<? extends IPatternMatch>>();
		this.monitors = new HashSet<ActivationMonitor>();
		this.notifier = iqEngine.getEmfRoot();
		this.editingDomain = TransactionUtil.getEditingDomain(notifier);
		this.allowMultipleFiring = allowMultipleFiring;
		this.activations = new HashSet<Activation<? extends IPatternMatch>>();
		
		this.activationProvider = new ActivationNotificationProvider() {
           
		    @Override
            protected void listenerAdded(IActivationNotificationListener listener, boolean fireNow) {
                if (fireNow) {
                    for (Activation<? extends IPatternMatch> activation : activations) {
                        listener.activationAppeared(activation);
                    }
                }
            }
		    
        };
		
		this.activationListener = new IActivationNotificationListener() {
            
            @Override
            public void activationDisappeared(Activation<? extends IPatternMatch> activation) {
                activations.add(activation);
                for (ActivationMonitor monitor : monitors) {
                    monitor.addActivation(activation);
                }
                activationProvider.notifyActivationAppearance(activation);
            }
            
            @Override
            public void activationAppeared(Activation<? extends IPatternMatch> activation) {
                activations.remove(activation);
                for (ActivationMonitor monitor : monitors) {
                    monitor.removeActivation(activation);
                }
                activationProvider.notifyActivationDisappearance(activation);
            }
        };
		
		if (this.editingDomain != null) {
		    updateCompleteProvider = new TransactionUpdateCompleteProvider(editingDomain);
	    } else {
	        NavigationHelper helper;
            try {
                helper = iqEngine.getBaseIndex();
                updateCompleteProvider = new IQBaseCallbackUpdateCompleteProvider(helper);
            } catch (IncQueryException e) {
                getLogger().error("The base index cannot be constructed for the engine!", e);
            }
	    }
	}
	
	/**
	 * Sets the {@link IRuleFactory} for the Agenda.
	 * 
	 * @param ruleFactory the {@link IRuleFactory} instance
	 */
	public void setRuleFactory(IRuleFactory ruleFactory) {
		this.ruleFactory = ruleFactory;
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.IAgenda#getNotifier()
     */
	@Override
    public Notifier getNotifier() {
		return notifier;
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.IAgenda#getEditingDomain()
     */
	@Override
    public TransactionalEditingDomain getEditingDomain() {
		return editingDomain;
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.IAgenda#isAllowMultipleFiring()
     */
    @Override
    public boolean isAllowMultipleFiring() {
        return allowMultipleFiring;
    }

    /* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.IAgenda#createRule(org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory)
     */
	@Override
    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> 
	IRule<Match> createRule(IMatcherFactory<Matcher> factory) {
		return createRule(factory, false, false);
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.IAgenda#createRule(org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory, boolean, boolean)
     */
	@Override
    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> 
		IRule<Match> createRule(IMatcherFactory<Matcher> factory, boolean upgradedStateUsed, boolean disappearedStateUsed) {
		AbstractRule<Match> rule = ruleFactory.
				createRule(iqEngine, factory, upgradedStateUsed, disappearedStateUsed);
		rule.addActivationNotificationListener(activationListener, true);
		rules.add(rule);
		return rule;
	}

	/* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.IAgenda#removeRule(org.eclipse.viatra2.emf.incquery.triggerengine.api.AbstractRule)
     */
	@Override
    public <MatchType extends IPatternMatch> void removeRule(AbstractRule<MatchType> rule) {
		if (rules.contains(rule)) {
			rule.removeActivationNotificationListener(activationListener);
			rule.dispose();
			rules.remove(rule);
		}
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.IAgenda#getRules()
     */
	@Override
    public Collection<IRule<? extends IPatternMatch>> getRules() {
		return rules;
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.IAgenda#dispose()
     */
	@Override
    public void dispose() {
	    if(updateCompleteProvider != null) {
	        updateCompleteProvider.dispose();
	    }
	    for (IRule<? extends IPatternMatch> rule : rules) {
			rule.dispose();
		}
	}

	/* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.IAgenda#getLogger()
     */
	@Override
    public Logger getLogger() {
		return iqEngine.getLogger();
	}

	/* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.IAgenda#getActivations()
     */
	@Override
    public Collection<Activation<? extends IPatternMatch>> getActivations() {
		return Collections.unmodifiableCollection(activations);
	}
	
	/**
     * @return the updateCompleteProvider
     */
    public IUpdateCompleteProvider getUpdateCompleteProvider() {
        return updateCompleteProvider;
    }

    /**
     * @param updateCompleteProvider the updateCompleteProvider to set
     */
    public void setUpdateCompleteProvider(IUpdateCompleteProvider updateCompleteProvider) {
        this.updateCompleteProvider = updateCompleteProvider;
    }
	
	/* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.IAgenda#newActivationMonitor(boolean)
     */
	@Override
    public ActivationMonitor newActivationMonitor(boolean fillAtStart) {
		ActivationMonitor monitor = new ActivationMonitor();
		if (fillAtStart) {
			for (Activation<? extends IPatternMatch> activation : activations) {
				monitor.addActivation(activation);
			}
		}
		monitors.add(monitor);
		return monitor;
	}

	
    /* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.firing.IUpdateCompleteProvider#addUpdateCompleteListener(org.eclipse.viatra2.emf.incquery.triggerengine.firing.IUpdateCompleteListener, boolean)
     */
    @Override
    public boolean addUpdateCompleteListener(IUpdateCompleteListener listener, boolean fireNow) {
        if(updateCompleteProvider != null) {
            return updateCompleteProvider.addUpdateCompleteListener(listener, fireNow);
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.firing.IUpdateCompleteProvider#removeUpdateCompleteListener(org.eclipse.viatra2.emf.incquery.triggerengine.firing.IUpdateCompleteListener)
     */
    @Override
    public boolean removeUpdateCompleteListener(IUpdateCompleteListener listener) {
        if(updateCompleteProvider != null) {
            return updateCompleteProvider.removeUpdateCompleteListener(listener);
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationProvider#addActivationNotificationListener(org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationListener, boolean)
     */
    @Override
    public boolean addActivationNotificationListener(IActivationNotificationListener listener, boolean fireNow) {
        return activationProvider.addActivationNotificationListener(listener, fireNow);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationProvider#removeActivationNotificationListener(org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationListener)
     */
    @Override
    public boolean removeActivationNotificationListener(IActivationNotificationListener listener) {
        return activationProvider.removeActivationNotificationListener(listener);
    }
    
}
