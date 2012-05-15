package org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.scc;


import java.util.Set;

import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphDataSource;

public class SCCResult<V> {

	private Set<Set<V>> sccs;
	private IGraphDataSource<V> gds;

	public SCCResult(Set<Set<V>> sccs, IGraphDataSource<V> gds) {
		this.sccs = sccs;
		this.gds = gds;
	}

	public Set<Set<V>> getSccs() {
		return sccs;
	}

	public int getSCCCount() {
		return sccs.size();
	}

	public double getAverageNodeCount() {
		double a = 0;

		for (Set<V> s : sccs) {
			a += s.size();
		}

		return a / sccs.size();
	}

	public double getAverageEdgeCount() {
		long edgeSum = 0;

		for (Set<V> scc : sccs) {
			for (V source : scc) {
				for (V target : gds.getTargetNodes(source)) {
					if (scc.contains(target))
						edgeSum++;
				}
			}
		}

		return (double) edgeSum / (double) sccs.size();
	}

	public int getBiggestSCCSize() {
		int max = 0;

		for (Set<V> scc : sccs) {
			if (scc.size() > max)
				max = scc.size();
		}

		return max;
	}

	public long getSumOfSquares() {
		long sum = 0;

		for (Set<V> scc : sccs) {
			sum += scc.size() * scc.size();
		}

		return sum;
	}
}
