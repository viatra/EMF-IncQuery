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

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.incquery.patternlanguage.annotations.impl.ExtensionBasedPatternAnnotationParameter;
import org.eclipse.incquery.patternlanguage.annotations.impl.ExtensionBasedPatternAnnotationValidator;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.AnnotationParameter;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternLanguageFactory;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class PatternAnnotationProvider {

    /**
     * 
     */
    private static final String VALIDATOR_PARAMETER_NAME = "additionalValidator";

    @Inject
    private Logger log;
    @Inject
    private Injector injector;

    private static final class ExtensionConverter implements
            Function<IConfigurationElement, ExtensionBasedPatternAnnotationParameter> {
        @Override
        public ExtensionBasedPatternAnnotationParameter apply(IConfigurationElement input) {
            Preconditions.checkNotNull(input, "input");
            final String parameterName = input.getAttribute("name");
            final boolean mandatory = Boolean.parseBoolean(input.getAttribute("mandatory"));
            final boolean multiple = Boolean.parseBoolean(input.getAttribute("multiple"));
            final String deprecatedString = input.getAttribute("deprecated");
            final boolean deprecated = Boolean.parseBoolean(deprecatedString);
            final String type = input.getAttribute("type");
            final String description = input.getAttribute("description");
            return new ExtensionBasedPatternAnnotationParameter(parameterName, type, description, multiple, mandatory,
                    deprecated);
        }
    }

    static final String EXTENSIONID = "org.eclipse.incquery.patternlanguage.annotation";
    private Map<String, IPatternAnnotationValidator> annotationValidators;

    protected void initializeValidators() {
        annotationValidators = new Hashtable<String, IPatternAnnotationValidator>();
        final IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSIONID);
        for (IConfigurationElement e : config) {
            try {
                final String annotationName = e.getAttribute("name");
                final String description = e.getAttribute("description");
                final String deprecatedString = e.getAttribute("deprecated");
                final boolean deprecated = Boolean.parseBoolean(deprecatedString);

                final IConfigurationElement[] parameters = e.getChildren("annotationparameter");

                IPatternAnnotationAdditionalValidator validator = null;

                final Iterable<ExtensionBasedPatternAnnotationParameter> parameterIterable = Iterables.transform(
                        Arrays.asList(parameters), new ExtensionConverter());
                if (e.getAttribute(VALIDATOR_PARAMETER_NAME) != null) {
                    validator = (IPatternAnnotationAdditionalValidator) e
                            .createExecutableExtension(VALIDATOR_PARAMETER_NAME);
                    injector.injectMembers(validator);
                }

                final IPatternAnnotationValidator annotationValidator = new ExtensionBasedPatternAnnotationValidator(
                        annotationName, description, deprecated, parameterIterable, validator);
                annotationValidators.put(annotationName, annotationValidator);
            } catch (CoreException ex) {
                log.error(
                        String.format("Error while initializing the validator for annotation %s.",
                                e.getAttribute("name")), ex);
            }
        }
    }

    /**
     * Returns a pattern annotation validator for a selected annotation name
     * 
     * @param annotationName
     * @return a pattern annotation validator
     */
    public IPatternAnnotationValidator getValidator(String annotationName) {
        if (annotationValidators == null) {
            initializeValidators();
        }
        return annotationValidators.get(annotationName);
    }

    public Annotation getAnnotationObject(String annotationName) {
        Annotation annotation = PatternLanguageFactory.eINSTANCE.createAnnotation();
        annotation.setName(annotationName);
        return annotation;

    }

    public AnnotationParameter getAnnotationParameter(String annotationName, String parameterName) {
        Annotation annotation = getAnnotationObject(annotationName);
        return getAnnotationParameter(annotation, parameterName);
    }

    public AnnotationParameter getAnnotationParameter(Annotation annotation, String parameterName) {
        AnnotationParameter parameter = PatternLanguageFactory.eINSTANCE.createAnnotationParameter();
        parameter.setName(parameterName);
        annotation.getParameters().add(parameter);
        return parameter;
    }

    /**
     * Decides whether a validator is defined for the selected annotation name.
     * 
     * @param annotationName
     * @return true, if a validator is defined
     */
    public boolean hasValidator(String annotationName) {
        if (annotationValidators == null) {
            initializeValidators();
        }
        return annotationValidators.containsKey(annotationName);
    }

    public Set<String> getAllAnnotationNames() {
        if (annotationValidators == null) {
            initializeValidators();
        }
        return annotationValidators.keySet();
    }

    public Iterable<String> getAnnotationParameters(String annotationName) {
        if (annotationValidators == null) {
            initializeValidators();
        }
        return annotationValidators.get(annotationName).getAllAvailableParameterNames();
    }

    public String getDescription(Annotation annotation) {
        return getDescription(annotation.getName());
    }

    public String getDescription(String annotationName) {
        if (annotationValidators == null) {
            initializeValidators();
        }
        return annotationValidators.get(annotationName).getDescription();
    }

    public String getDescription(AnnotationParameter parameter) {
        Annotation annotation = (Annotation) parameter.eContainer();
        return getDescription(annotation.getName(), parameter.getName());
    }

    public String getDescription(String annotationName, String parameterName) {
        if (annotationValidators == null) {
            initializeValidators();
        }
        if (!annotationValidators.containsKey(annotationName)) {
            return "";
        }
        return annotationValidators.get(annotationName).getDescription(parameterName);
    }

    public boolean isDeprecated(Annotation annotation) {
        return isDeprecated(annotation.getName());
    }

    public boolean isDeprecated(String annotationName) {
        if (annotationValidators == null) {
            initializeValidators();
        }
        return annotationValidators.containsKey(annotationName)
                && annotationValidators.get(annotationName).isDeprecated();
    }

    public boolean isDeprecated(AnnotationParameter parameter) {
        Annotation annotation = (Annotation) parameter.eContainer();
        return isDeprecated(annotation.getName(), parameter.getName());
    }

    public boolean isDeprecated(String annotationName, String parameterName) {
        if (annotationValidators == null) {
            initializeValidators();
        }
        return annotationValidators.containsKey(annotationName)
                && annotationValidators.get(annotationName).isDeprecated(parameterName);
    }

}
