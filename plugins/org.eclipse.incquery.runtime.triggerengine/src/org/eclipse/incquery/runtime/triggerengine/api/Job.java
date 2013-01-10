/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.triggerengine.api;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;

/**
 * @author Abel Hegedus
 * TODO implement match processor - activation state correspondence
 *  - 
 */
public class Job<Match extends IPatternMatch> {

    private static final NullResult result = new NullResult();
    /**
     * @author Abel Hegedus
     *
     */
    private static final class NullResult implements IActivationExecutionResult {
        /* (non-Javadoc)
         * @see org.eclipse.incquery.runtime.triggerengine.api.IActivationExecutionResult#getResult()
         */
        @Override
        public Object getResult() {
            return null;
        }
    }

    private ActivationState activationState;
    private IMatchProcessor<Match> matchProcessor;
    
    /**
     * @return the activationState
     */
    public ActivationState getActivationState() {
        return activationState;
    }
    
    /**
     * @return the matchProcessor
     */
    public IMatchProcessor<Match> getMatchProcessor() {
        return matchProcessor;
    }
    
    public Job(ActivationState activationState, IMatchProcessor<Match> matchProcessor){
        this.activationState = activationState;
        this.matchProcessor = matchProcessor;
    }
    
    public IActivationExecutionResult execute(final Activation<Match> activation){
        if(matchProcessor != null) {
            matchProcessor.process(activation.getPatternMatch());
        }
        return result;
    }
}
