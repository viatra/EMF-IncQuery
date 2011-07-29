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
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Buildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.VariableDeferredPConstraint;

/**
 * @author Bergmann Gábor
 *
 */
public class Inequality<PatternDescription, StubHandle> 
	extends VariableDeferredPConstraint<PatternDescription, StubHandle> 
//	implements IFoldablePConstraint 
{

	private PVariable subject;
	private Set<PVariable> inequals;
//	private IFoldablePConstraint incorporator;
	
	public Inequality(
			Buildable<PatternDescription, StubHandle, ?> buildable,
			PVariable who, PVariable withWhom) 
	{
		this(buildable, who, Collections.singleton(withWhom));
	}

	private Inequality(
			Buildable<PatternDescription, StubHandle, ?> buildable,
			PVariable subject, Set<PVariable> inequals) 
	{
		super(buildable, include(inequals, subject));
		this.subject = subject;
		this.inequals = inequals;
	}

	private static HashSet<PVariable> include(Set<PVariable> inequals, PVariable subject) {
		HashSet<PVariable> hashSet = new HashSet<PVariable>(inequals);
		hashSet.add(subject);
		return hashSet;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.VariableDeferredPConstraint#getDeferringVariables()
	 */
	@Override
	protected Set<PVariable> getDeferringVariables() {
		return getAffectedVariables();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.DeferredPConstraint#doCheckOn(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub)
	 */
	@Override
	protected Stub<StubHandle> doCheckOn(Stub<StubHandle> stub) throws RetePatternBuildException {
		Map<Object, Integer> variablesIndex = stub.getVariablesIndex();
		return buildable.buildInjectivityChecker(stub, variablesIndex.get(subject), mapIndices(variablesIndex, inequals));
	}

	private static int[] mapIndices(Map<Object, Integer> variablesIndex, Set<PVariable> keys) {
		int[] result = new int[keys.size()];
		int k = 0;
		for (PVariable key : keys) {
			result[k++] = variablesIndex.get(key);
		}
		return result;
	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.IFoldablePConstraint#getIncorporator()
//	 */
//	@Override
//	public IFoldablePConstraint getIncorporator() {
//		return incorporator;
//	}
//
//	@Override
//	public void registerIncorporatationInto(IFoldablePConstraint incorporator) {
//		this.incorporator = incorporator;
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.IFoldablePConstraint#incorporate(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.IFoldablePConstraint)
//	 */
//	@Override
//	public boolean incorporate(IFoldablePConstraint other) {
//		if (other instanceof Inequality<?, ?>) {
//			Inequality other2 = (Inequality) other;
//			if (subject.equals(other2.subject)) {
//				Set<PVariable> newInequals = new HashSet<PVariable>(inequals);
//				newInequals.addAll(other2.inequals);
//				return new Inequality<PatternDescription, StubHandle>(buildable, subject, newInequals);
//			}
//		} else return false;
//	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.BasePConstraint#toStringRest()
	 */
	@Override
	protected String toStringRest() {
		String result = subject.toString() + "!=";
		for (PVariable other : inequals) {
			result += other + ",";
		}
		return null;
	}

}
