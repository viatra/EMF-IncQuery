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
package org.eclipse.viatra2.emf.incquery.codegen.term.evaluator;

import org.eclipse.viatra2.emf.incquery.codegen.term.exception.ViatraCompiledCompileTimeException;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.enums.MultiplicityKind;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.enums.ValueKind;

/**
 * @author Akos Horvath
 *
 */
public abstract class AbstractEvaluator {
	
	/** Generates the serialized version of the input object
	 * @param o the object to serialized
	 * @return 
	 */
	public static String convertToString(Object o)
	{
		if(o == null) return "[NULL]";
		if(o instanceof MultiplicityKind)
		{
			if(MultiplicityKind.MANY_TO_MANY_LITERAL.equals(o))
				return "many to many";
			else if(MultiplicityKind.ONE_TO_MANY_LITERAL.equals(o))
				return "one to many";
			else if(MultiplicityKind.MANY_TO_ONE_LITERAL.equals(o))
				return "many to one";
			else if(MultiplicityKind.ONE_TO_ONE_LITERAL.equals(o))
				return "one to one";
		}
		
        return o.toString();
	}
	
	public static ValueKind getTypeofOperation(ValueKind type1, ValueKind type2) throws ViatraCompiledCompileTimeException{
		if (type1.equals(ValueKind.STRING_LITERAL) || type2.equals(ValueKind.STRING_LITERAL) ) 
		{
			return ValueKind.STRING_LITERAL;
			
		} else if (type1.equals(ValueKind.DOUBLE_LITERAL) ) 
		{ 
			// Double and Integer permitted
			if (type2.equals(ValueKind.DOUBLE_LITERAL) ||
					type2.equals(ValueKind.INTEGER_LITERAL))
				return ValueKind.DOUBLE_LITERAL;
					
			else throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.PARAM_NOT_SUITABLE_WITH_NO+type2.getName()+"/2");
		} else if (type1.equals(ValueKind.INTEGER_LITERAL)) 
		{ 
			if (type2.equals(ValueKind.DOUBLE_LITERAL)) 
				return ValueKind.DOUBLE_LITERAL;
			else if (type2.equals(ValueKind.INTEGER_LITERAL))
				return ValueKind.INTEGER_LITERAL;
			else throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.PARAM_NOT_SUITABLE_WITH_NO+type2.getName()+"/2");
		} else if(type1.equals(ValueKind.MODELELEMENT_LITERAL)){
			if(type2.equals(ValueKind.STRING_LITERAL))
				return ValueKind.STRING_LITERAL;
			else
				throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.PARAM_NOT_SUITABLE_WITH_NO+type2.getName()+"/2");
		}
//		else if (op1 instanceof MultiplicityKind) 
//		{ 
//			if ( ((MultiplicityKind.MANY_TO_MANY_LITERAL.equals(op1))&&(op2 instanceof MultiplicityKind))||
//					((MultiplicityKind.MANY_TO_MANY_LITERAL.equals(op2))&&(op1 instanceof MultiplicityKind))||
//					((MultiplicityKind.ONE_TO_MANY_LITERAL.equals(op1))&&(MultiplicityKind.MANY_TO_ONE_LITERAL.equals(op2)))||
//					((MultiplicityKind.ONE_TO_MANY_LITERAL.equals(op2))&&(MultiplicityKind.MANY_TO_ONE_LITERAL.equals(op1)))
//				)
//				return MultiplicityKind.MANY_TO_MANY_LITERAL;
//			
//			else if ( 
//					((MultiplicityKind.ONE_TO_ONE_LITERAL.equals(op1))&&(MultiplicityKind.MANY_TO_ONE_LITERAL.equals(op2)))||
//					((MultiplicityKind.ONE_TO_ONE_LITERAL.equals(op2))&&(MultiplicityKind.MANY_TO_ONE_LITERAL.equals(op1)))||
//					((MultiplicityKind.MANY_TO_ONE_LITERAL.equals(op1))&&(MultiplicityKind.MANY_TO_ONE_LITERAL.equals(op2)))
//				)
//				return MultiplicityKind.MANY_TO_ONE_LITERAL;
//			else if ( 
//					((MultiplicityKind.ONE_TO_ONE_LITERAL.equals(op1))&&(MultiplicityKind.ONE_TO_MANY_LITERAL.equals(op2)))||
//					((MultiplicityKind.ONE_TO_ONE_LITERAL.equals(op2))&&(MultiplicityKind.ONE_TO_MANY_LITERAL.equals(op1)))||
//					((MultiplicityKind.ONE_TO_MANY_LITERAL.equals(op1))&&(MultiplicityKind.ONE_TO_MANY_LITERAL.equals(op2)))
//				)
//				return MultiplicityKind.ONE_TO_MANY_LITERAL;
//			else if ( 
//					((MultiplicityKind.ONE_TO_ONE_LITERAL.equals(op1))&&(MultiplicityKind.ONE_TO_ONE_LITERAL.equals(op2)))
//				)
//				return MultiplicityKind.ONE_TO_ONE_LITERAL;
//			else throw new GTASMCompiledException(GTASMCompiledException.PARAM_NOT_SUITABLE_WITH_NO+"2",termToBeEvaluated);
//		}
		else if (type1.equals(ValueKind.BOOLEAN_LITERAL))
			if(type2.equals(ValueKind.BOOLEAN_LITERAL))
				return ValueKind.BOOLEAN_LITERAL;
			else throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.PARAM_NOT_SUITABLE_WITH_NO+type2.getName()+"/2");
		else throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.PARAM_NOT_SUITABLE_WITH_NO+type1.getName()+"/1");
		//return null;
	}

}
