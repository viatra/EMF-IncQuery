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
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.FlatTuple;

/**
 * @author Bergmann Gábor
 *
 */
public class TypeUnary<PatternDescription, StubHandle> 
	extends KeyedEnumerablePConstraint<Object, PatternDescription, StubHandle> 
	implements ITypeInfoProviderConstraint
{
	/**
	 * @param buildable
	 * @param variable
	 * @param typeKey
	 */
	public TypeUnary(
			PSystem<PatternDescription, StubHandle, ?> pSystem,
			PVariable variable, Object typeKey) {
		super(pSystem, new FlatTuple(variable), typeKey);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.EnumerablePConstraint#doCreateStub()
	 */
	@Override
	public Stub<StubHandle> doCreateStub() {	
		return buildable.unaryTypeStub(variablesTuple, supplierKey);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.ITypeInfoProviderConstraint#getTypeInfo(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable)
	 */
	@Override
	public Object getTypeInfo(PVariable variable) {
		if (variable.equals(variablesTuple.get(0))) 
			return ITypeInfoProviderConstraint.TypeInfoSpecials.wrapUnary(supplierKey);
		return ITypeInfoProviderConstraint.TypeInfoSpecials.NO_TYPE_INFO_PROVIDED;
	}

}
