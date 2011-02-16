package org.eclipse.viatra2.emf.incquery.codegen.gtasm.asm.template;

import java.util.ArrayList;
import java.util.List;


public class AsmRuleInvocationData extends GTASMElementData {

	public static int Id = 0;
	public int actualID;
	List<ActualParamData> parameterMapping;
	
	public AsmRuleInvocationData(){
		parameterMapping = new ArrayList<ActualParamData>();
		actualID = ++Id;
	}
	
	public List<ActualParamData> getParamMapping() {
		return parameterMapping;
	}

	public int getID() {
		// TODO Auto-generated method stub
		return actualID;
	}

}
