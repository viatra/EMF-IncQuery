/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.codegen.patternmatcher;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.emf.incquery.codegen.term.TermEvaluator;
import org.eclipse.viatra2.emf.incquery.codegen.term.UsedVariables;
import org.eclipse.viatra2.emf.incquery.codegen.term.exception.ViatraCompiledCompileTimeException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.adapters.CodegenRecorderGTASMBuildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.CodegenRecordingCoordinator;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.enums.ValueKind;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.Term;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.VariableReference;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.GTPattern;



/**
 * @author Bergmann Gábor
 *
 */
public class EMFCodegenBuildable extends CodegenRecorderGTASMBuildable {

	/**
	 * @param coordinator
	 * @param effort
	 * @param indent
	 * @param baseName
	 * @param instanceSuffix
	 */
	public EMFCodegenBuildable(CodegenRecordingCoordinator<GTPattern> coordinator,
			GTPattern effort, String indent, String baseName, String instanceSuffix)
	{
		super(coordinator, effort, indent, baseName, instanceSuffix);
	}

	@Override
	public EMFCodegenBuildable getNextContainer() {
		// TODO we lose the info that this is a separate container...
		// String varName = declareNextContainerVariable();
		return new EMFCodegenBuildable(coordinator, null /* No pattern assigned yet */, indent, myName, "");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Buildable#putOnTab(java.lang.Object)
	 */
	@Override
	public EMFCodegenBuildable putOnTab(GTPattern effort) {
		// String varName = declarePutOnTabVariable(effort);
		return new EMFCodegenBuildable(coordinator, effort, indent, myName, "");
	}

	@Override
	public void generateTermEvalCode(StringBuilder innerClass, String indent,
			Term term, Map<String, Integer> variableIndices,
			Map<String, String> variableEquivalence) throws RetePatternBuildException
		{
		UsedVariables usedVariables = new UsedVariables();
		//TODO: some hacking to get the variables from the term and then to generate the usedVariables map
		Iterator<EObject> iter = term.eAllContents();
		while(iter.hasNext())
		{
			EObject obj = iter.next();
			if(obj instanceof VariableReference)
				{
				VariableReference varRef = (VariableReference) obj;
				String equivalantVariable = variableEquivalence.get(varRef.getVariable().getName());
				int variable_index = variableIndices.get(equivalantVariable);
				usedVariables.put(varRef.getVariable(), ValueKind.UNDEF_LITERAL);
				usedVariables.addAlternateNametoVariable(varRef.getVariable(), "tuple.get("+variable_index+")");
				}
		}

		try {
			innerClass.append(indent+ "return "+TermEvaluator.evaluate(term, usedVariables).getTerm().toString()+";\n");
		} catch (ViatraCompiledCompileTimeException e) {
			throw new RetePatternBuildException(
					"Error at a term check while building incremental pattern matcher for pattern {1}: {2}",
					new String[]{this.effort.getFqn(), e.getMessage()}, this.effort, e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.CodegenRecorderBuildable#genUnaryType(java.lang.Object)
	 */
	@Override
	public String genUnaryType(Object type) {
		if (type==null) return "null";
		EClassifier classifier = (EClassifier)type;
		EPackage ePackage = classifier.getEPackage();
		return "EPackage.Registry.INSTANCE.getEPackage(\""
				+ ePackage.getNsURI() +
				"\").getEClassifier(\""
				+ classifier.getName() +
				"\")";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.CodegenRecorderBuildable#genBinaryEdgeType(java.lang.Object)
	 */
	@Override
	public String genBinaryEdgeType(Object type) {
		if (type==null) return "null";
		EStructuralFeature feature = (EStructuralFeature)type;
		EClass containingClass = feature.getEContainingClass();
		EPackage ePackage = containingClass.getEPackage();
		// ((EClass) (EPackage.Registry.INSTANCE.getEPackage( ePackage.getNsURI() ).getEClassifier( containingClass.getName() ))).getEStructuralFeature( feature.getFeatureID() );
		return "((EClass) (EPackage.Registry.INSTANCE.getEPackage(\""
			+ ePackage.getNsURI() +
			"\").getEClassifier(\""
			+ containingClass.getName() +
			"\"))).getEStructuralFeature(\""
			+ feature.getName() +
			"\")";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.CodegenRecorderBuildable#genTernaryEdgeType(java.lang.Object)
	 */
	@Override
	public String genTernaryEdgeType(Object type) {
		throw new UnsupportedOperationException("EMF context does not support ternary edges");
	}

}
