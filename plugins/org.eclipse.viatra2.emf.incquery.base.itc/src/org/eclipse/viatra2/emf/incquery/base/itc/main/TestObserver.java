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

package org.eclipse.viatra2.emf.incquery.base.itc.main;

import org.eclipse.viatra2.emf.incquery.base.itc.igraph.ITcObserver;

public class TestObserver implements ITcObserver<Integer> {

	@Override
	public void tupleInserted(Integer source, Integer target) {
		System.out.println("insert ("+source+","+target+")");	
	}

	@Override
	public void tupleDeleted(Integer source, Integer target) {
		System.out.println("delete ("+source+","+target+")");			
	}

}
