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
package org.eclipse.incquery.runtime.triggerengine.notification;

/**
 * @author Abel Hegedus
 * 
 */
public interface IActivationNotificationProvider {

    /**
     * Registers an {@link IActivationNotificationListener} to receive updates on activation appearance and
     * disappearance.
     * 
     * <p>
     * The listener can be unregistered via
     * {@link #removeActivationNotificationListener(IActivationNotificationListener)}.
     * 
     * @param fireNow
     *            if true, listener will be immediately invoked on all current activations as a one-time effect.
     * 
     * @param listener
     *            the listener that will be notified of each new activation that appears or disappears, starting from
     *            now.
     */
    boolean addActivationNotificationListener(IActivationNotificationListener listener, boolean fireNow);

    /**
     * Unregisters a listener registered by
     * {@link #addActivationNotificationListener(IActivationNotificationListener, boolean)}.
     * 
     * @param listener
     *            the listener that will no longer be notified.
     */
    boolean removeActivationNotificationListener(IActivationNotificationListener listener);

}