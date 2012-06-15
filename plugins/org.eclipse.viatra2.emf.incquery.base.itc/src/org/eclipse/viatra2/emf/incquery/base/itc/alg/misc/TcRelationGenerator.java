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

package org.eclipse.viatra2.emf.incquery.base.itc.alg.misc;


import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.topsort.TopSort;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IBiDirectionalGraphDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IBiDirectionalWrapper;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphDataSource;

public class TcRelationGenerator<V> {
	
	private IBiDirectionalGraphDataSource<V> gds = null;
	
	public TcRelationGenerator(IGraphDataSource<V> gds) {
		
		if (gds instanceof IBiDirectionalGraphDataSource<?>) {
			this.gds = (IBiDirectionalGraphDataSource<V>) gds;
		}
		else {
			this.gds = new IBiDirectionalWrapper<V>(gds);
		}
	}
	
	public org.eclipse.viatra2.emf.incquery.base.itc.alg.counting.TcRelation<V> getCounting1TcRelation() {
		
		org.eclipse.viatra2.emf.incquery.base.itc.alg.counting.TcRelation<V> tc = new org.eclipse.viatra2.emf.incquery.base.itc.alg.counting.TcRelation<V>(true);
		@SuppressWarnings("unchecked")
		List<V> topSort = (List<V>) TopSort.getTopologicalSorting(gds);
		
		Collections.reverse(topSort);
		for (V n : topSort) {			
			List<V> sourceNodes = gds.getSourceNodes(n);		
			
			if (sourceNodes != null) {		
				Set<V> tupEnds = tc.getTupleEnds(n);
				
				for (V s : sourceNodes) {

					tc.addTuple(s, n, 1);
					
					if (tupEnds != null) {
						for (V t : tupEnds) {
							tc.addTuple(s, t, 1);
						}
					}
				}
			}
		}
		return tc;
	}
	
	public org.eclipse.viatra2.emf.incquery.base.itc.alg.dred.TcRelation<V> getDRedTcRelation() {
		
		org.eclipse.viatra2.emf.incquery.base.itc.alg.dred.TcRelation<V> tc = new org.eclipse.viatra2.emf.incquery.base.itc.alg.dred.TcRelation<V>();
		@SuppressWarnings("unchecked")
		List<V> topSort = (List<V>) TopSort.getTopologicalSorting(gds);
		
		Collections.reverse(topSort);
		
		for (V n : topSort) {		
			List<V> sourceNodes = gds.getSourceNodes(n);
			
			if (sourceNodes != null) {
				
				Set<V> tupEnds = tc.getTupleEnds(n);
				
				for (V s : sourceNodes) {
					
					if (!s.equals(n))
						tc.addTuple(s, n);
					
					if (tupEnds != null) {
						for (V t : tupEnds) {
							
							if (!s.equals(t))
								tc.addTuple(s, t);
						}
					}
				}
			}
		}
		return tc;
	}
	
	public org.eclipse.viatra2.emf.incquery.base.itc.alg.counting2.TcRelation<V> getCounting2TcRelation() {
		
		org.eclipse.viatra2.emf.incquery.base.itc.alg.counting2.TcRelation<V> tc = new org.eclipse.viatra2.emf.incquery.base.itc.alg.counting2.TcRelation<V>();
		@SuppressWarnings("unchecked")
		List<V> topSort = (List<V>) TopSort.getTopologicalSorting(gds);
		BigInteger oneBI = BigInteger.valueOf(1);
		
		Collections.reverse(topSort);
		for (V n : topSort) {
			
			List<V> sourceNodes = gds.getSourceNodes(n);
			
			if (sourceNodes != null) {
				
				Set<V> tupEnds = tc.getTupleEnds(n);

				for (V s : sourceNodes) {
					
					if (!s.equals(n))
						tc.addTuple(s, n, oneBI);
					
					if (tupEnds != null) {
						for (V t : tupEnds) {
							
							if (!s.equals(t))
								tc.addTuple(s, t, tc.getCount(n, t));
						}
					}
				}
			}
		}
		return tc;
	}
}
