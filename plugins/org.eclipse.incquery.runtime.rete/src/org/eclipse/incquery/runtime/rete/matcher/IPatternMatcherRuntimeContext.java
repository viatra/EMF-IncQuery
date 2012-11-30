/*******************************************************************************
 * Copyright (c) 2004-2009 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.matcher;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import org.eclipse.incquery.runtime.rete.boundary.IManipulationListener;
import org.eclipse.incquery.runtime.rete.boundary.IPredicateTraceListener;

/**
 * Represents all knowledge of the outside world, that is needed durin runtime operation, towards the pattern matcher.
 * 
 * @author Bergmann Gábor
 * 
 */
public interface IPatternMatcherRuntimeContext<PatternDescription> extends IPatternMatcherContext<PatternDescription> {

    // ---------------------------------------------------------------------------------

    /**
     * @pre: network, framework, boundary, disconnectables initialised
     */
    IManipulationListener subscribePatternMatcherForUpdates(ReteEngine<PatternDescription> engine);

    /**
     * @pre: boundary, disconnectables initialised
     */
    IPredicateTraceListener subscribePatternMatcherForTraceInfluences(ReteEngine<PatternDescription> engine);

    Object ternaryEdgeTarget(Object relation);

    Object ternaryEdgeSource(Object relation);

    void enumerateAllUnaries(ModelElementCrawler crawler);

    void enumerateAllTernaryEdges(ModelElementCrawler crawler);

    void enumerateAllBinaryEdges(ModelElementPairCrawler crawler); // first=from, second=to

    void enumerateDirectUnaryInstances(Object typeObject, ModelElementCrawler crawler);

    void enumerateDirectTernaryEdgeInstances(Object typeObject, ModelElementCrawler crawler);

    void enumerateDirectBinaryEdgeInstances(Object typeObject, ModelElementPairCrawler crawler); // first=from,
                                                                                                 // second=to

    void enumerateAllUnaryInstances(Object typeObject, ModelElementCrawler crawler);

    void enumerateAllTernaryEdgeInstances(Object typeObject, ModelElementCrawler crawler);

    void enumerateAllBinaryEdgeInstances(Object typeObject, ModelElementPairCrawler crawler); // first=from, second=to

    void enumerateAllUnaryContainments(ModelElementPairCrawler crawler); // first=container, second=contained

    void enumerateAllInstantiations(ModelElementPairCrawler crawler); // first=type, second=instance

    void enumerateAllGeneralizations(ModelElementPairCrawler crawler); // first=supertype, second=subtype

    void modelReadLock();

    void modelReadUnLock();

    /**
     * The given runnable will be executed, and all model traversals will be delayed until the execution is done. If
     * there are any outstanding information to be read from the model, a single coalesced model traversal will
     * initialize the caches and deliver the notifications.
     * 
     * @param callable
     */
    public abstract <V> V coalesceTraversals(Callable<V> callable) throws InvocationTargetException;

    interface ModelElementCrawler {
        public void crawl(Object modelElement);
    }

    interface ModelElementPairCrawler {
        public void crawl(Object first, Object second);
    }
}
