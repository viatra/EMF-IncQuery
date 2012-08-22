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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;


/**
 * Depicts an abstract evaluator that evaluates tuples to Objects. Used inside evaluator nodes.
 * @author Bergmann Gábor
 *
 */
public abstract class AbstractEvaluator {
	/**
	 * Each tuple represents a trace of the activity during evaluation. 
	 * Change notifications received on these traces will be used to trigger re-evaluation.
	 */
	Set<Tuple> traces = new HashSet<Tuple>();
	
	public abstract Object doEvaluate(Tuple tuple) throws Throwable;
	
	Object evaluate(Tuple tuple) throws Throwable {
		traces.clear();
		return doEvaluate(tuple);
	}

	public Set<Tuple> getTraces() {
		return traces;
	}
}
