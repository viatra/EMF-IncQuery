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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Buildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;

/**
 * @author Bergmann Gábor
 *
 */
public abstract class EnumerablePConstraint<PatternDescription, StubHandle> extends BasePConstraint<PatternDescription, StubHandle> {
	protected Tuple variablesTuple;
	private Stub<StubHandle> stub;
	
	protected EnumerablePConstraint(Buildable<PatternDescription, StubHandle, ?> buildable, Tuple variablesTuple) {
		super(buildable, extractAffectedVariables(variablesTuple));
		this.variablesTuple = variablesTuple;
	}
	
	public Stub<StubHandle> getStub() throws RetePatternBuildException {
		if (stub == null) {
			stub = doCreateStub();
			stub.addConstraint(this);
			
			// check for any variable coincidences
			Map<Object, List<Integer>> indexWithMupliplicity = 
				stub.getVariablesTuple().invertIndexWithMupliplicity();
			for (PVariable var : getAffectedVariables()) {
				List<Integer> indices = indexWithMupliplicity.get(var);
				if (indices.size() > 1) { 
					int[] indexArray = new int[indices.size()];
					int m = 0;
					for (Integer index : indices)
						indexArray[m++] = index;
					stub = buildable.buildEqualityChecker(stub, indexArray);
				}
			}
		}
		return stub;
	}
	
	public abstract Stub<StubHandle> doCreateStub() throws RetePatternBuildException;


	/**
	 * 
	 */
	private static Set<PVariable> extractAffectedVariables(Tuple variablesTuple) {
		Set<PVariable> affectedVariables = new HashSet<PVariable>();
		Object[] elements = variablesTuple.getElements();
		for (Object object : elements) {
			affectedVariables.add((PVariable) object);
		}
		return affectedVariables;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PConstraint#toStringRest()
	 */
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
	
	
	

}
