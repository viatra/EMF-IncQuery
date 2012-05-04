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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index;

import java.util.Collection;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;

/**
 * An aggregation node that simply count the number of tuples conforming to the signature.
 * @author Bergmann Gábor
 *
 */
public class CountNode extends AggregatorNode {
	

	public CountNode(ReteContainer reteContainer, ProjectionIndexer projection) {
		super(reteContainer, projection);
	}


	int sizeOf(Collection<Tuple> group) {
		return group==null ? 0 : group.size();
	}


	@Override
	public Object aggregateGroup(Tuple signature, Collection<Tuple> group) {
		return sizeOf(group);
	}

	@Override
	public Object[] aggregateGroupBeforeAndNow(Tuple signature,
			Collection<Tuple> currentGroup, Direction direction,
			Tuple updateElement, boolean change) 
	{
		int currentSize = sizeOf(currentGroup);
		int previousSize = (direction==Direction.INSERT) ? currentSize - 1 : currentSize + 1;
		Object[] result = {previousSize, currentSize};
		return result;
	}



}
