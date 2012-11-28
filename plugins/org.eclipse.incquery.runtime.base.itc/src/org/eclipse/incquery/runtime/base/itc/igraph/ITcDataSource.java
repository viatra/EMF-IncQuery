/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.base.itc.igraph;

import java.util.Set;

/**
 * Defines those methods that are necessary to observ a tc relation provider. 
 * 
 * @author Tamás Szabó
 *
 * @param <V> the type parameter of the node
 */
public interface ITcDataSource<V> {
	
	/**
	 * Attach a tc relation observer.
	 * 
	 * @param to the observer object
	 */
	public void attachObserver(ITcObserver<V> to);
	
	/**
	 * Detach a tc relation observer.
	 * 
	 * @param to the observer object
	 */
	public void detachObserver(ITcObserver<V> to);
	
	/**
	 * Returns all nodes which are reachable from the source node.
	 * 
	 * @param source the source node
	 * @return the set of target nodes
	 */
	public Set<V> getAllReachableTargets(V source);
	
	/**
	 * Returns all nodes from which the target node is reachable.
	 * 
	 * @param target the target node
	 * @return the set of source nodes
	 */
	public Set<V> getAllReachableSources(V target);
	
	/**
	 * Returns true if the target node is reachable from the source node.
	 * 
	 * @param source the source node
	 * @param target the target node
	 * @return true if target is reachable from source, false otherwise
	 */
	public boolean isReachable(V source, V target);
	
	/**
	 * Call this method to properly dispose the data strucutres of a transitive closure algorithm.
	 */
	public void dispose();
}
