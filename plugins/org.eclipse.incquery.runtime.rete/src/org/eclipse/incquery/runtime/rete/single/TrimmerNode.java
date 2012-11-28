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

package org.eclipse.incquery.runtime.rete.single;

import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.tuple.TupleMask;

/**
 * Trims the matchings as specified by a mask.
 * 
 * @author Gabor Bergmann
 * 
 */
public class TrimmerNode extends TransformerNode {

	protected TupleMask mask;

	/**
	 * @param reteContainer
	 * @param mask
	 *            The mask used to trim substitutions.
	 */
	public TrimmerNode(ReteContainer reteContainer, TupleMask mask) {
		super(reteContainer);
		this.mask = mask;
	}

	/**
	 * @param reteContainer
	 * @param mask
	 *            The mask used to trim substitutions.
	 */
	public TrimmerNode(ReteContainer reteContainer) {
		super(reteContainer);
		this.mask = null;
	}

	/**
	 * @return the mask
	 */
	public TupleMask getMask() {
		return mask;
	}

	/**
	 * @param mask
	 *            the mask to set
	 */
	public void setMask(TupleMask mask) {
		this.mask = mask;
	}

	@Override
	protected Tuple transform(Tuple input) {
		return mask.transform(input);
	}

}
