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

package org.eclipse.viatra2.compiled.emf.runtime.internal;

import java.util.HashMap;

import org.eclipse.viatra2.compiled.emf.runtime.Activator;
import org.eclipse.viatra2.compiled.emf.runtime.IStatelessGeneratedRetePatternBuilder;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.ReteContainerBuildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.remote.Address;

/**
 * Internal RetePatternBuilder that multiplexes build requests to contributions to the extension point.
 * Multiplexation is keyed by pattern fqn.
 * @author Bergmann Gábor
 *
 */
public class MultiplexerPatternBuilder implements
		IRetePatternBuilder<String, Address<? extends Supplier>, Address<? extends Receiver>>
{
	ReteContainerBuildable<String> buildable;
	IPatternMatcherContext<String> context;

	/**
	 * @param buildable
	 * @param context
	 */
	public MultiplexerPatternBuilder(ReteContainerBuildable<String> buildable,
			IPatternMatcherContext<String> context) {
		super();
		this.buildable = buildable;
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder#construct(java.lang.Object)
	 */
	@Override
	public Address<? extends Receiver> construct(String gtPattern)
			throws RetePatternBuildException {
		IStatelessGeneratedRetePatternBuilder builder = Activator.getDefault().getContributedStatelessPatternBuilders().get(gtPattern);
		if (builder != null) return builder.construct(buildable, context, gtPattern);
		else throw new RetePatternBuildException("No RETE pattern builder generated for pattern {1}.",
				new String[]{gtPattern}, gtPattern);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder#getPosMapping(java.lang.Object)
	 */
	@Override
	public HashMap<Object, Integer> getPosMapping(String gtPattern) {
		IStatelessGeneratedRetePatternBuilder builder = Activator.getDefault().getContributedStatelessPatternBuilders().get(gtPattern);
		if (builder != null) return builder.getPosMapping(gtPattern); else return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder#refresh()
	 */
	@Override
	public void refresh() {
		throw new UnsupportedOperationException();
	}

}
