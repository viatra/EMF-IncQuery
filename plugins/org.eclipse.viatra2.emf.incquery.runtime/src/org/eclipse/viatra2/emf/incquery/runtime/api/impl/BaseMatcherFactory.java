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

package org.eclipse.viatra2.emf.incquery.runtime.api.impl;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra2.emf.incquery.runtime.api.EngineManager;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;

/**
 * Base implementation of IMatcherFactory.
 * @author Bergmann Gábor
 *
 */
public abstract class BaseMatcherFactory<Signature extends IPatternSignature, Matcher extends IncQueryMatcher<Signature>> 
	implements IMatcherFactory<Signature, Matcher> 
{
	public abstract Matcher instantiate(IncQueryEngine engine) throws IncQueryRuntimeException;
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory#getMatcher(org.eclipse.emf.common.notify.Notifier, int)
	 */
	@Override
	public Matcher getMatcher(Notifier emfRoot) throws IncQueryRuntimeException {
		IncQueryEngine engine = EngineManager.getInstance().getIncQueryEngine(emfRoot);
		return instantiate(engine);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory#getMatcher(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine)
	 */
	@Override
	public Matcher getMatcher(IncQueryEngine engine) throws IncQueryRuntimeException {
		return instantiate(engine);
	}


// // EXPERIMENTAL	
//	
//	/* (non-Javadoc)
//	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory#getMatcher(org.eclipse.emf.transaction.TransactionalEditingDomain)
//	 */
//	@Override
//	public Matcher getMatcher(TransactionalEditingDomain trDomain) throws IncQueryRuntimeException {
//		return getMatcher(trDomain, 0);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory#getMatcher(org.eclipse.emf.transaction.TransactionalEditingDomain, int)
//	 */
//	@Override
//	public Matcher getMatcher(TransactionalEditingDomain trDomain, int numThreads) throws IncQueryRuntimeException {
//		try {		
//			IncQueryEngine engine = EngineManager.getInstance().getReteEngine(trDomain, numThreads);
//			return instantiate(engine);
//		} catch (RetePatternBuildException e) {
//			throw new IncQueryRuntimeException(e);
//		}
//	}

}
