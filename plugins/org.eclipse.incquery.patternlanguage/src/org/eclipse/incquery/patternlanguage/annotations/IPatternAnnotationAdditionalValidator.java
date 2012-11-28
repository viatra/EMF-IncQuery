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
package org.eclipse.incquery.patternlanguage.annotations;

import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.validation.IIssueCallback;

/**
 * Interface for providing annotation-specific validators
 * 
 * @author Zoltan Ujhelyi
 * @since 0.7.0
 */
public interface IPatternAnnotationAdditionalValidator {

    /**
     * Executes additional, annotation-specific validation on a pattern.
     * 
     * @param annotation
     *            the pattern to validate
     * @param validator
     *            a callback validator to report errors and warnings
     */
    void executeAdditionalValidation(Annotation annotation, IIssueCallback validator);
}
