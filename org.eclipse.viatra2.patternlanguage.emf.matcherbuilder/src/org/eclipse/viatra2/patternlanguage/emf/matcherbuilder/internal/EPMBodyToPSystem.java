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

package org.eclipse.viatra2.patternlanguage.emf.matcherbuilder.internal;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Buildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PSystem;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicenumerables.TypeUnary;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.FlatTuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCompositionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassConstraint;
import org.eclipse.viatra2.patternlanguage.emf.matcherbuilder.runtime.PatternRegistry;

/**
 * @author Bergmann Gábor
 *
 */
public class EPMBodyToPSystem<StubHandle, Collector> {
	
	protected Pattern pattern;
	protected PatternBody body;
	protected IPatternMatcherContext<String> context;
	protected Buildable<String, StubHandle, Collector> buildable;
	
	protected PSystem<String, StubHandle, Collector> pSystem;

	/**
	 * @param pattern
	 * @param body
	 * @param builder
	 * @param buildable
	 */
	public EPMBodyToPSystem(Pattern pattern, PatternBody body,
			IPatternMatcherContext<String> context,
			Buildable<String, StubHandle, Collector> buildable) {
		super();
		this.pattern = pattern;
		this.body = body;
		this.context = context;
		this.buildable = buildable;
	}
	
	public PSystem<String, StubHandle, Collector> toPSystem() throws RetePatternBuildException {
		if (this.pSystem == null) {
			this.pSystem = new PSystem<String, StubHandle, Collector>(context, buildable, PatternRegistry.fqnOf(pattern));
			
			// TODO
//			preProcessAssignments();
			preProcessParameters();
			gatherBodyConstraints();
			
			gatherInequalityAssertions();
		}
		return pSystem;
	}

	public PVariable[] symbolicParameterArray() throws RetePatternBuildException {
		toPSystem();
		
		EList<Variable> symParameters = pattern.getParameters();
		int arity = symParameters.size();
		PVariable[] result = new PVariable[arity];
		for (int i=0; i<arity; ++i) result[i] = getPNode(symParameters.get(i));
		return result;
	}
	protected PVariable getPNode(String name) {
		return pSystem.getOrCreateVariableByName(name);
	}

	protected PVariable getPNode(Variable variable) {
		return getPNode(variable.getName());
	}
	protected PVariable getPNode(VariableReference variable) {
		return getPNode(variable.getVar());
	}
	protected Tuple getPNodeTuple(List<VariableReference> variables) {
		PVariable[] pNodeArray = getPNodeArray(variables);
		return new FlatTuple(pNodeArray);
	}
	public PVariable[] getPNodeArray(List<VariableReference> variables) {
		int k = 0;
		PVariable[] pNodeArray = new PVariable[variables.size()];
		for (VariableReference varRef : variables) {
			pNodeArray[k++] = getPNode(varRef);
		}
		return pNodeArray;
	}
	
	private void preProcessParameters() {
		EList<Variable> parameters = pattern.getParameters();
		for (Variable variable : parameters) {
			new ExportedParameter<String, StubHandle>(pSystem, getPNode(variable), variable.getName());
		}
	}
	private void gatherBodyConstraints() throws RetePatternBuildException {
		EList<Constraint> constraints = body.getConstraints();
		for (Constraint constraint : constraints) {
			if (constraint instanceof EClassConstraint) {
				EClassConstraint constraint2 = (EClassConstraint) constraint;
				EClass classname = ((ClassType)constraint2.getType()).getClassname();
				PVariable pNode = getPNode(constraint2.getVar());
				new TypeUnary<String, StubHandle>(pSystem, pNode, classname);
			}
			else if (constraint instanceof PatternCompositionConstraint) {
				PatternCompositionConstraint constraint2 = (PatternCompositionConstraint) constraint;
				Pattern patternRef = constraint2.getPatternRef();
				String fqnOf = PatternRegistry.fqnOf(patternRef);
				Tuple pNodeTuple = getPNodeTuple(constraint2.getParameters());
				if (constraint2.isNegative()) 
					new NegativePatternCall<String, StubHandle>(pSystem, pNodeTuple, fqnOf);
				else 
					new PositivePatternCall<String, StubHandle>(pSystem, pNodeTuple, fqnOf);
			// TODO OTHER CONSTRAINT TYPES
			} else {
				String fqnOf = PatternRegistry.fqnOf(pattern);
				throw new RetePatternBuildException(
						"Unsupported constraint type {1} in pattern {3}.", 
						new String[]{constraint.eClass().getName(), fqnOf}, 
						fqnOf);
			}
		}
	}
	private void gatherInequalityAssertions() {
		// TODO CHECK IF SHAREABLE, ETC.

		
	}

}
