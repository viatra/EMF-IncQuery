/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.base.itc.alg.incscc;

import java.util.Set;

import org.eclipse.incquery.runtime.base.itc.igraph.ITcObserver;

public class NotifierThread<V> extends Thread {

    private Set<V> sources;
    private Set<V> targets;
    private ITcObserver<V> observer;
    private int dir;

    public NotifierThread(Set<V> sources, Set<V> targets, ITcObserver<V> observer, int dir) {
        this.sources = sources;
        this.targets = targets;
        this.observer = observer;
        this.dir = dir;
    }

    @Override
    public void run() {
        for (V s : sources) {
            for (V t : targets) {
                if (dir == 1) {
                    observer.tupleInserted(s, t);
                }
                if (dir == -1) {
                    observer.tupleDeleted(s, t);
                }

            }
        }
    }

}
