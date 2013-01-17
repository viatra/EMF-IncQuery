/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.triggerengine.specific;

import org.eclipse.incquery.runtime.triggerengine.api.Scheduler;
import org.eclipse.incquery.runtime.triggerengine.api.Executor;

/**
 * A timed scheduler is similar to the {@link UpdateCompleteBasedScheduler} but it schedules in a periodic manner.
 *  One must define the interval between two consecutive schedulings.
 * 
 * @author Tamas Szabo
 * 
 */
public class TimedScheduler extends Scheduler {
    private long interval;
    private volatile boolean interrupted = false;
    private FiringThread firingThread;
    
    public TimedScheduler(Executor engine, long interval) {
        super(engine);
        this.interval = interval;
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
                schedule();
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
    
    public static class TimedSchedulerFactory implements ISchedulerFactory{
        
        private long interval;
        
        /**
         * @param interval the interval to set
         */
        public void setInterval(long interval) {
            this.interval = interval;
        }
        
        /**
         * @return the interval
         */
        public long getInterval() {
            return interval;
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.incquery.runtime.triggerengine.api.Scheduler.ISchedulerFactory#prepareScheduler()
         */
        @Override
        public Scheduler prepareScheduler(Executor engine) {
            return new TimedScheduler(engine, interval);
        }
        
        /**
         * 
         */
        public TimedSchedulerFactory(long interval) {
            this.interval = interval;
        }
        
    }
}
