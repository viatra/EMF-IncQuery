/**
 * 
 */
package org.eclipse.viatra2.emf.incquery.codegen.gtasm.asm.template;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.definitions.Machine;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.definitions.Rule;

/**
 * @author akinator
 *
 */
public class AsmMachineData extends GTASMElementData{
	
	
//	HashMap<GTPattern,List<PatternInvocationContextData>> usedPatterns;
	Map<Rule,StringBuffer> invokedASMRules;
	Machine machine;
	
	public AsmMachineData(Machine m) {
		machine = m;
	//	usedPatterns = new HashMap<GTPattern, List<PatternInvocationContextData>>();
		invokedASMRules = new HashMap<Rule, StringBuffer>();
		// TODO Auto-generated constructor stub
	}
	
	/**A special Pattern adder method for [IF operation] contained invocations
	 * @param gtPatt
	 * @param signatures
	 * @param type
	 */
/*	public PatternInvocationContextData addUsedPattern(GTPattern gtPatt,PatternCallSignature[] signatures, PatternType type){
		
		PatternInvocationContextData ruleData = new PatternInvocationContextData();
		ruleData.setName(evaluateusedPatternName(gtPatt, signatures, type));
		ruleData.setPatternCallSignatures(signatures);
		ruleData.setType(type);
		if(usedPatterns.containsKey(gtPatt))
			if(!usedPatterns.get(gtPatt).contains(ruleData))
				usedPatterns.get(gtPatt).add(ruleData);
			else //returns the object already contained in the list!
				return usedPatterns.get(gtPatt).get(usedPatterns.get(gtPatt).indexOf(ruleData));
		else
		{
			ArrayList<PatternInvocationContextData> list = new ArrayList<PatternInvocationContextData>();
			list.add(ruleData);
			usedPatterns.put(gtPatt,list);
		}
		
		return ruleData;
	}
*/	
	
	/** Adds a new pattern context to the list if it is not already contained!
	 * @param gtPatt the invoked GTPattern
	 * @param patternParams The Serialized Pattern Parameters
	 * @param signatures pattern call signatures of the invocation
	 * @param quantificationOrder quantification order
	 * @param type Type of the invocation
	 * @return The object representing the context information
	 */
	/*public PatternInvocationContextData addUsedPattern( GTPattern gtPatt,
			SerializedTerm[] patternParams,PatternCallSignature[] signatures,Integer[] quantificationOrder, PatternType type){
		
		PatternInvocationContextData ruleData = new PatternInvocationContextData(gtPatt,patternParams,
				signatures,quantificationOrder,type,
				evaluateusedPatternName(gtPatt, signatures, type));
		
		//ruleData.setName(evaluateusedPatternName(gtPatt, signatures, type));
		//ruleData.setPattern(gtPatt);
		//ruleData.setPatternCallSignatures(signatures);
		//ruleData.setType(type);
		//ruleData.setQuantificationOrder(quantificationOrder);
		//ruleData.setPatternParams(patternParams);
		if(usedPatterns.containsKey(ruleData.getPattern()))
			{if(!usedPatterns.get(ruleData.getPattern()).contains(ruleData))
				usedPatterns.get(ruleData.getPattern()).add(ruleData);
		//TODO: Note that the ruleData already contained in the Set is returned (due to the ID!)	
		//else //returns the object already contained in the list!
				//return usedPatterns.get(gtPatt).get(usedPatterns.get(gtPatt).indexOf(ruleData));
			//	return ruleData;
			}
		else
		{
			ArrayList<PatternInvocationContextData> list = new ArrayList<PatternInvocationContextData>();
			list.add(ruleData);
			usedPatterns.put(ruleData.getPattern(),list);
		
		}
		return ruleData;
	}
	*/
	
/*	
	public static String evaluateusedPatternName(PatternInvocationContextData pic){
		return evaluateusedPatternName(pic.getPattern(), pic.getPatternCallSignatures(), pic.getType());
	}
	*/	
	
	
	/** evaulates the unique name of the pattern call
	 *  template: patternName+type_[ExecutionMode][ParameterMode][Containment]_...
	 *  [ExecutionMode] = S (Simple) or M (Multiple)
	 *  [ParameterMode] = I (Input) or O (Output)
	 *  [ContainmentMode] = I (IN) or B (Below) or D (Default, meaning there is no containment constraint on the element) 
	 * @param gtPatt the pattern 
	 * @param parameterData the signature of the call
	 * @param type type of the pattern call
	 * @return the unique name gfenerated from the input parameters
	 */
/*	public static String evaluateusedPatternName(GTPattern gtPatt,PatternCallSignature[] signatures,PatternType type){
		String pattName = gtPatt.getName()+type.toString();
		
		if(type.compareTo(PatternType.GT_EMBEDDED) == 0 || type.compareTo(PatternType.FORALL) == 0
				|| type.compareTo(PatternType.CHOOSE) == 0)
		{	for (PatternCallSignature signature : signatures) {
				//parameterSpearator
				pattName += "_";
				if(signature.getExecutionMode().compareTo(ExecutionMode.SINGLE_RESULT) == 0)
					pattName += "S";
				else
					pattName += "M";
				
				if(signature.getParameterMode().compareTo(ParameterMode.INPUT) == 0)
					pattName += "I";
				else
					pattName += "O";
				if(signature.getParameterScope().getContainmentMode().compareTo(Scope.BELOW) == 0 &&
						(!signature.getParameterScope().getParent().equals(Scope.DEFAULT_PARENT)) )
					{pattName += "B";}
				else
					{if(signature.getParameterScope().getContainmentMode().compareTo(Scope.IN) == 0)
						{pattName += "I";}
					else
						{pattName += "D";}
					}
			}
		}
		return pattName;
		
	}

*/

	/**
	 * @return the invokedASMRules
	 */
	public Map<Rule, StringBuffer> getInvokedASMRules() {
		return invokedASMRules;
	}



	/**
	 * @param invokedASMRules the invokedASMRules to set
	 */
	public void setInvokedASMRules(Map<Rule, StringBuffer> invokedASMRules) {
		this.invokedASMRules = invokedASMRules;
	}



	/**
	 * @return the machine
	 */
	public Machine getMachine() {
		return machine;
	}
	


	


	

}
