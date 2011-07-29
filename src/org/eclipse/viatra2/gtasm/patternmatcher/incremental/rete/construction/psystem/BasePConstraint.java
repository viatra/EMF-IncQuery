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

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Buildable;

/**
 * @author Bergmann Gábor
 *
 */
public abstract class BasePConstraint<PatternDescription, StubHandle> implements PConstraint {
	protected Buildable<PatternDescription, StubHandle, ?> buildable;
	private Set<PVariable> affectedVariables;

	/**
	 * @param affectedVariables
	 */
	public BasePConstraint(Buildable<PatternDescription, StubHandle, ?> buildable, Set<PVariable> affectedVariables) {
		super();
		this.buildable = buildable;
		this.affectedVariables = affectedVariables;
		for (PVariable pVariable : affectedVariables) {
			pVariable.refer(this);
		}
	}
	@Override
	public String toString() {
		return "PC["+getClass().getSimpleName()+":"+toStringRest()+"]";
	}
	protected abstract String toStringRest();

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PConstraint#getAffectedVariables()
	 */
	@Override
	public Set<PVariable> getAffectedVariables() {
		return affectedVariables;
	}
}
