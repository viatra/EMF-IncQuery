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
import org.eclipse.incquery.runtime.triggerengine.api.Context;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * TODO write documentation
 * 
 * @author Abel Hegedus
 *
 */
public class RecordingJob<Match extends IPatternMatch> extends StatelessJob<Match>{

    public static final String RECORDING_JOB = "RecordingJobExecution";
    public static final String RECORDING_JOB_SESSION_DATA_KEY = "org.eclipse.incquery.triggerengine.specific.RecordingJob.SessionData";
    
    public static class RecordingJobSessionData {

        private Table<RecordingJob<IPatternMatch>,Activation<IPatternMatch>,Command> table;
        
        /**
         * 
         */
        public RecordingJobSessionData() {
            this.table = HashBasedTable.create();
        }

        /**
         * @return the table
         */
        public Table<RecordingJob<IPatternMatch>, Activation<IPatternMatch>, Command> getTable() {
            return table;
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
    public void execute(final Activation<Match> activation, final Context context) {
        IncQueryEngine engine = activation.getRule().getMatcher().getEngine();
        TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(engine.getEmfRoot());
        if (domain == null) {
            super.execute(activation, context);
        } else {
            final RecordingCommand command = new RecordingCommand(domain) {
                @Override
                protected void doExecute() {
                    RecordingJob.super.execute(activation, context);
                }
            };
            command.setLabel(RECORDING_JOB);
            domain.getCommandStack().execute(command);
            
            updateSessionData(activation, context, command);
        }
    }

    @SuppressWarnings("unchecked")
    private void updateSessionData(final Activation<Match> activation, final Context context, final Command command) {
        Object data = context.get(RECORDING_JOB_SESSION_DATA_KEY);
        RecordingJobSessionData result = null;
        if(data instanceof RecordingJobSessionData) {
            result = (RecordingJobSessionData) data;
        } else {
            result = new RecordingJobSessionData();
            context.put(RECORDING_JOB_SESSION_DATA_KEY, result);
        }
        RecordingJob<IPatternMatch> job = (RecordingJob<IPatternMatch>) this;
        Activation<IPatternMatch> act = (Activation<IPatternMatch>) activation;
        result.getTable().put(job, act, command);
    }
    
}
