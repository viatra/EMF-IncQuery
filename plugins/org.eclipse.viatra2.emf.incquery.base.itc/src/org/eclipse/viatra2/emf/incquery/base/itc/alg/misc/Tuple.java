package org.eclipse.viatra2.emf.incquery.base.itc.alg.misc;

public class Tuple<V> {

	private V source;
	private V target;

	public Tuple(V source, V target) {
		super();
		this.source = source;
		this.target = target;
	}

	public V getSource() {
		return source;
	}

	public void setSource(V source) {
		this.source = source;
	}

	public V getTarget() {
		return target;
	}

	public void setTarget(V target) {
		this.target = target;
	}

	@Override
	public String toString() {
		return "("+source.toString()+","+target.toString()+")";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Tuple) {
			Tuple<?> t = (Tuple<?>) o;
			
			if (t.getSource().equals(this.source) && t.getTarget().equals(this.target)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return source.hashCode() + target.hashCode();
	}
}
