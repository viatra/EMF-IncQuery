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

package org.eclipse.incquery.runtime.api.impl;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.runtime.api.EngineManager;
import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * Base implementation of IMatcherFactory.
 * 
 * @author Bergmann Gábor
 * 
 */
public abstract class BaseMatcherFactory<Matcher extends IncQueryMatcher<? extends IPatternMatch>> implements
        IMatcherFactory<Matcher> {

    protected abstract Matcher instantiate(IncQueryEngine engine) throws IncQueryException;

    @Override
    public Matcher getMatcher(Notifier emfRoot) throws IncQueryException {
        IncQueryEngine engine = EngineManager.getInstance().getIncQueryEngine(emfRoot);
        return instantiate(engine);
    }

    @Override
    public Matcher getMatcher(IncQueryEngine engine) throws IncQueryException {
        return instantiate(engine);
    }

    private String fullyQualifiedName;

    @Override
    public String getPatternFullyQualifiedName() {
        if (fullyQualifiedName == null)
            fullyQualifiedName = CorePatternLanguageHelper.getFullyQualifiedName(getPattern());
        return fullyQualifiedName;
    }

    // // EXPERIMENTAL
    //
    // @Override
    // public Matcher getMatcher(TransactionalEditingDomain trDomain) throws IncQueryRuntimeException {
    // return getMatcher(trDomain, 0);
    // }
    //
    // @Override
    // public Matcher getMatcher(TransactionalEditingDomain trDomain, int numThreads) throws IncQueryRuntimeException {
    // try {
    // IncQueryEngine engine = EngineManager.getInstance().getReteEngine(trDomain, numThreads);
    // return instantiate(engine);
    // } catch (RetePatternBuildException e) {
    // throw new IncQueryRuntimeException(e);
    // }
    // }

}
