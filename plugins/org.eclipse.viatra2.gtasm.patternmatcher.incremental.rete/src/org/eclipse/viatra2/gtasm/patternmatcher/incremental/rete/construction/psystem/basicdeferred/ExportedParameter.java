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
import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PSystem;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.VariableDeferredPConstraint;

/**
 * @author Bergmann Gábor
 *
 */
public class ExportedParameter<PatternDescription, StubHandle> extends 
	VariableDeferredPConstraint<PatternDescription, StubHandle>  
{
	PVariable parameterVariable;
	String parameterName;
	

	/**
	 * @param buildable
	 * @param parameterVariable
	 */
	public ExportedParameter(
			PSystem<PatternDescription, StubHandle, ?> pSystem,
			PVariable parameterVariable,
			String parameterName) {
		super(pSystem, Collections.singleton(parameterVariable));
		this.parameterVariable = parameterVariable;
		this.parameterName = parameterVariable.getName();
		//parameterVariable.setExportedParameter(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PConstraint#replaceVariable(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable)
	 */
	@Override
	public void doReplaceVariable(PVariable obsolete, PVariable replacement) {
		if (obsolete.equals(parameterVariable)) parameterVariable = replacement;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.BasePConstraint#toStringRest()
	 */
	@Override
	protected String toStringRest() {
		String varName = parameterVariable.getName();
		return parameterName.equals(varName) ? 
				parameterName :
				parameterName + "(" + varName + ")"	;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PConstraint#getDeducedVariables()
	 */
	@Override
	public Set<PVariable> getDeducedVariables() {
		return Collections.emptySet();
	}

	/**
	 * @return the parameterName
	 */
	public String getParameterName() {
		return parameterName;
	}

	/**
	 * @return the parameterVariable
	 */
	public PVariable getParameterVariable() {
		return parameterVariable;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.VariableDeferredPConstraint#getDeferringVariables()
	 */
	@Override
	protected Set<PVariable> getDeferringVariables() {
		return Collections.singleton(parameterVariable);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.DeferredPConstraint#doCheckOn(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub)
	 */
	@Override
	protected Stub<StubHandle> doCheckOn(Stub<StubHandle> stub) throws RetePatternBuildException {
		return stub;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.BasePConstraint#checkSanity()
	 */
	@Override
	public void checkSanity() throws RetePatternBuildException {
		super.checkSanity();
		if (!parameterVariable.isDeducable()){
			String[] args = {parameterName};
			String msg = "Impossible to match pattern: "
				+ "exported pattern variable {1} can not be determined based on the pattern constraints. "
				+ "HINT: certain constructs (e.g. negative patterns or check expressions) cannot output symbolic parameters.";
			throw new RetePatternBuildException(msg, args, null);
		}					

	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.VariableDeferredPConstraint#raiseForeverDeferredError(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub)
	 */
	@Override
	public void raiseForeverDeferredError(Stub<StubHandle> stub) throws RetePatternBuildException 
	{
			String[] args = {parameterName};
			String msg = "Pattern Graph Search terminated incompletely: "
				+ "exported pattern variable {1} could not be determined based on the pattern constraints. "
				+ "HINT: certain constructs (e.g. negative patterns or check expressions) cannot output symbolic parameters.";
			throw new RetePatternBuildException(msg, args, null);
	}
	
}
