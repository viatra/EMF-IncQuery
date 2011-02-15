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
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.ModelElementQuery;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.Term;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.queryFunctions.Aggregate;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.queryFunctions.ElementReference;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.queryFunctions.FullyQualifiedName;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.queryFunctions.Inverse;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.queryFunctions.Name;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.queryFunctions.Value;

/**
 * @author Akos Horvath
 *
 */
public class ModelElementQueryEvaluator extends AbstractEvaluator{
	
	
	public static SerializedTerm evaluate(Term termToBeEvaluated, UsedVariables usedVariables) throws ViatraCompiledCompileTimeException {
		ModelElementQuery modelElementQuery = (ModelElementQuery)termToBeEvaluated;
		
		//parameter of the model manipulation operation
		SerializedTerm param;
		if(modelElementQuery instanceof Aggregate)
		{
			throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_MODELMAN_UNIMP,modelElementQuery);
		}
		else if(modelElementQuery instanceof FullyQualifiedName)
		{
			param = TermEvaluator.evaluate((modelElementQuery).getArgument(),usedVariables);
			//result is a model element right now - if it's correct...
			param.insert(0, "VPMUtil.getElementFQN((EObject)");
			param.append(")");
			param.setType(ValueKind.STRING_LITERAL);
			
			return param;
		}
		else if(modelElementQuery instanceof Name)
		{
			param = TermEvaluator.evaluate((modelElementQuery).getArgument(),usedVariables);
//			param.insert(0,"((String)((EObject)");
//			param.append(").getName())");
			param.insert(0, "VPMTermEvaluator.name(");
			param.append(")");
			param.setType(ValueKind.STRING_LITERAL);
			
			return param;
			//String parameter = param.getTerm().toString();
			//param.insert(0, "((String)((EObject)");
			//param.setType(ValueKind.STRING_LITERAL);
			//return param.append(".eGet(((EObject)"+parameter+").eClass().getEStructuralFeature(\"name\"))))");			
		}
		else if(modelElementQuery instanceof ElementReference)
		{
			param = TermEvaluator.evaluate((modelElementQuery).getArgument(),usedVariables);
			
			param.insert(0, "VPMUtil.getByRef((IVariable)");
			param.append(")");
			param.setType(ValueKind.MODELELEMENT_LITERAL);
			
			return param;
		}
		else if(modelElementQuery instanceof Value)
		{
			//TODO: only works if the EObject has a value attribute
			param = TermEvaluator.evaluate((modelElementQuery).getArgument(),usedVariables);
			return param;
			//String parameter = param.getTerm().toString();
			//param.insert(0, "((String)((Eobject)");
			//param.setType(ValueKind.STRING_LITERAL);
			//return param.append(".eGet(((EObject)"+parameter+").eClass().getEStructuralFeature(\"value\"))))");							
		}
		else if(modelElementQuery instanceof Inverse)
		{
			//There is a possibility that somehow this term could be used to get the EInverse of an EReference
			throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_MODELMAN_UNIMP,modelElementQuery);
		}
		throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_MODELMAN_UNIMP,termToBeEvaluated);
	}


}
