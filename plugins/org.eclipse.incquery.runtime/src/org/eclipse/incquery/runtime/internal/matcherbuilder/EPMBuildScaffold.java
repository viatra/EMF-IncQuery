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

package org.eclipse.incquery.runtime.internal.matcherbuilder;

import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.incquery.runtime.rete.construction.Buildable;
import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;
import org.eclipse.incquery.runtime.rete.construction.Stub;
import org.eclipse.incquery.runtime.rete.construction.helpers.BuildHelper;
import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherContext;
import org.eclipse.incquery.runtime.rete.util.Options;
import org.eclipse.incquery.runtime.rete.util.Options.BuilderMethod;

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
						Options.builderMethod.<Pattern,StubHandle,Collector>layoutStrategy().layout(converter.toPSystem());
				BuildHelper.projectIntoCollector(currentBuildable, bodyFinal, production, converter.symbolicParameterArray());
			}

		}
		
		
		return null;
	}
	
}
