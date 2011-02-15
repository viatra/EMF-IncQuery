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

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Node;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMask;


/**
 * A node that indexes incoming Tuples by their signatures as specified by a
 * TupleMask. Notifies listeners about such update events through the IndexerListener.
 * 
 * Signature tuples are created by transforming the update tuples using the mask. 
 * Tuples stored with the same signature are grouped together. The group or a reduction thereof is retrievable.
 * @author Gabor Bergmann
 */
public interface Indexer extends Node {
	/**
	 * @return the mask by which the contents are indexed.
	 */
	public TupleMask getMask();
	
	/**
	 * @return the node whose contents are indexed.
	 */
	public Supplier getParent();
	
	/**
	 * @return all stored tuples that conform to the specified signature, null if there are none such.
	 * CONTRACT: do not modify!
	 */
	public Collection<Tuple> get(Tuple signature);

	public void attachListener(IndexerListener listener);
	public void detachListener(IndexerListener listener);

}
