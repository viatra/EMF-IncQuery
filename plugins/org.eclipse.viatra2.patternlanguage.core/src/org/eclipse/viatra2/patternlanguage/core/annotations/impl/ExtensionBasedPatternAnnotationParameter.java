/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.patternlanguage.core.annotations.impl;

public class ExtensionBasedPatternAnnotationParameter {
	public static final String STRING = "string";
	public static final String VARIABLEREFERENCE = "variablereference";
	public static final String LIST = "list";
	public static final String BOOLEAN = "boolean";
	public static final String DOUBLE = "double";
	public static final String INT = "int";
	private String name;
	private String type;
	private boolean multiple;
	private boolean mandatory;
	private String description;
	public ExtensionBasedPatternAnnotationParameter(String name,
			String type, String description, boolean multiple, boolean mandatory) {
		super();
		this.name = name;
		this.type = type;
		this.description = description;
		this.multiple = multiple;
		this.mandatory = mandatory;
	}
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	public String getDescription() {
		return description;
	}
	public boolean isMultiple() {
		return multiple;
	}
	public boolean isMandatory() {
		return mandatory;
	}
}