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
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.EnumerablePConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.FlatTuple;

/**
 * @author Bergmann Gábor
 *
 * @param <PatternDescription>
 * @param <StubHandle>
 */
public abstract class CoreModelRelationship<PatternDescription, StubHandle>
		extends EnumerablePConstraint<PatternDescription, StubHandle> {

	protected boolean transitive;

	protected abstract Stub<StubHandle> doCreateTransitiveStub();

	protected abstract Stub<StubHandle> doCreateDirectStub();

	/**
	 * @param buildable
	 * @param variablesTuple
	 */
	public CoreModelRelationship(
			Buildable<PatternDescription, StubHandle, ?> buildable, 
			PVariable parent, 
			PVariable child, 
			boolean transitive) 
	{
		super(buildable, new FlatTuple(parent, child));
		this.transitive = transitive;
	}

	@Override
	public Stub<StubHandle> doCreateStub() throws RetePatternBuildException {
		return isTransitive() ? 
				doCreateTransitiveStub() : 
				doCreateDirectStub();
	}

	@Override
	protected String toStringRestRest() {
		return transitive ? "transitive" : "direct";
	}

	/**
	 * @return the transitive
	 */
	public boolean isTransitive() {
		return transitive;
	}

}