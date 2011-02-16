package org.eclipse.viatra2.emf.incquery.codegen.gtasm.asm.template;

import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.enums.DirectionKind;

public class AsmRuleInvocationTemplate {

	protected static String nl;
	  public static synchronized AsmRuleInvocationTemplate create(String lineSeparator)
	  {
	    nl = lineSeparator;
	    AsmRuleInvocationTemplate result = new AsmRuleInvocationTemplate();
	    nl = null;
	    return result;
	  }

	  protected final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
	  protected final String TEXT_1 = "Array[Object] returnedValues_";
	  protected final String TEXT_2 = "(";
	  protected final String TEXT_3 = ",";
	  protected final String TEXT_4 = ");";
	  protected final String TEXT_5 = " = returnedValues_";
	  protected final String TEXT_6 = "[";
	  protected final String TEXT_7 = "];";
	  protected final String TEXT_8 = "";
	  
	  public String generate(Object argument)
	  {
	    final StringBuffer stringBuffer = new StringBuffer();
	    AsmRuleInvocationData data = (AsmRuleInvocationData) argument;
	    stringBuffer.append(TEXT_1);
	    stringBuffer.append(data.getID());
	    stringBuffer.append(" ");
	    stringBuffer.append(data.getName());
	    stringBuffer.append(TEXT_2);
	    for(int i= 0; i< data.getParamMapping().size(); i++){
	    	ActualParamData param = data.getParamMapping().get(i);
	    	if(!param.getDirection().equals(DirectionKind.OUT_LITERAL))
	    	{
	    		stringBuffer.append(param.getSerialzedTerm());
	    		if(i!= data.getParamMapping().size()-1)
	    			stringBuffer.append(TEXT_3);
	    	}
	    }
	    stringBuffer.append(TEXT_4);
	    //set the return values
	    int j = 0;
	    
	    for(int i= 0; i< data.getParamMapping().size(); i++){
	    	ActualParamData param = data.getParamMapping().get(i);
	    	if(!param.getDirection().equals(DirectionKind.IN_LITERAL))
	    	{
	    		stringBuffer.append(data.getParamMapping().get(i));
	    		stringBuffer.append(TEXT_5);
	    		stringBuffer.append(data.getID());
	    		stringBuffer.append(param.getSerialzedTerm());
	    		stringBuffer.append(TEXT_6);
	    		stringBuffer.append(j);
	    		stringBuffer.append(TEXT_7);
	    		stringBuffer.append(NL);
	    		j++;
	    	}
	    }
	    
	    return stringBuffer.toString();
	  }

}
