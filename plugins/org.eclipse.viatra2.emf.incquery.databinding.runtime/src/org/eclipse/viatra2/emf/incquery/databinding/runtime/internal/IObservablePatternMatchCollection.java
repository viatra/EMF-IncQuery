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

import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

/**
 * Common interface for observable pattern match collections (e.g. 
 * {@link ObservablePatternMatchList} and {@link ObservablePatternMatchSet}).
 * 
 * @author Abel Hegedus
 *
 */
public interface IObservablePatternMatchCollection<Match extends IPatternMatch>{

    /**
     * Can be called to indicate that 
     * @param match
     */
    void addMatch(Match match);

    void removeMatch(Match match);
    
}
