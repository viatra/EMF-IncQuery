package org.eclipse.viatra2.emf.incquery.base.itc.alg.dred;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.Tuple;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.dfs.DFSAlg;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphObserver;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.ITcDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.ITcObserver;

/**
 * This class is the optimized implementation of the DRED algorithm. 
 * 
 * @author Tamas Szabo
 *
 * @param <V> the type parameter of the nodes in the graph data source
 */
public class DRedAlg<V> implements IGraphObserver<V>, ITcDataSource<V> {

	private static final long serialVersionUID = 356353826099208151L;
	private IGraphDataSource<V> graphDataSource = null;
	private TcRelation<V> tc = null;
	private TcRelation<V> dtc = null;
	private ArrayList<ITcObserver<V>> observers;
	public static long sumOfUpdateTime = 0;
	public static long updateCount = 0;
	public static long initTime = 0;
	
	/**
	 * Constructs a new DRED algorithm and initializes the transitive closure relation with the given graph data source.
	 * Attach itself on the graph data source as an observer. 
	 * 
	 * @param gds the graph data source instance
	 */
	public DRedAlg(IGraphDataSource<V> gds) {	
		this.observers = new ArrayList<ITcObserver<V>>();
		this.graphDataSource = gds;
		this.tc = new TcRelation<V>();
		this.dtc = new TcRelation<V>();
		initTc();
		graphDataSource.attachObserver(this);
	}
	
	/**
	 * Constructs a new DRED algorithm and initializes the transitive closure relation with the given relation.
	 * Attach itself on the graph data source as an observer. 
	 * 
	 * @param gds the graph data source instance
	 * @param tc the transitive closure instance
	 */
	public DRedAlg(IGraphDataSource<V> gds, TcRelation<V> tc) {
		this.graphDataSource = gds;
		this.tc = tc;
		this.dtc = new TcRelation<V>();
		graphDataSource.attachObserver(this);
	}
	
	/**
	 * Initializes the transitive closure relation.
	 */
	private void initTc() {
		long tmp = System.nanoTime();
		DFSAlg<V> dfsa = new DFSAlg<V>(this.graphDataSource);
		this.setTcRelation(dfsa.getTcRelation());
		this.graphDataSource.detachObserver(dfsa);
		initTime = (System.nanoTime() - tmp);
	}
	
	/*
	 * (non-Javadoc)
	 * @see itc.igraph.IGraphObserver#edgeInserted(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void edgeInserted(V source, V target) {
		if (!source.equals(target)) {
			updateCount++;
			long tmp = System.nanoTime();
			
			Set<V> tupStarts = null;
			Set<V> tupEnds = null;
			Set<Tuple<V>> tuples = new HashSet<Tuple<V>>();
			
			//1. d+(tc(x,y)) :- d(l(x,y))
			if (!source.equals(target)) {
				if (tc.addTuple(source, target)) {
					tuples.add(new Tuple<V>(source, target));
				}
			}
			
			//2. d+(tc(x,y)) :- d+(tc(x,z)) & lv(z,y)  Descartes product
			tupStarts = tc.getTupleStarts(source);
			tupEnds = tc.getTupleEnds(target);
			
			if (tupStarts != null && tupEnds != null)
				for (V s : tupStarts) {
					for (V t : tupEnds) {
						if (!s.equals(t)) {
							if (tc.addTuple(s, t)) {
								tuples.add(new Tuple<V>(s, t));
							}
						}
					}
				}
			
			// (s, source) -> (source, target)
			//tupStarts = tc.getTupleStarts(source);
			if (tupStarts != null) {
				for (V s : tupStarts) {
					if (!s.equals(target)) {
						if (tc.addTuple(s, target)) {
							tuples.add(new Tuple<V>(s, target));
						}
					}
				}
			}
			
			// (source, target) -> (target, t)
			//tupEnds = tc.getTupleEnds(target);
			if (tupEnds != null) {
				for (V t : tupEnds) {
					if (!source.equals(t)) {
						if (tc.addTuple(source, t)) {
							tuples.add(new Tuple<V>(source, t));
						}
					}
				}
			}
			
			sumOfUpdateTime += (System.nanoTime() - tmp);
			notifyTcObservers(tuples, 1);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see itc.igraph.IGraphObserver#edgeDeleted(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void edgeDeleted(V source, V target) {		
		if (!source.equals(target)) {
			updateCount++;
			long tmp = System.nanoTime();
			
			//Computing overestimate, Descartes product of A and B sets, where
			// A: those nodes from which source is reachable
			// B: those nodes which is reachable from target
			
			Map<Tuple<V>, Integer> tuples = new HashMap<Tuple<V>, Integer>();
			Set<V> sources = tc.getTupleStarts(source);
			Set<V> targets = tc.getTupleEnds(target);
			
			tc.removeTuple(source, target);
			tuples.put(new Tuple<V>(source,target), -1);
	
			for (V s : sources) {
				for (V t : targets) {
					if (!s.equals(t)) {
						tc.removeTuple(s, t);
						tuples.put(new Tuple<V>(s,t), -1);
					}
				}
			}
			
			for (V s : sources) {
				if (!s.equals(target)) {
					tc.removeTuple(s, target);
					tuples.put(new Tuple<V>(s,target), -1);
				}
			}
			
			for (V t : targets) {
				if (!source.equals(t)) {
					tc.removeTuple(source, t);
					tuples.put(new Tuple<V>(source,t), -1);
				}
			}
			
			//System.out.println("overestimate: "+dtc);
			
			//Modify overestimate with those tuples that have alternative derivations
			//1. q+(tc(x,y)) :- lv(x,y)
			for (V s : graphDataSource.getAllNodes()) {	
				List<V> targetNodes = graphDataSource.getTargetNodes(s);
				if (targetNodes != null) {
					for (V t : targetNodes) {
						if (!s.equals(t)) {
							tc.addTuple(s, t);
							
							Tuple<V> tuple = new Tuple<V>(s, t);
							
							Integer count = tuples.get(tuple);
							if (count != null && count == -1) {
								tuples.remove(tuple);
							}
						}
					}
				}
			}
			
			//2. q+(tc(x,y)) :- tcv(x,z) & lv(z,y)
			TcRelation<V> newTups = new TcRelation<V>();
			dtc.clear();
			dtc.union(tc);
			
			while (!dtc.isEmpty()) {
				
				newTups.clear();
				newTups.union(dtc);
				dtc.clear();
				
				for (V s : newTups.getTupleStarts()) {
					for (V t : newTups.getTupleEnds(s)) { 
						List<V> targetNodes = graphDataSource.getTargetNodes(t);
						if (targetNodes != null) {
							for (V tn : targetNodes) { 
								if (!s.equals(tn)) { 		
									if (tc.addTuple(s, tn)) {
										dtc.addTuple(s, tn);
										tuples.remove(new Tuple<V>(s, tn));
									}
								}
							}
						}
					}
				}
			}
			sumOfUpdateTime += (System.nanoTime() - tmp);
			notifyTcObservers(tuples.keySet(), -1);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see itc.igraph.IGraphObserver#nodeInserted(java.lang.Object)
	 */
	@Override
	public void nodeInserted(V n) {
		//Node inserted does not result new tc tuple.	
	}

	/*
	 * (non-Javadoc)
	 * @see itc.igraph.IGraphObserver#nodeDeleted(java.lang.Object)
	 */
	@Override
	public void nodeDeleted(V n) {
		//FIXME node deletion may involve the deletion of incoming and outgoing edges too
		Set<V> set = tc.getTupleEnds(n);
		Set<V> modSet = null;
		
		// n -> target
		if (set != null) {
			modSet = new HashSet<V>(set);
			
			for (V tn : modSet) {
				this.tc.removeTuple(n, tn);
			}
		}
		
		//source -> n
		set = tc.getTupleStarts(n);
		
		if (set != null) {
			modSet = new HashSet<V>(set);
			
			for (V sn : modSet) {
				this.tc.removeTuple(sn, n);
			}
		}
	}
	
	public TcRelation<V> getTcRelation() {
		return this.tc;
	}
	
	public void setTcRelation(TcRelation<V> tc) {
		this.tc = tc;
	}

	/*
	 * (non-Javadoc)
	 * @see itc.igraph.ITcDataSource#isReachable(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isReachable(V source, V target) {
		return tc.containsTuple(source, target);
	}

	/*
	 * (non-Javadoc)
	 * @see itc.igraph.ITcDataSource#attachObserver(itc.igraph.ITcObserver)
	 */
	@Override
	public void attachObserver(ITcObserver<V> to) {
		this.observers.add(to);
	}

	/*
	 * (non-Javadoc)
	 * @see itc.igraph.ITcDataSource#detachObserver(itc.igraph.ITcObserver)
	 */
	@Override
	public void detachObserver(ITcObserver<V> to) {
		this.observers.remove(to);
	}

	/*
	 * (non-Javadoc)
	 * @see itc.igraph.ITcDataSource#getTargetNodes(java.lang.Object)
	 */
	@Override
	public Set<V> getAllReachableTargets(V source) {
		return tc.getTupleEnds(source);
	}

	/*
	 * (non-Javadoc)
	 * @see itc.igraph.ITcDataSource#getSourceNodes(java.lang.Object)
	 */
	@Override
	public Set<V> getAllReachableSources(V target) {
		return tc.getTupleStarts(target);
	}
	
	protected void notifyTcObservers(Set<Tuple<V>> tuples, int dir) {
		for (ITcObserver<V> o : observers) {
			for (Tuple<V> t : tuples) {
				if (!t.getSource().equals(t.getTarget())) {
					if (dir == 1) {
						o.tupleInserted(t.getSource(), t.getTarget());
					}
					if (dir == -1) {
						o.tupleDeleted(t.getSource(), t.getTarget());
					}
				}
			}
		}
	}

	@Override
	public void dispose() {
		tc = null;
		dtc = null;
	}
}
