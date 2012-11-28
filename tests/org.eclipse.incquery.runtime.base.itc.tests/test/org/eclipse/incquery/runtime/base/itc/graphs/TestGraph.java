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
package org.eclipse.incquery.runtime.base.itc.graphs;

import org.eclipse.incquery.runtime.base.itc.graphimpl.Graph;
import org.eclipse.incquery.runtime.base.itc.misc.TestObserver;

public abstract class TestGraph<T> extends Graph<T> {

	private static final long serialVersionUID = 1L;

	protected TestObserver<Integer> observer;
	
	public TestGraph(TestObserver<Integer> observer) {
		this.observer = observer;
	}
	
	public abstract void modify();
	
	public TestObserver<Integer> getObserver() {
		return observer;
	}
	
}
