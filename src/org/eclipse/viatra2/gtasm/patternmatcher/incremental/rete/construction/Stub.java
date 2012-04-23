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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;


/**
 * 
 * @author Bergmann Gábor
 *
 * @param <HandleType> the buildable-specific representation of RETE node handle this stub type will augment
 */
public class Stub<HandleType> {
	private HandleType handle;
	private Tuple variablesTuple;
	private Map<Object, Integer> variablesIndex;
	private Set<PConstraint> constraints;
	private Stub<HandleType> primaryParentStub;
	private Stub<HandleType> secondaryParentStub;

	private Stub(Map<Object, Integer> variablesIndex, Tuple variablesTuple, HandleType handle) {
		super();
		this.variablesIndex = variablesIndex;
		this.variablesTuple = variablesTuple;
		this.handle = handle;
		this.constraints = new HashSet<PConstraint>();
	}
	public Stub(Tuple variablesTuple, HandleType handle) {
		this(variablesTuple.invertIndex(), variablesTuple, handle);
	}
//	public Stub(Stub<HandleType> template) {
//		this(template.variablesIndex, template.variablesTuple, template.getHandle());
//	}	
	public Stub(Stub<HandleType> primaryParent, HandleType handle) {
		this(primaryParent.variablesIndex, primaryParent.variablesTuple, handle);
		this.primaryParentStub = primaryParent;
		constraints.addAll(primaryParent.getAllEnforcedConstraints());
	}	
	public Stub(Stub<HandleType> primaryParent, Tuple variablesTuple, HandleType handle) {
		this(variablesTuple.invertIndex(), variablesTuple, handle);
		this.primaryParentStub = primaryParent;
		constraints.addAll(primaryParent.getAllEnforcedConstraints());
	}	
	public Stub(Stub<HandleType> primaryParent, Stub<HandleType> secondaryParent, Tuple variablesTuple, HandleType handle) {
		this(variablesTuple.invertIndex(), variablesTuple, handle);
		this.primaryParentStub = primaryParent;
		this.secondaryParentStub = secondaryParent;
		constraints.addAll(primaryParent.getAllEnforcedConstraints());
		constraints.addAll(secondaryParent.getAllEnforcedConstraints());
	}	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Stub("+getVariablesTuple()+"@"+getHandle()+"|");
		for (PConstraint constraint : constraints) sb.append(constraint.toString() + "&");
		sb.append(")");
		return sb.toString();
	}
	/**
	 * @return the tuple of variables that define the schame emanating from the handle
	 */
	public Tuple getVariablesTuple() {
		return variablesTuple;
	}
	/**
	 * @return the handle of a RETE supplier node that hosts a certain relation (set of tuples)  
	 */
	public HandleType getHandle() {
		return handle;
	}
	/**
	 * @return the index of the variable within variablesTuple
	 */
	public Map<Object, Integer> getVariablesIndex() {
		return variablesIndex;
	}
	/**
	 * @return the set of variables involved
	 */
	public Set<Object> getVariablesSet() {
		return variablesIndex.keySet();
	}	
	/**
	 * @return all constraints already enforced at this handle
	 */
	public Set<PConstraint> getAllEnforcedConstraints() {
		return constraints;
	}
	/**
	 * @return the new constraints enforced at this handle, that aren't yet enforced at parents
	 */
	public Set<PConstraint> getDeltaEnforcedConstraints() {
		Set<PConstraint> result = new HashSet<PConstraint>(constraints);
		if (primaryParentStub != null) result.removeAll(primaryParentStub.getAllEnforcedConstraints());
		if (secondaryParentStub != null) result.removeAll(secondaryParentStub.getAllEnforcedConstraints());
		return result;
	}
	/**
	 * @return the constraints
	 */
	public void addConstraint(PConstraint constraint) {
		constraints.add(constraint);
	}
	/**
	 * @return the primaryParentStub
	 */
	public Stub<HandleType> getPrimaryParentStub() {
		return primaryParentStub;
	}
	/**
	 * @return the secondaryParentStub
	 */
	public Stub<HandleType> getSecondaryParentStub() {
		return secondaryParentStub;
	}	
	
}
