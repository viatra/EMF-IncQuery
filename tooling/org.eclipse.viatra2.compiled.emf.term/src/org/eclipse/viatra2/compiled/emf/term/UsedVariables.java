/*******************************************************************************
 * Copyright (c) 2004-2009 Akos Horvath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.compiled.emf.term;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.definitions.Variable;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.enums.ValueKind;

/**
 * @author Akos Horvath
 *
 */
public class UsedVariables extends HashMap<Variable, ValueKind> {

	private static final long serialVersionUID = -8686888965067097839L;
	Map<Variable,String> alternateNamesofVariables;

	public UsedVariables(){
		super();
		alternateNamesofVariables = null;
	}

	public UsedVariables(Map<Variable,String> alternateNamesofVariables){
		super();
		this.alternateNamesofVariables = alternateNamesofVariables;
	}

	public String getSerialzedNameofVariable(Variable variable){
		if(!this.containsKey(variable)) return "";

		if(alternateNamesofVariables != null)
			return alternateNamesofVariables.get(variable);
		else
			return variable.getName();
	}

	public void addAlternateNames(Map<Variable,String> alternateNamesofVariables){
		this.alternateNamesofVariables = alternateNamesofVariables;
	}

	public void addAlternateNametoVariable(Variable variable,String alternatename){
		if(alternateNamesofVariables == null)
			alternateNamesofVariables = new HashMap<Variable, String>();

		alternateNamesofVariables.put(variable, alternatename);
	}
}
