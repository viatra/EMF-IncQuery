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
package org.eclipse.incquery.databinding.runtime.util;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.triggerengine.qrm.TriggeredQueryResultMultimap;

/**
 * Multimap for managing multiple patterns and related matches for a given notifier.
 * 
 * @author Abel Hegedus
 * 
 */
public class PatternToMatchMultimap<MatchType extends IPatternMatch> extends
        TriggeredQueryResultMultimap<MatchType, Pattern, MatchType> {

    /**
     * Creates a new multimap for the given engine.
     * 
     * @param engine
     *            the engine to use
     */
    public PatternToMatchMultimap(IncQueryEngine engine) {
        super(engine);
    }

    /**
     * Creates a new multimap for the given notifier
     * 
     * @param notifier
     *            the notifier to use
     * @throws IncQueryException  if the {@link IncQueryEngine} creation fails on the {@link Notifier}
     */
    public PatternToMatchMultimap(Notifier notifier) throws IncQueryException {
        super(notifier);
    }

    @Override
    protected Pattern getKeyFromMatch(MatchType match) {
        return match.pattern();
    }

    @Override
    protected MatchType getValueFromMatch(MatchType match) {
        return match;
    }

}
