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

package org.eclipse.viatra2.emf.incquery.validation.core;


import java.util.Collection;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.misc.DeltaMonitor;

/**
 * @author Bergmann Gábor
 *
 */
public class Validator<Signature extends IPatternSignature> {
	private final Constraint<Signature> constraint;
	private final IFile f;
	private final IncQueryMatcher<Signature> matcher;
	private final DeltaMonitor<Signature> dm;
	private final Runnable processMatchesRunnable;


	public Validator(Constraint<Signature> constraint, Resource resource, IFile f) throws IncQueryRuntimeException {
		this.constraint = constraint;
		this.f = f;
		this.matcher = constraint.matcherFactory().getMatcher(resource);
		this.dm = matcher.newDeltaMonitor(true);
		this.processMatchesRunnable = new Runnable() {		
			@Override
			public void run() {
				// after each model update, check the delta monitor
				// FIXME should be: after each complete transaction, check the delta monitor
				try {
					dm.matchFoundEvents.removeAll( processNewMatches(dm.matchFoundEvents) );
					dm.matchLostEvents.removeAll( processLostMatches(dm.matchLostEvents) );		
				} catch (CoreException e) {
					e.printStackTrace();
				}
				
			}
		};
	}

	/**
	 * Call this once to start monitoring validation problems.
	 */
	public void startMonitoring() {
		matcher.addCallbackAfterUpdates(processMatchesRunnable);
		processMatchesRunnable.run();
	}
	
	public Collection<Signature> processNewMatches(Collection<Signature> signatures) throws CoreException {
		Vector<Signature> processed = new Vector<Signature>();
		for (Signature signature : signatures) {
			markProblem(signature);			
			processed.add(signature);
		}
		return processed;
	}

	public Collection<Signature> processLostMatches(Collection<Signature> signatures) throws CoreException {
		Vector<Signature> processed = new Vector<Signature>();
		for (Signature signature : signatures) {
			unmarkProblem(signature);
			// FIXME: visual un-marking not supported by Papyrus
			processed.add(signature);
		}
		return processed;
	}
	
	public void markProblem(Signature affectedElements) throws CoreException {
		// check if it already exists
		ValidationProblem vp = new ValidationProblem<Signature>(constraint, affectedElements);
		if (!ValidationUtil.knownProblem(f, vp)) {
			// if it does not exist yet
			ValidationUtil.putProblem(f, vp);
		}
	}
	
	public void unmarkProblem(Signature affectedElements) throws CoreException {
		ValidationProblem vp = new ValidationProblem<Signature>(constraint, affectedElements);
		if (ValidationUtil.knownProblem(f, vp)) {
			ValidationUtil.removeProblem(f, vp);
		}
	}


}
