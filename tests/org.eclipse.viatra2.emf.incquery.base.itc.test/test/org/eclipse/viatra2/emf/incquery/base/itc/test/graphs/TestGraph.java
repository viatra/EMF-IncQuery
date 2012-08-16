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
package org.eclipse.viatra2.emf.incquery.base.itc.test.graphs;

import org.eclipse.viatra2.emf.incquery.base.itc.graphimpl.Graph;

public abstract class TestGraph<T> extends Graph<T> {

	private static final long serialVersionUID = 1L;

	public abstract void modify();
	
}
