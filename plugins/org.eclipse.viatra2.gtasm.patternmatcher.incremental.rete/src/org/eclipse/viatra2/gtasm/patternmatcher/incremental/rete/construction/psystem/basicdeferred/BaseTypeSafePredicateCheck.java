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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.helpers.TypeHelper;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PSystem;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.VariableDeferredPConstraint;

/**
 * @author Bergmann Gábor
 *
 */
public abstract class BaseTypeSafePredicateCheck<PatternDescription, StubHandle> extends
		VariableDeferredPConstraint<PatternDescription, StubHandle> 
{
	private Map<PVariable, Set<Object>> allTypeRestrictions;

	/**
	 * @param buildable
	 * @param affectedVariables
	 */
	public BaseTypeSafePredicateCheck(
			PSystem<PatternDescription, StubHandle, ?> pSystem,
			Set<PVariable> affectedVariables) {
		super(pSystem, affectedVariables);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PConstraint#getDeducedVariables()
	 */
	@Override
	public Set<PVariable> getDeducedVariables() {
		return Collections.emptySet();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.VariableDeferredPConstraint#getDeferringVariables()
	 */
	@Override
	protected Set<PVariable> getDeferringVariables() {
		return getAffectedVariables();
	}


	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.VariableDeferredPConstraint#isReadyAt(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub)
	 */
	@Override
	public boolean isReadyAt(Stub<StubHandle> stub) {
		if (super.isReadyAt(stub)) {
			return checkTypeSafety(stub) == null;
		}
		return false;
	}

	/**
	 * Checks whether all type restrictions are already enforced on affected variables. 
	 * @param stub
	 * @return a variable whose type safety is not enforced yet, or null if the stub is typesafe
	 */
	protected PVariable checkTypeSafety(Stub<StubHandle> stub) {
		for (PVariable pVariable : getAffectedVariables()) {
			Set<Object> allTypeRestrictionsForVariable = getAllTypeRestrictions().get(pVariable);
			Set<Object> checkedTypeRestrictions = TypeHelper.inferTypes(pVariable, stub.getAllEnforcedConstraints());
			Set<Object> uncheckedTypeRestrictions = 
				TypeHelper.subsumeTypes(
						allTypeRestrictionsForVariable, 
						checkedTypeRestrictions, 
						this.pSystem.getContext());
			if (!uncheckedTypeRestrictions.isEmpty()) return pVariable;
		} 
		return null;
	}

	/**
	 * @return the allTypeRestrictions
	 */
	public Map<PVariable, Set<Object>> getAllTypeRestrictions() {
		if (allTypeRestrictions == null) {
			allTypeRestrictions = new HashMap<PVariable, Set<Object>>();
			for (PVariable pVariable : getAffectedVariables()) {
				allTypeRestrictions.put(
						pVariable, 
						TypeHelper.inferTypes(pVariable, pVariable.getReferringConstraints()));
			}		
		}
		return allTypeRestrictions;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.VariableDeferredPConstraint#raiseForeverDeferredError(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub)
	 */
	@Override
	public void raiseForeverDeferredError(Stub<StubHandle> stub)
			throws RetePatternBuildException {
		if (!super.isReadyAt(stub)) {
			super.raiseForeverDeferredError(stub);
		} else {
			String[] args = {toString(), checkTypeSafety(stub).toString()};
			String msg = "The checking of pattern constraint {1} cannot be deferred further, but variable {2} is still not type safe. " + 
				"HINT: the incremental matcher is not an equation solver, please make sure that all variable values are deducible.";
			String shortMsg = "Could not check all constraints due to undeducible type restrictions";
			throw new RetePatternBuildException(msg, args, shortMsg, null);
		}
			
	}
}
