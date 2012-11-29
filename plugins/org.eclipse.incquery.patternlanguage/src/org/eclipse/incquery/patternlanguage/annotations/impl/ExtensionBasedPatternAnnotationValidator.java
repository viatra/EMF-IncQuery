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
package org.eclipse.incquery.patternlanguage.annotations.impl;

import org.eclipse.incquery.patternlanguage.annotations.IPatternAnnotationAdditionalValidator;
import org.eclipse.incquery.patternlanguage.annotations.IPatternAnnotationValidator;
import org.eclipse.incquery.patternlanguage.annotations.PatternAnnotationProvider;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.AnnotationParameter;
import org.eclipse.incquery.patternlanguage.patternLanguage.BoolValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.DoubleValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.IntValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.ListValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.StringValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.ValueReference;
import org.eclipse.incquery.patternlanguage.patternLanguage.VariableValue;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * A type-safe wrapper created from a pattern annotation extension point together with validation-related methods. Such
 * validators are instantiated in {@link PatternAnnotationProvider}.
 * 
 * @author Zoltan Ujhelyi
 * @noinstantiate This class is not intended to be instantiated by clients
 */
public class ExtensionBasedPatternAnnotationValidator implements IPatternAnnotationValidator {

    private final Iterable<ExtensionBasedPatternAnnotationParameter> definedAttributes;
    private final String name;
    private final String description;
    private final boolean deprecated;
    private final IPatternAnnotationAdditionalValidator validator;

    private static final ImmutableMap<String, Class<? extends ValueReference>> TYPEMAPPING = new ImmutableMap.Builder<String, Class<? extends ValueReference>>()
            .put(ExtensionBasedPatternAnnotationParameter.INT, IntValue.class)
            .put(ExtensionBasedPatternAnnotationParameter.STRING, StringValue.class)
            .put(ExtensionBasedPatternAnnotationParameter.DOUBLE, DoubleValue.class)
            .put(ExtensionBasedPatternAnnotationParameter.BOOLEAN, BoolValue.class)
            .put(ExtensionBasedPatternAnnotationParameter.LIST, ListValue.class)
            .put(ExtensionBasedPatternAnnotationParameter.VARIABLEREFERENCE, VariableValue.class).build();

    public ExtensionBasedPatternAnnotationValidator(String name, String description, boolean deprecated,
            Iterable<ExtensionBasedPatternAnnotationParameter> parameters,
            IPatternAnnotationAdditionalValidator validator) {
        super();
        this.name = name;
        this.description = description;
        this.deprecated = deprecated;
        this.definedAttributes = parameters;
        this.validator = validator;
    }

    private static final class AnnotationParameterName implements Function<AnnotationParameter, String> {
        @Override
        public String apply(AnnotationParameter input) {
            Preconditions.checkNotNull(input, "annotation");
            return input.getName();
        }
    }

    private static final class AnnotationDefinitionParameterName implements
            Function<ExtensionBasedPatternAnnotationParameter, String> {

        @Override
        public String apply(ExtensionBasedPatternAnnotationParameter input) {
            Preconditions.checkNotNull(input, "input");
            return input.getName();
        }

    }

    @Override
    public Iterable<String> getAllAvailableParameterNames() {
        return Iterables.transform(definedAttributes, new AnnotationDefinitionParameterName());
    }

    private Iterable<String> getParameterNames(Annotation annotation) {
        return Iterables.transform(annotation.getParameters(), new AnnotationParameterName());
    }

    @Override
    public Iterable<String> getMissingMandatoryAttributes(Annotation annotation) {
        final Iterable<String> actualAttributeNames = getParameterNames(annotation);
        final Iterable<ExtensionBasedPatternAnnotationParameter> filteredParameters = Iterables.filter(
                definedAttributes, new Predicate<ExtensionBasedPatternAnnotationParameter>() {

                    @Override
                    public boolean apply(ExtensionBasedPatternAnnotationParameter input) {
                        Preconditions.checkNotNull(input, "input");
                        return input.isMandatory() && !Iterables.contains(actualAttributeNames, input.getName());
                    }
                });
        return Iterables.transform(filteredParameters, new AnnotationDefinitionParameterName());
    }

    @Override
    public Iterable<AnnotationParameter> getUnknownAttributes(Annotation annotation) {
        final Iterable<String> parameterNames = Iterables.transform(definedAttributes,
                new AnnotationDefinitionParameterName());
        return Iterables.filter(annotation.getParameters(), new Predicate<AnnotationParameter>() {

            @Override
            public boolean apply(AnnotationParameter input) {
                Preconditions.checkNotNull(input, "input");
                return !Iterables.contains(parameterNames, input.getName());
            }
        });
    }

    @Override
    public Class<? extends ValueReference> getExpectedParameterType(AnnotationParameter parameter) {
        ExtensionBasedPatternAnnotationParameter expectedParameter = null;
        for (ExtensionBasedPatternAnnotationParameter p : definedAttributes) {
            if (p.getName().equals(parameter.getName())) {
                expectedParameter = p;
            }
        }
        if (expectedParameter == null) {
            return null;
        }
        String type = expectedParameter.getType();
        if (type != null && TYPEMAPPING.containsKey(type)) {
            return TYPEMAPPING.get(type);
        }
        return null;
    }

    @Override
    public String getAnnotationName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getDescription(String parameterName) {
        for (ExtensionBasedPatternAnnotationParameter param : definedAttributes) {
            if (param.getName().equals(parameterName)) {
                return param.getDescription();
            }
        }
        return "";
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }

    @Override
    public boolean isDeprecated(String parameterName) {
        for (ExtensionBasedPatternAnnotationParameter param : definedAttributes) {
            if (param.getName().equals(parameterName)) {
                return param.isDeprecated();
            }
        }
        return false;
    }

    @Override
    public IPatternAnnotationAdditionalValidator getAdditionalValidator() {
        return validator;
    }

}
