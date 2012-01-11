/*******************************************************************************
 * Copyright (c) 2004-2010 Akos Horvath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.runtime.exception;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;

public class IncQueryRuntimeException extends Exception{
	
	private static final long serialVersionUID = -74252748358355750L;
	
	public static String PARAM_NOT_SUITABLE_WITH_NO = "The type of the parameters are not suitable for the operation. Parameter number: ";
	public static String CONVERSION_FAILED = "Could not convert the term to the designated type";
	public static String CONVERT_NULL_PARAMETER = "Could not convert null to the designated type";
	public static String RELATIONAL_PARAM_UNSUITABLE = "The parameters are not acceptable by the operation"; 
	public static String PATTERN_MATCHER_PROBLEM = "The following error occurred during the preparation of the generated pattern matcher";
	public static String GETNAME_FAILED = "Could not get 'name' attribute of the result";
	public static String INVALID_EMFROOT = "Incremental query engine can only be attached on the contents of an EMF EObject, Resource, or ResourceSet.";
    
	
	public IncQueryRuntimeException(String s) {
		super(s);
	}
	public IncQueryRuntimeException(RetePatternBuildException e) {
		super(PATTERN_MATCHER_PROBLEM+": " + e.getMessage(), e);
	}

}
