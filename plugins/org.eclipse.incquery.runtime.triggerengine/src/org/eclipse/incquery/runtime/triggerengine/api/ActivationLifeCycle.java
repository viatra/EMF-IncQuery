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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * life cycle management
 *  - handle events that may cause activation state changes
 *  - match appear/disappear/update, activation firing
 *  - does NOT include functionality related to jobs or triggering!
 *  
 * @author Abel Hegedus
 * 
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

    public boolean containsFrom(ActivationState state) {
        return stateTransitionTable.containsRow(state);
    }
    
    public boolean containsTo(ActivationState state) {
        return stateTransitionTable.containsValue(state);
    }
    
    public static ActivationLifeCycle copyOf(ActivationLifeCycle lifeCycle) {
        checkNotNull(lifeCycle,"Null life cycle cannot be copied!");
        ActivationLifeCycle lc = new ActivationLifeCycle();
        lc.stateTransitionTable = HashBasedTable.create(lifeCycle.stateTransitionTable);
        return lc;
    }
    
    /**
     * @return the stateTransitionTable
     */
    public Table<ActivationState, ActivationLifeCycleEvent, ActivationState> getStateTransitionTable() {
        return HashBasedTable.create(stateTransitionTable);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("table", stateTransitionTable).toString();
    }
}
