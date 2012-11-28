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

package org.eclipse.incquery.runtime.base.itc.alg.incscc;

public class UnionFindNodeProperty<V> {

	public int rank;
	public V parent;
	
	public UnionFindNodeProperty() {
		this.rank = 0;
		this.parent = null;
	}
	
	public UnionFindNodeProperty(int rank, V parent) {
		super();
		this.rank = rank;
		this.parent = parent;
	}

	@Override
	public String toString() {
		return "[rank:"+rank+", parent:"+parent.toString()+"]";
	}
}
