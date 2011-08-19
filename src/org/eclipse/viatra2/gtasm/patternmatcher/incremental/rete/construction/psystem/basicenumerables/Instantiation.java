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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicenumerables;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Buildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable; import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PSystem;

/**
 * @author Bergmann Gábor
 *
 */
public class Instantiation<PatternDescription, StubHandle> extends
		CoreModelRelationship<PatternDescription, StubHandle> {

	/**
	 * @param buildable
	 * @param parent
	 * @param child
	 * @param transitive
	 */
	public Instantiation(
			PSystem<PatternDescription, StubHandle, ?> pSystem,
			PVariable parent, PVariable child, boolean transitive) {
		super(pSystem, parent, child, transitive);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicenumerables.CoreModelRelationship#doCreateTransitiveStub()
	 */
	@Override
	protected Stub<StubHandle> doCreateTransitiveStub() {
		return buildable.instantiationTransitiveStub(variablesTuple);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicenumerables.CoreModelRelationship#doCreateDirectStub()
	 */
	@Override
	protected Stub<StubHandle> doCreateDirectStub() {
		return buildable.instantiationDirectStub(variablesTuple);
	}

}
