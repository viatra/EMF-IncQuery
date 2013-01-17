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

package org.eclipse.incquery.runtime.rete.construction.psystem.basicdeferred;

import java.util.Collections;
import java.util.Set;

import org.eclipse.incquery.runtime.rete.collections.CollectionsFactory;
import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;
import org.eclipse.incquery.runtime.rete.construction.Stub;
import org.eclipse.incquery.runtime.rete.construction.psystem.DeferredPConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.PSystem;
import org.eclipse.incquery.runtime.rete.construction.psystem.PVariable;

/**
 * @author Bergmann Gábor
 * 
 */
public class Equality<PatternDescription, StubHandle> extends DeferredPConstraint<PatternDescription, StubHandle> {

    private PVariable who;
    private PVariable withWhom;

    /**
     * @param buildable
     * @param affectedVariables
     */
    public Equality(PSystem<PatternDescription, StubHandle, ?> pSystem, PVariable who, PVariable withWhom) {
        super(pSystem, buildSet(who, withWhom));
        this.who = who;
        this.withWhom = withWhom;
    }

    private static Set<PVariable> buildSet(PVariable who, PVariable withWhom) {
        Set<PVariable> set = CollectionsFactory.getSet();//new HashSet<PVariable>();
        set.add(who);
        set.add(withWhom);
        return set;
    }

    public boolean isMoot() {
        return who.equals(withWhom);
    }

    @Override
    public void doReplaceVariable(PVariable obsolete, PVariable replacement) {
        if (obsolete.equals(who))
            who = replacement;
        if (obsolete.equals(withWhom))
            withWhom = replacement;
    }

    @Override
    protected String toStringRest() {
        return who.getName() + "=" + withWhom.getName();
    }

    /**
     * @return the who
     */
    public PVariable getWho() {
        return who;
    }

    /**
     * @return the withWhom
     */
    public PVariable getWithWhom() {
        return withWhom;
    }

    @Override
    public Set<PVariable> getDeducedVariables() {
        return Collections.emptySet();
    }

    @Override
    public boolean isReadyAt(Stub<StubHandle> stub) {
        return isMoot() || stub.getVariablesIndex().containsKey(who) && stub.getVariablesIndex().containsKey(withWhom);
        // will be replaced by || if copierNode is available;
        // until then, LayoutHelper.unifyVariablesAlongEqualities(PSystem<PatternDescription, StubHandle, Collector>) is
        // recommended.
    }

    @Override
    protected Stub<StubHandle> doCheckOn(Stub<StubHandle> stub) throws RetePatternBuildException {
        if (isMoot())
            return stub;

        Integer index1 = stub.getVariablesIndex().get(who);
        Integer index2 = stub.getVariablesIndex().get(withWhom);
        if (index1 != null && index2 != null) {
            if (index1.equals(index2))
                return stub;
            else
                return buildable.buildEqualityChecker(stub, new int[] { index1, index2 });
        } else if (index1 == null) {
            // TODO build copierNode here
        }
        return null;
    }

    @Override
    public void raiseForeverDeferredError(Stub<StubHandle> stub) throws RetePatternBuildException {
        String[] args = { who.toString(), withWhom.toString() };
        String msg = "Cannot express equality of variables {1} and {2} if neither of them is deducable.";
        String shortMsg = "Equality between undeducible variables.";
        throw new RetePatternBuildException(msg, args, shortMsg, null);
    }
}
