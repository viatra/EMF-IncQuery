package org.eclipse.viatra2.compiled.emf.gtasm.asm;

import org.eclipse.viatra2.compiled.emf.gtasm.GTASMCompiler;
import org.eclipse.viatra2.compiled.emf.gtasm.asm.template.ActualParamData;
import org.eclipse.viatra2.compiled.emf.gtasm.asm.template.AsmRuleInvocationData;
import org.eclipse.viatra2.compiled.emf.gtasm.asm.template.AsmRuleInvocationTemplate;
import org.eclipse.viatra2.compiled.emf.term.SerializedTerm;
import org.eclipse.viatra2.compiled.emf.term.TermEvaluator;
import org.eclipse.viatra2.compiled.emf.term.exception.ViatraCompiledCompileTimeException;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.compoundRules.IterateRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.enums.DirectionKind;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.enums.LogLevel;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.ASMRuleInvocation;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.CallRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.FailRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.LogRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.PrintLnRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.PrintRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.SkipRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.VariableReference;

public class BasicRuleInterpreter extends RuleInterpreter {



	public static StringBuffer evaluate(ASMRuleInvocation ruleToBeInterpreted) throws ViatraCompiledCompileTimeException
	{
		if(ruleToBeInterpreted instanceof SkipRule)
		{
			return new StringBuffer(";");
		}
		else if(ruleToBeInterpreted instanceof FailRule)
		{
			return new StringBuffer("throw new [RuleFailed]Exception();");
		}
		else if(ruleToBeInterpreted instanceof PrintRule)
		{
			SerializedTerm op1 = TermEvaluator.evaluate(((PrintRule)ruleToBeInterpreted).getOut(),
					GTASMCompiler.getInstance().getUsedVariables());
			op1.insert(0, "VPMUtil.print(");
			op1.append(");");
			return op1.getTerm();
		}
		else if(ruleToBeInterpreted instanceof PrintLnRule)
		{
			SerializedTerm op1 = TermEvaluator.evaluate(((PrintRule)ruleToBeInterpreted).getOut(),
					GTASMCompiler.getInstance().getUsedVariables());
			op1.insert(0, "VPMUtil.println(");
			op1.append(");");
			return op1.getTerm();

		}
		else if(ruleToBeInterpreted instanceof CallRule)
		{

			AsmRuleInvocationData aData = new AsmRuleInvocationData();
			aData.setName(((CallRule)ruleToBeInterpreted).getRule().getName());
			// Binding the symbolic variables. Need to evaluate them before running the actual rule.


			for (int i=0;i<((CallRule)ruleToBeInterpreted).getActualParameters().size();i++) {
				// If not an IN parameter, it must be a variable
				if(!((CallRule)ruleToBeInterpreted).getRule().getSymParameters().get(i).getDirection().equals(DirectionKind.IN_LITERAL))
				{
					if(!(((CallRule)ruleToBeInterpreted).getActualParameters().get(i) instanceof VariableReference))
							throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.PARAM_IN_INOUT_NOT_VARREF,ruleToBeInterpreted);

				}
				// Bind the symbolic variables
				SerializedTerm  evaluatedValue =TermEvaluator.
				evaluate(((CallRule)ruleToBeInterpreted).getActualParameters().get(i),
						GTASMCompiler.getInstance().getUsedVariables());

				// save the serialized input parameter terms
				aData.getParamMapping().add(new ActualParamData(  evaluatedValue.getTerm(),((CallRule)ruleToBeInterpreted).getRule().getSymParameters().get(i).getDirection()));

				//save the type of the input parameters
				GTASMCompiler.getInstance().getUsedVariables().put(
						((CallRule)ruleToBeInterpreted).getRule().getSymParameters().get(i).getVariable()
						,evaluatedValue.getType());

			}
			//Is the call rule invokes a rule of the current Machine
			if(GTASMCompiler.getInstance().getCurrentMachine().getMachine().equals(((CallRule)ruleToBeInterpreted).getRule().getNamespace()))
			{// the invoked rule is already evaluated does not needed to be again
				if(!GTASMCompiler.getInstance().getInvokedASMRules().containsKey(((CallRule)ruleToBeInterpreted).getRule()))
				{

					GTASMCompiler.getInstance().getInvokedASMRules().put(((CallRule)ruleToBeInterpreted).getRule(), null);
					StringBuffer ruleResult= RuleInterpreter.evaluate(((CallRule)ruleToBeInterpreted).getRule());
					GTASMCompiler.getInstance().getInvokedASMRules().put(((CallRule)ruleToBeInterpreted).getRule(), ruleResult);
				}
			}
			else //the called rule is in an other machine
			{
				//GTASMCompiler.getInstance().setCurrentMachine(((CallRule)ruleToBeInterpreted).getRule().getNamespace());
				//TODO have to parse it or check it before going forward
				throw new ViatraCompiledCompileTimeException("More then one Machine is involved in the serialization");
			}

			//TODO: If the rule failed, we have to restore the previous state. - so we don't have to write the values of the
			// variables back to the current execution environment, and we have to (?) restore the ASMFunctions too.

			return new StringBuffer(AsmRuleInvocationTemplate.create("\n").generate(aData));
			//return null;
		}
		else if(ruleToBeInterpreted instanceof IterateRule)
		{
			StringBuffer op1 = RuleInterpreter.evaluate(((IterateRule)ruleToBeInterpreted).getBody());

			op1.insert(0,"try{ \n while(true){\n");
			op1.append("}//end of while(true) \n }catch(RuleFailedException e) {}\n");

			return op1;
		}
		else if(ruleToBeInterpreted instanceof LogRule)
		{
			SerializedTerm msg = TermEvaluator.evaluate(((LogRule)ruleToBeInterpreted).getOut(),
					GTASMCompiler.getInstance().getUsedVariables());
			switch(((LogRule)ruleToBeInterpreted).getLevel().getValue())
			{
			case LogLevel.INFO:
				msg.insert(0, "VPMUtil.log(VPMUtil.INFO_LOGLEVEL,");
				msg.append(")");
				break;
			case LogLevel.DEBUG:
				msg.insert(0, "VPMUtil.log(VPMUtil.DEBUG_LOGLEVEL,");
				msg.append(")");
				break;
			case LogLevel.WARNING:
				msg.insert(0, "VPMUtil.log(VPMUtil.WARNING_LOGLEVEL,");
				msg.append(")");
				break;
			case LogLevel.ERROR:
				msg.insert(0, "VPMUtil.log(VPMUtil.ERROR_LOGLEVEL,");
				msg.append(")");
				break;
			case LogLevel.FATAL:
				msg.insert(0, "VPMUtil.log(VPMUtil.FATAL_LOGLEVEL,");
				msg.append(")");
				break;
			default:
				msg.insert(0, "VPMUtil.log(VPMUtil.FATAL_LOGLEVEL,");
				msg.append(")");
				break;
			}
			return msg.getTerm();
		}

		throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_RULE_UNIMPL,ruleToBeInterpreted);
		//return Boolean.FALSE;
	}



}
