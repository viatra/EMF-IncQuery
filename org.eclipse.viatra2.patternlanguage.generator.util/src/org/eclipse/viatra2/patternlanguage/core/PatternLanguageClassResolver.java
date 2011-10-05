package org.eclipse.viatra2.patternlanguage.core;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

public class PatternLanguageClassResolver {

	public static EClass getVariableType() {
		EPackage corePackage = EPackage.Registry.INSTANCE.getEPackage("http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage");
		return (EClass) corePackage.getEClassifier("Variable");
	}
	public static EClass getVariableReferenceType() {
		EPackage corePackage = EPackage.Registry.INSTANCE.getEPackage("http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage");
		return (EClass) corePackage.getEClassifier("VariableReference");
	}
}
