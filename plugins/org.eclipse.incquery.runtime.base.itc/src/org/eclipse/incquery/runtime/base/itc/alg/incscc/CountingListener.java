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

import org.eclipse.incquery.runtime.base.itc.igraph.ITcObserver;

/**
 * @author Tamas Szabo
 *
 */
public class CountingListener<V> implements ITcObserver<V> {

	private IncSCCAlg<V> alg;
	
	public CountingListener(IncSCCAlg<V> alg) {
		this.alg = alg;
	}
	
	@Override
	public void tupleInserted(V source, V target) {
		alg.notifyTcObservers(alg.sccs.setMap.get(source), alg.sccs.setMap.get(target), Direction.INSERT);
	}

	@Override
	public void tupleDeleted(V source, V target) {
		alg.notifyTcObservers(alg.sccs.setMap.get(source), alg.sccs.setMap.get(target), Direction.DELETE);
	}

}