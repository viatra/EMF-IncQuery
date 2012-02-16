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

package org.eclipse.viatra2.emf.incquery.runtime.internal.matcherbuilder;

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

/**
 * @author Bergmann Gábor
 *
 */
public class EPMBuildScaffold<StubHandle, Collector> {
	
	protected Buildable<Pattern, StubHandle, Collector> baseBuildable;
	protected IPatternMatcherContext<Pattern> context;
	
	/**
	 * @param baseBuildable
	 * @param context
	 */
	public EPMBuildScaffold(
			Buildable<Pattern, StubHandle, Collector> baseBuildable,
			IPatternMatcherContext<Pattern> context) {
		super();
		this.baseBuildable = baseBuildable;
		this.context = context;
	}

	public Collector construct(Pattern pattern) throws RetePatternBuildException {
		Collector production = baseBuildable.putOnTab(pattern).patternCollector(pattern);
		// TODO check annotations for reinterpret
		
		context.logDebug("EPMBuilder starts construction of: " + pattern.getName());
		for (PatternBody body : pattern.getBodies()) {
			Buildable<Pattern, StubHandle, Collector> currentBuildable = 
					baseBuildable.getNextContainer().putOnTab(pattern);
			if (Options.builderMethod == BuilderMethod.LEGACY) {
				throw new UnsupportedOperationException();
			} else { 
				EPMBodyToPSystem<StubHandle, Collector> converter = 
					new EPMBodyToPSystem<StubHandle, Collector>(pattern, body, context, currentBuildable);
				Stub<StubHandle> bodyFinal = 
					new BasicLinearLayout<Pattern, StubHandle, Collector>().layout(converter.toPSystem());
				BuildHelper.projectIntoCollector(currentBuildable, bodyFinal, production, converter.symbolicParameterArray());
			}

		}
		
		
		return null;
	}

}
