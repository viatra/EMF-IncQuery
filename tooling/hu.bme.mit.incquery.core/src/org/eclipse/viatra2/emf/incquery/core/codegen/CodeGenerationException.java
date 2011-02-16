/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.core.codegen;

/**
 * Exception during the code generation of EMF-IncQuery
 * @author Bergmann Gábor
 *
 */
public class CodeGenerationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4204924820066556434L;

	/**
	 * 
	 */
	public CodeGenerationException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CodeGenerationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public CodeGenerationException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public CodeGenerationException(Throwable arg0) {
		super(arg0);
	}
	

}
