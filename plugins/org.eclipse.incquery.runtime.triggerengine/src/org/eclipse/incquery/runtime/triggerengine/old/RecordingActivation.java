/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.triggerengine.specific;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.triggerengine.api.Activation;

public class RecordingActivation<MatchType extends IPatternMatch> extends Activation<MatchType> {

    /**
     * 
     */
    public static final String RECORDING_ACTIVATION = "RecordingActivation";
    private RecordingRule<MatchType> rule;

    public RecordingActivation(RecordingRule<MatchType> rule, MatchType patternMatch) {
        super(rule, patternMatch);
        this.rule = rule;
    }

    // Overridden because of DSE purposes
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        RecordingActivation<MatchType> oA = (RecordingActivation<MatchType>) obj;
        if (oA.patternMatch.equals(this.patternMatch) && oA.rule.equals(this.rule)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "[AbstractRule: " + rule + "][Match: " + patternMatch + "][State: " + state + "][Fired: " + fired + "]";
    }

    /**
     * Fires the activation and records the EMF model manipulations within a {@link RecordingCommand}. This way the
     * model manipulations can be re- or undone.
     * 
     * Note that, if the {@link RecordingRule} is not registered on a {@link TransactionalEditingDomain} then the model
     * manipulation will not be recorded and null will be returned.
     * 
     * @return the {@link RecordingCommand} which recorded the model manipulations during firing or null if recording
     *         was not possible
     */
    public RecordingCommand fireWithRecording() {
        TransactionalEditingDomain domain = this.rule.getEditingDomain();
        if (domain == null) {
            fire();
            return null;
        } else {
            final RecordingCommand command = new RecordingCommand(domain) {
                @Override
                protected void doExecute() {
                    fire();
                }
            };
            command.setLabel(RECORDING_ACTIVATION);
            domain.getCommandStack().execute(command);

            return command;
        }
    }
}
