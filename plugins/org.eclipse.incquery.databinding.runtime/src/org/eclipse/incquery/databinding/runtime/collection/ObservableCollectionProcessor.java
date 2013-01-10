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
package org.eclipse.incquery.databinding.runtime.collection;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.base.itc.alg.incscc.Direction;

/**
 * Match processor that can be parameterized with a {@link Direction} and an {@link IObservablePatternMatchCollectionUpdate}.
 * It can be registered for rules that take care of keeping the observable collection up-to-date (see
 * {@link ObservableCollectionHelper#createRuleInAgenda}).
 * 
 * @author Abel Hegedus
 * 
 * @param <Match>
 */
public class ObservableCollectionProcessor<Match extends IPatternMatch> implements IMatchProcessor<Match> {

    private final Direction direction;
    private final IObservablePatternMatchCollectionUpdate<Match> collection;

    /**
     * Creates a processor with the given direction and observable collection.
     * 
     * @param direction
     *            the {@link Direction} of updates that are handled
     * @param collection
     *            the {@link IObservablePatternMatchCollectionUpdate} to manage
     */
    public ObservableCollectionProcessor(Direction direction, IObservablePatternMatchCollectionUpdate<Match> collection) {
        this.direction = direction;
        this.collection = collection;
    }

    @Override
    public void process(Match match) {
        if (direction == Direction.INSERT) {
            collection.addMatch(match);
        } else {
            collection.removeMatch(match);
        }

    }

}