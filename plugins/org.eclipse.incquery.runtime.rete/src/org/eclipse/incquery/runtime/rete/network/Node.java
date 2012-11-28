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

/**
 * A node of a rete network, should be uniquely identified by network and
 * nodeId. NodeId can be requested by registering at the Network on
 * construction.
 * 
 * @author Gabor Bergmann
 */
public interface Node {
	/**
	 * @return the network this node belongs to.
	 */
	ReteContainer getContainer();

	/**
	 * @return the identifier unique to this node within the network.
	 */
	long getNodeId();

	/**
	 * Assigns a descriptive tag to the node
	 */
	void setTag(Object tag);

	/**
	 * @return the tag of the node
	 */
	Object getTag();
	
//	/**
//	 * The semantics of the tuples contained in this node.
//	 * @return a tuple of correct size representing the semantics of each position.
//	 * @post not null
//	 */
//	Tuple getSemantics();

}
