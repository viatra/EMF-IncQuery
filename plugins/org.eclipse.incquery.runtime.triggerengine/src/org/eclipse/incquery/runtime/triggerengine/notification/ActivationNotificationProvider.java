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

package org.eclipse.incquery.runtime.triggerengine.notification;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.triggerengine.api.Activation;

/**
 * Classes implement this interface to provide notifications about the changes in the collection of activations within
 * the AbstractRule Engine.
 * 
 * @author Tamas Szabo
 * 
 */
public abstract class ActivationNotificationProvider implements IActivationNotificationProvider {

    protected Set<IActivationNotificationListener> activationNotificationListeners;

    public ActivationNotificationProvider() {
        this.activationNotificationListeners = new HashSet<IActivationNotificationListener>();
    }

    @Override
    public boolean addActivationNotificationListener(IActivationNotificationListener listener, boolean fireNow) {
        boolean notContained = this.activationNotificationListeners.add(listener);
        if (notContained) {
            listenerAdded(listener, fireNow);
        }
        return notContained;
    }

    protected abstract void listenerAdded(IActivationNotificationListener listener, boolean fireNow);

    @Override
    public boolean removeActivationNotificationListener(IActivationNotificationListener listener) {
        return this.activationNotificationListeners.remove(listener);
    }

    public void notifyActivationAppearance(Activation<? extends IPatternMatch> activation) {
        for (IActivationNotificationListener listener : this.activationNotificationListeners) {
            listener.activationAppeared(activation);
        }
    }

    public void notifyActivationDisappearance(Activation<? extends IPatternMatch> activation) {
        for (IActivationNotificationListener listener : this.activationNotificationListeners) {
            listener.activationDisappeared(activation);
        }
    }
}
