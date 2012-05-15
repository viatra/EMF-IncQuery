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
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * A type-safe wrapper created from a pattern annotation extension point
 * together with validation-related methods. Such validators are instantiated in
 * {@link PatternAnnotationProvider}.
 * 
 * @author Zoltan Ujhelyi
 * @noinstantiate This class is not intended to be instantiated outside the plug-in
 */
public class ExtensionBasedPatternAnnotationValidator implements
		IPatternAnnotationValidator {

	Iterable<ExtensionBasedPatternAnnotationParameter> definedAttributes;

	public ExtensionBasedPatternAnnotationValidator(String name,
			String languageID,
			Iterable<ExtensionBasedPatternAnnotationParameter> parameters) {
		super();
		this.definedAttributes = parameters;
	}
	
	private class ParameterName implements Function<ExtensionBasedPatternAnnotationParameter, String> {

		@Override
		public String apply(ExtensionBasedPatternAnnotationParameter input) {
			return input.name;
		}
		
	}
	
	@Override
	public Iterable<String> getAllAvailableParameterNames() {
		return Iterables.transform(definedAttributes, new Function<ExtensionBasedPatternAnnotationParameter, String>() {

			@Override
			public String apply(ExtensionBasedPatternAnnotationParameter input) {
				return input.getName();
			}
		});
	}
	
	private Iterable<String> getParameterNames(Annotation annotation) {
		return Iterables.transform(annotation.getParameters(), new Function<AnnotationParameter, String>() {

			@Override
			public String apply(AnnotationParameter input) {
				return input.getName();
			}
		});
	}
	
	@Override
	public Iterable<String> getMissingMandatoryAttributes(Annotation annotation) {
		final Iterable<String> actualAttributeNames = getParameterNames(annotation);
		final Iterable<ExtensionBasedPatternAnnotationParameter> filteredParameters = 
				Iterables.filter(definedAttributes, new Predicate<ExtensionBasedPatternAnnotationParameter>() {

			@Override
			public boolean apply(ExtensionBasedPatternAnnotationParameter input) {
				return input.isMandatory() && !Iterables.contains(actualAttributeNames, input.getName());
			}
		});
		return Iterables.transform(filteredParameters, new ParameterName()); 
	}

	@Override
	public Iterable<AnnotationParameter> getUnknownAttributes(
			Annotation annotation) {
		final Iterable<String> parameterNames = Iterables.transform(definedAttributes, new ParameterName());
		return Iterables.filter(annotation.getParameters(), new Predicate<AnnotationParameter>() {

			@Override
			public boolean apply(AnnotationParameter input) {
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
		if (expectedParameter == null || expectedParameter.getType() == null) {
			return null;
		}
		if (ExtensionBasedPatternAnnotationParameter.INT.equals(expectedParameter.getType())) {
			return IntValue.class;
		} else if (ExtensionBasedPatternAnnotationParameter.STRING.equals(expectedParameter.getType())) {
			return StringValue.class;
		} else if (ExtensionBasedPatternAnnotationParameter.DOUBLE.equals(expectedParameter.getType())) {
			return DoubleValue.class;
		} else if (ExtensionBasedPatternAnnotationParameter.BOOLEAN.equals(expectedParameter.getType())) {
			return BoolValue.class;
		} else if (ExtensionBasedPatternAnnotationParameter.LIST.equals(expectedParameter.getType())) {
			return ListValue.class;
		} if (ExtensionBasedPatternAnnotationParameter.VARIABLEREFERENCE.equals(expectedParameter.getType())) {
			return VariableValue.class;
		} 
		return null;
	}

}
