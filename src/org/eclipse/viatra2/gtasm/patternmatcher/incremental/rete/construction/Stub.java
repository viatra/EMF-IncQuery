/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction;

import java.util.Map;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;


/**
 * 
 * @author Bergmann Gábor
 *
 * @param <HandleType> the type of RETE node handle this stub type will augment
 */
public class Stub<HandleType> {
	public HandleType handle;
	public Tuple calibrationPattern;
	public Map<Object, Integer> calibrationIndex;


	public Stub(Map<Object, Integer> calibrationIndex, Tuple calibrationPattern, HandleType handle) {
		super();
		this.calibrationIndex = calibrationIndex;
		this.calibrationPattern = calibrationPattern;
		this.handle = handle;
	}
	public Stub(Tuple calibrationPattern, HandleType handle) {
		super();
		this.calibrationIndex = calibrationPattern.invertIndex();
		this.calibrationPattern = calibrationPattern;
		this.handle = handle;
	}
	public Stub(Stub<HandleType> template) {
		this(template.calibrationIndex, template.calibrationPattern, template.handle);
	}	
	public Stub(Stub<HandleType> template, HandleType handle) {
		this(template.calibrationIndex, template.calibrationPattern, handle);
	}	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Stub("+handle+")";
	}
	
}
