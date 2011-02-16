package org.eclipse.viatra2.emf.incquery.codegen.gtasm.asm;

import org.eclipse.viatra2.emf.incquery.codegen.term.exception.ViatraCompiledCompileTimeException;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.compoundRules.BlockRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.compoundRules.NestedRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.definitions.Rule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.ASMRuleInvocation;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.ConditionalRuleIf;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.ConditionalRuleTry;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.ModelManipulationRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.RuleUpdate;


public class RuleInterpreter {

		//protected ExecutionEnvironment executionEnvironment;
		
		
		
		public static StringBuffer evaluate(Rule ruleToBeInterpreted) throws ViatraCompiledCompileTimeException
		{
					return evaluate(ruleToBeInterpreted.getBody());
		}
		
		public static StringBuffer evaluate(ASMRuleInvocation ruleToBeInterpreted) throws ViatraCompiledCompileTimeException
		{
			 	if(ruleToBeInterpreted instanceof NestedRule)
				{
					return NestedRuleInterpreter.evaluate(ruleToBeInterpreted);			
				}
				else if(ruleToBeInterpreted instanceof ConditionalRuleIf || ruleToBeInterpreted instanceof ConditionalRuleTry)
				{
					return ConditionalRuleInterpreter.evaluate(ruleToBeInterpreted);
				}
				else if(ruleToBeInterpreted instanceof BlockRule)
				{
					return BlockRuleInterpreter.evaluate(ruleToBeInterpreted);
				}
				else if(ruleToBeInterpreted instanceof ModelManipulationRule)
				{
					return ModelManipulationRuleInterpreter.evaluate(ruleToBeInterpreted);
				}
				else if(ruleToBeInterpreted instanceof RuleUpdate)
				{
					return RuleUpdateInterpreter.evalute(ruleToBeInterpreted);
				}
				else
				{
					return BasicRuleInterpreter.evaluate(ruleToBeInterpreted);
				}
		}
}
