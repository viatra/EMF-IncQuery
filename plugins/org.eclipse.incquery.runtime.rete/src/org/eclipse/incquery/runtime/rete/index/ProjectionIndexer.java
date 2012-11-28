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

package org.eclipse.incquery.runtime.rete.index;


/**
 * An iterable indexer that receives updates from a node, and groups received tuples intact, i.e. it does not reduce tuple groups.
 * 
 * @author Bergmann Gábor
 *
 */
public interface ProjectionIndexer extends IterableIndexer {

}
