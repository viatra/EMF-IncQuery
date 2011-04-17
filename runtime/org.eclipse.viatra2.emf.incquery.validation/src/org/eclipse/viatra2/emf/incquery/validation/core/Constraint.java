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

package org.eclipse.viatra2.emf.incquery.validation.core;


import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;

/**
 * @author Bergmann Gábor
 *
 */
public abstract class Constraint<Signature extends IPatternSignature> {
	public abstract String getMessage();
	public abstract IMatcherFactory<Signature, ? extends IncQueryMatcher<Signature>> matcherFactory();
		
	// override if needed
	public String prettyPrintSignature(Signature signature) {
		return signature.prettyPrint();
	}
	// override if needed
	public Object[] extractAffectedElements(Signature signature) {
		return signature.toArray();
	}
	
	protected Constraint() {
	}
	
}
