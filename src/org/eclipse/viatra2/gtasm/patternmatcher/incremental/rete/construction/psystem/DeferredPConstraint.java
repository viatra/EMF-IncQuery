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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem;

import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;

/**
 * @author Bergmann Gábor
 *
 */
public abstract class DeferredPConstraint<PatternDescription, StubHandle> extends BasePConstraint<PatternDescription, StubHandle> {

	public DeferredPConstraint(PSystem<PatternDescription, StubHandle, ?> pSystem, Set<PVariable> affectedVariables) {
		super(pSystem, affectedVariables);
	}

	public abstract boolean isReadyAt(Stub<StubHandle> stub);
	
	/**
	 * @pre this.isReadyAt(stub);
	 */
	public Stub<StubHandle> checkOn(Stub<StubHandle> stub) throws RetePatternBuildException {
		Stub<StubHandle> newStub = doCheckOn(stub);
		newStub.addConstraint(this);
		return newStub;
	}
	protected abstract Stub<StubHandle> doCheckOn(Stub<StubHandle> stub) throws RetePatternBuildException;

	/**
	 * Called when the constraint is not ready, but cannot be deferred further. 
	 * @param stub
	 * @throws RetePatternBuildException to indicate the error in detail.
	 * PRE: !isReady(stub)
	 */
	public abstract void raiseForeverDeferredError(Stub<StubHandle> stub) throws RetePatternBuildException;
}
