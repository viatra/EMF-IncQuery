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

package org.eclipse.incquery.runtime.rete.construction.psystem;

import java.util.Set;

import org.eclipse.incquery.runtime.rete.collections.CollectionsFactory;
import org.eclipse.incquery.runtime.rete.construction.Buildable;
import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;

/**
 * @author Bergmann Gábor
 * 
 */
public abstract class BasePConstraint<PatternDescription, StubHandle> implements PConstraint {
    protected PSystem<PatternDescription, StubHandle, ?> pSystem;
    protected Buildable<PatternDescription, StubHandle, ?> buildable;
    private final Set<PVariable> affectedVariables;

    /**
     * @param affectedVariables
     */
    public BasePConstraint(PSystem<PatternDescription, StubHandle, ?> pSystem, Set<PVariable> affectedVariables) {
        super();
        this.pSystem = pSystem;
        this.buildable = pSystem.getBuildable();
        this.affectedVariables = CollectionsFactory.getSet(affectedVariables);//new HashSet<PVariable>(affectedVariables);

        for (PVariable pVariable : affectedVariables) {
            pVariable.refer(this);
        }
        pSystem.registerConstraint(this);
    }

    @Override
    public String toString() {
        return "PC[" + getClass().getSimpleName() + ":" + toStringRest() + "]";
    }

    protected abstract String toStringRest();

    @Override
    public Set<PVariable> getAffectedVariables() {
        return affectedVariables;
    }

    @Override
    public void replaceVariable(PVariable obsolete, PVariable replacement) {
        if (affectedVariables.remove(obsolete)) {
            affectedVariables.add(replacement);
            obsolete.unrefer(this);
            replacement.refer(this);
            doReplaceVariable(obsolete, replacement);
        }
    }

    protected abstract void doReplaceVariable(PVariable obsolete, PVariable replacement);

    @Override
    public void delete() {
        for (PVariable pVariable : affectedVariables) {
            pVariable.unrefer(this);
        }
        pSystem.unregisterConstraint(this);
    }

    @Override
    public void checkSanity() throws RetePatternBuildException {
    }
}
