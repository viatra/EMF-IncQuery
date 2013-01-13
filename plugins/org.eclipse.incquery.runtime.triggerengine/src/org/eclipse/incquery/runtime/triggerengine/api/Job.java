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

import java.util.Collection;
import java.util.List;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.triggerengine.specific.RecordingJob;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * TODO write documentation
 *  - use {@link RecordingJob} when modifying the model
 *  
 * @author Abel Hegedus
 */
public class Job<Match extends IPatternMatch> {

    private static final NullResult result = new NullResult();
    /**
     * @author Abel Hegedus
     *
     */
    public static final class NullResult implements IActivationExecutionResult {
        /* (non-Javadoc)
         * @see org.eclipse.incquery.runtime.triggerengine.api.IActivationExecutionResult#getResult()
         */
        @Override
        public Object getResult() {
            return null;
        }

    }
    
    public static class CompositeResult implements IActivationExecutionResult{

        private List<IActivationExecutionResult> subResults;
        
        /**
         * 
         */
        public CompositeResult() {
            this.subResults = Lists.newArrayList();
        }
        
        public CompositeResult(Collection<IActivationExecutionResult> results) {
            this.subResults = Lists.newArrayList(results);
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.incquery.runtime.triggerengine.api.IActivationExecutionResult#getResult()
         */
        @Override
        public Collection<IActivationExecutionResult> getResult() {
            return ImmutableList.copyOf(subResults);
        }

        /* (non-Javadoc)
         * @see org.eclipse.incquery.runtime.triggerengine.api.IActivationExecutionResult#addSubResult(org.eclipse.incquery.runtime.triggerengine.api.IActivationExecutionResult)
         */
        public void addSubResult(IActivationExecutionResult subResult) {
            subResults.add(subResult);
        }
        
    }

    public static NullResult createNullResult() {
        return result;
    }
    
    public static CompositeResult createCompositeResult() {
        return new CompositeResult();
    }
    
    public static CompositeResult createCompositeResult(IActivationExecutionResult initialResult) {
        CompositeResult compositeResult = new CompositeResult();
        compositeResult.addSubResult(initialResult);
        return compositeResult;
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
        Preconditions.checkNotNull(activationState);
        Preconditions.checkNotNull(matchProcessor);
        this.activationState = activationState;
        this.matchProcessor = matchProcessor;
    }
    
    public IActivationExecutionResult execute(final Activation<Match> activation){
        Preconditions.checkNotNull(activation);
        matchProcessor.process(activation.getPatternMatch());
        return result;
    }
}
