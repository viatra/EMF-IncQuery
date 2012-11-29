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

package org.eclipse.incquery.runtime.internal.matcherbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;
import org.eclipse.incquery.runtime.rete.construction.Stub;
import org.eclipse.incquery.runtime.rete.construction.psystem.PVariable;
import org.eclipse.incquery.runtime.rete.construction.psystem.basicdeferred.BaseTypeSafePredicateCheck;
import org.eclipse.incquery.runtime.rete.tuple.FlatTuple;
import org.eclipse.xtext.xbase.XExpression;

/**
 * XExpression check constraint: the given XExpression formed over the variables
 * must evaluate to true.
 * 
 * @author Bergmann Gábor
 * 
 */
public class XBaseCheck<StubHandle> extends
		BaseTypeSafePredicateCheck<Pattern, StubHandle> {

	private final XExpression xExpression;
	private final EPMBodyToPSystem<StubHandle, ?> pGraph;
	private final Pattern pattern;

	// private final IQualifiedNameProvider nameProvider;
	// private final IExpressionInterpreter interpreter = 
	// IncQueryRuntimePlugin.getDefault().getInjector().getInstance(IExpressionInterpreter.class);

	/**
	 * @param pSystem
	 * @param affectedVariables
	 */
	public XBaseCheck(EPMBodyToPSystem<StubHandle, ?> pGraph,
			XExpression xExpression, Pattern pattern) {
		super(pGraph.pSystem, getExternalPNodeReferencesOfXExpression(pGraph,
				xExpression));
		this.pGraph = pGraph;
		this.xExpression = xExpression;
		this.pattern = pattern;

		// Injector injector = XtextInjectorProvider.INSTANCE.getInjector();
		// nameProvider = injector.getInstance(IQualifiedNameProvider.class);
	}

	@Override
	protected Stub<StubHandle> doCheckOn(Stub<StubHandle> stub)
			throws RetePatternBuildException {
		Set<Integer> affectedIndices = new HashSet<Integer>();
		// Map<QualifiedName, Integer> qualifiedNameMap = new
		// HashMap<QualifiedName, Integer>();
		Map<String, Integer> tupleNameMap = new HashMap<String, Integer>();
		Set<Variable> variables = CorePatternLanguageHelper
				.getReferencedPatternVariablesOfXExpression(xExpression);
		for (Variable variable : variables) {
			PVariable pNode = pGraph.getPNode(variable);
			Integer position = stub.getVariablesIndex().get(pNode);
			// qualifiedNameMap.put(
			// QualifiedName.create(variable.getSimpleName()), position);
			tupleNameMap.put(variable.getSimpleName(), position);
			affectedIndices.add(position);
		}
		int[] indices = new int[affectedIndices.size()];
		int k = 0;
		for (Integer index : affectedIndices)
			indices[k++] = index;

		// Map<String, String> variableEquivalence = new HashMap<String,
		// String>();
		// Set<String> variableNames = extractAffectedVariableNames(pGraph,
		// topTerm);
		// for (String name : variableNames) {
		// PVariable pNode = pGraph.getPNode(name);
		// variableEquivalence.put(name, pNode.getName());
		// variableIndices.put(pNode.getName(),
		// stub.getVariablesIndex().get(pNode));
		// }
		// return
		// gtBuildable.buildGTASMTermChecker(topTerm, variableIndices,
		// variableEquivalence, null, stub);
		XBaseEvaluator evaluator = new XBaseEvaluator(xExpression,
				tupleNameMap, pattern);
		return buildable.buildPredicateChecker(evaluator, null, indices, stub);

	}

	private static Set<PVariable> getExternalPNodeReferencesOfXExpression(
			EPMBodyToPSystem<?, ?> pGraph, XExpression xExpression) {
		Set<PVariable> result = new HashSet<PVariable>();
		Set<Variable> variables = CorePatternLanguageHelper
				.getReferencedPatternVariablesOfXExpression(xExpression);
		for (Variable variable : variables) {
			result.add(pGraph.getPNode(variable));
		}
		return result;
	}

	@Override
	protected String toStringRest() {
		return new FlatTuple(
				new ArrayList<PVariable>(getAffectedVariables()).toArray())
				.toString()
				+ "|=" + xExpression.toString();
	}

	@Override
	protected void doReplaceVariable(PVariable obsolete, PVariable replacement) {
	}

}
