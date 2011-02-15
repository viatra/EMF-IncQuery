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
package org.eclipse.viatra2.compiled.emf.term.evaluator;

import org.eclipse.viatra2.compiled.emf.term.SerializedTerm;
import org.eclipse.viatra2.compiled.emf.term.TermEvaluator;
import org.eclipse.viatra2.compiled.emf.term.UsedVariables;
import org.eclipse.viatra2.compiled.emf.term.exception.ViatraCompiledCompileTimeException;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.enums.ValueKind;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.Term;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.ConversionOperation;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.ToBoolean;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.ToDouble;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.ToInt;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.ToModelElement;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.ToMultiplicity;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.builtInFunctions.ToString;


/**
 * @author Akos Horvath
 *
 */
public class ConversionOperationEvaluator extends AbstractEvaluator{

	public static SerializedTerm evaluate(Term termToBeEvaluated, UsedVariables usedVariables)
	throws ViatraCompiledCompileTimeException{
		ConversionOperation conversionOperation=((ConversionOperation)termToBeEvaluated);

		if (conversionOperation.getActualParameters()
				.size() != 1)
			throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_CONV_PARAM_NUM, conversionOperation);

		// First we have to evaluate the parameter, and then perform the operation.

		SerializedTerm op1 = TermEvaluator.evaluate(conversionOperation.getActualParameters().get(0),usedVariables);

		if (conversionOperation instanceof ToBoolean)
		{
			op1.insert(0, "VPMTermEvaluator.toBoolean(");
			op1.append(")");
			op1.setType(ValueKind.BOOLEAN_LITERAL);

			return op1;
		}
		else if (conversionOperation instanceof ToModelElement){
			op1.insert(0, "VPMTermEvaluator.toModelElement(");
			op1.append(")");
			op1.setType(ValueKind.MODELELEMENT_LITERAL);

			return op1;
		}
			else if (conversionOperation instanceof ToString){
			op1.append(".toString()");
			op1.setType(ValueKind.STRING_LITERAL);

			return op1;
		}
		else if (conversionOperation instanceof ToInt) {
			op1.insert(0, "VPMTermEvaluator.toInteger(");
			op1.append(")");
			op1.setType(ValueKind.INTEGER_LITERAL);

			return op1;
		} else if (conversionOperation instanceof ToDouble) {
			op1.insert(0, "VPMTermEvaluator.toDouble(");
			op1.append(")");
			op1.setType(ValueKind.DOUBLE_LITERAL);

			return op1;
		} else if (conversionOperation instanceof ToMultiplicity) {
			op1.insert(0, "VPMTermEvaluator.toMultiplicity(");
			op1.append(")");
			op1.setType(ValueKind.MULTIPLICITY_LITERAL);

			return op1;
		}
		// the control gets here, if a non-implemented Term was evaluated.
		else throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.AMS_CONVERSION_UNIMP, conversionOperation);

		}


}
