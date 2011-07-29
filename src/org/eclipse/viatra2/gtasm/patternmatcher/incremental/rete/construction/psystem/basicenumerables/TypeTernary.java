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

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Buildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.KeyedEnumerablePConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.FlatTuple;

/**
 * @author Bergmann Gábor
 *
 */
public class TypeTernary<PatternDescription, StubHandle> extends
		KeyedEnumerablePConstraint<Object, PatternDescription, StubHandle> {

	/**
	 * @param buildable
	 * @param variablesTuple
	 * @param supplierKey
	 */
	public TypeTernary(
			Buildable<PatternDescription, StubHandle, ?> buildable,
			PVariable edge, PVariable source, PVariable target, 
			Object supplierKey) {
		super(buildable, new FlatTuple(edge, source, target), supplierKey);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.EnumerablePConstraint#doCreateStub()
	 */
	@Override
	public Stub<StubHandle> doCreateStub() {
		return buildable.ternaryEdgeTypeStub(variablesTuple, supplierKey);
	}

}
