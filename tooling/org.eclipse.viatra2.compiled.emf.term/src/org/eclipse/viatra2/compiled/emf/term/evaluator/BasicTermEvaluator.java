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

import java.util.Iterator;

import org.eclipse.viatra2.compiled.emf.term.SerializedTerm;
import org.eclipse.viatra2.compiled.emf.term.TermEvaluator;
import org.eclipse.viatra2.compiled.emf.term.UsedVariables;
import org.eclipse.viatra2.compiled.emf.term.exception.ViatraCompiledCompileTimeException;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.enums.ValueKind;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.ASMFunctionInvocation;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.Constant;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.GTPatternCall;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.NativeFunctionInvocation;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.Term;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.VariableReference;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.queryFunctions.Multiplicity;


/**
 * @author Akos Horvath
 *
 */
public class BasicTermEvaluator extends AbstractEvaluator{


	/**
	 * This function generates the basic Terms. Constants, ASMFunctionInvocations,
	 * NativeFunctionInvocations, VariableReferences.
	 *
	 *
	 */
	public static SerializedTerm evaluate(Term termToBeEvaluated, UsedVariables usedVariables) throws ViatraCompiledCompileTimeException
	{
		if(termToBeEvaluated instanceof Constant)
		{
			int kind=((Constant)termToBeEvaluated).getKind().getValue();
			switch(kind)
			{
			case ValueKind.BOOLEAN:
				return new SerializedTerm("Boolean."+("true".equals(((Constant)termToBeEvaluated).getValue())?
						"TRUE":
						("false".equals(((Constant)termToBeEvaluated).getValue())?"FALSE":null)),ValueKind.BOOLEAN_LITERAL);

//			case ValueKind.DOUBLE:
//				return "new Double("+((Constant)termToBeEvaluated).getValue()+")";
//			case ValueKind.INTEGER:
//				return "new Integer("+((Constant)termToBeEvaluated).getValue()+")";
//			case ValueKind.STRING:
			case ValueKind.DOUBLE:
				return new SerializedTerm(((Constant)termToBeEvaluated).getValue(), ValueKind.DOUBLE_LITERAL);
			case ValueKind.INTEGER:
				return new SerializedTerm(((Constant)termToBeEvaluated).getValue(),ValueKind.INTEGER_LITERAL);
			case ValueKind.STRING:
				return new SerializedTerm("\""+((Constant)termToBeEvaluated).getValue()+"\"",ValueKind.STRING_LITERAL);
			case ValueKind.MODELELEMENT: //this element is not valid in case of EMF
				return new SerializedTerm("VPMUtil.getByFQN(\""+((Constant)termToBeEvaluated).getValue()+"\")",ValueKind.MODELELEMENT_LITERAL);
			case ValueKind.MULTIPLICITY:
					convertToString(((Constant)termToBeEvaluated).getValue());
			case ValueKind.UNDEF: // it represents a null ModelElement
				return new SerializedTerm("null",ValueKind.MODELELEMENT_LITERAL);
			default:
				throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_COMPILED,termToBeEvaluated);

			}
		}
		else if(termToBeEvaluated instanceof ASMFunctionInvocation)
		{
			if(((ASMFunctionInvocation)termToBeEvaluated).getActualParameters().size() != 1)
				throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_FUNCTION_PARAM,termToBeEvaluated);
			else
			{
			// Evaluating the parameter
				String asmFunction=((ASMFunctionInvocation)termToBeEvaluated).getName()+".get(";

				Term term = (((ASMFunctionInvocation)termToBeEvaluated).getActualParameters().get(0));
				asmFunction+=TermEvaluator.evaluate(term,usedVariables).getTerm().toString();
				return new SerializedTerm(asmFunction+")",ValueKind.UNDEF_LITERAL);
			}
		}
		else if(termToBeEvaluated instanceof NativeFunctionInvocation)
		{
			//TODO: native function is only a stub all param is passed as an object, without the model manager
			// Name of the native function
			String nativeFunction=((NativeFunctionInvocation)termToBeEvaluated).getFunctionName()+"(";
			// array for the evaluated parameters
			Iterator<Term> iter = ((NativeFunctionInvocation)termToBeEvaluated).getActualParameters().iterator();

			// Evaluating each parameter
			while(iter.hasNext())
			{
				Term term = iter.next();
				nativeFunction+="Object "+TermEvaluator.evaluate(term,usedVariables).getTerm().toString();
				if(iter.hasNext())
					 nativeFunction+= ", ";
			}

			return new SerializedTerm(nativeFunction+ ")",ValueKind.UNDEF_LITERAL);


		}
		else if(termToBeEvaluated instanceof VariableReference)
		{
			//TODO: have to check that the variable type is already known in the machine
			ValueKind variableType = usedVariables.get(((VariableReference)termToBeEvaluated).getVariable());

			return new SerializedTerm(usedVariables.getSerialzedNameofVariable(((VariableReference)termToBeEvaluated).getVariable())
					,variableType);
		}
		else if(termToBeEvaluated instanceof GTPatternCall)
		{
			// Evaluates GTPatternCall for match. Boolean return value only.
			//((GTPatternBody)((GTPatternCall)termToBeEvaluated).getCalledPattern().getPatternBodies().get(0)).get

//
//			GTPattern gtPattern = ((GTPatternCall)termToBeEvaluated).getCalledPattern();
//			String gtPatternCallString = "";
//
//
//			//TODO: currently all params are input
//			Iterator iter = ((GTPatternCall)termToBeEvaluated).getActualParameters().iterator();
//			//TODO: it is a legacy from gervarro...
//			PatternCallSignature[] signatures = new PatternCallSignature[((GTPatternCall)termToBeEvaluated).getActualParameters().size()];
//			int i = 0;
//			while(iter.hasNext())	{
//					 Term term = (Term) iter.next();
//					 gtPatternCallString +=  TermEvaluator.evaluate(term);
//					 PatternCallSignature signature = new PatternCallSignature();
//					 signature.setExecutionMode(ExecutionMode.SINGLE_RESULT);
//					 signature.setParameterMode(ParameterMode.INPUT);
//					 signature.setParameterScope(new Scope());
//					 signatures[i] = signature;
//					 i++;
//					 // comma is needed
//					 if(iter.hasNext())
//						 gtPatternCallString+= ", ";
//			}
//			gtPatternCallString = AsmMachineData.evaluateusedPatternName(gtPattern, signatures, PatternType.IF)+"("+ gtPatternCallString;
//			GTASMCompiler.getInstance().getCurrentMachine().addUsedPattern(gtPattern, signatures, PatternType.IF);
//			return new SerializedTerm(gtPatternCallString + ")",ValueKind.BOOLEAN_LITERAL);

//		return new SerializedTerm("***GTPattern Call***",ValueKind.BOOLEAN_LITERAL);
		throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.TERM_UNIMP+termToBeEvaluated.getClass().toString(),termToBeEvaluated);
		}
		else if(termToBeEvaluated instanceof Multiplicity)
		{
			return new SerializedTerm(convertToString(termToBeEvaluated),ValueKind.MULTIPLICITY_LITERAL);
		}
		// the control gets here, if a non-implemented Term was evaluated.
		throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.TERM_UNIMP+termToBeEvaluated.getClass().toString(),termToBeEvaluated);

	}


}
