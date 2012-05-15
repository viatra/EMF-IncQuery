package org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.scc;

public class SCCProperty {
	private int index;
	private int lowlink;
	
	public SCCProperty(int index, int lowlink) {
		super();
		this.index = index;
		this.lowlink = lowlink;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getLowlink() {
		return lowlink;
	}

	public void setLowlink(int lowlink) {
		this.lowlink = lowlink;
	}
}
