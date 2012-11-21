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

package org.eclipse.viatra2.emf.incquery.base.itc.test.misc;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.Tuple;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.ITcObserver;

/**
 * The {@link TestObserver} class can be used to assert the notifications of a 
 * transitive closure algorithm. Before each edge deletion/insertion set the 
 * expected tuples of notifications and the observer assert these tuples. 
 * Don't forget to erase the contents of the observer before the next 
 * edge manipulation. 
 * 
 * @author Tamas Szabo
 *
 * @param <V> the type parameter of the tuples
 */
public class TestObserver<V> implements ITcObserver<V> {

	private Set<Tuple<V>> deletedTuples;
	private Set<Tuple<V>> insertedTuples;
	
	public TestObserver() {
		this.deletedTuples = new HashSet<Tuple<V>>();
		this.insertedTuples = new HashSet<Tuple<V>>();
	}
	
	public void addDeletedTuple(Tuple<V> tuple) {
		this.deletedTuples.add(tuple);
	}
	
	public void addInsertedTuple(Tuple<V> tuple) {
		this.insertedTuples.add(tuple);
	}
	
	public void clearTuples() {
		this.deletedTuples.clear();
		this.insertedTuples.clear();
	}

	@Override
	public void tupleInserted(V source, V target) {
		assertTrue(this.insertedTuples.contains(new Tuple<V>(source, target)));
	}

	@Override
	public void tupleDeleted(V source, V target) {
		assertTrue(this.deletedTuples.contains(new Tuple<V>(source, target)));
	}

}
