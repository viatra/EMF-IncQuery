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

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.triggerengine.api.Activation;
import org.eclipse.incquery.runtime.triggerengine.api.ActivationState;
import org.eclipse.incquery.runtime.triggerengine.api.Session;

/**
 * TODO write documentation
 * 
 * @author Abel Hegedus
 *
 */
public class RecordingJob<Match extends IPatternMatch> extends StatelessJob<Match>{

    public static final String RECORDING_JOB = "RecordingJobExecution";
    public static final String RECORDING_JOB_SESSION_DATA_KEY = "org.eclipse.incquery.triggerengine.specific.RecordingJob.SessionData";
    
    public static class RecordingJobExecutionResult<Match extends IPatternMatch> {

        private RecordingJob<Match> job;
        private Activation<Match> activation;
        private Command command;
        
        /**
         * 
         */
        public RecordingJobExecutionResult(RecordingJob<Match> job, Activation<Match> activation, Command command) {
            this.job = job;
            this.activation = activation;
            this.command = command;
        }
        
        /**
         * @return the activation
         */
        public Activation<Match> getActivation() {
            return activation;
        }
        
        /**
         * @return the command
         */
        public Command getCommand() {
            return command;
        }
        
        /**
         * @return the job
         */
        public RecordingJob<Match> getJob() {
            return job;
        }

    }
    
    /**
     * @param activationState
     * @param matchProcessor
     */
    public RecordingJob(ActivationState activationState, IMatchProcessor<Match> matchProcessor) {
        super(activationState, matchProcessor);
    }

    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.triggerengine.api.StatelessJob#execute(org.eclipse.incquery.runtime.triggerengine.api.Activation)
     */
    @Override
    public void execute(final Activation<Match> activation, final Session session) {
        IncQueryEngine engine = activation.getRule().getMatcher().getEngine();
        TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(engine.getEmfRoot());
        if (domain == null) {
            super.execute(activation, session);
        } else {
            final RecordingCommand command = new RecordingCommand(domain) {
                @Override
                protected void doExecute() {
                    RecordingJob.super.execute(activation, session);
                }
            };
            command.setLabel(RECORDING_JOB);
            domain.getCommandStack().execute(command);
            
            updateSessionData(activation, session, command);
        }
    }

    @SuppressWarnings("unchecked")
    private void updateSessionData(final Activation<Match> activation, final Session session, final RecordingCommand command) {
        RecordingJobExecutionResult<Match> result = new RecordingJobExecutionResult<Match>(RecordingJob.this, activation, command);
        Object data = session.get(RECORDING_JOB_SESSION_DATA_KEY);
        if(data instanceof Collection<?>) {
            Collection<RecordingJobExecutionResult<IPatternMatch>> dataColl = (Collection<RecordingJobExecutionResult<IPatternMatch>>) data;
            dataColl.add((RecordingJobExecutionResult<IPatternMatch>) result);
        } else {
            HashSet<RecordingJobExecutionResult<IPatternMatch>> dataColl = new HashSet<RecordingJob.RecordingJobExecutionResult<IPatternMatch>>();
            dataColl.add((RecordingJobExecutionResult<IPatternMatch>) result);
            session.put(RECORDING_JOB_SESSION_DATA_KEY, dataColl);
        }
    }
    
}
