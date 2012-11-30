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

/**
 * Indicates whether a propagated update event signals the insertion or deletion of an element
 * 
 * @author Gabor Bergmann
 * 
 */
public enum Direction {
    INSERT, REVOKE;

    public Direction opposite() {
        switch (this) {
        case INSERT:
            return REVOKE;
        case REVOKE:
            return INSERT;
        default:
            return INSERT;
        }
    }
}
