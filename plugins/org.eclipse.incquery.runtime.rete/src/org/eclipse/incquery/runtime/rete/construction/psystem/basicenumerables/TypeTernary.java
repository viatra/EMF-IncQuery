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

package org.eclipse.incquery.runtime.rete.construction.psystem.basicenumerables;

import org.eclipse.incquery.runtime.rete.construction.Stub;
import org.eclipse.incquery.runtime.rete.construction.psystem.ITypeInfoProviderConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.KeyedEnumerablePConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.PSystem;
import org.eclipse.incquery.runtime.rete.construction.psystem.PVariable;
import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherContext;
import org.eclipse.incquery.runtime.rete.tuple.FlatTuple;

/**
 * @author Bergmann Gábor
 * 
 */
public class TypeTernary<PatternDescription, StubHandle> extends
        KeyedEnumerablePConstraint<Object, PatternDescription, StubHandle> implements ITypeInfoProviderConstraint {
    private final IPatternMatcherContext<PatternDescription> context;

    /**
     * @param buildable
     * @param variablesTuple
     * @param supplierKey
     *            type info
     */
    public TypeTernary(PSystem<PatternDescription, StubHandle, ?> pSystem,
            IPatternMatcherContext<PatternDescription> context, PVariable edge, PVariable source, PVariable target,
            Object supplierKey) {
        super(pSystem, new FlatTuple(edge, source, target), supplierKey);
        this.context = context;
    }

    @Override
    public Stub<StubHandle> doCreateStub() {
        return buildable.ternaryEdgeTypeStub(variablesTuple, supplierKey);
    }

    @Override
    public Object getTypeInfo(PVariable variable) {
        if (variable.equals(variablesTuple.get(0)))
            return ITypeInfoProviderConstraint.TypeInfoSpecials.wrapTernary(supplierKey);
        if (variable.equals(variablesTuple.get(1)))
            return ITypeInfoProviderConstraint.TypeInfoSpecials.wrapAny(context.ternaryEdgeSourceType(supplierKey));
        if (variable.equals(variablesTuple.get(2)))
            return ITypeInfoProviderConstraint.TypeInfoSpecials.wrapAny(context.ternaryEdgeTargetType(supplierKey));
        return ITypeInfoProviderConstraint.TypeInfoSpecials.NO_TYPE_INFO_PROVIDED;
    }

    @Override
    protected String keyToString() {
        return pSystem.getContext().printType(supplierKey);
    }

}
