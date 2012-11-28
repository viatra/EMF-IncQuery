/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.triggerengine.api;

import java.util.List;

import org.eclipse.incquery.patternlanguage.emf.core.patternLanguage.Pattern;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationProvider;

/**
 * Basic interface for rules, for the default implementation, see {@link AbstractRule}.
 * 
 * <p> The interface allows acces to the precondition pattern, the corresponding agenda
 *  and the unmodifiable list of activations.
 *  
 * <p> The {@link #setStateChangeProcessor(ActivationState, IMatchProcessor)} method allows
 *  the registration of processors that will be invoked when an activation changes states.
 *  
 * <p> The {@link #getStateChangeProcessor(ActivationState)} method returns a registered processor
 * for a given state.
 * 
 * 
 * @author Abel Hegedus
 *
 * @param <MatchType>
 */
public interface IRule<MatchType extends IPatternMatch> extends IActivationNotificationProvider{

    /**
     * Returns the precondition pattern defined for the rule.
     * 
     * @return the precondition pattern
     */
    Pattern getPattern();

    /**
     * Returns the agenda corresponding to the rule.
     * 
     * @return the agenda managing the rule
     */
    IAgenda getAgenda();

    /**
     * Returns the unmodifiable list of current activations for the rule.
     * 
     * @return the list of activations
     */
    List<Activation<MatchType>> getActivations();
    
    /**
     * Registers a processor for the given state. The processor will be invoked, if an activation
     * that is in the given state is fired.
     * 
     * @param newState the state where the processor is used
     * @param processor the processor to use in the given state
     */
    void setStateChangeProcessor(ActivationState newState, IMatchProcessor<MatchType> processor);
    
    /**
     * Returns the processor registered for the given state. If no processor is registered, it returns 
     * null instead.
     * 
     * @param newState the state for which the processor is requested
     * @return the processor, if set, null otherwise
     */
    IMatchProcessor<MatchType> getStateChangeProcessor(ActivationState newState);

    /**
     * This method is called when the rule is no longer managed by the agenda
     */
    void dispose();
    

}