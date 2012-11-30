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

package org.eclipse.incquery.runtime.api;

/**
 * A generic, abstract match processor for handling matches as arrays.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 * @author Bergmann Gábor
 */
public abstract class GenericMatchProcessor implements IMatchProcessor<IPatternMatch> {

    @Override
    public void process(IPatternMatch match) {
        process(match.toArray());
    }

    /**
     * Defines the action that is to be executed on each match.
     * 
     * @param parameters
     *            a single match of the pattern that must be processed by the implementation of this method, represented
     *            as an array containing the values of each pattern parameter
     */
    public abstract void process(Object[] parameters);

}
