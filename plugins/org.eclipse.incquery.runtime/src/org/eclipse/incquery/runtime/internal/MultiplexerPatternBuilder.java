package org.eclipse.incquery.runtime.internal;
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
//package org.eclipse.viatra2.emf.incquery.runtime.internal;
//
//import java.util.HashMap;
//
//import org.eclipse.viatra2.emf.incquery.runtime.extensibility.BuilderRegistry;
//import org.eclipse.viatra2.emf.incquery.runtime.extensibility.IStatelessRetePatternBuilder;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.ReteContainerBuildable;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.remote.Address;
//import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
//import org.eclipse.incquery.patternlanguage.PatternLanguage.Pattern;
//
///**
// * Internal RetePatternBuilder that multiplexes build requests to contributions to the BuilderRegistry.
// * Multiplexation is keyed by pattern fqn.
// * @author Bergmann Gábor
// *
// */
//public class MultiplexerPatternBuilder implements
//		IRetePatternBuilder<Pattern, Address<? extends Supplier>, Address<? extends Receiver>>
//{
//	ReteContainerBuildable<Pattern> baseBuildable;
//	IPatternMatcherContext<Pattern> context;
//	IRetePatternBuilder<Pattern, Address<? extends Supplier>, Address<? extends Receiver>> fallbackBuilder;
//
//	/**
//	 * @param baseBuildable
//	 * @param context
//	 */
//	public MultiplexerPatternBuilder(
//			ReteContainerBuildable<Pattern> baseBuildable,
//			IPatternMatcherContext<Pattern> context,
//			IRetePatternBuilder<Pattern, Address<? extends Supplier>, Address<? extends Receiver>> fallbackBuilder) {
//		super();
//		this.baseBuildable = baseBuildable;
//		this.context = context;
//		this.fallbackBuilder = fallbackBuilder;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder#construct(java.lang.Object)
//	 */
//	@Override
//	public Address<? extends Receiver> construct(Pattern gtPattern)
//			throws RetePatternBuildException {
//		IStatelessRetePatternBuilder builder = BuilderRegistry.getContributedStatelessPatternBuilders().get(gtPattern);
//		if (builder != null) return builder.construct(baseBuildable, context, gtPattern);
//		else throw new RetePatternBuildException("No RETE pattern builder generated for pattern {1}.",
//				new String[]{CorePatternLanguageHelper.getFullyQualifiedName(gtPattern).toString()}, gtPattern);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder#getPosMapping(java.lang.Object)
//	 */
//	@Override
//	public HashMap<Object, Integer> getPosMapping(Pattern pattern) {
//		IStatelessRetePatternBuilder builder = BuilderRegistry.getContributedStatelessPatternBuilders().get(pattern);
//		if (builder != null) return builder.getPosMapping(pattern); else return fallbackBuilder.getPosMapping(pattern);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder#refresh()
//	 */
//	@Override
//	public void refresh() {
//		throw new UnsupportedOperationException();
//	}
//
////	/* (non-Javadoc)
////	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder#getBuildable()
////	 */
////	@Override
////	public Buildable<String, Address<? extends Supplier>, Address<? extends Receiver>> getBuildable() {
////		return baseBuildable;
////	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder#getContext()
//	 */
//	@Override
//	public IPatternMatcherContext<Pattern> getContext() {
//		return context;
//	}
//
//}
