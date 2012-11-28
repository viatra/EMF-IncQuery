/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.validation.runtime;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedMatcher;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedMatcherFactory;

public abstract class Constraint<T extends IPatternMatch> {

	public abstract String getMessage();

	public abstract EObject getLocationObject(T signature);

	public String prettyPrintSignature(T signature) {
		return signature.prettyPrint();
	}

	public Object[] extractAffectedElements(T signature) {
		return signature.toArray();
	}
	
	public abstract int getSeverity();
	
	public abstract BaseGeneratedMatcherFactory<? extends BaseGeneratedMatcher<T>> getMatcherFactory();
}
