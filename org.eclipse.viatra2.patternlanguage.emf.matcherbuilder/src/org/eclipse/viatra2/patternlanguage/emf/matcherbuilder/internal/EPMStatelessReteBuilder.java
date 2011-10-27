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

package org.eclipse.viatra2.patternlanguage.emf.matcherbuilder.internal;

import java.util.HashMap;

import org.eclipse.viatra2.emf.incquery.runtime.extensibility.IStatelessRetePatternBuilder;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.ReteContainerBuildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.remote.Address;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.emf.matcherbuilder.runtime.PatternRegistry;

/**
 * Uses a PatternRegistry to resolve pattern names to patterns and build them.
 *  
 * @author Bergmann Gábor
 *
 */
public class EPMStatelessReteBuilder implements IStatelessRetePatternBuilder {
	private PatternRegistry patternRegistry;
	
	/**
	 * @param patternRegistry
	 */
	public EPMStatelessReteBuilder(PatternRegistry patternRegistry) {
		super();
		this.patternRegistry = patternRegistry;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.extensibility.IStatelessRetePatternBuilder#construct(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.ReteContainerBuildable, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext, java.lang.String)
	 */
	@Override
	public Address<? extends Receiver> construct(
			ReteContainerBuildable<String> buildable,
			IPatternMatcherContext<String> context, 
			String gtPattern) throws RetePatternBuildException 
	{
		Pattern pattern = getPattern(gtPattern);
		EPMBuildScaffold<Address<? extends Supplier>, Address<? extends Receiver>> epmBuildScaffold = 
				new EPMBuildScaffold<Address<? extends Supplier>, Address<? extends Receiver>>(buildable, context);
		return epmBuildScaffold.construct(pattern);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.extensibility.IStatelessRetePatternBuilder#getPosMapping(java.lang.String)
	 */
	@Override
	public HashMap<Object, Integer> getPosMapping(String gtPattern) {
		Pattern pattern = patternRegistry.findPattern(gtPattern);
		return (pattern != null) ? getPosMapping(pattern) : null;
	}
	
	public Pattern getPattern(String gtPattern) throws RetePatternBuildException {
		Pattern pattern = patternRegistry.findPattern(gtPattern);
		if (pattern == null) 
			throw new RetePatternBuildException("Pattern name {1} not registered in PatternRegistry", 
					new String[]{gtPattern}, gtPattern);
		return pattern;
	}

	public HashMap<Object, Integer> getPosMapping(Pattern pattern) {
		HashMap<Object, Integer> posMapping = new HashMap<Object, Integer>();
		int l = 0;
		for (Variable o : pattern.getParameters()) {
			posMapping.put(o.getName(), l++);
		}
		return posMapping;
	}
	
}
