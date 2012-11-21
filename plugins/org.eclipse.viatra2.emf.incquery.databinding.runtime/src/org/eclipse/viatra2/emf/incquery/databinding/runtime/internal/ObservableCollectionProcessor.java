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
package org.eclipse.viatra2.emf.incquery.databinding.runtime.internal;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.incscc.Direction;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

public class ObservableCollectionProcessor<Match extends IPatternMatch> implements IMatchProcessor<Match>{

    private Direction direction;
    private IObservablePatternMatchCollection<Match> collection;
    
    public ObservableCollectionProcessor(Direction direction, IObservablePatternMatchCollection<Match> collection) {
        this.direction = direction;
        this.collection = collection;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor#process(org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch)
     */
    @Override
    public void process(Match match) {
        if(direction == Direction.INSERT) {
            collection.addMatch(match);
        } else {
            collection.removeMatch(match);
        }
        
    }
    
}