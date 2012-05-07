package org.eclipse.viatra2.emf.incquery.base.itc.alg.incscc;

import java.util.Set;

import org.eclipse.viatra2.emf.incquery.base.itc.igraph.ITcObserver;

public class NotifierThread<V> extends Thread {

	private Set<V> sources;
	private Set<V> targets;
	private ITcObserver<V> observer;
	private int dir;
	
	public NotifierThread(Set<V> sources, Set<V> targets, ITcObserver<V> observer, int dir) {
		this.sources = sources;
		this.targets = targets;
		this.observer = observer;
		this.dir = dir;
	}

	@Override
	public void run() {
		for (V s : sources) {
			for (V t : targets) {
				if (dir == 1) {
					observer.tupleInserted(s, t);
				}
				if (dir == -1) {
					observer.tupleDeleted(s, t);
				}
				
			}
		}
	}
	
	
}
