/*******************************************************************************
 * Copyright (c) 2004-2012 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.index;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.incquery.runtime.rete.network.Direction;
import org.eclipse.incquery.runtime.rete.network.Node;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.network.Supplier;
import org.eclipse.incquery.runtime.rete.tuple.FlatTuple;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.tuple.TupleMask;

/**
 * Defines an abstract trivial indexer that projects the contents of some stateful node to the empty tuple, and can therefore save space.
 * Can only exist in connection with a stateful store, and must be operated by another node (the active node). Do not attach parents directly!
 * @author Bergmann Gábor
 */
public abstract class NullIndexer extends SpecializedProjectionIndexer {

	protected abstract Collection<Tuple> getTuples();

	static Object[] empty = {};
	static Tuple nullSignature = new FlatTuple(empty);
	static Collection<Tuple> nullSingleton = Collections.singleton(nullSignature);
	static Collection<Tuple> emptySet = Collections.emptySet();

	public NullIndexer(ReteContainer reteContainer, int tupleWidth, Supplier parent, Node activeNode) {
		super(reteContainer, TupleMask.linear(0, tupleWidth), parent, activeNode);
	}

	public Collection<Tuple> get(Tuple signature) {
		if (nullSignature.equals(signature)) return isEmpty()? null :getTuples();
		else return null;
	}

	public Collection<Tuple> getSignatures() {
		return isEmpty() ? emptySet : nullSingleton;
	}

	/**
	 * @return
	 */
	protected boolean isEmpty() {
		return getTuples().isEmpty();
	}
	protected boolean isSingleElement() {
		return getTuples().size() == 1;
	}

	public Iterator<Tuple> iterator() {
		return getTuples().iterator();
	}

	public void propagate(Direction direction, Tuple updateElement) {
		boolean radical = (direction==Direction.REVOKE && isEmpty()) || (direction==Direction.INSERT && isSingleElement());
		propagate(direction, updateElement, nullSignature, radical);
	}


}