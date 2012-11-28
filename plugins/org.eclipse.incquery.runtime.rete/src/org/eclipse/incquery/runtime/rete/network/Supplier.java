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

package org.eclipse.incquery.runtime.rete.network;

import java.util.Collection;

import org.eclipse.incquery.runtime.rete.index.ProjectionIndexer;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.tuple.TupleMask;


/**
 * @author Gabor Bergmann
 * 
 *         A supplier is an object that can propagate insert or revoke events
 *         towards receivers.
 */
public interface Supplier extends Node {

	/**
	 * pulls the contents of this object in this particular moment into a target
	 * collection
	 */
	public void pullInto(Collection<Tuple> collector);

	/**
	 * appends a receiver that will continously receive insert and revoke
	 * updates from this supplier
	 */
	void appendChild(Receiver receiver);

	/**
	 * removes a receiver
	 */
	void removeChild(Receiver receiver);
	
	/**
	 * Instantiates (or reuses, depending on implementation) an index according to the given mask. 
	 * Intended for internal use; clients should invoke through Library.accesProjectionIndexer() instead to enable reusing.
	 */
	ProjectionIndexer constructIndex(TupleMask mask);

	/**
	 * lists receivers
	 */
	Collection<Receiver> getReceivers();

}
