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
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.enums.ValueKind;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.Term;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.And;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.Equals;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.GreaterThan;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.GreaterThanOrEqualTo;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.LessThan;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.LessThanOrEqualTo;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.Not;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.NotEquals;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.Or;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.RelationalOperation;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.XOr;


/**
 * @author Akos Horvath
 *
 */
public class RelationalOperationEvaluator extends AbstractEvaluator{


	public static SerializedTerm evaluate(Term termToBeEvaluated, UsedVariables usedVariables) throws ViatraCompiledCompileTimeException
	{
	RelationalOperation relationalOperation=(RelationalOperation)termToBeEvaluated;

	// Not is an operation with one parameter! Every other relational operations have two parameters.

	if(relationalOperation instanceof Not) // Any->Bool
	{
		SerializedTerm opNot =TermEvaluator.evaluate(relationalOperation.getActualParameters().get(0),usedVariables);
		opNot.insert(0,"(!");
		opNot.append(")");
		opNot.setType(ValueKind.BOOLEAN_LITERAL);

		return opNot;
	}

	if(relationalOperation.getActualParameters().size()!=2)
		throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_REL_PARAM_NUM,relationalOperation);

	// First we have to evaluate both operands, and then perform the
	// operation.


	SerializedTerm op1 = TermEvaluator.evaluate(relationalOperation.getActualParameters().get(0),usedVariables);
	SerializedTerm op2 = TermEvaluator.evaluate(relationalOperation.getActualParameters().get(1),usedVariables);

	op1.append(",");
	op1.append(op2);
	op1.append(")");


	if (relationalOperation instanceof XOr)
	{
		op1.insert("VPMTermEvaluator.xor(");
		return new SerializedTerm(op1.getTerm(),ValueKind.BOOLEAN_LITERAL);
		}
	else if (relationalOperation instanceof NotEquals)
		{
		op1.insert("VPMTermEvaluator.notEquals(");
		return new SerializedTerm(op1.getTerm(),ValueKind.BOOLEAN_LITERAL);
		}
	else if (relationalOperation instanceof And)
		{
		op1.insert("VPMTermEvaluator.and(");
		return new SerializedTerm(op1.getTerm(),ValueKind.BOOLEAN_LITERAL);
		}
		else if (relationalOperation instanceof Or)
		{
			op1.insert("VPMTermEvaluator.or(");
			return new SerializedTerm(op1.getTerm(),ValueKind.BOOLEAN_LITERAL);
		}

		else if (relationalOperation instanceof GreaterThan)
		{
			op1.insert("VPMTermEvaluator.greaterThan(");
			return new SerializedTerm(op1.getTerm(),ValueKind.BOOLEAN_LITERAL);
		}
		else if (relationalOperation instanceof Equals)
		{
			op1.insert("VPMTermEvaluator.equals(");
			return new SerializedTerm(op1.getTerm(),ValueKind.BOOLEAN_LITERAL);
		}
		else if (relationalOperation instanceof LessThanOrEqualTo)
		{
			op1.insert("VPMTermEvaluator.lessThanorEqual(");
			return new SerializedTerm(op1.getTerm(),ValueKind.BOOLEAN_LITERAL);
		}
		else if (relationalOperation instanceof LessThan)
		{
			op1.insert("VPMTermEvaluator.lessThan(");
			return new SerializedTerm(op1.getTerm(),ValueKind.BOOLEAN_LITERAL);
		}

		else if (relationalOperation instanceof GreaterThanOrEqualTo)
		{
			op1.insert("VPMTermEvaluator.greaterThanorEqual(");
			return new SerializedTerm(op1.getTerm(),ValueKind.BOOLEAN_LITERAL);
		}
	// The control can only get here, if it was a not implemented RelationalOperation
	throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_UNIMP_RELATIONAL,relationalOperation);
}


}
