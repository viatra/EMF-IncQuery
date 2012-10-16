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

public class TestObserver implements ITcObserver<Integer> {

	private Set<Tuple<Integer>> tuples;
	
	public TestObserver() {
		this.tuples = new HashSet<Tuple<Integer>>();
	}
	
	@Override
	public void tupleInserted(Integer source, Integer target) {
		assertTrue(this.tuples.contains(new Tuple<Integer>(source, target)));
	}

	@Override
	public void tupleDeleted(Integer source, Integer target) {
		assertTrue(this.tuples.contains(new Tuple<Integer>(source, target)));
	}
	
	public Set<Tuple<Integer>> getTuples() {
		return tuples;
	}

}
