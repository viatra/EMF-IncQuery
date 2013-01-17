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

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.rete.collections.CollectionsFactory;
import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;
import org.eclipse.incquery.runtime.rete.construction.Stub;
import org.eclipse.incquery.runtime.rete.construction.psystem.PSystem;
import org.eclipse.incquery.runtime.rete.construction.psystem.PVariable;
import org.eclipse.incquery.runtime.rete.construction.psystem.VariableDeferredPConstraint;

/**
 * @author Bergmann Gábor
 * 
 */
public class Inequality<PatternDescription, StubHandle> extends
        VariableDeferredPConstraint<PatternDescription, StubHandle>
// implements IFoldablePConstraint
{

    private PVariable who;
    private PVariable withWhom;
    // private IFoldablePConstraint incorporator;

    /**
     * The inequality constraint is weak if it can be ignored when who is the same as withWhom, or if any if them is
     * undeducible.
     */
    private boolean weak;

    public Inequality(PSystem<PatternDescription, StubHandle, ?> pSystem, PVariable who, PVariable withWhom) {
        this(pSystem, who, withWhom, false);
    }

    public Inequality(PSystem<PatternDescription, StubHandle, ?> pSystem, PVariable who, PVariable withWhom,
            boolean weak) {
        super(pSystem, CollectionsFactory.getSet(Arrays.asList(new PVariable[] { who, withWhom }) ));
        // this(pSystem, who, Collections.singleton(withWhom));
        this.who = who;
        this.withWhom = withWhom;
        this.weak = weak;
    }

    // private Inequality(
    // PSystem<PatternDescription, StubHandle, ?> pSystem,
    // PVariable subject, Set<PVariable> inequals)
    // {
    // super(pSystem, include(inequals, subject));
    // this.subject = subject;
    // this.inequals = inequals;
    // }

    // private static HashSet<PVariable> include(Set<PVariable> inequals, PVariable subject) {
    // HashSet<PVariable> hashSet = new HashSet<PVariable>(inequals);
    // hashSet.add(subject);
    // return hashSet;
    // }

    @Override
    protected Set<PVariable> getDeferringVariables() {
        return getAffectedVariables();
    }

    @Override
    protected Stub<StubHandle> doCheckOn(Stub<StubHandle> stub) throws RetePatternBuildException {
        Map<Object, Integer> variablesIndex = stub.getVariablesIndex();
        return buildable.buildInjectivityChecker(stub, variablesIndex.get(who),
                new int[] { variablesIndex.get(withWhom) });
    }

    // private static int[] mapIndices(Map<Object, Integer> variablesIndex, Set<PVariable> keys) {
    // int[] result = new int[keys.size()];
    // int k = 0;
    // for (PVariable key : keys) {
    // result[k++] = variablesIndex.get(key);
    // }
    // return result;
    // }

    // @Override
    // public IFoldablePConstraint getIncorporator() {
    // return incorporator;
    // }
    //
    // @Override
    // public void registerIncorporatationInto(IFoldablePConstraint incorporator) {
    // this.incorporator = incorporator;
    // }
    //
    // @Override
    // public boolean incorporate(IFoldablePConstraint other) {
    // if (other instanceof Inequality<?, ?>) {
    // Inequality other2 = (Inequality) other;
    // if (subject.equals(other2.subject)) {
    // Set<PVariable> newInequals = new HashSet<PVariable>(inequals);
    // newInequals.addAll(other2.inequals);
    // return new Inequality<PatternDescription, StubHandle>(buildable, subject, newInequals);
    // }
    // } else return false;
    // }

    @Override
    protected String toStringRest() {
        return who.toString() + (isWeak() ? "!=?" : "!=") + withWhom.toString();
    }

    @Override
    public void doReplaceVariable(PVariable obsolete, PVariable replacement) {
        if (obsolete.equals(who))
            who = replacement;
        if (obsolete.equals(withWhom))
            withWhom = replacement;
    }

    @Override
    public Set<PVariable> getDeducedVariables() {
        return Collections.emptySet();
    }

    /**
     * The inequality constraint is weak if it can be ignored when who is the same as withWhom, or if any if them is
     * undeducible.
     * 
     * @return the weak
     */
    public boolean isWeak() {
        return weak;
    }

    /**
     * A weak inequality constraint is eliminable if who is the same as withWhom, or if any if them is undeducible.
     */
    public boolean isEliminable() {
        return isWeak() && (who.equals(withWhom) || !who.isDeducable() || !withWhom.isDeducable());
    }

    /**
     * Eliminates a weak inequality constraint if it can be ignored when who is the same as withWhom, or if any if them
     * is undeducible.
     */
    public void eliminateWeak() {
        if (isEliminable())
            delete();
    }

}
