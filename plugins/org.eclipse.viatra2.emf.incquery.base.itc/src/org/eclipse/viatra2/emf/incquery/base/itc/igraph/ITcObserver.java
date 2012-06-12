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

package org.eclipse.viatra2.emf.incquery.base.itc.igraph;

/**
 * Interface ITcObserver is used to observ the changes in a transitive closure relation; tuple insertion/deleteion.
 * 
 * @author Szabó Tamás
 *
 */
public interface ITcObserver<V> {
	
	/**
	 * Used to notify when a tuple is inserted into the transitive closure relation. 
	 * 
	 * @param source the source of the tuple
	 * @param target the target of the tuple
	 */
	public void tupleInserted(V source, V target);
	
	/**
	 * Used to notify when a tuple is deleted from the transitive closure relation.
	 * 
	 * @param source the source of the tuple
	 * @param target the target of the tuple
	 */
	public void tupleDeleted(V source, V target);
}
