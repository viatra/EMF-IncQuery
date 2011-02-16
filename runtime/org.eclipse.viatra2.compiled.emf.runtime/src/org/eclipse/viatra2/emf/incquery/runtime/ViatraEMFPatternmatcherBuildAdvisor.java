/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.runtime;

import org.eclipse.viatra2.emf.incquery.runtime.exception.ViatraCompiledRuntimeException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.ReteContainerBuildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;



/**
 * Applies a precompiled builder on an incremental pattern matcher engine.
 * @author Bergmann Gábor
 *
 */
public interface ViatraEMFPatternmatcherBuildAdvisor {
	
	void applyBuilder(ReteEngine<String> engine,
			ReteContainerBuildable<String> buildable,
			IPatternMatcherContext<String> context) throws ViatraCompiledRuntimeException;

}
