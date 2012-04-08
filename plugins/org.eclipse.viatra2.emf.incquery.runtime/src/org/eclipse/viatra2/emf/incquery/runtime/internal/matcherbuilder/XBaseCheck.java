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

package org.eclipse.viatra2.emf.incquery.runtime.internal.matcherbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra2.emf.incquery.runtime.IncQueryRuntimePlugin;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicdeferred.BaseTypeSafePredicateCheck;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.FlatTuple;
import org.eclipse.viatra2.patternlanguage.core.naming.PatternNameProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.XExpression;

/**
 * XExpression check constraint: the given XExpression formed over the variables must evaluate to true.
 * @author Bergmann Gábor
 *
 */
public class XBaseCheck<StubHandle> extends BaseTypeSafePredicateCheck<Pattern, StubHandle> {

	private final XExpression xExpression;
	private final EPMBodyToPSystem<StubHandle, ?> pGraph;
	private final IQualifiedNameProvider nameProvider =
			IncQueryRuntimePlugin.getDefault().getInjector().getInstance(PatternNameProvider.class);
//	private final IExpressionInterpreter interpreter = 
//			IncQueryRuntimePlugin.getDefault().getInjector().getInstance(IExpressionInterpreter.class);

	
	/**
	 * @param pSystem
	 * @param affectedVariables
	 */
	public XBaseCheck(EPMBodyToPSystem<StubHandle, ?> pGraph, XExpression xExpression) {
		super(pGraph.pSystem, getExternalPNodeReferencesOfXExpression(pGraph, xExpression));
		this.pGraph = pGraph;
		this.xExpression = xExpression;
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.DeferredPConstraint#doCheckOn(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub)
	 */
	@Override
	protected Stub<StubHandle> doCheckOn(Stub<StubHandle> stub)
			throws RetePatternBuildException {
		Set<Integer> affectedIndices = new HashSet<Integer>();
		Map<QualifiedName, Integer> qualifiedNameMap = new HashMap<QualifiedName, Integer>();
		Set<Variable> variables = getExternalPatternVariableReferencesOfXExpression(xExpression);
		for (Variable variable : variables) {
			PVariable pNode = pGraph.getPNode(variable);
			Integer position = stub.getVariablesIndex().get(pNode);
			qualifiedNameMap.put(nameProvider.getFullyQualifiedName(variable), position);
			affectedIndices.add(position);
		}
		int[] indices = new int[affectedIndices.size()];
		int k=0;
		for (Integer index : affectedIndices) indices[k++] = index;
		
//		Map<String, String> variableEquivalence = new HashMap<String, String>();
//		Set<String> variableNames = extractAffectedVariableNames(pGraph, topTerm);
//		for (String name : variableNames) {
//			PVariable pNode = pGraph.getPNode(name);
//			variableEquivalence.put(name, pNode.getName());
//			variableIndices.put(pNode.getName(), stub.getVariablesIndex().get(pNode));
//		}
//		return 
//				gtBuildable.buildGTASMTermChecker(topTerm, variableIndices, variableEquivalence, null, stub);
		XBaseEvaluator evaluator = new XBaseEvaluator(xExpression, qualifiedNameMap);
		return buildable.buildPredicateChecker(evaluator, null, indices, stub);
		
	}

	private static Set<PVariable> getExternalPNodeReferencesOfXExpression(
			EPMBodyToPSystem<?, ?> pGraph, 
			XExpression xExpression) {
		Set<PVariable> result = new HashSet<PVariable>();
		Set<Variable> variables = getExternalPatternVariableReferencesOfXExpression(xExpression);
		for (Variable variable : variables) {
			result.add(pGraph.getPNode(variable));
		}
		return result;
	}

	private static Set<Variable> getExternalPatternVariableReferencesOfXExpression(
			XExpression xExpression) {
		Set<Variable> result = new HashSet<Variable>();
		TreeIterator<EObject> eAllContents = xExpression.eAllContents();
		while (eAllContents.hasNext()) {
			EList<EObject> eCrossReferences = eAllContents.next().eCrossReferences();
			for (EObject eObject : eCrossReferences) {
				if (eObject instanceof Variable && !EcoreUtil.isAncestor(xExpression, eObject)) {
					result.add((Variable)eObject);
				}
			}
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.BasePConstraint#toStringRest()
	 */
	@Override
	protected String toStringRest() {
		return new FlatTuple(new ArrayList<PVariable>(getAffectedVariables()).toArray()).toString() + "|=" + xExpression.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.BasePConstraint#doReplaceVariable(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable)
	 */
	@Override
	protected void doReplaceVariable(PVariable obsolete, PVariable replacement) {}

}
