/*******************************************************************************
 * Copyright (c) 2010-2012, Okrosa, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Okrosa - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.patternlanguage.types;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;

/**
 * A small interface extending the {@link ITypeProvider} capabilities.
 */
public interface IEMFTypeProvider {

	/**
	 * @param variable
	 * @return the {@link EClassifier} for the given {@link Variable}. Returns
	 *         null, if it fails.
	 */
	public EClassifier getClassifierForVariable(Variable variable);

}