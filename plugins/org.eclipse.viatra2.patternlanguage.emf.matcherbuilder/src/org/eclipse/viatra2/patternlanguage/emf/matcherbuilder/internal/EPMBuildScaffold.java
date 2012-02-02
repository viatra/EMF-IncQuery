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

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Buildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.basiclinear.BasicLinearLayout;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.helpers.BuildHelper;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.util.Options;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.util.Options.BuilderMethod;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.emf.matcherbuilder.runtime.PatternRegistry;

/**
 * @author Bergmann Gábor
 *
 */
public class EPMBuildScaffold<StubHandle, Collector> {
	
	protected Buildable<String, StubHandle, Collector> baseBuildable;
	protected IPatternMatcherContext<String> context;
	
	/**
	 * @param baseBuildable
	 * @param context
	 */
	public EPMBuildScaffold(
			Buildable<String, StubHandle, Collector> baseBuildable,
			IPatternMatcherContext<String> context) {
		super();
		this.baseBuildable = baseBuildable;
		this.context = context;
	}

	public Collector construct(Pattern pattern) throws RetePatternBuildException {
		String fqn = PatternRegistry.fqnOf(pattern);
		Collector production = baseBuildable.putOnTab(fqn).patternCollector(fqn);
		// TODO check annotations for reinterpret
		
		context.logDebug("EPMBuilder starts construction of: " + pattern.getName());
		for (PatternBody body : pattern.getBodies()) {
			Buildable<String, StubHandle, Collector> currentBuildable = 
					baseBuildable.getNextContainer().putOnTab(fqn);
			if (Options.builderMethod == BuilderMethod.LEGACY) {
				throw new UnsupportedOperationException();
			} else { 
				EPMBodyToPSystem<StubHandle, Collector> converter = 
					new EPMBodyToPSystem<StubHandle, Collector>(pattern, body, context, currentBuildable);
				Stub<StubHandle> bodyFinal = 
					new BasicLinearLayout<String, StubHandle, Collector>().layout(converter.toPSystem());
				BuildHelper.projectIntoCollector(currentBuildable, bodyFinal, production, converter.symbolicParameterArray());
			}

		}
		
		
		return null;
	}

}
