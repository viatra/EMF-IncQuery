package org.eclipse.viatra2.patternlanguage;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionHead;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionTail;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Type;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ReferenceType;

public class EMFPatternLanguageScopeHelper {

	public static final String NOT_AN_ENUMERATION_REFERENCE_ERROR = "Not an enumeration reference";

	public static EEnum calculateEnumerationType(PathExpressionHead head) throws ResolutionException{
		if (head.getTail() == null) throw new ResolutionException(NOT_AN_ENUMERATION_REFERENCE_ERROR);
		return calculateEnumerationType(head.getTail());
	}
	
	public static EEnum calculateEnumerationType(PathExpressionTail tail) throws ResolutionException{
		EClassifier classifier = calculateExpressionType(tail);
		if (classifier instanceof EEnum) {
			return (EEnum) classifier;
		}
		throw new ResolutionException(NOT_AN_ENUMERATION_REFERENCE_ERROR);
	}
	
	public static EClassifier calculateExpressionType(PathExpressionHead head) throws ResolutionException{
		if (head.getTail() == null) throw new ResolutionException(NOT_AN_ENUMERATION_REFERENCE_ERROR);
		return calculateExpressionType(head.getTail());
	}

	public static EClassifier calculateExpressionType(PathExpressionTail tail)
			throws ResolutionException {
		if (tail.getTail() == null) {
			Type type = tail.getType();
			return ((ReferenceType)type).getRefname().getEType();
		} else {
			return calculateEnumerationType(tail.getTail());
		}
	}
}
