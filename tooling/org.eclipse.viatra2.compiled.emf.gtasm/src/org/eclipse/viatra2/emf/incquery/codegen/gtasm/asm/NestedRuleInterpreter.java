package org.eclipse.viatra2.emf.incquery.codegen.gtasm.asm;

import org.eclipse.viatra2.emf.incquery.codegen.term.exception.ViatraCompiledCompileTimeException;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.compoundRules.NestedRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.compoundRules.ParallelRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.compoundRules.RandomRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.compoundRules.SequentialRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.ASMRuleInvocation;

public class NestedRuleInterpreter extends RuleInterpreter {

	private static NestedRuleInterpreter _instance = new NestedRuleInterpreter();


	public static NestedRuleInterpreter getInstance() {
		return _instance;
	}

	public static  StringBuffer evaluate(ASMRuleInvocation ruleToBeInterpreted)
		throws ViatraCompiledCompileTimeException{

		NestedRule nestedRule = (NestedRule)ruleToBeInterpreted;
		StringBuffer rules = new StringBuffer();

		//the parallel rule are mapped to sequential rule
		if(nestedRule instanceof SequentialRule || nestedRule instanceof ParallelRule)
		{
			for(int i=0;i<((SequentialRule)nestedRule).getSubrules().size();i++)
			{
				rules.append(RuleInterpreter.evaluate(((SequentialRule)nestedRule).getSubrules().get(i)));
				rules.append("\n");

			}

			return rules;
		}
		else if(nestedRule instanceof RandomRule)
		{
			int maxNumber= (int)Math.round(Math.random()*(((RandomRule)nestedRule).getSubrules().size()-1));
			StringBuffer randomRules = new StringBuffer();
			randomRules.append("randomValue = randomGenerator.nextInt("+maxNumber+");\n");
			randomRules.append("switch(randomValue)\n {");

			for(int i=0; i<=maxNumber;i++){
				randomRules.append("case "+i+" :\n");
				randomRules.append(RuleInterpreter.evaluate(((SequentialRule)nestedRule).getSubrules().get(i)));
				randomRules.append("break;\n");
				}
			randomRules.append("default : throw new [TYPE]Exception(\"Wrong random number in random rule\"); \n}");
			return randomRules;
		}

		throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_NESTED_RULE_UNIMP,nestedRule);

	}

}
