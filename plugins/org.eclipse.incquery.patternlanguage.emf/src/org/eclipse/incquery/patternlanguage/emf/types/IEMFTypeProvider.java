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
package org.eclipse.incquery.patternlanguage.emf.types;

import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.incquery.patternlanguage.patternLanguage.Type;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.xtext.xbase.typing.ITypeProvider;

/**
 * A small interface extending the {@link ITypeProvider} capabilities.
 */
@SuppressWarnings("restriction")
public interface IEMFTypeProvider {

    /**
     * @param variable
     * @return the {@link EClassifier} for the given {@link Variable}. Returns null, if it fails.
     */
    public EClassifier getClassifierForVariable(Variable variable);

    /**
     * @param type
     * @return the {@link EClassifier} for the given {@link Type}. Returns null, if it fails.
     */
    public EClassifier getClassifierForType(Type type);

    /**
     * @param patternBody
     * @param variable
     * @return the list of possible classifiers computed from the constraints in the patternbody.
     */
    public Set<EClassifier> getPossibleClassifiersForVariableInBody(PatternBody patternBody, Variable variable);

    /**
     * @param variable
     * @return the {@link EClassifier} for the given {@link Variable}. Returns null, if it fails.
     */
    public EClassifier getClassifierForPatternParameterVariable(Variable variable);
}