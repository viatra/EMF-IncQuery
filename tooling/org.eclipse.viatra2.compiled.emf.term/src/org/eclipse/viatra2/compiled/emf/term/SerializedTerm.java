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

import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.enums.ValueKind;


/**
 * @author Akos Horvath
 *
 */
public class SerializedTerm {
	
	StringBuffer term;
	ValueKind type;
	
	
	public SerializedTerm(String s, ValueKind vk){
		this.term = new StringBuffer(s);
		this.type = vk;
	}
	
	public SerializedTerm(StringBuffer s, ValueKind vk){
		this.term = s;
		this.type = vk;
	}
	
	public SerializedTerm insert(int offset,String s){
		this.term.insert(offset, s);
		return this;
	}
	
	public SerializedTerm insert(String s){
		this.term.insert(0, s);
		return this;
	}

	public SerializedTerm append(String s){
		this.term.append(s);
		return this;
	}

	/**
	 * @return the type
	 */
	public ValueKind getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(ValueKind type) {
		this.type = type;
	}
	/**
	 * @return the value
	 */
	public StringBuffer getTerm() {
		return term;
	}
	/**
	 * @param value the value to set
	 */
	public void setTerm(String value) {
		this.term = new StringBuffer(value);
	}

	public SerializedTerm append(SerializedTerm op2) {
		this.term.append(op2.getTerm());
		return this;
	}

}
