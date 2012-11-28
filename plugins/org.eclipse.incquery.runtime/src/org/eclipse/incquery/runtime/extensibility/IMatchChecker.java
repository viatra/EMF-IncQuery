/*******************************************************************************
 * Copyright (c) 2004-2010 Andras Okros and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andras Okros - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.extensibility;

import java.util.Map;

import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * An interface for the org.eclipse.viatra2.emf.incquery.xexpressionevaluator
 * extension point schema}
 */
public interface IMatchChecker {

	/**
	 * The implementation should return the result value of the xexpression.
	 * 
	 * @param tuple
	 *            which holds the actual values for this expression
	 * @param tupleNameMap
	 *            which holds the used name-integer pairs, to be able to get out
	 *            the attributes from the tuple
	 */
	public Object evaluateXExpression(final Tuple tuple,
			final Map<String, Integer> tupleNameMap);

}
