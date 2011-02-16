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
package org.eclipse.viatra2.compiled.emf.term;

import org.eclipse.viatra2.compiled.emf.term.evaluator.AbstractEvaluator;
import org.eclipse.viatra2.compiled.emf.term.evaluator.ArithmeticOperationEvaluator;
import org.eclipse.viatra2.compiled.emf.term.evaluator.BasicTermEvaluator;
import org.eclipse.viatra2.compiled.emf.term.evaluator.ConversionOperationEvaluator;
import org.eclipse.viatra2.compiled.emf.term.evaluator.ModelElementQueryEvaluator;
import org.eclipse.viatra2.compiled.emf.term.evaluator.RelationalOperationEvaluator;
import org.eclipse.viatra2.compiled.emf.term.exception.ViatraCompiledCompileTimeException;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.enums.ValueKind;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.ModelElementQuery;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.Term;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.ArithmeticOperation;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.ConversionOperation;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.RelationalOperation;

/**
 * The generator class for the EMF IncQuery terms
 * Generates terms using the VPMTermEvaulator
 * @author Akos Horvath
 *
 */
public class TermEvaluator extends AbstractEvaluator {

	/**
	 * Evaluates the given term
	 * @param termToBeEvaluated
	 * @param usedVariables

	 * @return StringBuffer containing the serialized representation of the input term
	 * @throws ViatraCompiledCompileTimeException
	 */
	public static SerializedTerm evaluate(Term termToBeEvaluated, UsedVariables usedVariables)  throws ViatraCompiledCompileTimeException
	{
		if(termToBeEvaluated instanceof ArithmeticOperation)
		{
			//call the ArithmeticOperationEvaluator...
			return ArithmeticOperationEvaluator.evaluate(termToBeEvaluated,usedVariables);
		}
		else if(termToBeEvaluated instanceof ConversionOperation)
		{
			return ConversionOperationEvaluator.evaluate(termToBeEvaluated,usedVariables);
		}
		else if(termToBeEvaluated instanceof RelationalOperation)
		{
			return RelationalOperationEvaluator.evaluate(termToBeEvaluated,usedVariables);
		}
		else if(termToBeEvaluated instanceof ModelElementQuery)
		{
			return ModelElementQueryEvaluator.evaluate(termToBeEvaluated,usedVariables);
		}
		else if(null == termToBeEvaluated)
		{
			throw new ViatraCompiledCompileTimeException("Invalid term to evaluate.");
		}
		else
		{
			return BasicTermEvaluator.evaluate( termToBeEvaluated, usedVariables);
		}
	}

	/** Converts the input type to its corresponding Java equivalent
	 * @param type The type to be converted
	 * @return The java equivalent type
	 * @throws ViatraCompiledCompileTimeException
	 */
	public static String convertValueKindtoJavaType(ValueKind type) throws ViatraCompiledCompileTimeException{

		if(type == null)
			return null;

		if(type.equals(ValueKind.BOOLEAN_LITERAL))
			return "Boolean";
		if(type.equals(ValueKind.DOUBLE_LITERAL))
			return "Double";
		if(type.equals(ValueKind.INTEGER_LITERAL))
			return "Integer";
		if(type.equals(ValueKind.MODELELEMENT_LITERAL))
			return "EObject";
		if(type.equals(ValueKind.MULTIPLICITY_LITERAL))
			return "[Multiplicity]";
		if(type.equals(ValueKind.STRING_LITERAL))
			return "String";
		// it is a ModelElement!!
		if(type.equals(ValueKind.UNDEF_LITERAL))
			return "Object";


		throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.TERM_NOTYPE,type);

	}
}
