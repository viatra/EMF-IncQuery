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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index;

import java.util.Collection;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMask;


/**
 * @author Gabor Bergmann Indexer whose lifetime last until the first get() DO
 *         NOT connect to nodes!
 */
public class OnetimeIndexer extends GenericProjectionIndexer {

	public OnetimeIndexer(ReteContainer reteContainer, TupleMask mask) {
		super(reteContainer, mask);
	}

	@Override
	public Collection<Tuple> get(Tuple signature) {
		if (org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.util.Options.releaseOnetimeIndexers) {
			reteContainer.unregisterClearable(memory);
			reteContainer.unregisterNode(this);
		}
		return super.get(signature);
	}

	@Override
	public void appendParent(Supplier supplier) {
		throw new UnsupportedOperationException(
				"onetime indexer cannot have parents");
	}

	@Override
	public void attachListener(IndexerListener listener) {
		throw new UnsupportedOperationException(
				"onetime indexer cannot have listeners");
	}

}
