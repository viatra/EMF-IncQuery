package org.eclipse.incquery.runtime.extensibility;
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
//package org.eclipse.viatra2.emf.incquery.runtime.extensibility;
//
//import java.util.HashMap;
//
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.ReteContainerBuildable;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.remote.Address;
//import org.eclipse.incquery.patternlanguage.PatternLanguage.Pattern;
//
///**
// * Stateless version of IRetePatternBuilder: takes all parameters upon call.
// * Can be registered in BuilderRegistry for those patterns I can build the matcher for.
// * 
// * Only a simple Context is used to parameterize the construction;
// * 	all additional pattern-independent information should also be known by me.
// * 
// * @author Bergmann Gábor
// *
// */
//public interface IStatelessRetePatternBuilder {
//	/**
//	 * Builds a part of the rete network that will match occurences of a given
//	 * pattern.
//	 * 
//	 * @param buildable
//	 * @param context no builder context required
//	 * @param gtPattern
//	 *            the pattern itself whose matcher subnet has to be built.
//	 * @return production.
//	 * 	          the Production node that should store matchings of
//	 *            the given pattern.
//	 * @throws RetePatternBuildException
//	 *             if construction fails.
//	 */
//	public Address<? extends Receiver> construct(
//			ReteContainerBuildable<Pattern> buildable,
//			IPatternMatcherContext<Pattern> context,
//			Pattern gtPattern) throws RetePatternBuildException;
//	
//	/**
//	 * Extract the position mapping of the graph pattern.
//	 * @return map.
//	 */
//	HashMap<Object, Integer> getPosMapping(Pattern gtPattern);
//
//
//}
