/**
 * 
 */
package org.eclipse.viatra2.patternlanguage.core.annotations;

import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;

/**
 * An interface for validating pattern {@link Annotation} objects.
 * @author Zoltan Ujhelyi
 *
 */
public interface IPatternAnnotationValidator {

	/**
	 * Validates the annotation using the annotation definiton.
	 * @param annotation
	 * @param validator
	 */
	public void validateAnnotation(Annotation annotation, AbstractDeclarativeValidator validator);
}
