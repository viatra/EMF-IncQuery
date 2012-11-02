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
	private int cachedHash = -1;
	
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
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		else {
			StarWork<?> sw = (StarWork<?>) obj;
			if (sw.source.equals(this.source) && 
				sw.target.equals(this.target) && 
				sw.fromIdx == this.fromIdx && 
				sw.toIdx == this.toIdx && 
				sw.sd == this.sd) {
				return true;
			}
			else {
				return false;
			}
		}
	}

	@Override
	public int hashCode() {
		if (cachedHash == -1) {
			int hash = 7;
			hash = 31 * hash + source.hashCode();
			hash = 31 * hash + target.hashCode();
			hash = 31 * hash + fromIdx;
			hash = 31 * hash + toIdx;
			hash = 31 * hash + sd.ordinal();
			cachedHash = hash;
		}
		return cachedHash;
	}

	@Override
	public int compareTo(V o) {
		if (this.hashCode() < o.hashCode())  {
			return -1;
		}
		else if (this.hashCode() == o.hashCode()) {
			return 0;
		}
		else return 1;
	}
}
