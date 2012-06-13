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
