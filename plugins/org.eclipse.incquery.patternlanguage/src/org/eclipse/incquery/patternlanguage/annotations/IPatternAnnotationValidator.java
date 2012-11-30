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
import org.eclipse.incquery.patternlanguage.patternLanguage.AnnotationParameter;
import org.eclipse.incquery.patternlanguage.patternLanguage.ValueReference;

/**
 * An interface for validating pattern {@link Annotation} objects.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public interface IPatternAnnotationValidator {

    Iterable<String> getMissingMandatoryAttributes(Annotation annotation);

    /**
     * @param annotation
     * @return
     */
    Iterable<AnnotationParameter> getUnknownAttributes(Annotation annotation);

    /**
     * Returns whether a parameter of an annotation is mistyped
     * 
     * @param parameter
     * @return the expected class of the parameter variable
     */
    Class<? extends ValueReference> getExpectedParameterType(AnnotationParameter parameter);

    Iterable<String> getAllAvailableParameterNames();

    String getAnnotationName();

    String getDescription();

    String getDescription(String parameterName);

    boolean isDeprecated();

    boolean isDeprecated(String parameterName);

    /**
     * Provides an additional validator implementation.
     * 
     * @return the validator object if specified or null otherwise.
     * @since 0.7.0
     */
    IPatternAnnotationAdditionalValidator getAdditionalValidator();
}
