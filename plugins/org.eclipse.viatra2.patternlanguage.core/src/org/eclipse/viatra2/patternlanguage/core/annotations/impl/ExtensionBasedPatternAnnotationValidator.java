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
package org.eclipse.viatra2.patternlanguage.core.annotations.impl;

import org.eclipse.viatra2.patternlanguage.core.annotations.IPatternAnnotationValidator;
import org.eclipse.viatra2.patternlanguage.core.annotations.PatternAnnotationProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.AnnotationParameter;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.BoolValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.DoubleValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.IntValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ListValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.StringValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ValueReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableValue;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * A type-safe wrapper created from a pattern annotation extension point
 * together with validation-related methods. Such validators are instantiated in
 * {@link PatternAnnotationProvider}.
 * 
 * @author Zoltan Ujhelyi
 * @noinstantiate This class is not intended to be instantiated by clients
 */
public class ExtensionBasedPatternAnnotationValidator implements
		IPatternAnnotationValidator {

	private Iterable<ExtensionBasedPatternAnnotationParameter> definedAttributes;
	private final String name;
	
	private final static ImmutableMap<String, Class<? extends ValueReference>> typeMapping = new ImmutableMap.Builder<String, Class<? extends ValueReference>>()
			.put(ExtensionBasedPatternAnnotationParameter.INT, IntValue.class)
			.put(ExtensionBasedPatternAnnotationParameter.STRING,
					StringValue.class)
			.put(ExtensionBasedPatternAnnotationParameter.DOUBLE,
					DoubleValue.class)
			.put(ExtensionBasedPatternAnnotationParameter.BOOLEAN,
					BoolValue.class)
			.put(ExtensionBasedPatternAnnotationParameter.LIST, ListValue.class)
			.put(ExtensionBasedPatternAnnotationParameter.VARIABLEREFERENCE,
					VariableValue.class).build();

	public ExtensionBasedPatternAnnotationValidator(String name,
			Iterable<ExtensionBasedPatternAnnotationParameter> parameters) {
		super();
		this.name = name;
		this.definedAttributes = parameters;
	}
	
	private static final class AnnotationParameterName implements
			Function<AnnotationParameter, String> {
		@Override
		public String apply(AnnotationParameter input) {
			Preconditions.checkNotNull(input, "annotation");
			return input.getName();
		}
	}

	private static final class AnnotationDefinitionParameterName implements Function<ExtensionBasedPatternAnnotationParameter, String> {

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
		final Iterable<ExtensionBasedPatternAnnotationParameter> filteredParameters = 
				Iterables.filter(definedAttributes, new Predicate<ExtensionBasedPatternAnnotationParameter>() {

			@Override
			public boolean apply(ExtensionBasedPatternAnnotationParameter input) {
				Preconditions.checkNotNull(input, "input");
				return input.isMandatory() && !Iterables.contains(actualAttributeNames, input.getName());
			}
		});
		return Iterables.transform(filteredParameters, new AnnotationDefinitionParameterName()); 
	}

	@Override
	public Iterable<AnnotationParameter> getUnknownAttributes(
			Annotation annotation) {
		final Iterable<String> parameterNames = Iterables.transform(definedAttributes, new AnnotationDefinitionParameterName());
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
			if (p.name.equals(parameter.getName())) {
				expectedParameter = p;
			}
		}
		String type = expectedParameter.getType();
		if (expectedParameter == null || type == null) {
			return null;
		}
		if (typeMapping.containsKey(type)) {
			return typeMapping.get(type);
		} 
		return null;
	}

	@Override
	public String getAnnotationName() {
		return name;
	}

}
