package org.eclipse.viatra2.patternlanguage.core.annotations.impl;



public class ExtensionBasedPatternAnnotationParameter {
	public static final String STRING = "string";
	public static final String VARIABLEREFERENCE = "variablereference";
	public static final String LIST = "list";
	public static final String BOOLEAN = "boolean";
	public static final String DOUBLE = "double";
	public static final String INT = "int";
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
}