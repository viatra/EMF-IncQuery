/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.construction;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherContext;

/**
 * Exchangeable component of ReteEngine, responsible for building pattern matcher rete subnets.
 * 
 * @author Gabor Bergmann
 */
public interface IRetePatternBuilder<PatternDescription, StubHandle, Collector> {

    /**
     * Builds a part of the rete network that will match occurences of a given pattern.
     * 
     * @param gtPattern
     *            the pattern whose matcher subnet has to be built.
     * @return production. the Production node that should store matchings of the given pattern.
     * @throws RetePatternBuildException
     *             if construction fails.
     */
    Collector construct(PatternDescription gtPattern) throws RetePatternBuildException;

    /**
     * Extract the position mapping of the graph pattern.
     */
    Map<Object, Integer> getPosMapping(PatternDescription gtPattern);

    // /**
    // * Extends the rete network beyond a production node to
    // * further constrain the symbolic parameters with containment scopes.
    // *
    // * @param unscopedProduction
    // * the production node to be extended.
    // * @param additionalScopeMap
    // * maps the indices of a subset of the symbolic variables to
    // * the scopes that are to be applied on those variables
    // * @param production
    // * the now-empty Production node that should store matchings of
    // * the given pattern.
    // * @return production.
    // * @throws PatternMatcherCompileTimeException
    // * if construction fails.
    // */
    // Collector constructScoper(
    // Address<? extends Production> unscopedProduction,
    // Map<Integer, Scope> additionalScopeMap,
    // Collector production)
    // throws PatternMatcherCompileTimeException;

    // /**
    // * Returns the buildable associated with this builder.
    // */
    // public Buildable<PatternDescription, StubHandle, Collector> getBuildable();
    /**
     * Returns the context associated with this builder.
     */
    public IPatternMatcherContext<PatternDescription> getContext();

    /**
     * After the ReteEngine is reinitialized, the pattern builder has to be notified about the change.
     */
    void refresh();

}
