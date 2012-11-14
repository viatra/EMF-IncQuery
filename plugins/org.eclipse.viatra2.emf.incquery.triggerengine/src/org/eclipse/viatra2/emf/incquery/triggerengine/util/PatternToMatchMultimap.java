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
package org.eclipse.viatra2.emf.incquery.triggerengine.util;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.triggerengine.Agenda;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * @author Abel Hegedus
 *
 */
public class PatternToMatchMultimap<MatchType extends IPatternMatch> extends TriggeredQueryResultMultiMap<MatchType, Pattern, MatchType> {

    /**
     * @param agenda
     */
    public PatternToMatchMultimap(Agenda agenda) {
        super(agenda);
    }
    
    /**
     * @param engine
     */
    public PatternToMatchMultimap(IncQueryEngine engine) {
        super(engine);
    }
    
    /**
     * @param notifier
     */
    public PatternToMatchMultimap(Notifier notifier) {
        super(notifier);
    }

    /* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.util.TriggeredQueryResultMultiMap#getKeyFromMatch(org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch)
     */
    @Override
    protected Pattern getKeyFromMatch(MatchType match) {
        return match.pattern();
    }

    /* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.util.TriggeredQueryResultMultiMap#getValueFromMatch(org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch)
     */
    @Override
    protected MatchType getValueFromMatch(MatchType match) {
        return match;
    }

}
