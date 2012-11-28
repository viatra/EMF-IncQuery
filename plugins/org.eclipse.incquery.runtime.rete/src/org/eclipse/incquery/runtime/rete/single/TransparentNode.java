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

import java.util.Collection;

import org.eclipse.incquery.runtime.rete.network.Direction;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;


/**
 * Simply propagates everything. Might be used to join or fork.
 * 
 * @author Gabor Bergmann
 */
public class TransparentNode extends SingleInputNode {

	public TransparentNode(ReteContainer reteContainer) {
		super(reteContainer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver#update(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple)
	 */
	public void update(Direction direction, Tuple updateElement) {
		propagateUpdate(direction, updateElement);

	}

	public void pullInto(Collection<Tuple> collector) {
		propagatePullInto(collector);
	}

}
