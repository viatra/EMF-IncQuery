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

import org.eclipse.incquery.runtime.api.IPatternMatch;

/**
 * @author Abel Hegedus
 *
 */
public abstract class Job<Match extends IPatternMatch> {

    private ActivationState activationState;
    
    /**
     * @return the activationState
     */
    public ActivationState getActivationState() {
        return activationState;
    }
    
    /**
     * 
     */
    public Job(ActivationState activationState) {
        this.activationState = checkNotNull(activationState, "Cannot create job with null activation state!");
    }
    
    public abstract void execute(final Activation<Match> activation, Context context);
    
}
