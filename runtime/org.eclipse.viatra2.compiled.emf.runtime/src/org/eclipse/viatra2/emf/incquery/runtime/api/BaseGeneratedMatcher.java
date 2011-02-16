/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.runtime.api;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.viatra2.emf.incquery.runtime.EngineManager;
import org.eclipse.viatra2.emf.incquery.runtime.exception.ViatraCompiledRuntimeException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;

/**
 * Performs the initialization of a BaseMatcher so that it is adapted to the EMF-IncQuery runtime component.
 * @author Bergmann Gábor
 * @param <Signature>
 *
 */
public abstract class BaseGeneratedMatcher<Signature extends IPatternSignature> extends BaseMatcher<Signature> {
	public BaseGeneratedMatcher(Notifier emfRoot) throws ViatraCompiledRuntimeException {
		this(emfRoot, 0);
	}	
	public BaseGeneratedMatcher(Notifier emfRoot, int numThreads) throws ViatraCompiledRuntimeException {		
		try {		
			ReteEngine<String> reteEngine = EngineManager.getInstance().getReteEngine(emfRoot, numThreads);
			engine = reteEngine;
			patternMatcher = reteEngine.accessMatcher(getPatternName());
		} catch (RetePatternBuildException e) {
			throw new ViatraCompiledRuntimeException(e);
		}
	}
	public BaseGeneratedMatcher(TransactionalEditingDomain trDomain) throws ViatraCompiledRuntimeException {
		this(trDomain, 0);
	}	
	public BaseGeneratedMatcher(TransactionalEditingDomain trDomain, int numThreads) throws ViatraCompiledRuntimeException {

		try {		
			ReteEngine<String> reteEngine = EngineManager.getInstance().getReteEngine(trDomain,numThreads);
			engine = reteEngine;
			patternMatcher = reteEngine.accessMatcher(getPatternName());
		} catch (RetePatternBuildException e) {
			throw new ViatraCompiledRuntimeException(e);
		}	
	}
	
}
