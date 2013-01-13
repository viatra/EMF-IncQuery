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

import org.eclipse.incquery.runtime.triggerengine.api.ActivationState;

/**
 * This is the default implementation for an activation life cycle.
 * 
 * The following is the summary of the possible transitions, in the form of StateFrom -Event-> StateTo (U : Update state
 * used, D : Disappeared state used), :
 * <ul>
 * <li>Inactive -Match Appears-> Appeared</li>
 * <li>Appeared -Match Disappears-> Inactive</li>
 * <li>Appeared -Activation fires-> Fired</li>
 * <li>Fired -Match Updates-> Updated (U)</li>
 * <li>Updated -Activation fires-> Fired (U)</li>
 * <li>Updated -Match Disappears-> Inactive (U), Disappeared (UD)</li>
 * <li>Fired -Match Disappears-> Inactive / Disappeared (D)</li>
 * <li>Disappeared -Match Appears-> Fired (D)</li>
 * <li>Disappeared -Activation fires-> Inactive (D)</li>
 * </ul>
 * 
 * @author Abel Hegedus
 * 
 */
public final class DefaultActivationLifeCycle extends UnmodifiableActivationLifeCycle {

    private static DefaultActivationLifeCycle DEFAULT;
    private static DefaultActivationLifeCycle DEFAULT_NO_UPDATE;
    private static DefaultActivationLifeCycle DEFAULT_NO_DISAPPEAR;
    private static DefaultActivationLifeCycle DEFAULT_NO_UPDATE_AND_DISAPPEAR;

    /**
     * Creates an activation life cycle with the default state transition map.
     * 
     * @param updateStateUsed
     *            if set, the Updated activation state is also used
     * @param disappearedStateUsed
     *            if set, the Disappeared activations state is also used
     */
    public DefaultActivationLifeCycle(boolean updateStateUsed, boolean disappearedStateUsed) {

        internalAddStateTransition(ActivationState.INACTIVE, ActivationLifeCycleEvent.MATCH_APPEARS,
                ActivationState.APPEARED);

        internalAddStateTransition(ActivationState.APPEARED, ActivationLifeCycleEvent.MATCH_DISAPPEARS,
                ActivationState.INACTIVE);
        internalAddStateTransition(ActivationState.APPEARED, ActivationLifeCycleEvent.ACTIVATION_FIRES,
                ActivationState.FIRED);

        if (updateStateUsed) {
            internalAddStateTransition(ActivationState.FIRED, ActivationLifeCycleEvent.MATCH_UPDATES,
                    ActivationState.UPDATED);
            internalAddStateTransition(ActivationState.UPDATED, ActivationLifeCycleEvent.ACTIVATION_FIRES,
                    ActivationState.FIRED);
            if (disappearedStateUsed) {
                internalAddStateTransition(ActivationState.UPDATED, ActivationLifeCycleEvent.MATCH_DISAPPEARS,
                        ActivationState.DISAPPEARED);
            } else {
                internalAddStateTransition(ActivationState.UPDATED, ActivationLifeCycleEvent.MATCH_DISAPPEARS,
                        ActivationState.INACTIVE);
            }
        }

        if (disappearedStateUsed) {
            internalAddStateTransition(ActivationState.FIRED, ActivationLifeCycleEvent.MATCH_DISAPPEARS,
                    ActivationState.DISAPPEARED);
            internalAddStateTransition(ActivationState.DISAPPEARED, ActivationLifeCycleEvent.MATCH_APPEARS,
                    ActivationState.FIRED);
            internalAddStateTransition(ActivationState.DISAPPEARED, ActivationLifeCycleEvent.ACTIVATION_FIRES,
                    ActivationState.INACTIVE);
        } else {
            internalAddStateTransition(ActivationState.FIRED, ActivationLifeCycleEvent.MATCH_DISAPPEARS,
                    ActivationState.INACTIVE);
        }

    }

    /**
     * Creates an activation life cycle with the default state transition map using both Updated and Disappeared states.
     */
    public DefaultActivationLifeCycle() {
        this(true, true);
    }

    /**
     * @return the dEFAULT
     */
    public static DefaultActivationLifeCycle getDEFAULT() {
        if (DEFAULT == null) {
            DEFAULT = new DefaultActivationLifeCycle();
        }
        return DEFAULT;
    }

    /**
     * @return the dEFAULT_NO_UPDATE
     */
    public static DefaultActivationLifeCycle getDEFAULT_NO_UPDATE() {
        if (DEFAULT_NO_UPDATE == null) {
            DEFAULT_NO_UPDATE = new DefaultActivationLifeCycle(false, true);
        }
        return DEFAULT_NO_UPDATE;
    }

    /**
     * @return the dEFAULT_NO_DISAPPEAR
     */
    public static DefaultActivationLifeCycle getDEFAULT_NO_DISAPPEAR() {
        if (DEFAULT_NO_DISAPPEAR == null) {
            DEFAULT_NO_DISAPPEAR = new DefaultActivationLifeCycle(true, false);
        }
        return DEFAULT_NO_DISAPPEAR;
    }

    /**
     * @return the dEFAULT_NO_UPDATE_AND_DISAPPEAR
     */
    public static DefaultActivationLifeCycle getDEFAULT_NO_UPDATE_AND_DISAPPEAR() {
        if (DEFAULT_NO_UPDATE_AND_DISAPPEAR == null) {
            DEFAULT_NO_UPDATE_AND_DISAPPEAR = new DefaultActivationLifeCycle(false, false);
        }
        return DEFAULT_NO_UPDATE_AND_DISAPPEAR;
    }

}
