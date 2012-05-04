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

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;

/**
 * An indexer that allows the iteration of all retrievable tuple groups (or reduced groups).
 * 
 * @author Bergmann Gábor
 *
 */
public interface IterableIndexer extends Indexer, Iterable<Tuple> {

	/**
	 * A collection consisting of exactly those signatures whose tuple group is not empty
	 * CONTRACT: do not modify
	 */
	public Collection<Tuple> getSignatures();
}
