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

package org.eclipse.incquery.runtime.rete.construction.quasitree;

import java.util.Collections;
import java.util.Set;

import org.eclipse.incquery.runtime.rete.construction.Stub;

/**
 * @author Bergmann Gábor
 * 
 */
class JoinCandidate<StubHandle> {
    Stub<StubHandle> primary;
    Stub<StubHandle> secondary;

    Set<Object> varPrimary;
    Set<Object> varSecondary;

    JoinCandidate(Stub<StubHandle> primary, Stub<StubHandle> secondary) {
        super();
        this.primary = primary;
        this.secondary = secondary;

        varPrimary = getPrimary().getVariablesSet();
        varSecondary = getSecondary().getVariablesSet();

    }

    /**
     * @return the a
     */
    public Stub<StubHandle> getPrimary() {
        return primary;
    }

    /**
     * @return the b
     */
    public Stub<StubHandle> getSecondary() {
        return secondary;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return primary.toString() + " |x| " + secondary.toString();
    }

    /**
     * @return the varPrimary
     */
    public Set<Object> getVarPrimary() {
        return varPrimary;
    }

    /**
     * @return the varSecondary
     */
    public Set<Object> getVarSecondary() {
        return varSecondary;
    }

    public boolean isTrivial() {
        return getPrimary().equals(getSecondary());
    }

    public boolean isCheckOnly() {
        return varPrimary.containsAll(varSecondary) || varSecondary.containsAll(varPrimary);
    }

    public boolean isDescartes() {
        return Collections.disjoint(varPrimary, varSecondary);
    }

}
