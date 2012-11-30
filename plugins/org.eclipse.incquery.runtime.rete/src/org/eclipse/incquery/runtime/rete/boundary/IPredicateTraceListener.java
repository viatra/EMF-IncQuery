/*******************************************************************************
 * Copyright (c) 2004-2009 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.boundary;

import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * Collects traces of predicate evaluation, and notifies the predicate evaluator node to re-evaluate predicates when
 * these traces are influenced by changes.
 * 
 * @author Bergmann Gábor
 * 
 */
public interface IPredicateTraceListener extends Disconnectable {

    public void registerSensitiveTrace(Tuple trace, PredicateEvaluatorNode node);

    public void unregisterSensitiveTrace(Tuple trace, PredicateEvaluatorNode node);

}