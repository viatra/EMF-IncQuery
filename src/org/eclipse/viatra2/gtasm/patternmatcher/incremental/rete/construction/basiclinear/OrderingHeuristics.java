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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.basiclinear;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.DeferredPConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.EnumerablePConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicenumerables.ConstantValue;

/**
 * @author Bergmann Gábor
 *
 */
public class OrderingHeuristics<PatternDescription, StubHandle, Collector> implements Comparator<PConstraint>  {
	private Stub<StubHandle> stub;

	/**
	 * @param stub
	 */
	public OrderingHeuristics(Stub<StubHandle> stub) {
		super();
		this.stub = stub;
	}
	
	static int preferTrue(boolean b1, boolean b2) {
		return (b1^b2) ? (b1 ? -1: +1) : 0; 
	}
	static <T> int preferLess(Comparable<T> c1, T c2) {
		return c1.compareTo(c2); 
	}
	static int lexi(int moreSignificant, int lessSignificant) {
		return (moreSignificant == 0) ? lessSignificant : moreSignificant;
	}	
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(PConstraint o1, PConstraint o2) {
		int result = 0;
		result = lexi(result, preferTrue(isConstant(o1), isConstant(o2)));
		result = lexi(result, preferTrue(isReady(o1), isReady(o2)));
		
		Set<PVariable> bound1 = boundVariables(o1);
		Set<PVariable> bound2 = boundVariables(o2);
		result = lexi(result, preferTrue(isBound(o1, bound1), isBound(o2, bound2)));
		result = lexi(result, -preferLess(degreeBound(o1, bound1), degreeBound(o2, bound2)));
		result = lexi(result, preferLess(degreeFree(o1, bound1), degreeFree(o2, bound2)));
		
		result = lexi(result, preferLess(o1.toString(), o2.toString()));
		return result;
	}


	
	boolean isConstant(PConstraint o) {
		return (o instanceof ConstantValue<?, ?>); 
	}
	boolean isReady(PConstraint o) {
		return 
			(o instanceof EnumerablePConstraint<?, ?>) || 
			(o instanceof DeferredPConstraint<?, ?> && 
					((DeferredPConstraint<PatternDescription, StubHandle>) o).isReadyAt(stub)); 
	}
	Set<PVariable> boundVariables(PConstraint o) {
		Set<PVariable> boundVariables = new HashSet<PVariable>(o.getAffectedVariables());
		boundVariables.retainAll(stub.getVariablesIndex().keySet());
		return boundVariables;
	}
	boolean isBound(PConstraint o, Set<PVariable> boundVariables) {
		return boundVariables.size() == o.getAffectedVariables().size();
	}
	int degreeBound(PConstraint o, Set<PVariable> boundVariables) {
		return boundVariables.size();
	}
	int degreeFree(PConstraint o, Set<PVariable> boundVariables) {
		return o.getAffectedVariables().size() - boundVariables.size();
	}

	
}
