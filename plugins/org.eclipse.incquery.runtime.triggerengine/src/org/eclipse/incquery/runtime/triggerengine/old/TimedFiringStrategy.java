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

package org.eclipse.incquery.runtime.triggerengine.firing;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.triggerengine.api.Activation;
import org.eclipse.incquery.runtime.triggerengine.api.ActivationMonitor;

/**
 * A timed firing strategy is similar to the {@link AutomaticFiringStrategy} as it also fires all the applicable
 * activations but it does so in a periodic manner. One must define the interval between two consecutive firings.
 * 
 * @author Tamas Szabo
 * 
 */
public class TimedFiringStrategy {

    private long interval;
    private volatile boolean interrupted = false;
    private ActivationMonitor monitor;
    private FiringThread firingThread;

    public TimedFiringStrategy(ActivationMonitor monitor, long interval) {
        this.interval = interval;
        this.monitor = monitor;
        this.firingThread = new FiringThread();
    }

    public void start() {
        this.firingThread.start();
    }

    private class FiringThread extends Thread {

        public FiringThread() {
            this.setName("TimedFiringStrategy [interval: " + interval + "]");
        }

        @Override
        public void run() {
            while (!interrupted) {
                for (Activation<? extends IPatternMatch> a : monitor.getActivations()) {
                    a.fire();
                }
                monitor.clear();
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                }
            }
        }
    }

    public void dispose() {
        interrupted = true;
    }
}
