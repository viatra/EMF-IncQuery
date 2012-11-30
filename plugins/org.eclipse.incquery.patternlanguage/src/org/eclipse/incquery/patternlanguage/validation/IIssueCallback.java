/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.validation;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Zoltan Ujhelyi
 * 
 */
public interface IIssueCallback {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.xtext.validation.AbstractDeclarativeValidator#warning(java.lang.String,
     * org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature, java.lang.String, java.lang.String[])
     */
    public abstract void warning(String message, EObject source, EStructuralFeature feature, String code,
            String... issueData);

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.xtext.validation.AbstractDeclarativeValidator#error(java.lang.String,
     * org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature, java.lang.String, java.lang.String[])
     */
    public abstract void error(String message, EObject source, EStructuralFeature feature, String code,
            String... issueData);

}