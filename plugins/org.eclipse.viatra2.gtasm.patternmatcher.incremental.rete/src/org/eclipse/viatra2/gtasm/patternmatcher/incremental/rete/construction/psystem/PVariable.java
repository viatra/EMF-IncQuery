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

import java.util.HashSet;
import java.util.Set;

/**
 * @author Bergmann Gábor
 *
 */
public class PVariable {
	private PSystem<?,?,?> pSystem;
	/**
	 * The name of the pattern variable. This is the unique key of the pattern node.
	 */
	private String name;
	/**
	 * virtual pVariables are nodes that do not correspond to actual pattern
	 * variables; they represent constants or Term substitutes
	 */
	private boolean virtual;
//	/**
//	 * whether this is an exported symbolic parameter
//	 */
//	private boolean exportedParameter;	
	
	/**
	 * Set of constraints that mention this variable
	 */
	private Set<PConstraint> referringConstraints;
	
	/**
	 * Determines whether there are any constraints that can deduce this variable
	 */	
	private Boolean deducable;
	
	/**
	 * Another PVariable this variable has been unified into. 
	 * Please use the other variable instead of this.
	 * Null iff this is still a first-class variable.
	 */
	private PVariable unifiedInto;
	
	
	
	PVariable(PSystem<?,?,?> pSystem, String name) {
		this(pSystem, name, false);
	}

	PVariable(PSystem<?,?,?> pSystem, String name, boolean virtual) {
		super();
		this.pSystem = pSystem;
		this.name = name;
		this.virtual = virtual;
		//this.exportedParameter = false;
		this.referringConstraints = new HashSet<PConstraint>();
		this.unifiedInto = null;
		this.deducable = false;
	}
	
	/**
	 * Replaces this variable with a given other, resulting in their unification.
	 * This variable will no longer be unique.
	 * @param constraint
	 */
	public void unifyInto(PVariable replacement) {
		replacementCheck();
		replacement = replacement.getUnifiedIntoRoot();
		//replacement.referringConstraints.addAll(this.referringConstraints);
		//replacement.exportedParameter |= this.exportedParameter;
		replacement.virtual &= this.virtual;
		if (replacement.deducable != null && this.deducable != null)
			replacement.deducable |= this.deducable;
		else 
			replacement.deducable = null;
		Set<PConstraint> snapshotConstraints = // avoid ConcurrentModificationX
			new HashSet<PConstraint>(this.referringConstraints);
		for (PConstraint constraint : snapshotConstraints) {
			constraint.replaceVariable(this, replacement);
		}
		// replacementCheck() will fail from this point
		this.unifiedInto = replacement;
		pSystem.noLongerUnique(this);
	}
	
	/**
	 * Determines whether there are any constraints that can deduce this variable
	 * @return
	 */
	public boolean isDeducable() {
		replacementCheck();
		if (deducable == null) {
			deducable = false;
			for (PConstraint pConstraint : getReferringConstraints()) {
				if (pConstraint.getDeducedVariables().contains(this)) {
					deducable = true;
					break;
				}
			}
		}
		return deducable;
	}

	/**
	 * Register that this variable is referred by the given constraint.
	 * @param constraint
	 */
	public void refer(PConstraint constraint) {
		replacementCheck();
		deducable = null;
		referringConstraints.add(constraint);
	}
	
	/**
	 * Register that this variable is no longer referred by the given constraint.
	 * @param constraint
	 */
	public void unrefer(PConstraint constraint) {
		replacementCheck();
		deducable = null;
		referringConstraints.remove(constraint);
	}
	
	
	/**
	 * @return the name of the pattern variable. This is the unique key of the pattern node.
	 */
	public String getName() {
		replacementCheck();
		return name;
	}

	/**
	 * @return the virtual
	 */
	public boolean isVirtual() {
		replacementCheck();
		return virtual;
	}

	
//	/**
//	 * @return the exportedParameter
//	 */
//	public boolean isExportedParameter() {
//		replacementCheck();
//		return exportedParameter;
//	}
//
//	/**
//	 * @param exportedParameter the exportedParameter to set
//	 */
//	public void setExportedParameter(boolean exportedParameter) {
//		replacementCheck();
//		this.exportedParameter = exportedParameter;
//	}

	/**
	 * @return the referringConstraints
	 */
	public Set<PConstraint> getReferringConstraints() {
		replacementCheck();
		return referringConstraints;
	}	
	
	@SuppressWarnings("unchecked")
	public <ConstraintType> Set<ConstraintType> getReferringConstraintsOfType(Class<ConstraintType> constraintClass) {
		replacementCheck();
		Set<ConstraintType> result = new HashSet<ConstraintType>();
		for (PConstraint pConstraint : referringConstraints) {
			if (constraintClass.isInstance(pConstraint))
				result.add((ConstraintType) pConstraint);
		}
		return result;
	}
	

	@Override
	public String toString() {
		// replacementCheck();
		return name;// + ":PatternNode";
	}
	
	public PVariable getDirectUnifiedInto() {
		return unifiedInto;
	}
	public PVariable getUnifiedIntoRoot() {
		PVariable nextUnified = unifiedInto;
		PVariable oldUnifiedInto = this;
		while (nextUnified != null) {
			oldUnifiedInto = nextUnified;
			nextUnified = oldUnifiedInto.getDirectUnifiedInto();
		} 
		return oldUnifiedInto; //unifiedInto;
	}
	public boolean isUnique() {
		return unifiedInto == null;
	}
	private void replacementCheck() {
		if (unifiedInto != null)
			throw new IllegalStateException("Illegal usage of variable " + name + " which has been replaced with " + unifiedInto.name);
	}
	
}
