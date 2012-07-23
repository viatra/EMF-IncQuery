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

package org.eclipse.viatra2.emf.incquery.base.itc.alg.counting;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.ITcRelation;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.TcRelationGenerator;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IBiDirectionalGraphDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IBiDirectionalWrapper;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphObserver;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.ITcDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.ITcObserver;

/**
 * This class is the optimized implementation of the Counting algorithm.
 * 
 * @author Tamas Szabo
 * 
 * @param <V>
 *            the type parameter of the nodes in the graph data source
 */
public class CountingAlg<V> implements IGraphObserver<V>, ITcDataSource<V> {

	private static final long serialVersionUID = -2383210800242398869L;
	private CountingTcRelation<V> tc = null;
	private CountingTcRelation<V> dtc = null;
	private IBiDirectionalGraphDataSource<V> gds = null;
	private ArrayList<ITcObserver<V>> observers;

	/**
	 * Constructs a new Counting algorithm and initializes the transitive
	 * closure relation with the given graph data source. Attach itself on the
	 * graph data source as an observer.
	 * 
	 * @param gds
	 *            the graph data source instance
	 */
	public CountingAlg(IGraphDataSource<V> gds) {

		if (gds instanceof IBiDirectionalGraphDataSource<?>) {
			this.gds = (IBiDirectionalGraphDataSource<V>) gds;
		} else {
			this.gds = new IBiDirectionalWrapper<V>(gds);
		}

		observers = new ArrayList<ITcObserver<V>>();
		tc = new CountingTcRelation<V>(true);
		dtc = new CountingTcRelation<V>(false);

		initTc();
		gds.attachObserver(this);
	}

	/**
	 * Initializes the transitive closure relation.
	 */
	private void initTc() {
		TcRelationGenerator<V> tcg = new TcRelationGenerator<V>(gds);
		this.setTcRelation(tcg.getTcRelation());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see itc.igraph.IGraphObserver#edgeInserted(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public void edgeInserted(V source, V target) {
		if (!source.equals(target)) {
			deriveTc(source, target, 1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see itc.igraph.IGraphObserver#edgeDeleted(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public void edgeDeleted(V source, V target) {
		if (!source.equals(target)) {
			deriveTc(source, target, -1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see itc.igraph.IGraphObserver#nodeInserted(java.lang.Object)
	 */
	@Override
	public void nodeInserted(V n) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see itc.igraph.IGraphObserver#nodeDeleted(java.lang.Object)
	 */
	@Override
	public void nodeDeleted(V n) {
		this.tc.deleteTupleEnd(n);
	}

	/**
	 * Derives the transitive closure relation when an edge is inserted or
	 * deleted.
	 * 
	 * @param source
	 *            the source of the edge
	 * @param target
	 *            the target of the edge
	 * @param dCount
	 *            the value is -1 if an edge was deleted and +1 if an edge was
	 *            inserted
	 */
	private void deriveTc(V source, V target, int dCount) {

//		if (dCount == 1 && isReachable(target, source)) {
//			System.out.println("The graph contains cycle with (" + source + ","+ target + ") edge!");
//		}

		dtc.clear();
		Set<V> tupEnds = null;

		// 1. d(tc(x,y)) :- d(l(x,y))
		if (tc.addTuple(source, target, dCount)) {
			dtc.addTuple(source, target, dCount);
			notifyTcObservers(source, target, dCount);
		}

		// 2. d(tc(x,y)) :- d(l(x,z)) & tc(z,y)
		tupEnds = tc.getTupleEnds(target);
		if (tupEnds != null) {
			for (V tupEnd : tupEnds) {
				if (!tupEnd.equals(source)) {	
					if (tc.addTuple(source, tupEnd, dCount)) {
						dtc.addTuple(source, tupEnd, dCount);
						notifyTcObservers(source, tupEnd, dCount);
					}
				}
			}
		}		
		
		// 3. d(tc(x,y)) :- lv(x,z) & d(tc(z,y))
		CountingTcRelation<V> newTuples = new CountingTcRelation<V>(false);
		CountingTcRelation<V> tmp = null;
		List<V> nodes = null;
		newTuples.union(dtc);

		while (!newTuples.isEmpty()) {

			tmp = dtc;
			dtc = newTuples;
			newTuples = tmp;
			newTuples.clear();

			for (V tS : dtc.getTupleStarts()) {

				nodes = gds.getSourceNodes(tS);
				if (nodes != null) {

					for (V nS : nodes) {

						tupEnds = dtc.getTupleEnds(tS);
						if (tupEnds != null) {

							for (V tT : tupEnds) {

								if (!nS.equals(tT)) {
									if (tc.addTuple(nS, tT, dCount)) {
										newTuples.addTuple(nS, tT, dCount);
										notifyTcObservers(nS, tT, dCount);
									}
								}
							}
						}
					}
				}
			}
		}
		
		//System.out.println(tc);
	}

	public ITcRelation<V> getTcRelation() {
		return this.tc;
	}

	public void setTcRelation(ITcRelation<V> tc) {
		this.tc = new CountingTcRelation<V>(true);
		for (V s : tc.getTupleStarts()) {
			for (V t : tc.getTupleEnds(s)) {
				this.tc.addTuple(s, t, 1);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see itc.igraph.ITcDataSource#isReachable(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public boolean isReachable(V source, V target) {
		return tc.containsTuple(source, target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see itc.igraph.ITcDataSource#attachObserver(itc.igraph.ITcObserver)
	 */
	@Override
	public void attachObserver(ITcObserver<V> to) {
		this.observers.add(to);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see itc.igraph.ITcDataSource#detachObserver(itc.igraph.ITcObserver)
	 */
	@Override
	public void detachObserver(ITcObserver<V> to) {
		this.observers.remove(to);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see itc.igraph.ITcDataSource#getTargetNodes(java.lang.Object)
	 */
	@Override
	public Set<V> getAllReachableTargets(V source) {
		return this.tc.getTupleEnds(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see itc.igraph.ITcDataSource#getSourceNodes(java.lang.Object)
	 */
	@Override
	public Set<V> getAllReachableSources(V target) {
		return this.tc.getTupleStarts(target);
	}

	private void notifyTcObservers(V source, V target, int dir) {
		for (ITcObserver<V> o : observers) {
			if (dir == 1)
				o.tupleInserted(source, target);
			if (dir == -1)
				o.tupleDeleted(source, target);
		}
	}

	@Override
	public void dispose() {
		tc.clear();
		tc = null;
		dtc.clear();
		dtc = null;
		gds = null;
		observers = null;
	}
}