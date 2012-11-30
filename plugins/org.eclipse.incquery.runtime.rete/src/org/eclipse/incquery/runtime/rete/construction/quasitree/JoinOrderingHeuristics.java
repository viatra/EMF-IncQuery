/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.construction.quasitree;

import java.util.Comparator;

import org.eclipse.incquery.runtime.rete.util.OrderingCompareAgent;

/**
 * @author Bergmann Gábor
 * 
 */
public class JoinOrderingHeuristics<PatternDescription, StubHandle, Collector> implements
        Comparator<JoinCandidate<StubHandle>> {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(JoinCandidate<StubHandle> jc1, JoinCandidate<StubHandle> jc2) {
        return new OrderingCompareAgent<JoinCandidate<StubHandle>>(jc1, jc2) {
            @Override
            protected void doCompare() {
                swallowBoolean(true && consider(preferTrue(a.isTrivial(), b.isTrivial()))
                        && consider(preferTrue(a.isCheckOnly(), b.isCheckOnly()))
                        && consider(preferFalse(a.isDescartes(), b.isDescartes()))

                        // TODO main heuristic decisions

                        && consider(preferLess(a.toString(), b.toString())));
            }
        }.compare();

    }

}