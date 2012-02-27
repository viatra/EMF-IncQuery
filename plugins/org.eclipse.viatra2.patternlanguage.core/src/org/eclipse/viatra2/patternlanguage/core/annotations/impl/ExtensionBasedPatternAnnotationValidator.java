package org.eclipse.viatra2.patternlanguage.core.annotations.impl;

import org.eclipse.viatra2.patternlanguage.core.annotations.IPatternAnnotationValidator;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;

public class ExtensionBasedPatternAnnotationValidator implements IPatternAnnotationValidator{

	private class AnnotationParameter {
		String name;
		ParameterType type;
		boolean multiple;
	}
	
	private enum ParameterType {
		INT,
		STRING,
		VARIABLE,
		DOUBLE,
		BOOL,
		LIST
	}
	
	String name;
	String languageID;
	
	
	
	@Override
	public void validateAnnotation(Annotation annotation,
			AbstractDeclarativeValidator validator) {
		// TODO Auto-generated method stub
		
	}
	
	
}
