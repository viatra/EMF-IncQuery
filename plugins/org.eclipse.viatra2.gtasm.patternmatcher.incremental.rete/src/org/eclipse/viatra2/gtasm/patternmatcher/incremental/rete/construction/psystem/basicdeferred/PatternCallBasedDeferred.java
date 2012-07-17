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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicdeferred;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.helpers.BuildHelper;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PSystem;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.VariableDeferredPConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;

/**
 * @author Bergmann Gábor
 *
 * @param <PatternDescription>
 * @param <StubHandle>
 */
public abstract class PatternCallBasedDeferred<PatternDescription, StubHandle>
		extends VariableDeferredPConstraint<PatternDescription, StubHandle> {

	protected Tuple actualParametersTuple;

	protected abstract void doDoReplaceVariables(PVariable obsolete, PVariable replacement);

	protected abstract Set<PVariable> getCandidateQuantifiedVariables();

	protected PatternDescription pattern;
	private Set<PVariable> deferringVariables;

	/**
	 * @param buildable
	 * @param additionalAffectedVariables
	 */
	public PatternCallBasedDeferred(
			PSystem<PatternDescription, StubHandle, ?> pSystem,
			Tuple actualParametersTuple, PatternDescription pattern, 
			Set<PVariable> additionalAffectedVariables) {
		super(pSystem, union(actualParametersTuple.<PVariable>getDistinctElements(), additionalAffectedVariables));
		this.actualParametersTuple = actualParametersTuple;
		this.pattern = pattern;
	}
	public PatternCallBasedDeferred(
			PSystem<PatternDescription, StubHandle, ?> pSystem,
			Tuple actualParametersTuple, PatternDescription pattern) {
		this(pSystem, actualParametersTuple, pattern, Collections.<PVariable>emptySet());
	}

	private static Set<PVariable> union(
			Set<PVariable> a,
			Set<PVariable> b) {
		Set<PVariable> result = new HashSet<PVariable>();
		result.addAll(a);
		result.addAll(b);
		return result;
	}

	@Override
	protected Set<PVariable> getDeferringVariables() {
		if (deferringVariables == null) {
			deferringVariables = new HashSet<PVariable>();
			for (PVariable var : getCandidateQuantifiedVariables()) {
				if (var.isDeducable()) deferringVariables.add(var);
			}
		}
		return deferringVariables;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.BasePConstraint#checkSanity()
	 */
	@Override
	public void checkSanity() throws RetePatternBuildException {
		super.checkSanity();
		for (Object obj : this.actualParametersTuple.getDistinctElements()) {
			PVariable var = (PVariable) obj;
			if (!getDeferringVariables().contains(var)) {
				// so this is a free variable of the NAC / aggregation?
				for (PConstraint pConstraint : var.getReferringConstraints()) { 
					if (
							pConstraint!=this && 
							!(
									pConstraint instanceof Equality<?, ?> && 
									((Equality<?, ?>)pConstraint).isMoot()
							)
						) 
					throw new RetePatternBuildException(
							"Variable {1} of constraint {2} is not a positively determined part of the pattern, yet it is also affected by {3}.", 
							new String[]{var.toString(), this.toString(), pConstraint.toString()}, 
							"Read-only variable can not be deduced", null);
				}
			}
		}

	}

	/**
	 * @param stub
	 * @param sideStub
	 * @return
	 */
	protected BuildHelper.JoinHelper<StubHandle> getJoinHelper(Stub<StubHandle> stub, Stub<StubHandle> sideStub) {
		BuildHelper.JoinHelper<StubHandle> joinHelper = new BuildHelper.JoinHelper<StubHandle>(stub, sideStub);
		return joinHelper;
	}

	/**
	 * @return
	 * @throws RetePatternBuildException
	 */
	protected Stub<StubHandle> getSideStub() throws RetePatternBuildException {
		Stub<StubHandle> sideStub = buildable.patternCallStub(actualParametersTuple, pattern);
		sideStub = BuildHelper.enforceVariableCoincidences(buildable, sideStub);
		return sideStub;
	}

	@Override
	protected void doReplaceVariable(PVariable obsolete, PVariable replacement) {
		if (deferringVariables != null) { 
			throw new IllegalStateException("Cannot replace variables on " + this + 
					" when deferring variables have already been identified.");
		}
		actualParametersTuple.replaceAll(obsolete, replacement);
		doDoReplaceVariables(obsolete, replacement);
	}

}