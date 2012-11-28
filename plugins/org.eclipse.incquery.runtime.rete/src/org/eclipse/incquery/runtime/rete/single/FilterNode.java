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
 * This node implements a simple filter. A stateless abstract check() predicate
 * determines whether a matching is allowed to pass.
 * 
 * 
 * 
 * @author Gabor Bergmann
 * 
 */
public abstract class FilterNode extends SingleInputNode {

	public FilterNode(ReteContainer reteContainer) {
		super(reteContainer);
	}

	/**
	 * Abstract filtering predicate. Expected to be stateless.
	 * 
	 * @param ps
	 *            the matching to be checked.
	 * @return true if and only if the parameter matching is allowed to pass
	 *         through this node.
	 */
	public abstract boolean check(Tuple ps);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier#pullInto(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver)
	 */
	public void pullInto(Collection<Tuple> collector) {
		for (Tuple ps : reteContainer.pullPropagatedContents(this)) {
			if (check(ps))
				collector.add(ps);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver#update(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple)
	 */
	public void update(Direction direction, Tuple updateElement) {
		if (check(updateElement))
			propagateUpdate(direction, updateElement);
	}

}
