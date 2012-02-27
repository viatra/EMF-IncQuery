/**
 * 
 */
package org.eclipse.viatra2.patternlanguage.core.annotations;

import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.AnnotationParameter;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ValueReference;

/**
 * An interface for validating pattern {@link Annotation} objects.
 * @author Zoltan Ujhelyi
 *
 */
public interface IPatternAnnotationValidator {

	public Iterable<String> getMissingMandatoryAttributes(Annotation annotation);
	
	/**
	 * @param annotation
	 * @return
	 */
	public Iterable<AnnotationParameter> getUnknownAttributes(Annotation annotation);
	
	/**
	 * Returns whether a parameter of an annotation is mistyped
	 * @param parameter
	 * @return the 
	 */
	public Class<ValueReference> getParameterTypeError(AnnotationParameter parameter);
}
