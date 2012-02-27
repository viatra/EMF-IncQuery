package org.eclipse.viatra2.patternlanguage.core.annotations;

import java.util.Hashtable;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.viatra2.patternlanguage.core.annotations.impl.ExtensionBasedPatternAnnotationValidator;

public class PatternAnnotationProvider {

	static final String EXTENSIONID = "org.eclipse.viatra2.patternlanguage.core.annotation";
	Hashtable<String, IPatternAnnotationValidator> annotationValidators;

	protected void initializeValidators() {
		annotationValidators = new Hashtable<String, IPatternAnnotationValidator>();
		final IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EXTENSIONID);
		for (IConfigurationElement e : config) {
			final String annotationName = e.getAttribute("name");
			IPatternAnnotationValidator annotationValidator = new ExtensionBasedPatternAnnotationValidator();

			annotationValidators.put(annotationName, annotationValidator);
		}
	}

	/**
	 * Returns a pattern annotation validator for a selected annotation name
	 * @param annotationName
	 * @return a pattern annotation validator
	 */
	public IPatternAnnotationValidator getValidator(String annotationName) {
		if (annotationValidators == null) {
			initializeValidators();
		}
		return annotationValidators.get(annotationName);
	}
	
	/**
	 * Decides whether a validator is defined for the selected annotation name.
	 * @param annotationName
	 * @return true, if a validator is defined
	 */
	public boolean hasValidator(String annotationName) {
		if (annotationValidators == null) {
			initializeValidators();
		}
		return annotationValidators.containsKey(annotationName);
	}
}
