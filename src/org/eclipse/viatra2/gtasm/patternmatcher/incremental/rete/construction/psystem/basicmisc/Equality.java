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
import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Buildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.BasePConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable; import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PSystem;

/**
 * @author Bergmann Gábor
 *
 */
public class Equality<PatternDescription, StubHandle> extends
		BasePConstraint<PatternDescription, StubHandle> {

	private PVariable who; 
	private PVariable withWhom;
	
	/**
	 * @param buildable
	 * @param affectedVariables
	 */
	public Equality(PSystem<PatternDescription, StubHandle, ?> pSystem,
			PVariable who, PVariable withWhom) {
		super(pSystem, buildSet(who, withWhom));
		this.who = who;
		this.withWhom = withWhom;
	}


	private static Set<PVariable> buildSet(PVariable who, PVariable withWhom) {
		Set<PVariable> set = new HashSet<PVariable>();
		set.add(who);
		set.add(withWhom);
		return set;
	}
	
	public boolean isMoot() {
		return who.equals(withWhom);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.BasePConstraint#replaceVariable(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable)
	 */
	@Override
	public void doReplaceVariable(PVariable obsolete, PVariable replacement) {
		if (obsolete.equals(who)) who = replacement;
		if (obsolete.equals(withWhom)) withWhom = replacement;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.BasePConstraint#toStringRest()
	 */
	@Override
	protected String toStringRest() {
		return who.getName() + "=" + withWhom.getName();
	}


	/**
	 * @return the who
	 */
	public PVariable getWho() {
		return who;
	}

	/**
	 * @return the withWhom
	 */
	public PVariable getWithWhom() {
		return withWhom;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PConstraint#getDeducedVariables()
	 */
	@Override
	public Set<PVariable> getDeducedVariables() {
		return Collections.emptySet();
	}
}
