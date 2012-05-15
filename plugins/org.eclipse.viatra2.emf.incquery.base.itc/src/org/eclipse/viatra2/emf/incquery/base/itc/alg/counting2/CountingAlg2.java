package org.eclipse.viatra2.emf.incquery.base.itc.alg.counting2;

import java.math.BigInteger;
import java.util.Set;

import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphObserver;


public class CountingAlg2<V> implements IGraphObserver<V> {

	private static final long serialVersionUID = -2383210800242398869L;
	private TcRelation<V> tc = null;

	public CountingAlg2(IGraphDataSource<V> g) {
		tc = new TcRelation<V>();
		g.attachObserver(this);
	}

	@Override
	public void edgeInserted(V source, V target) {
		deriveTc(source, target, 1);
	}

	@Override
	public void edgeDeleted(V source, V target) {
		deriveTc(source, target, -1);
	}

	@Override
	public void nodeInserted(V n) {

	}

	@Override
	public void nodeDeleted(V n) {

	}

	private void deriveTc(V source, V target, int dCount) {
		
		Set<V> starts = tc.getTupleStarts(source);
		Set<V> targets = tc.getTupleEnds(target);
		BigInteger sCount = null;
		BigInteger dCountBI = BigInteger.valueOf(dCount);

		tc.addTuple(source, target, dCountBI);
		
		if (starts != null && targets != null) {
			for (V s : starts) {
				sCount = tc.getCount(s, source);
				for (V t : targets) {
					if (!s.equals(t))
						tc.addTuple(s, t, (sCount.multiply(tc.getCount(target, t))).multiply(dCountBI));
				}
			}
		}

		if (starts != null) {
			for (V s : starts) {
				if (!s.equals(target))
					tc.addTuple(s, target, tc.getCount(s, source).multiply(dCountBI));
			}
		}

		if (targets != null) {
			for (V t : targets) {
				if (!source.equals(t))
					tc.addTuple(source, t, tc.getCount(target, t).multiply(dCountBI));
			}
		}
		
		tc.normalize();
	}

	public TcRelation<V> getTcRelation() {
		return this.tc;
	}

	public void setTcRelation(TcRelation<V> tc) {
		this.tc = tc;
	}

	public boolean isReachable(V source, V target) {
		return tc.containsTuple(source, target);
	}
}