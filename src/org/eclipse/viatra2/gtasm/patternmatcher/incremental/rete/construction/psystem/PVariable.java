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
	/**
	 * The name of the pattern variable. This is the unique key of the pattern node.
	 */
	private String name;
	/**
	 * virtual pVariables are nodes that do not correspond to actual pattern
	 * variables; they represent constants or Term substitutes
	 */
	private boolean virtual;
	/**
	 * whether this is an exported symbolic parameter
	 */
	private boolean exportedParameter;	
	
	/**
	 * Set of constraints that mention this variable
	 */
	private Set<PConstraint> referringConstraints;
	
	
	public PVariable(String name) {
		this(name, false);
	}

	public PVariable(String name, boolean virtual) {
		super();
		this.name = name;
		this.virtual = virtual;
		this.exportedParameter = false;
		this.referringConstraints = new HashSet<PConstraint>();
	}
	
	/**
	 * Register that this variable is referred by the given constraint.
	 * @param constraint
	 */
	public void refer(PConstraint constraint) {
		referringConstraints.add(constraint);
	}
	
	/**
	 * @return the name of the pattern variable. This is the unique key of the pattern node.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the virtual
	 */
	public boolean isVirtual() {
		return virtual;
	}

	/**
	 * @return the exportedParameter
	 */
	public boolean isExportedParameter() {
		return exportedParameter;
	}

	/**
	 * @param exportedParameter the exportedParameter to set
	 */
	public void setExportedParameter(boolean exportedParameter) {
		this.exportedParameter = exportedParameter;
	}

	/**
	 * @return the referringConstraints
	 */
	public Set<PConstraint> getReferringConstraints() {
		return referringConstraints;
	}	

	@Override
	public String toString() {
		return name;// + ":PatternNode";
	}

	
}
