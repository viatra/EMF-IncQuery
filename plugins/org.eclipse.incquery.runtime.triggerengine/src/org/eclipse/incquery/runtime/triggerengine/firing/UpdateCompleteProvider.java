/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.triggerengine.firing;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.incquery.runtime.triggerengine.notification.IActivationNotificationListener;

/**
 * @author Abel Hegedus
 *
 */
public abstract class UpdateCompleteProvider implements IUpdateCompleteProvider {

    private Set<IUpdateCompleteListener> listeners;
    
    /**
     * 
     */
    public UpdateCompleteProvider() {
        listeners = new HashSet<IUpdateCompleteListener>();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.firing.IUpdateCompleteProvider#addUpdateCompleteListener(org.eclipse.viatra2.emf.incquery.triggerengine.firing.IUpdateCompleteListener, boolean)
     */
    @Override
    public boolean addUpdateCompleteListener(IUpdateCompleteListener listener, boolean fireNow) {
        boolean added = listeners.add(listener);
        if(added) {
            listener.updateComplete();
        }
        return added;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.firing.IUpdateCompleteProvider#removeUpdateCompleteListener(org.eclipse.viatra2.emf.incquery.triggerengine.firing.IUpdateCompleteListener)
     */
    @Override
    public boolean removeUpdateCompleteListener(IUpdateCompleteListener listener) {
        return this.listeners.remove(listener);
    }
    
    protected void updateCompleted() {
        for (IUpdateCompleteListener listener : this.listeners) {
            listener.updateComplete();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.firing.IUpdateCompleteProvider#dispose()
     */
    @Override
    public void dispose() {
        listeners.clear();
    }
    
}
