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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.ITypeInfoProviderConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;

/**
 * @author Bergmann Gábor
 *
 */
public class TypeHelper {

	/**
	 * Infers type information for the given variable, based on the given constraints. 
	 * Generalization and subsumptions are not taken into account. 
	 * @param pVariable the variable to infer types for
	 * @param constraints the set of constraints to extract type info from
	 */
	public static Set<Object> inferTypes(PVariable pVariable, Set<PConstraint> constraints) {
		Set<Object> inferredTypes = new HashSet<Object>();
		for (PConstraint pConstraint : constraints) {
			if (pConstraint instanceof ITypeInfoProviderConstraint) {
				Object typeInfo = ((ITypeInfoProviderConstraint) pConstraint).getTypeInfo(pVariable);
				if (typeInfo != ITypeInfoProviderConstraint.TypeInfoSpecials.NO_TYPE_INFO_PROVIDED) 
					inferredTypes.add(typeInfo);
			}
		}
		return inferredTypes;
	}

	/**
	 * Calculates the closure of a set of types, with respect to supertyping.
	 * @return the set of all types in typesToClose and all their direct and indirect supertypes
	 */
	public static Set<Object> typeClosure(Set<Object> typesToClose, IPatternMatcherContext<?> context) {
		Set<Object> closure = new HashSet<Object>(typesToClose);
		Set<Object> delta = closure;
		while(!delta.isEmpty()) {
			Set<Object> newTypes = new HashSet<Object>();
			for (Object deltaType : delta) {
				if (deltaType instanceof ITypeInfoProviderConstraint.TypeInfoSpecials) continue;
				if (context.isUnaryType(deltaType)) newTypes.add(ITypeInfoProviderConstraint.TypeInfoSpecials.ANY_UNARY);
				if (context.isTernaryEdgeType(deltaType)) newTypes.add(ITypeInfoProviderConstraint.TypeInfoSpecials.ANY_TERNARY);
				Collection<? extends Object> directSupertypes = context.enumerateDirectSupertypes(deltaType);
				newTypes.addAll(directSupertypes);
			}
			newTypes.removeAll(closure);
			delta = newTypes;
			closure.addAll(delta);
		}
		return closure;
	}

	/**
	 * Calculates a remainder set of types from a larger set, that are not subsumed by a given set of subsuming types.
	 * @param subsumableTypes a set of types from which some may be implied by the subsming types
	 * @param subsumingTypes a set of types that may imply some of the subsming types
	 * @return  the collection of types in subsumableTypes that are NOT identical to or supertypes of any type in subsumingTypes.
	 */
	public static Set<Object> subsumeTypes(
			Set<Object> subsumableTypes,
			Set<Object> subsumingTypes, 
			IPatternMatcherContext<?> context) 
	{
		Set<Object> closure = typeClosure(subsumingTypes, context);
		Set<Object> subsumed = new HashSet<Object>(subsumableTypes);
		subsumed.removeAll(closure);
		return subsumed;
	}

}
