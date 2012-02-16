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

package org.eclipse.viatra2.emf.incquery.runtime.api;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Interface for an IncQuery matcher factory.
 * Each factory is associated with a pattern.
 * Methods instantiate a matcher of the pattern with various parameters.
 * 
 * @author Bergmann Gábor
 *
 */
public interface IMatcherFactory<Signature extends IPatternMatch, Matcher extends IncQueryMatcher<Signature>> {
	
	/** 
	 * @returns the pattern for which matchers can be instantiated. 
	 */
	public Pattern getPattern();
	/**
	 * Identifies the pattern for which matchers can be instantiated. 
	 */
	public String getPatternFullyQualifiedName();
	
	/**
	 * Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet). 
	 * If a pattern matcher is already constructed with the same root, only a lightweight reference is created.
	 * The match set will be incrementally refreshed upon updates from the given EMF root and below.
	 * 
	 * Note: if emfRoot is a resourceSet, the scope will include even those resources that are not part of the resourceSet but are referenced. 
	 * 	This is mainly to support nsURI-based instance-level references to registered EPackages.
	 * 
	 * @param emfRoot the root of the EMF tree where the pattern matcher will operate. Recommended: Resource or ResourceSet.
	 * @throws IncQueryRuntimeException if an error occurs during pattern matcher creation
	 */
	public Matcher getMatcher(Notifier emfRoot) throws IncQueryRuntimeException;
	
	/**
	 * Initializes the pattern matcher within an existing EMF-IncQuery engine. 
	 * If the pattern matcher is already constructed in the engine, only a lightweight reference is created.
	 * The match set will be incrementally refreshed upon updates.
	 * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
	 * @throws IncQueryRuntimeException if an error occurs during pattern matcher creation
	 */
	public Matcher getMatcher(IncQueryEngine engine) throws IncQueryRuntimeException;

// // EXPERIMENTAL	
// 	
//	/**
//	 * Initializes the pattern matcher over a given EMF root (recommended: Resource or ResourceSet). 
//	 * If a pattern matcher is already constructed with the same root, only a lightweight reference is created.
//	 * The match set will be incrementally refreshed upon updates from the given EMF root and below.
//	 * 
//	 * Note: if emfRoot is a resourceSet, the scope will include even those resources that are not part of the resourceSet but are referenced. 
//	 * 	This is mainly to support nsURI-based instance-level references to registered EPackages.
//	 * 
//	 * @param emfRoot the root of the EMF tree where the pattern matcher will operate. Recommended: Resource or ResourceSet.
//	 * @param numThreads 0 for single-threaded execution (recommended), 
//	 *   or a positive number of separate threads of pattern matter execution (experimental).
//	 * @throws IncQueryRuntimeException if an error occurs during pattern matcher creation
//	 */
//	public Matcher getMatcher(Notifier emfRoot, int numThreads) throws IncQueryRuntimeException;

// // EXPERIMENTAL	
//	
//	/**
//	 * Initializes the pattern matcher over a given EMF transactional editing domain. 
//	 * If a pattern matcher is already constructed with the same editing domain, only a lightweight reference is created.
//	 * The match set will be incrementally refreshed upon committed EMF transactions.
//	 * @param trDomain the EMF transactional editing domain over which the pattern matcher will operate.
//	 * @param numThreads 0 for single-threaded execution (recommended), 
//	 *  or a positive number of separate threads of pattern matter execution (experimental).
//	 * @throws IncQueryRuntimeException if an error occurs during pattern matcher creation
//	 */
//	public Matcher getMatcher(TransactionalEditingDomain trDomain) throws IncQueryRuntimeException;
//	/**
//	 * Initializes the pattern matcher over a given EMF transactional editing domain. 
//	 * If a pattern matcher is already constructed with the same editing domain, only a lightweight reference is created.
//	 * The match set will be incrementally refreshed upon committed EMF transactions.
//	 * @param trDomain the EMF transactional editing domain over which the pattern matcher will operate.
//	 * @throws IncQueryRuntimeException if an error occurs during pattern matcher creation
//	 */
//	public Matcher getMatcher(TransactionalEditingDomain trDomain, int numThreads) throws IncQueryRuntimeException;
}
