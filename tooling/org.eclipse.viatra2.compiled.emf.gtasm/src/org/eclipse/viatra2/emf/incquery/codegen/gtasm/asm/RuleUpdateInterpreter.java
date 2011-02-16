package org.eclipse.viatra2.emf.incquery.codegen.gtasm.asm;



import org.eclipse.viatra2.emf.incquery.codegen.gtasm.GTASMCompiler;
import org.eclipse.viatra2.emf.incquery.codegen.term.SerializedTerm;
import org.eclipse.viatra2.emf.incquery.codegen.term.TermEvaluator;
import org.eclipse.viatra2.emf.incquery.codegen.term.exception.ViatraCompiledCompileTimeException;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.enums.ValueKind;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.ASMRuleInvocation;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.RuleUpdate;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.RuleUpdateASMFunction;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.RuleUpdateVariable;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.Term;


public class RuleUpdateInterpreter extends RuleInterpreter {

	//private static RuleUpdateInterpreter _instance = new RuleUpdateInterpreter();


	//public static RuleUpdateInterpreter getInstance() {
	//	return _instance;
	//}

	//private RuleUpdateInterpreter()
	//{
	//	;
	//}

	public static StringBuffer evalute(ASMRuleInvocation ruleToBeInterpreted) throws ViatraCompiledCompileTimeException
	{
		RuleUpdate ruleUpdate=(RuleUpdate)ruleToBeInterpreted;
		if(ruleUpdate instanceof RuleUpdateVariable)
		{
			SerializedTerm sTerm = TermEvaluator.evaluate(((RuleUpdateVariable)ruleUpdate).getValue(),
					GTASMCompiler.getInstance().getUsedVariables());

			ValueKind varType = GTASMCompiler.getInstance().getUsedVariables().get
			(((RuleUpdateVariable)ruleUpdate).getVariable().getVariable());
			//if the type of the term and the type of the variable are matching or possible to auto-cast
			if(varType.equals(sTerm.getType()) ||
					(varType.equals(ValueKind.STRING_LITERAL)) ||
					(varType.equals(ValueKind.DOUBLE_LITERAL) && sTerm.getTerm().equals(ValueKind.INTEGER_LITERAL)) ||
					(varType.equals(ValueKind.INTEGER_LITERAL) &&  sTerm.getTerm().equals(ValueKind.DOUBLE_LITERAL)))
				{
				sTerm.insert(((RuleUpdateVariable)ruleUpdate).getVariable().getVariable().getName()+ " = ");
				sTerm.append(";");
				return sTerm.getTerm();
				}
			else
				throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_VAR_TERM_TYPEMISMATCH+ varType.getName()+" "+sTerm.getType().getName(),ruleUpdate);
		}
		else if(ruleUpdate instanceof RuleUpdateASMFunction)
		{
			if(((RuleUpdateASMFunction)ruleUpdate).getLocations().size() != 1)
				throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_FUNCTION_PARAM ,ruleUpdate);

			Term locationTerm = ((RuleUpdateASMFunction)ruleUpdate).getLocations().get(0);
			SerializedTerm serializedLocation = TermEvaluator.evaluate(locationTerm,
					GTASMCompiler.getInstance().getUsedVariables());
			SerializedTerm serializedValue = TermEvaluator.evaluate(((RuleUpdateASMFunction)ruleUpdate).getValue(),
					GTASMCompiler.getInstance().getUsedVariables());


			serializedLocation.insert(((RuleUpdateASMFunction)ruleUpdate).getName()+".put(");
			serializedLocation.append(",");
			serializedLocation.append(serializedValue);
			serializedLocation.append(");");
			return serializedLocation.getTerm();
		}
		throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_RULE_UNIMPL,ruleUpdate);
	}
}
