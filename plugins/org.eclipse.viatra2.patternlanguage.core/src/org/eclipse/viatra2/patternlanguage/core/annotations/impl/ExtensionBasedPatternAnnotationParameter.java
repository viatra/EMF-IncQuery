package org.eclipse.viatra2.patternlanguage.core.annotations.impl;

import org.eclipse.viatra2.patternlanguage.core.annotations.ParameterType;


public class ExtensionBasedPatternAnnotationParameter {
	String name;
	String type;
	boolean multiple;
	boolean mandatory;
	public ExtensionBasedPatternAnnotationParameter(String name,
			String type, boolean multiple, boolean mandatory) {
		super();
		this.name = name;
		this.type = type;
		this.multiple = multiple;
		this.mandatory = mandatory;
	}
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	public boolean isMultiple() {
		return multiple;
	}
	public boolean isMandatory() {
		return mandatory;
	}
	
	private ParameterType getType(String typeStr) {
		if ("string".equals(typeStr)) {
			return ParameterType.STRING;
		} else if ("int".equals(typeStr)) {
			return ParameterType.INT;
		} else if ("double".equals(typeStr)) {
			return ParameterType.DOUBLE;
		} else if ("boolean".equals(typeStr)) {
			return ParameterType.BOOL;
		} else if ("list".equals(typeStr)) {
			return ParameterType.LIST;
		} else if ("variablereference"
				.equals(typeStr)) {
			return ParameterType.VARIABLE;
		}
		return null;
	}
}