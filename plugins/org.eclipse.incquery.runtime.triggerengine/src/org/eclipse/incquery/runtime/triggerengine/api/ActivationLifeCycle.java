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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * @author Abel Hegedus
 * 
 * TODO implement life cycle management
 *  - handle events that may cause activation state changes
 *  - match appear/disappear/update, activation firing
 *  - does NOT include functionality related to jobs or triggering!
 */
public class ActivationLifeCycle {

    public enum ActivationLifeCycleEvent{
        MATCH_APPEARS, MATCH_DISAPPEARS, MATCH_UPDATES, ACTIVATION_FIRES
    }
    
    private Table<ActivationState, ActivationLifeCycleEvent, ActivationState> stateTransitionTable;
    
    public ActivationState nextActivationState(ActivationState currentState, ActivationLifeCycleEvent event) {
        if(stateTransitionTable != null) {
            return stateTransitionTable.get(currentState, event);
        } else {
            return null;
        }
    }
    
    public boolean addStateTransition(ActivationState from, ActivationLifeCycleEvent event, ActivationState to) {
        if(stateTransitionTable == null) {
            stateTransitionTable = HashBasedTable.create();
        }
        if(stateTransitionTable.contains(from, event)) {
            return false;
        } else {
            stateTransitionTable.put(from, event, to);
            return true;
        }
    }
    
}
