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
//import java.util.Map;
//
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Buildable;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
//import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
//import org.eclipse.xtext.xbase.XExpression;
//
///**
// * @author Bergmann Gábor
// *
// */
//public interface EPMBuildable<StubHandle, Collector> extends Buildable<Pattern, StubHandle, Collector> {
//	public Stub<StubHandle> buildEPMTermChecker(XExpression expression, 
//			Map<String, Integer> variableIndices,
//			Stub<StubHandle> stub) throws RetePatternBuildException;
//
//	public EPMBuildable<StubHandle, Collector> getNextContainer();
//	public EPMBuildable<StubHandle, Collector> putOnTab(Pattern effort);
//}
