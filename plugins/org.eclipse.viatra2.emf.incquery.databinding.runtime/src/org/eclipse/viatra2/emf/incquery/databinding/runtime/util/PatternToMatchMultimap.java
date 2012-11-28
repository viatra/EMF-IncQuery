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
package org.eclipse.viatra2.emf.incquery.databinding.runtime.util;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.patternlanguage.emf.core.patternLanguage.Pattern;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.Agenda;
import org.eclipse.viatra2.emf.incquery.triggerengine.qrm.TriggeredQueryResultMultimap;

/**
 * Multimap for managing multiple patterns and related matches for a given notifier.
 * 
 * @author Abel Hegedus
 *
 */
public class PatternToMatchMultimap<MatchType extends IPatternMatch> extends TriggeredQueryResultMultimap<MatchType, Pattern, MatchType> {

    /**
     * Creates a new multimap for the given agenda.
     * 
     * @param agenda the agenda to use
     */
    public PatternToMatchMultimap(Agenda agenda) {
        super(agenda);
    }
    
    /**
     * Creates a new multimap for the given engine.
     * 
     * @param engine the engine to use
     */
    public PatternToMatchMultimap(IncQueryEngine engine) {
        super(engine);
    }
    
    /**
     * Creates a new multimap for the given notifier
     * 
     * @param notifier the notifier to use
     */
    public PatternToMatchMultimap(Notifier notifier) {
        super(notifier);
    }

    /* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.util.TriggeredQueryResultMultimap#getKeyFromMatch(org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch)
     */
    @Override
    protected Pattern getKeyFromMatch(MatchType match) {
        return match.pattern();
    }

    /* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.util.TriggeredQueryResultMultimap#getValueFromMatch(org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch)
     */
    @Override
    protected MatchType getValueFromMatch(MatchType match) {
        return match;
    }

}
