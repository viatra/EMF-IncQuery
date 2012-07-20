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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicenumerables;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.ITypeInfoProviderConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.KeyedEnumerablePConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PSystem;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.FlatTuple;

/**
 * @author Bergmann Gábor
 *
 */
public class TypeBinary<PatternDescription, StubHandle> 
	extends KeyedEnumerablePConstraint<Object, PatternDescription, StubHandle> 
	implements ITypeInfoProviderConstraint
{
	private IPatternMatcherContext<PatternDescription> context;

	/**
	 * @param buildable
	 * @param variablesTuple
	 * @param typeKey
	 */
	public TypeBinary(
			PSystem<PatternDescription, StubHandle, ?> pSystem,
			IPatternMatcherContext<PatternDescription> context,
			PVariable source, PVariable target, 
			Object typeKey) {
		super(pSystem, new FlatTuple(source, target), typeKey);
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.EnumerablePConstraint#doCreateStub()
	 */
	@Override
	public Stub<StubHandle> doCreateStub() {
		return buildable.binaryEdgeTypeStub(variablesTuple, supplierKey);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.ITypeInfoProviderConstraint#getTypeInfo(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable)
	 */
	@Override
	public Object getTypeInfo(PVariable variable) {
		if (variable.equals(variablesTuple.get(0))) return context.binaryEdgeSourceType(supplierKey);
		if (variable.equals(variablesTuple.get(1))) return context.binaryEdgeTargetType(supplierKey);
		return ITypeInfoProviderConstraint.TypeInfoSpecials.NO_TYPE_INFO_PROVIDED;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.KeyedEnumerablePConstraint#keyToString()
	 */
	@Override
	protected String keyToString() {
		return pSystem.getContext().printType(supplierKey);
	}
	
}
