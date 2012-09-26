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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.helpers;

import java.util.Collections;
import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.ITypeInfoProviderConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PSystem;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicdeferred.Equality;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicdeferred.Inequality;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicenumerables.TypeUnary;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;

/**
 * @author Bergmann Gábor
 *
 */
public class LayoutHelper {

	/**
	 * Unifies allVariables along equalities so that they can be handled as one.
	 * @param pSystem
	 */
	public static <PatternDescription, StubHandle, Collector> void unifyVariablesAlongEqualities(
			PSystem<PatternDescription, StubHandle, Collector> pSystem) 
	{
		Set<Equality> equals = pSystem.getConstraintsOfType(Equality.class);
		for (Equality<PatternDescription, StubHandle> equality : equals) {
			if (!equality.isMoot()) {
				equality.getWho().unifyInto(equality.getWithWhom());
			}
			// equality.delete();
		}
	}

	/**
	 * Eliminates weak inequalities if they are not substantiated.
	 * @param pSystem
	 */
	public static <PatternDescription, StubHandle, Collector> void eliminateWeakInequalities(
			PSystem<PatternDescription, StubHandle, Collector> pSystem) 
	{
		for (Inequality inequality : pSystem.getConstraintsOfType(Inequality.class)) inequality.eliminateWeak();
	}
	
	/**
	 * Eliminates all unary type constraints that are inferrable from other constraints.
	 */
	public static <PatternDescription, StubHandle, Collector> void eliminateInferrableUnaryTypes(
			final PSystem<PatternDescription, StubHandle, Collector> pSystem,
			IPatternMatcherContext<PatternDescription> context) 
	{
		Set<TypeUnary> constraintsOfType = pSystem.getConstraintsOfType(TypeUnary.class);
		for (TypeUnary<PatternDescription, StubHandle> typeUnary : constraintsOfType) {
			PVariable var = (PVariable) typeUnary.getVariablesTuple().get(0);
			Object expressedType = typeUnary.getTypeInfo(var);
			Set<ITypeInfoProviderConstraint> typeRestrictors = var.getReferringConstraintsOfType(ITypeInfoProviderConstraint.class);
			typeRestrictors.remove(typeUnary);
			for (ITypeInfoProviderConstraint iTypeRestriction : typeRestrictors) {
				Object typeInfo = iTypeRestriction.getTypeInfo(var);
				if (typeInfo != ITypeInfoProviderConstraint.TypeInfoSpecials.NO_TYPE_INFO_PROVIDED) {
					Set<Object> typeClosure = 
						TypeHelper.typeClosure(Collections.singleton(typeInfo), context);
					if (typeClosure.contains(expressedType)) {
						typeUnary.delete();								
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Verifies the sanity of all constraints. Should be issued as a preventive check before layouting.
	 * @param pSystem
	 * @throws RetePatternBuildException 
	 */
	public static <PatternDescription, StubHandle, Collector> void checkSanity(
			PSystem<PatternDescription, StubHandle, Collector> pSystem) throws RetePatternBuildException 
	{
		for (PConstraint pConstraint : pSystem.getConstraints()) pConstraint.checkSanity();
	}
	
	/**
	 * Finds an arbitrary constraint that is not enforced at the given stub.
	 * @param <PatternDescription>
	 * @param <StubHandle>
	 * @param <Collector>
	 * @param pSystem
	 * @param stub
	 * @return a PConstraint that is not enforced, if any, or null if all are enforced
	 */
	public static <PatternDescription, StubHandle, Collector> PConstraint getAnyUnenforcedConstraint(
			PSystem<PatternDescription, StubHandle, Collector> pSystem, 
			Stub<StubHandle> stub) 
	{
		Set<PConstraint> allEnforcedConstraints = stub.getAllEnforcedConstraints();
		Set<PConstraint> constraints = pSystem.getConstraints();
		for (PConstraint pConstraint : constraints) {
			if (!allEnforcedConstraints.contains(pConstraint)) return pConstraint;
		}
		return null;
	}

	
	/**
	 * Verifies whether all constraints are enforced and exported parameters are present.
	 * @param pSystem
	 * @param stub
	 * @throws RetePatternBuildException
	 */
	public static <PatternDescription, StubHandle, Collector> void finalCheck(
			final PSystem<PatternDescription, StubHandle, Collector> pSystem,
			Stub<StubHandle> stub) 
		throws RetePatternBuildException 
	{
		PConstraint unenforcedConstraint = getAnyUnenforcedConstraint(pSystem, stub);
		if (unenforcedConstraint != null) {
			throw new RetePatternBuildException(
					"Pattern matcher construction terminated without successfully enforcing constraint {1}." +
					" Could be caused if the value of some variables can not be deduced, e.g. by circularity of pattern constraints.", 
					new String[]{unenforcedConstraint.toString()}, 
					"Could not enforce a pattern constraint", null);
		}
		for (ExportedParameter<PatternDescription, StubHandle> export : 
			pSystem.getConstraintsOfType(ExportedParameter.class)) 
		{
			if (!export.isReadyAt(stub)) { 
				throw new RetePatternBuildException(
					"Exported pattern parameter {1} could not be deduced during pattern matcher construction." + 
					" A pattern constraint is required to positively deduce its value.", 
					new String[]{export.getParameterName().toString()}, 
					"Could not calculate pattern parameter", null);
			}
		}
	}


}
