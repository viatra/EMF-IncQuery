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

import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.RetePatternMatcher;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;

/**
 * This is a generic pattern matcher for an arbitrary IncQuery pattern.
 * Please instantiate using a GenericMatcherFactory. 
 * 
 * Matches of the pattern will be represented as GenericPatternSignature. 
 * See also the generated matcher and signature of the pattern, with pattern-specific API simplifications.
 * 
 * @author Bergmann Gábor
 *
 */
@SuppressWarnings("unused")
public class GenericPatternMatcher extends BaseMatcher<GenericPatternSignature> implements IncQueryMatcher<GenericPatternSignature> {

	String patternName;
	private String[] parameterNames;
	
	
	/**
	 * Wraps the familiar API around an internal matcher object.
	 * @throws IncQueryRuntimeException 
	 */
	GenericPatternMatcher(String patternName, IncQueryEngine engine, RetePatternMatcher matcher) 
			throws IncQueryRuntimeException 
	{
		super(engine, matcher);
		this.patternName = patternName;		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#getPatternName()
	 */
	@Override
	public String getPatternName() {
		return patternName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#getParameterNames()
	 */
	@Override
	public String[] getParameterNames() {
		if (parameterNames == null) {
			HashMap<Object, Integer> rawPosMapping = patternMatcher.getPosMapping();
			parameterNames = new String[rawPosMapping.size()];
			for (Entry<Object, Integer> entry : rawPosMapping.entrySet()) {
				Object key = entry.getKey();
				if (key instanceof String) 
					parameterNames[entry.getValue()] = (String) key;
			}
		}
		return parameterNames;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#arrayToSignature(java.lang.Object[])
	 */
	@Override
	public GenericPatternSignature arrayToSignature(Object[] signature) {
		return new GenericPatternSignature(getPatternName(), getParameterNames(), getPosMapping(), signature);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.BaseMatcher#tupleToSignature(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple)
	 */
	@Override
	protected GenericPatternSignature tupleToSignature(Tuple t) {
		return new GenericPatternSignature(getPatternName(), getParameterNames(), getPosMapping(), t.getElements());
	}

}
