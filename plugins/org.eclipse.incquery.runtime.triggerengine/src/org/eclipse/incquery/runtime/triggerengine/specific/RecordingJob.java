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

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.triggerengine.api.Activation;
import org.eclipse.incquery.runtime.triggerengine.api.ActivationState;
import org.eclipse.incquery.runtime.triggerengine.api.IActivationExecutionResult;
import org.eclipse.incquery.runtime.triggerengine.api.Job;

/**
 * TODO write documentation
 * 
 * @author Abel Hegedus
 *
 */
public class RecordingJob<Match extends IPatternMatch> extends Job<Match>{

    public static final String RECORDING_JOB = "RecordingJobExecution";
    
    public static class CommandExecutionResult implements IActivationExecutionResult{

        private Command result;
        
        
        /**
         * 
         */
        public CommandExecutionResult(Command command) {
            this.result = command;
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.incquery.runtime.triggerengine.api.IActivationExecutionResult#getResult()
         */
        @Override
        public Command getResult() {
            return result;
        }

    }
    
    /**
     * @param activationState
     * @param matchProcessor
     */
    public RecordingJob(ActivationState activationState, IMatchProcessor<Match> matchProcessor) {
        super(activationState, matchProcessor);
    }
    
    public static CommandExecutionResult createCommandExecutionResult(Command command){
        return new CommandExecutionResult(command);
    }

    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.triggerengine.api.Job#execute(org.eclipse.incquery.runtime.triggerengine.api.Activation)
     */
    @Override
    public IActivationExecutionResult execute(final Activation<Match> activation) {
        IncQueryEngine engine = activation.getRule().getMatcher().getEngine();
        TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(engine.getEmfRoot());
        if (domain == null) {
            return super.execute(activation);
        } else {
            final RecordingCommand command = new RecordingCommand(domain) {
                @Override
                protected void doExecute() {
                    RecordingJob.super.execute(activation);
                }
            };
            command.setLabel(RECORDING_JOB);
            domain.getCommandStack().execute(command);

            return new CommandExecutionResult(command);
        }
    }
    
}
