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

package org.eclipse.incquery.runtime.rete.network;

import java.util.Map;

import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * Interface intended for nodes containing complete matches.
 * 
 * @author Gabor Bergmann
 */
public interface Production extends Tunnel, Iterable<Tuple> {

    /**
     * @return the position mapping of this particular pattern that maps members of the tuple type to their positions
     */
    Map<Object, Integer> getPosMapping();

    // /**
    // * Removes all parents of the production node,
    // * making it once independent from the pattern recognition subnet,
    // * so that a new pattern definition can be applied.
    // */
    // void tearOff();
    //
    // /**
    // * Sets the dirty flag.
    // */
    // void setDirty(boolean dirty);
    //
    // /**
    // * Returns the value of the dirty flag.
    // * If true, pattern matcher needs to be reconstructed.
    // */
    // boolean isDirty();
}
