package org.eclipse.viatra2.emf.incquery.base.itc.alg.kingopt;

public class EdgeWork<V> {
	private int dir;
	private V source;
	private V target;
	private int levelNumber;

	public EdgeWork(int dir, V source, V target, int levelNumber) {
		super();
		this.dir = dir;
		this.source = source;
		this.target = target;
		this.levelNumber = levelNumber;
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
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

	public int getLevelNumber() {
		return levelNumber;
	}

	public void setLevelNumber(int levelNumber) {
		this.levelNumber = levelNumber;
	}

	@Override
	public String toString() {
		return dir+" "+source+" "+target+" "+levelNumber;
	}

}
