/*******************************************************************************
 * Copyright (c) 2004-2009 Akos Horvath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.compiled.emf.term.exception;

import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.core.Annotation;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.core.GTASMElement;

/**
 * @author Akos Horvath
 *
 */
public class ViatraCompiledCompileTimeException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1234134231L;
	
	//*********************TERM****************************
	public static String TERM_PARAM_NUM = "Only two parameters are allowed to an artihmetic operation";
	public static String TERM_PARAM_NULL = "The TERM is evaluated to null";
	public static String TERM_UNIMP_ARITHMETIC = "This arithmetic operation is not supported in the compiled version";
	public static String TERM_UNIMP = "This TERM is not supported in the compiled version";
	public static String TERM_VARIABLE_NOTINIT = "The variable was not initialized before it was used in a Term";
	public static String TERM_PARAM_NOTYPE = "Can not evaulate the type of the Term, can lead to Errors in the Java code";
	public static String TERM_NOTYPE = "The ValueKind is not uspported in the Compiled version";
	
	//**********************ASM*****************************
	public static String ASM_FUNCTION_PARAM = "An ASM function can have only one input parameter in the compiled version";
	public static String ASM_COMPILED = "Undefined type of constant - unable to generate";
	public static String ASM_CONV_PARAM_NUM = "Conversion operators can have only one input parameters";
	public static String AMS_CONVERSION_UNIMP = "Unimplemented conversion operation";
	public static String ASM_UNIMP_RELATIONAL = "Unimplemented relation operation";
	public static String ASM_REL_PARAM_NUM = "Only two params are allowed to a relation operation";
	public static String ASM_MODELMAN_UNIMP = "This model element query/manipulation rule is not supported";
	public static String ASM_RULE_UNIMPL = "This asm rule is not supported by the compiled version";
	public static String ASM_NESTED_RULE_UNIMP = "The nested rule is not supported";
	public static String ASM_VAR_TERM_TYPEMISMATCH = "The type of the variable and the term are not matching: ";
	public static String ASM_NO_TYPE= "The CREATE rule does not define a Type: ";
	public static String ASM_PARENT = "Can NOT define a parent for the entity creation rule";
	public static String ASM_SCOPE_NOT_EXIST = "The Scope of the parameter does not exists";
	public static String AMS_BLOCKRULE_PARAMS_NOT_USED = "There are unused parameter in the forall/choose invocation";
	public static String ASM_NONVARIABLE_OUPUT = "One of the output parameters of the pattern is not a Variable";
	
	public static String PARAM_NOT_SUITABLE_WITH_NO = "The type of the parameters are not suitable for the operation! Parameter (type/number:) " ;
	public static String PARAM_IN_INOUT_NOT_VARREF = "The OUT/INOUT symbolic parameter is not a Variable";

	
	
	
	
	//**********************GT******************************
	
	
	Object errorfulElement;
	
	/**
	 * 
	 */
	public ViatraCompiledCompileTimeException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public ViatraCompiledCompileTimeException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	
	public ViatraCompiledCompileTimeException(String message, Object eobject) {
		super(message);
		errorfulElement = eobject;
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public ViatraCompiledCompileTimeException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ViatraCompiledCompileTimeException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#printStackTrace()
	 */
	@Override
	public void printStackTrace() {
		super.printStackTrace();
		
		if(errorfulElement instanceof GTASMElement)
		{GTASMElement element = (GTASMElement)errorfulElement;
			  for (int j = 0; j < element.getAnnotations().size(); j++) {
		            Annotation annotation = 
		                element.getAnnotations().get(j);	
			
		            if(annotation.getKey().equals("node_info"))
		            System.err.println(annotation.getValue());
			  }
		}
	
		
	}

}
