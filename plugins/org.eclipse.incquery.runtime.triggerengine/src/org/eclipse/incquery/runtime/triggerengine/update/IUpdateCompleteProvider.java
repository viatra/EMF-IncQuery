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
package org.eclipse.incquery.runtime.triggerengine.update;

import org.eclipse.incquery.runtime.triggerengine.notification.IActivationNotificationListener;

/**
 * @author Abel Hegedus
 * 
 */
public interface IUpdateCompleteProvider {

    /**
     * Registers an {@link IUpdateCompleteListener} to receive notification on completed updates.
     * 
     * <p>
     * The listener can be unregistered via {@link #removeUpdateCompleteListener(IUpdateCompleteListener)}.
     * 
     * @param fireNow
     *            if true, listener will be immediately invoked without waiting for the next update
     * 
     * @param listener
     *            the listener that will be notified of each completed update
     */
    boolean addUpdateCompleteListener(IUpdateCompleteListener listener, boolean fireNow);

    /**
     * Unregisters a listener registered by
     * {@link #addActivationNotificationListener(IActivationNotificationListener, boolean)}.
     * 
     * @param listener
     *            the listener that will no longer be notified.
     */
    boolean removeUpdateCompleteListener(IUpdateCompleteListener listener);

    /**
     * 
     */
    void dispose();

}