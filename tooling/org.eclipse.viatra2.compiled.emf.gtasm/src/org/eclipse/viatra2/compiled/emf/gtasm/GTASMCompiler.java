/**
 * 
 */
package org.eclipse.viatra2.compiled.emf.gtasm;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.viatra2.compiled.emf.gtasm.asm.template.AsmMachineData;
import org.eclipse.viatra2.compiled.emf.term.UsedVariables;
import org.eclipse.viatra2.compiled.emf.term.exception.ViatraCompiledCompileTimeException;
import org.eclipse.viatra2.core.IModelManager;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.definitions.Machine;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.definitions.Rule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.definitions.Variable;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.enums.ValueKind;
import org.eclipse.viatra2.logger.Logger;

/**
 * @author akinator
 *
 */
public class GTASMCompiler {
	
	
	String vpmPrefix,packagePrefix, filename;
	UsedVariables usedVariables;
	
	//The machines used during the rule interpretation 
	Map<Machine,AsmMachineData> usedMachines;
	
	//currently parsed machine
	AsmMachineData currentMachine;
	
	Logger log;
	
	public static GTASMCompiler _instance = new GTASMCompiler();
	
	public GTASMCompiler() {
		usedVariables = new UsedVariables();
		usedMachines = new HashMap<Machine,AsmMachineData>();	
	}
	
	
	public void operate(IModelManager manager) {
		
		//TODO: Test only!!
//		try{
//			Set<Entry<GTPattern, List<PatternInvocationContextData>>>  entries = currentMachine.getUsedPatterns().entrySet();
//			for(Entry<GTPattern, List<PatternInvocationContextData>> patternEntry: entries){
//				EMFPatternMatcher mp = new EMFPatternMatcher(patternEntry.getKey(), log, manager, null);
//				FlattenedPattern fp = mp.getFlattenedPatterns()[0];
//				Collection<SearchPlanOperation> sp;
//			
//					sp = fp.generateCoreSearchPlan(patternEntry.getValue().get(0).getAdornemnt(), manager);
//				
//				PatternVisitor pv = new PatternVisitor(sp.iterator(), patternEntry.getValue().get(0));
//				
//				System.out.println(pv.nextStep());
//			}		
//		} catch (PatternMatcherRuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (PatternMatcherCompileTimeException e) {
//			// TODO: handle exception
//		}
	}
	
	
	public static GTASMCompiler getInstance(){
		return _instance;
	}
	
	
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	/**
	 * @return the packagePrefix
	 */
	public String getPackagePrefix() {
		return packagePrefix;
	}
	/**
	 * @param packagePrefix the packagePrefix to set
	 */
	public void setPackagePrefix(String packagePrefix) {
		this.packagePrefix = packagePrefix;
	}
	/**
	 * @return the usedVariables
	 */
	public UsedVariables getUsedVariables() {
		return usedVariables;
	}
	/**
	 * @param usedVariables the usedVariables to set
	 */
	public void setUsedVariables(UsedVariables usedVariables) {
		this.usedVariables = usedVariables;
	}
	/**
	 * @return the vpmPrefix
	 */
	public String getVpmPrefix() {
		return vpmPrefix;
	}
	/**
	 * @param vpmPrefix the vpmPrefix to set
	 */
	public void setVpmPrefix(String vpmPrefix) {
		this.vpmPrefix = vpmPrefix;
	}


	public ValueKind getVariableType(Variable variable) throws ViatraCompiledCompileTimeException{
		
		if(usedVariables.containsKey(variable))
			return usedVariables.get(variable);	
		else
			throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.TERM_VARIABLE_NOTINIT,variable);
			
	}


	/**
	 * @return the currentMachine
	 */
	public AsmMachineData getCurrentMachine() {
		return currentMachine;
	}


	/**
	 * @param currentMachine the currentMachine to set
	 */
	public void setCurrentMachine(AsmMachineData currentMachine) {
		
		if(usedMachines.containsKey(currentMachine.getMachine()))
			this.currentMachine = usedMachines.get(currentMachine.getMachine());
			else
		{this.currentMachine = currentMachine;
		 usedMachines.put(currentMachine.getMachine(), currentMachine);
		}
	}


	/**
	 * @return the usedMachines
	 */
	public Map<Machine, AsmMachineData> getUsedMachines() {
		return usedMachines;
	}


	/**
	 * @param usedMachines the usedMachines to set
	 */
	public void setUsedMachines(Map<Machine, AsmMachineData> usedMachines) {
		this.usedMachines = usedMachines;
	}


	public Map<Rule, StringBuffer> getInvokedASMRules() {
		return currentMachine.getInvokedASMRules();
	}

	
}
