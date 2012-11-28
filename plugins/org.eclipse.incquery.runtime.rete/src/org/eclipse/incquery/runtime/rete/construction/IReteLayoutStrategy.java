/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.construction;

import org.eclipse.incquery.runtime.rete.construction.psystem.PSystem;

/**
 * An algorithm that builds a RETE net based on a PSystem. 
 * @author Bergmann Gábor
 *
 */
public interface IReteLayoutStrategy<PatternDescription, StubHandle, Collector> {
	public Stub<StubHandle> layout(PSystem<PatternDescription, StubHandle, Collector> pSystem) 
		throws RetePatternBuildException;
}
