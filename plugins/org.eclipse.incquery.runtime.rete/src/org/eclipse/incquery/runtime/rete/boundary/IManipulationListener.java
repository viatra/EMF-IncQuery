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

package org.eclipse.incquery.runtime.rete.boundary;

/**
 * @author Bergmann Gábor
 *
 */
public interface IManipulationListener extends Disconnectable {
	
	/**
	 * @param element
	 * @param termEvaluatorNode
	 */
	void registerSensitiveTerm(Object element,
			PredicateEvaluatorNode termEvaluatorNode);

	/**
	 * @param element
	 * @param termEvaluatorNode
	 */
	void unregisterSensitiveTerm(Object element,
			PredicateEvaluatorNode termEvaluatorNode);

}
