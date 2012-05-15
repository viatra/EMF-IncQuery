package org.eclipse.viatra2.emf.incquery.base.itc.alg.incscc;

public class UnionFindNodeProperty<V> {

	public int rank;
	public V parent;
	public boolean deleted;
	
	public UnionFindNodeProperty() {
		this.rank = 0;
		this.parent = null;
		this.deleted = false;
	}
	
	public UnionFindNodeProperty(int rank, V parent) {
		super();
		this.rank = rank;
		this.parent = parent;
		this.deleted = false;
	}

	@Override
	public String toString() {
		return "[rank:"+rank+", parent:"+parent.toString()+"]";
	}
}
