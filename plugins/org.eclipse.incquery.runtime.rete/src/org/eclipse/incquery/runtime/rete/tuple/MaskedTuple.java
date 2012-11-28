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

package org.eclipse.incquery.runtime.rete.tuple;

/**
 * A tuple that transparently provides a masked (transformed) view of another tuple.
 * @author Bergmann Gábor
 *
 */
public class MaskedTuple extends Tuple {

	Tuple wrapped;
	TupleMask mask;
	
	
	public MaskedTuple(Tuple wrapped, TupleMask mask) {
		super();
//		if (wrapped instanceof MaskedTuple) {
//			MaskedTuple parent = (MaskedTuple)wrapped;
//			this.wrapped = parent.wrapped;
//			this.mask = mask.transform(parent.mask);
//		}
//		else 
		{
			this.wrapped = wrapped;
			this.mask = mask;
		}
	}

	@Override
	public Object get(int index) {
		return wrapped.get(mask.indices[index]);
	}

	@Override
	public int getSize() {
		return mask.indices.length;
	}

}
