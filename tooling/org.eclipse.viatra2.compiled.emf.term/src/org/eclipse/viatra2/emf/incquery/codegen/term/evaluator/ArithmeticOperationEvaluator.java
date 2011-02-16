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

import org.eclipse.viatra2.emf.incquery.codegen.term.SerializedTerm;
import org.eclipse.viatra2.emf.incquery.codegen.term.TermEvaluator;
import org.eclipse.viatra2.emf.incquery.codegen.term.UsedVariables;
import org.eclipse.viatra2.emf.incquery.codegen.term.exception.ViatraCompiledCompileTimeException;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.Term;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.ArithmeticOperation;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.Division;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.Minus;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.Multiply;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.Plus;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.Remainder;

/**
 * @author Akos Horvath
 *
 */
public class ArithmeticOperationEvaluator extends AbstractEvaluator {


	/** returns the arithmetic operation with it's two operands
	 * @param termToBeEvaluated the term that have to be evaluated
	 * @return the serialized operation with the operands
	 * @throws ViatraCompiledCompileTimeException
	 */
	public static SerializedTerm evaluate(Term termToBeEvaluated, UsedVariables usedVariables) throws ViatraCompiledCompileTimeException
	{
			ArithmeticOperation arithmeticOperation=(ArithmeticOperation)termToBeEvaluated;

			if(arithmeticOperation.getActualParameters().size()!=2)
				throw new ViatraCompiledCompileTimeException (ViatraCompiledCompileTimeException .TERM_PARAM_NUM,arithmeticOperation);

			// First we have to evaluate both parameters, and then perform the
			// operation.
			SerializedTerm  op1 = TermEvaluator.evaluate(arithmeticOperation.getActualParameters().get(0),usedVariables);
			SerializedTerm  op2 = TermEvaluator.evaluate(arithmeticOperation.getActualParameters().get(1),usedVariables);
			if(op1 == null)
				throw new ViatraCompiledCompileTimeException (ViatraCompiledCompileTimeException .TERM_PARAM_NULL,arithmeticOperation.getActualParameters().get(0));
			if(op2 == null)
				throw new ViatraCompiledCompileTimeException (ViatraCompiledCompileTimeException .TERM_PARAM_NULL,arithmeticOperation.getActualParameters().get(1));
//			if(op1.getType().equals(ValueKind.UNDEF_LITERAL))
//				throw new GTASMCompiledException (GTASMCompiledException .TERM_PARAM_NOTYPE,arithmeticOperation.getActualParameters().get(0));
//			if(op2.getType().equals(ValueKind.UNDEF_LITERAL))
//				throw new GTASMCompiledException (GTASMCompiledException .TERM_PARAM_NOTYPE,arithmeticOperation.getActualParameters().get(1));
//
			if (arithmeticOperation instanceof Plus)
				{
				return new SerializedTerm(
						"VPMTermEvaluator.plus("
						//+TermEvaluator.convertValueKindtoJavaType(op1.getType())

						+op1.getTerm()
						+","
						//+TermEvaluator.convertValueKindtoJavaType(op2.getType())
						+op2.getTerm()
						+")"
						, TermEvaluator.getTypeofOperation(op1.getType(),op2.getType()));
				}
			else if (arithmeticOperation instanceof Minus)
				{
				return new SerializedTerm("VPMTermEvaluator.minus("+op1.getTerm()+ "," + op2.getTerm()+")",TermEvaluator.getTypeofOperation(op1.getType(),op2.getType()));
				}
			else if (arithmeticOperation instanceof Multiply)
				{
				return new SerializedTerm("VPMTermEvaluator.multiply("+op1.getTerm()+ "," + op2.getTerm()+")",TermEvaluator.getTypeofOperation(op1.getType(),op2.getType()));
				}
			else if (arithmeticOperation instanceof Division)
				{
				return new SerializedTerm("VPMTermEvaluator.division("+op1.getTerm()+ ", " + op2.getTerm()+")",TermEvaluator.getTypeofOperation(op1.getType(),op2.getType()));
				}
			else if (arithmeticOperation instanceof Remainder)
				{ // Only for Integer!
				return new SerializedTerm("VPMTermEvaluator.remainder("+op1.getTerm()+ ", " + op2.getTerm()+")",TermEvaluator.getTypeofOperation(op1.getType(),op2.getType()));
				}
			else throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException .TERM_UNIMP_ARITHMETIC,arithmeticOperation);


	}






}
