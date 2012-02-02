///*******************************************************************************
// * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// *    Gabor Bergmann - initial API and implementation
// *******************************************************************************/
//
//package org.eclipse.viatra2.patternlanguage.emf.matcherbuilder.internal;
//
//import java.util.HashMap;
//
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Buildable;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.basiclinear.BasicLinearLayout;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.helpers.BuildHelper;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.util.Options;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.util.Options.BuilderMethod;
//import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
//import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
//import org.eclipse.viatra2.patternlanguage.emf.matcherbuilder.runtime.PatternRegistry;
//
///**
// * @author Bergmann Gábor
// *
// */
//public class EPMBuilder<StubHandle, Collector> implements IRetePatternBuilder<String, StubHandle, Collector> 
//{
//	protected Buildable<String, StubHandle, Collector> baseBuildable;
//	protected IPatternMatcherContext<Pattern> context;
//
//	/**
//	 * @param baseBuildable
//	 * @param context
//	 */
//	EPMBuilder(Buildable<String, StubHandle, Collector> baseBuildable,
//			IPatternMatcherContext<Pattern> context) {
//		super();
//		this.baseBuildable = baseBuildable;
//		this.context = context;
//	}
//
//
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder#getContext()
//	 */
//	@Override
//	public IPatternMatcherContext<Pattern> getContext() {
//		return context;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder#refresh()
//	 */
//	@Override
//	public void refresh() {
//		baseBuildable.reinitialize();
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder#construct(java.lang.Object)
//	 */
//	@Override
//	public Collector construct(String gtPattern)
//			throws RetePatternBuildException {
//		throw new UnsupportedOperationException();
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder#getPosMapping(java.lang.Object)
//	 */
//	@Override
//	public HashMap<Object, Integer> getPosMapping(String gtPattern) {
//		throw new UnsupportedOperationException();
//	}
//}
