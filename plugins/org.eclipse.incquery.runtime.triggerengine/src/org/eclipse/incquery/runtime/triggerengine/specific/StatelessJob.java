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
package org.eclipse.incquery.runtime.triggerengine.specific;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.triggerengine.api.Activation;
import org.eclipse.incquery.runtime.triggerengine.api.ActivationState;
import org.eclipse.incquery.runtime.triggerengine.api.Job;
import org.eclipse.incquery.runtime.triggerengine.api.Session;

/**
 * TODO write documentation
 *  - use {@link RecordingJob} when modifying the model
 *  
 * @author Abel Hegedus
 */
public class StatelessJob<Match extends IPatternMatch> extends Job<Match> {

    private IMatchProcessor<Match> matchProcessor;
    
    /**
     * @return the matchProcessor
     */
    public IMatchProcessor<Match> getMatchProcessor() {
        return matchProcessor;
    }
    
    public StatelessJob(ActivationState activationState, IMatchProcessor<Match> matchProcessor){
        super(activationState);
        this.matchProcessor = checkNotNull(matchProcessor, "StatelessJob cannot be instantiated with null match processor");
    }
    
    @Override
    public void execute(final Activation<Match> activation, Session session){
        checkNotNull(activation, "Job cannot be executed on null activation");
        matchProcessor.process(activation.getPatternMatch());
    }
}
