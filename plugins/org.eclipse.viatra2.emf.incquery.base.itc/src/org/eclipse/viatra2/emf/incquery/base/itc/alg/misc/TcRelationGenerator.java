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

package org.eclipse.viatra2.emf.incquery.base.itc.alg.misc;


import java.util.List;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.topsort.TopSort;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IBiDirectionalGraphDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IBiDirectionalWrapper;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.IGraphDataSource;

public class TcRelationGenerator<V> {
	
	private IBiDirectionalGraphDataSource<V> gds = null;
	
	public TcRelationGenerator(IGraphDataSource<V> gds) {
		
		if (gds instanceof IBiDirectionalGraphDataSource<?>) {
			this.gds = (IBiDirectionalGraphDataSource<V>) gds;
		}
		else {
			this.gds = new IBiDirectionalWrapper<V>(gds);
		}
	}
	
	@SuppressWarnings("unchecked")
	public ITcRelation<V> getTcRelation() {
		return TcRelation.createFrom((List<V>) TopSort.getTopologicalSorting(gds), gds);
	}
}
