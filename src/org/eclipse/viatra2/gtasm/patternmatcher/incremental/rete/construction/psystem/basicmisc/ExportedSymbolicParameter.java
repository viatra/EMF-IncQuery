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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicmisc;

import java.util.Collections;
import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.BasePConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PSystem;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable;

/**
 * @author Bergmann Gábor
 *
 */
public class ExportedSymbolicParameter<PatternDescription, StubHandle> extends 
	BasePConstraint<PatternDescription, StubHandle> 
{
	PVariable parameterVariable;
	String parameterName;
	

	/**
	 * @param buildable
	 * @param parameterVariable
	 */
	public ExportedSymbolicParameter(
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
	
}
