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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Buildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicenumerables.ConstantValue;

/**
 * @author Bergmann Gábor
 *
 */
public class PSystem<PatternDescription, StubHandle, Collector> {
	private PatternDescription pattern;
	private IRetePatternBuilder<PatternDescription, StubHandle, Collector> builder;
	private Buildable<PatternDescription, StubHandle, Collector> buildable;

	private Set<PVariable> allVariables;
	private Set<PVariable> uniqueVariables;
	private Map<String, PVariable> variablesByName;
	private Set<PConstraint> constraints;
	private int nextVirtualNodeID;
	
	/**
	 * 
	 */
	public PSystem(
			IRetePatternBuilder<PatternDescription, StubHandle, Collector> builder, 
			Buildable<PatternDescription, StubHandle, Collector> buildable,
			PatternDescription pattern) 
	{
		super();
		this.pattern = pattern;
		this.builder = builder;
		this.buildable = buildable;
		allVariables = new HashSet<PVariable>();
		uniqueVariables = new HashSet<PVariable>();
		variablesByName = new HashMap<String, PVariable>();
		constraints = new HashSet<PConstraint>();
	}
	/**
	 * @return whether the submission of the new variable was successful
	 */
	private boolean addVariable(PVariable var) {
		String name = var.getName();
		if (!variablesByName.containsKey(name)) {
			allVariables.add(var);
			if (var.isUnique()) uniqueVariables.add(var);
			variablesByName.put(name, var);
			return true;
		} else {
			return false; 
		}
	}	
	/**
	 * Use this method to add a newly created constraint to the pSystem.
	 * @return whether the submission of the new constraint was successful
	 */
	boolean registerConstraint(PConstraint constraint) {
		return constraints.add(constraint);
	}	
	/**
	 * Use this method to remove an obsolete constraint from the pSystem.
	 * @return whether the removal of the constraint was successful
	 */
	boolean unregisterConstraint(PConstraint constraint) {
		return constraints.remove(constraint);
	}	
	
	@SuppressWarnings("unchecked")
	public <ConstraintType> Set<ConstraintType> getConstraintsOfType(Class<ConstraintType> constraintClass) {
		Set<ConstraintType> result = new HashSet<ConstraintType>();
		for (PConstraint pConstraint : constraints) {
			if (constraintClass.isInstance(pConstraint))
				result.add((ConstraintType) pConstraint);
		}
		return result;
	}
			
	public PVariable newVirtualVariable() {
		String name;
		do {
			name = ".virtual{" + nextVirtualNodeID++ + "}";
		} while (variablesByName.containsKey(name));
		PVariable var = new PVariable(this, name, true);
		addVariable(var);
		return var;
	}
	public PVariable newConstantVariable(Object value) {
		PVariable virtual = newVirtualVariable();
		new ConstantValue<PatternDescription, StubHandle>(this, virtual, value);
		return virtual;
	}
	
	/**
	 * @return the builder
	 */
	public IRetePatternBuilder<PatternDescription, StubHandle, Collector> getBuilder() {
		return builder;
	}
	/**
	 * @return the buildable
	 */
	public Buildable<PatternDescription, StubHandle, Collector> getBuildable() {
		return buildable;
	}	
	/**
	 * @return the allVariables
	 */
	public Set<PVariable> getAllVariables() {
		return allVariables;
	}
	/**
	 * @return the uniqueVariables
	 */
	public Set<PVariable> getUniqueVariables() {
		return uniqueVariables;
	}

	/**
	 * @return the variable by name
	 */
	private PVariable getVariableByName(String name) {
		return variablesByName.get(name).getUnifiedIntoRoot();
	}	
	/**
	 * @return the variable by name
	 */
	public PVariable getOrCreateVariableByName(String name) {
		if (!variablesByName.containsKey(name)) addVariable(new PVariable(this, name));
		return getVariableByName(name);
	}		
	/**
	 * @return the constraints
	 */
	public Set<PConstraint> getConstraints() {
		return constraints;
	}
	/**
	 * @return the pattern
	 */
	public PatternDescription getPattern() {
		return pattern;
	}
	/**
	 * @param pVariable
	 */
	void noLongerUnique(PVariable pVariable) {
		assert(!pVariable.isUnique());
		uniqueVariables.remove(pVariable);
	}

	
	

}
