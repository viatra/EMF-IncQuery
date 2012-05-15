package org.eclipse.viatra2.emf.incquery.base.itc.alg.king;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphObserver;

public class KingAlg<V> implements IGraphObserver<V> {

	private static final long serialVersionUID = -748676749122336868L;
	private int levelCount;
	private ArrayList<TcRelation<V>> relations;
	private IGraphDataSource<V> gds;

	public KingAlg(IGraphDataSource<V> gds) {
		this.gds = gds;
		// calculate k
		if (gds.getAllNodes().size() != 0) {
			double j = Math.log10(gds.getAllNodes().size()) / Math.log10(2);
			this.levelCount = (int) ((j == Math.floor(j)) ? j : j + 1);
		} else
			this.levelCount = 0;

		this.relations = new ArrayList<TcRelation<V>>();
		for (int i = 0; i < levelCount; i++)
			this.relations.add(new TcRelation<V>());

		gds.attachObserver(this);
	}

	@Override
	public void edgeInserted(V source, V target) {
		deriveTc(source, target, 1);
	}

	@Override
	public void edgeDeleted(V source, V target) {
		deriveTc(source, target, -1);
	}

	private void deriveTc(V source, V target, int dCount) {

		Set<V> tupEnds = null;
		HashMap<V, HashSet<V>> modEdges = null;
		HashMap<V, HashSet<V>> E = null;

		// dE(0)
		if (!source.equals(target))
			relations.get(0).addTuple(source, target, dCount);

		for (int i = 1; i < levelCount; i++) {

			modEdges = relations.get(i - 1).getModEdges();

			// dE(i-1) X U'(i-1)
			for (V s : modEdges.keySet()) {
				for (V t : modEdges.get(s))
					for (int j = 0; j < i; j++) {
						tupEnds = relations.get(j).getTupleEnds(t);
						if (tupEnds != null) {
							for (V tEnd : tupEnds)
								if (!tEnd.equals(s))
									relations.get(i).addTuple(s, tEnd, dCount);
						}
					}
			}

			E = relations.get(i - 1).getOriginalEdges(dCount);
			HashMap<V, HashSet<V>> dU = dUnionAll(i - 1, dCount);
			// E(i-1) X dU(i-1)
			for (V s : E.keySet()) {
				for (V t : E.get(s)) {

					tupEnds = dU.get(t);

					if (tupEnds != null)
						for (V tEnd : dU.get(t)) {
							if (!s.equals(tEnd))
								relations.get(i).addTuple(s, tEnd, dCount);
						}
				}
			}

			// System.out.println("-------------");
		}

		for (int i = 0; i < levelCount; i++)
			relations.get(i).clearModEdges();

		//System.out.println(relations);
	}

	public void fullGen() {

		for (int i = 0; i < levelCount; i++)
			relations.get(i).clearAll();

		// E(0)
		for (V s : gds.getAllNodes()) {
			for (V t : gds.getTargetNodes(s)) {
				if (!s.equals(t))
					relations.get(0).addTuple(s, t, 1);
			}
		}

		// E(i-1) X U(i-1)
		for (int i = 1; i < levelCount; i++) {

			HashMap<V, HashSet<V>> U = unionAll(i - 1);

			for (V s : relations.get(i - 1).getTuplesForward().keySet()) {
				for (V t : relations.get(i - 1).getTuplesForward().get(s)
						.keySet()) {

					HashSet<V> tupEnds = U.get(t);

					if (tupEnds != null)
						for (V tEnd : U.get(t)) {
							if (!s.equals(tEnd))
								relations.get(i).addTuple(s, tEnd, 1);
						}
				}
			}
		}

		for (int i = 0; i < levelCount; i++)
			relations.get(i).clearModEdges();

		// System.out.println(relations);
	}

	private HashMap<V, HashSet<V>> unionAll(int i) {
		HashMap<V, HashSet<V>> map = new HashMap<V, HashSet<V>>();

		for (int j = 0; j <= i; j++) {

			for (V s : relations.get(j).getTuplesForward().keySet()) {
				for (V t : relations.get(j).getTuplesForward().get(s).keySet()) {
					if (map.containsKey(s)) {
						map.get(s).add(t);
					} else {
						HashSet<V> set = new HashSet<V>();
						set.add(t);
						map.put(s, set);
					}
				}
			}
		}

		return map;
	}

	private HashMap<V, HashSet<V>> dUnionAll(int i, int dCount) {

		HashMap<V, HashSet<V>> map = new HashMap<V, HashSet<V>>();

		for (int j = 0; j <= i; j++) {

			HashMap<V, HashMap<V, Integer>> forw = relations.get(j)
					.getTuplesForward();

			for (V s : relations.get(j).getModEdges().keySet()) {
				for (V t : relations.get(j).getModEdges().get(s)) {

					if (!(dCount == -1 && forw.containsKey(s) && forw.get(s)
							.containsKey(t))) {

						if (map.containsKey(s)) {
							map.get(s).add(t);
						} else {
							HashSet<V> set = new HashSet<V>();
							set.add(t);
							map.put(s, set);
						}
					}
				}
			}
		}

		return map;
	}

	@Override
	public void nodeInserted(V n) {
		double j = Math.log10(gds.getAllNodes().size()) / Math.log10(2);
		int newLevelCount = (int) ((j == Math.floor(j)) ? j : j + 1);

		if (newLevelCount > levelCount) {
			relations.add(new TcRelation<V>());
			this.levelCount = newLevelCount;
		}
	}

	@Override
	public void nodeDeleted(V n) {
		double j = Math.log10(gds.getAllNodes().size()) / Math.log10(2);
		int newLevelCount = (int) ((j == Math.floor(j)) ? j : j + 1);

		if (newLevelCount < levelCount) {
			relations.remove(levelCount - 1);
			this.levelCount = newLevelCount;
		}
	}

	public org.eclipse.viatra2.emf.incquery.base.itc.alg.dred.TcRelation<V> getMergedRelation() {
		org.eclipse.viatra2.emf.incquery.base.itc.alg.dred.TcRelation<V> dTc = new org.eclipse.viatra2.emf.incquery.base.itc.alg.dred.TcRelation<V>();
		
		for (TcRelation<V> tc : relations) {
			for (V s : tc.getTupleStarts()) {
				for (V t : tc.getTupleEnds(s)) {
					dTc.addTuple(s, t);
				}
			}
		}
		
		return dTc;
	}
}
