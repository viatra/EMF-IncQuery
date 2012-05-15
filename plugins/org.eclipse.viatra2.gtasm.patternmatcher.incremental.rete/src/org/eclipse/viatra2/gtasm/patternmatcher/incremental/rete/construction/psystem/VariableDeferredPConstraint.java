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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;

/**
 * A kind of deferred constraint that can only be checked when a set of deferring variables are all present in a stub.
 * @author Bergmann Gábor
 *
 */
public abstract class VariableDeferredPConstraint<PatternDescription, StubHandle> extends DeferredPConstraint<PatternDescription, StubHandle> {
	/**
	 * @param affectedVariables
	 */
	public VariableDeferredPConstraint(PSystem<PatternDescription, StubHandle, ?> pSystem, Set<PVariable> affectedVariables) {
		super(pSystem, affectedVariables);
	}
	protected abstract Set<PVariable> getDeferringVariables();
	/**
	 * Refine further if needed
	 */
	@Override
	public boolean isReadyAt(Stub<StubHandle> stub) {
		return stub.getVariablesIndex().keySet().containsAll(getDeferringVariables());
	}
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.DeferredPConstraint#raiseForeverDeferredError(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub)
	 */
	@Override
	public void raiseForeverDeferredError(Stub<StubHandle> stub)
			throws RetePatternBuildException {
		Set<PVariable> missing = new HashSet<PVariable>(getDeferringVariables());
		missing.removeAll(stub.getVariablesIndex().keySet());
		String[] args = {toString(), missing.toArray().toString()};
		String msg = "The checking of pattern constraint {1} requires the values of variables {2}, but it cannot be deferred further. " + 
			"HINT: the incremental matcher is not an equation solver, please make sure that all variable values are deducible.";
		throw new RetePatternBuildException(msg, args, null);
	}
}
