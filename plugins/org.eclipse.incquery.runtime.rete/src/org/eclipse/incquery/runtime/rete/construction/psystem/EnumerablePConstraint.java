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

package org.eclipse.incquery.runtime.rete.construction.psystem;

import java.util.Set;

import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;
import org.eclipse.incquery.runtime.rete.construction.Stub;
import org.eclipse.incquery.runtime.rete.construction.helpers.BuildHelper;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * A constraint for which all satisfying tuples of variable values can be enumerated at any point during run-time.
 * @author Bergmann Gábor
 *
 */
public abstract class EnumerablePConstraint<PatternDescription, StubHandle> extends BasePConstraint<PatternDescription, StubHandle> {
	protected Tuple variablesTuple;
	private Stub<StubHandle> stub;
	
	protected EnumerablePConstraint(PSystem<PatternDescription, StubHandle, ?> pSystem, Tuple variablesTuple) {
		super(pSystem, variablesTuple.<PVariable>getDistinctElements());
		this.variablesTuple = variablesTuple;
	}
	
	public Stub<StubHandle> getStub() throws RetePatternBuildException {
		if (stub == null) {
			stub = doCreateStub();
			stub.addConstraint(this);
			
			// check for any variable coincidences and enforce them
			stub = BuildHelper.enforceVariableCoincidences(buildable, stub);
		}
		return stub;
	}
	
	public abstract Stub<StubHandle> doCreateStub() throws RetePatternBuildException;

	@Override
	public void doReplaceVariable(PVariable obsolete, PVariable replacement) {
		variablesTuple = variablesTuple.replaceAll(obsolete, replacement);
	}

	@Override
	protected String toStringRest() {
		String stringRestRest = toStringRestRest();
		String tupleString = "@" + variablesTuple.toString();
		return stringRestRest == null ? tupleString : ":" + stringRestRest + tupleString;
	}
	protected String toStringRestRest() {
		return null;
	}

	/**
	 * @return the variablesTuple
	 */
	public Tuple getVariablesTuple() {
		return variablesTuple;
	}
	
	@Override
	public Set<PVariable> getDeducedVariables() {
		return getAffectedVariables();
	}	
	
	

}
