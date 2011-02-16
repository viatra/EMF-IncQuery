package org.eclipse.viatra2.emf.incquery.codegen.gtasm.asm.template;

import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.enums.DirectionKind;

public class ActualParamData extends GTASMElementData{

	StringBuffer serialzedTerm;
	DirectionKind direction;
	
	public ActualParamData(StringBuffer term, DirectionKind direction) {
		this.serialzedTerm = term;
		this.direction = direction;
	}

	public StringBuffer getSerialzedTerm() {
		return serialzedTerm;
	}

	public DirectionKind getDirection() {
		return direction;
	}

}
