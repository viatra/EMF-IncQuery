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

package org.eclipse.viatra2.compiled.emf.runtime;

import java.util.HashMap;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.ReteContainerBuildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.remote.Address;

/**
 * Stateless version of IRetePatternBuilder: takes all parameters upon call.
 * Bound to serve generated code (PatternDescriptor = String).
 * Because code is generated, no BuilderContext required, only a simple Context.
 * @author Bergmann Gábor
 *
 */
public interface IStatelessGeneratedRetePatternBuilder {
	/**
	 * Builds a part of the rete network that will match occurences of a given
	 * pattern.
	 * 
	 * @param buildable
	 * @param context no builder context required
	 * @param gtPattern
	 *            the pattern itself whose matcher subnet has to be built.
	 * @return production.
	 * 	          the Production node that should store matchings of
	 *            the given pattern.
	 * @throws RetePatternBuildException
	 *             if construction fails.
	 */
	public Address<? extends Receiver> construct(
			ReteContainerBuildable<String> buildable,
			IPatternMatcherContext<String> context,
			String gtPattern) throws RetePatternBuildException;
	
	/**
	 * Extract the position mapping of the graph pattern.
	 * @return map.
	 */
	HashMap<Object, Integer> getPosMapping(String gtPattern);

}
