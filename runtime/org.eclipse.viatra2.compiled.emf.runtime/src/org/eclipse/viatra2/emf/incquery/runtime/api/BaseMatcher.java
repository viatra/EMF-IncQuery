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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.RetePatternMatcher;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.misc.DeltaMonitor;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;

/**
 * @author Bergmann Gábor
 *
 * @param <Signature>
 */
public abstract class BaseMatcher<Signature extends IPatternSignature> implements IncQueryMatcher<Signature> {

	// FIELDS AND CONSTRUCTOR
	
	protected ReteEngine<?> engine; // MUST BE SET UP by subclass constructor
	protected RetePatternMatcher patternMatcher; // MUST BE SET UP by subclass constructor
	protected Map<String, Integer> posMapping;

	public BaseMatcher() {
		super();
		initPosMapping();
	}


	// HELPERS
	
	protected abstract Signature tupleToSignature(Tuple t);

	private Object[] emptyArray() {
		return new Object[getParameterNames().length];
	}

	private boolean[] notNull(Object[] signature) {
		boolean[] notNull = new boolean[signature.length];
		for (int i=0; i<signature.length; ++i) notNull[i] = signature[i] != null;
		return notNull;
	}
	
	private void initPosMapping() {
		if (posMapping == null)
		{
			posMapping = new HashMap<String, Integer>();
			int parameterPosition = 0;
			for (String parameterName : getParameterNames()) {
				posMapping.put(parameterName, parameterPosition++);
			}
		}
	}	
 
	// BASE IMPLEMENTATION

	@Override
	public Integer getPositionOfParameter(String parameterName) {
		return posMapping.get(parameterName);
	}

	@Override
	public Collection<Object[]> getAllMatchesAsArray() {
		return getAllMatchesAsArray(emptyArray());
	}

	@Override
	public Collection<Signature> getAllMatchesAsObject() {
		return getAllMatchesAsObject(emptyArray());
	}

	@Override
	public Collection<Object[]> getAllMatchesAsArray(Object[] signature) {
		ArrayList<Tuple> m = patternMatcher.matchAll(signature, notNull(signature));
		ArrayList<Object[]> matches = new ArrayList<Object[]>();		
		//clones the tuples into Object arrays to prevent the Tuples from modifications outside of the ReteMatcher 
		for(Tuple t: m) matches.add(t.getElements());
		return matches;
	}

	@Override
	public Collection<Signature> getAllMatchesAsObject(Object[] signature) {
		ArrayList<Tuple> m = patternMatcher.matchAll(signature, notNull(signature));
		ArrayList<Signature> matches = new ArrayList<Signature>();		
		//clones the tuples into Object arrays to prevent the Tuples from modifications outside of the ReteMatcher 
		for(Tuple t: m) matches.add(tupleToSignature(t));
		return matches;
	}

	@Override
	public Collection<Object[]> getAllMatchesAsArray(Signature signature) {
		return getAllMatchesAsArray(signature.toArray());
	}

	@Override
	public Collection<Signature> getAllMatchesAsObject(Signature signature) {
		return getAllMatchesAsObject(signature.toArray());
	}
	// with input binding as pattern-specific parameters: not declared in interface

	@Override
	public Object[] getOneMatchAsArray() {
		return getOneMatchAsArray(emptyArray());
	}

	@Override
	public Signature getOneMatchAsObject() {
		return getOneMatchAsObject(emptyArray());
	}

	@Override
	public Object[] getOneMatchAsArray(Object[] signature) {
		Tuple t = patternMatcher.matchOne(signature, notNull(signature));
		if(t != null) 
			return t.getElements();
		else
			return null; 	
	}

	@Override
	public Signature getOneMatchAsObject(Object[] signature) {
		Tuple t = patternMatcher.matchOne(signature, notNull(signature));
		if(t != null) 
			return tupleToSignature(t);
		else
			return null; 	
	}

	@Override
	public Object[] getOneMatchAsArray(Signature signature) {
		return getOneMatchAsArray(signature.toArray());
	}

	@Override
	public Signature getOneMatchAsObject(Signature signature) {
		return getOneMatchAsObject(signature.toArray());
	}
	// with input binding as pattern-specific parameters: not declared in interface

	@Override
	public boolean hasMatch(Object[] signature) {
		return patternMatcher.count(signature, notNull(signature)) > 0;
	}

	@Override
	public boolean hasMatch(Signature signature) {
		return hasMatch(signature.toArray());
	}
	// with input binding as pattern-specific parameters: not declared in interface

	@Override
	public int countMatches() {
		return countMatches(emptyArray());
	}

	@Override
	public int countMatches(Object[] signature) {
		return patternMatcher.count(signature, notNull(signature));
	}

	@Override
	public int countMatches(Signature signature) {
		return countMatches(signature.toArray());
	}
	// with input binding as pattern-specific parameters: not declared in interface

	@Override
	public DeltaMonitor<Signature> newDeltaMonitor(boolean fillAtStart) {
		DeltaMonitor<Signature> dm = new DeltaMonitor<Signature>(engine.getReteNet().getHeadContainer()) {
			@Override
			public Signature statelessConvert(Tuple t) {
				return tupleToSignature(t);
			}
		};
		patternMatcher.connect(dm, fillAtStart);
		return dm;
	}

	@Override
	public boolean addCallbackAfterUpdates(Runnable callback) {
		return engine.getAfterUpdateCallbacks().add(callback);
	}

	@Override
	public boolean removeCallbackAfterUpdates(Runnable callback) {
		return engine.getAfterUpdateCallbacks().remove(callback);
	}
	
	@Override
	public Object[] signatureToArray(Signature signature) {
		return signature.toArray();
	}
}