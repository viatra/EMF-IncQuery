package org.eclipse.viatra2.emf.incquery.base.itc.alg.misc;

public class Edge<V> {
	private V source;
	private V target;

	public Edge(V source, V target) {
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

}
