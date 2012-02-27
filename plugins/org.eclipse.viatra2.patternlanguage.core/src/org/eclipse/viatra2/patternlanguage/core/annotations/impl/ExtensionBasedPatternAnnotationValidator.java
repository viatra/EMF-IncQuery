package org.eclipse.viatra2.patternlanguage.core.annotations.impl;

import org.eclipse.viatra2.patternlanguage.core.annotations.IPatternAnnotationValidator;
import org.eclipse.viatra2.patternlanguage.core.annotations.PatternAnnotationProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.AnnotationParameter;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ValueReference;

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

	String name;
	String languageID;
	Iterable<ExtensionBasedPatternAnnotationParameter> definedAttributes;

	public ExtensionBasedPatternAnnotationValidator(String name,
			String languageID,
			Iterable<ExtensionBasedPatternAnnotationParameter> parameters) {
		super();
		this.name = name;
		this.languageID = languageID;
		this.definedAttributes = parameters;
	}
	
	private class ParameterName implements Function<ExtensionBasedPatternAnnotationParameter, String> {

		@Override
		public String apply(ExtensionBasedPatternAnnotationParameter input) {
			return input.name;
		}
		
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
	public Class<ValueReference> getParameterTypeError(
			AnnotationParameter parameter) {
		// TODO Auto-generated method stub
		return null;
	}

}
