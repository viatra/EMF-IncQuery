/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.base.itc.alg.kingopt;

public class StarWork<V> implements Comparable<V> {
	
	private StarDir sd;
	private V source;
	private V target;
	private int fromIdx;
	private int toIdx;
	
	public StarWork(StarDir sd, V source, V target, int fromIdx, int toIdx) {
		super();
		this.sd = sd;
		this.source = source;
		this.target = target;
		this.fromIdx = fromIdx;
		this.toIdx = toIdx;
	}

	public StarDir getSd() {
		return sd;
	}

	public void setSd(StarDir sd) {
		this.sd = sd;
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

	public int getFromIdx() {
		return fromIdx;
	}

	public void setFromIdx(int fromIdx) {
		this.fromIdx = fromIdx;
	}

	public int getToIdx() {
		return toIdx;
	}

	public void setToIdx(int toIdx) {
		this.toIdx = toIdx;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof StarWork) {
			StarWork<?> sw = (StarWork<?>) o;
			if (sw.hashCode() == this.hashCode()) return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return source.hashCode() + target.hashCode() + fromIdx + toIdx + sd.ordinal();
	}

	@Override
	public int compareTo(V o) {
		if (this.hashCode() < o.hashCode()) return -1;
		else if (this.hashCode() == o.hashCode()) return 0;
		else return 1;
	}
}
