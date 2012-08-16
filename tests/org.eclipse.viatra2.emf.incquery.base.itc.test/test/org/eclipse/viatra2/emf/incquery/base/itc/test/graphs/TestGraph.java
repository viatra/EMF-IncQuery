package org.eclipse.viatra2.emf.incquery.base.itc.test.graphs;

import org.eclipse.viatra2.emf.incquery.base.itc.graphimpl.Graph;

public abstract class TestGraph<T> extends Graph<T> {

	private static final long serialVersionUID = 1L;

	public abstract void modify();
	
}
