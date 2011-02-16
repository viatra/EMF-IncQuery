package org.eclipse.viatra2.compiled.emf.gtasm.asm;

import org.eclipse.viatra2.compiled.emf.gtasm.GTASMCompiler;
import org.eclipse.viatra2.compiled.emf.gtasm.asm.template.AsmConditionalIfData;
import org.eclipse.viatra2.compiled.emf.gtasm.asm.template.AsmConditionalIfTemplate;
import org.eclipse.viatra2.compiled.emf.gtasm.asm.template.AsmConditionalTryData;
import org.eclipse.viatra2.compiled.emf.term.SerializedTerm;
import org.eclipse.viatra2.compiled.emf.term.TermEvaluator;
import org.eclipse.viatra2.compiled.emf.term.exception.ViatraCompiledCompileTimeException;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.ASMRuleInvocation;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.ConditionalRuleIf;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.ConditionalRuleTry;

public class ConditionalRuleInterpreter extends RuleInterpreter {

	
	public static StringBuffer evaluate(ASMRuleInvocation ruleToBeInterpreted) throws ViatraCompiledCompileTimeException
	{
		
		/*
		 * This long line is a concise form of determining whether the rule is an if or a try type rule.
		 * In case it is an if type, the condition term is evaluated, in the other case, the rule is interpreted.
		 * Thereafter the appropriate rule is interpreted.
		 */

		if(ruleToBeInterpreted instanceof ConditionalRuleIf)
		{
			SerializedTerm termIf = TermEvaluator.evaluate(((ConditionalRuleIf) ruleToBeInterpreted).getExpressionToTest(),
					GTASMCompiler.getInstance().getUsedVariables());
			StringBuffer trueBranch = RuleInterpreter.evaluate(((ConditionalRuleIf) ruleToBeInterpreted).getRuleTrue());
			StringBuffer falseBranch = null; 
			if(((ConditionalRuleIf) ruleToBeInterpreted).getRuleFalse() != null)
				falseBranch = RuleInterpreter.evaluate(((ConditionalRuleIf) ruleToBeInterpreted).getRuleFalse());
			return new StringBuffer(AsmConditionalIfTemplate.create("\n").generate(
					new AsmConditionalIfData(termIf,falseBranch,trueBranch)));
		}
		else if(ruleToBeInterpreted instanceof ConditionalRuleTry)
		{
			StringBuffer tryBranch = RuleInterpreter.evaluate(((ConditionalRuleTry) ruleToBeInterpreted).getRuleToTry());
			StringBuffer elseBranch = null;
			if(((ConditionalRuleTry) ruleToBeInterpreted).getRuleElse() != null)
				elseBranch = RuleInterpreter.evaluate(((ConditionalRuleTry) ruleToBeInterpreted).getRuleElse());
			return new StringBuffer(AsmConditionalIfTemplate.create("\n")
					.generate(new AsmConditionalTryData(tryBranch,elseBranch)));
		}
		
		throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_RULE_UNIMPL,ruleToBeInterpreted);
	}
}
