package org.eclipse.viatra2.emf.incquery.codegen.gtasm.asm;

import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra2.emf.incquery.codegen.gtasm.GTASMCompiler;
import org.eclipse.viatra2.emf.incquery.codegen.term.SerializedTerm;
import org.eclipse.viatra2.emf.incquery.codegen.term.TermEvaluator;
import org.eclipse.viatra2.emf.incquery.codegen.term.exception.ViatraCompiledCompileTimeException;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.compoundRules.BlockRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.compoundRules.ChooseRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.compoundRules.CollectionIteratorRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.compoundRules.ForallRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.compoundRules.LetRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.definitions.Variable;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.definitions.VariableDefinition;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.ASMRuleInvocation;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.GTPatternCall;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.Term;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.VariableReference;


public class BlockRuleInterpreter extends RuleInterpreter {


	public StringBuffer interpretRule(ASMRuleInvocation ruleToBeInterpreted) throws ViatraCompiledCompileTimeException
	{

		BlockRule blockRule=(BlockRule)ruleToBeInterpreted;
		if(blockRule instanceof LetRule)
		{
			LetRule letBlockRule = (LetRule)ruleToBeInterpreted;
			return interpretLetRule(letBlockRule);
		}
		else if(blockRule instanceof ForallRule)
		{
			ForallRule forallBlockRule = (ForallRule) blockRule;
			if (forallBlockRule.getGtrule()==null)
			{
				if(forallBlockRule.getCondition() instanceof GTPatternCall)
				{
//FORALL with GTPATTERN
					return interpretForallRulewithGTPattern( forallBlockRule);
				}
				else
				{
//FORALL with ASMFUNCTION
					return interpretForallRulewithASMFunction(forallBlockRule);
				}
			}
			else
			{
//FORALL with GTRULE
				return interpretForallRulewithGTRule( forallBlockRule);
			}
		}
		else if(blockRule instanceof ChooseRule)
		{
			ChooseRule chooseBlockRule = ((ChooseRule)blockRule);

			if (chooseBlockRule.getGtrule()==null) {
				if(chooseBlockRule.getCondition() instanceof GTPatternCall)
				{
//CHOOSE with GTPATTERN
					return interpretChooseRulewithGTPattern(chooseBlockRule);
				}
				else
				{
//CHOOSE with ASMFUNCTION
				return interpretChooseRulewithASMFunction(chooseBlockRule);
				}
			}
			else
			{
//CHOOSE with GTRULE
				return interpretChooseRulewithGTRule(chooseBlockRule);
			}
		}
		return null;
	}

	private StringBuffer interpretLetRule(LetRule letBlockRule) throws ViatraCompiledCompileTimeException{
		//type of the actually evaluated variable
		String elementType;
		// holds all let rule compilant
		StringBuffer letRules = new StringBuffer();
		//evaluate the variable definitions' values.
		for (Object variableDefinition : letBlockRule.getDefinitions()) {
			SerializedTerm termValue = TermEvaluator.evaluate(((VariableDefinition)variableDefinition).getValue(),
					GTASMCompiler.getInstance().getUsedVariables());
			GTASMCompiler.getInstance().getUsedVariables().put(((VariableDefinition)variableDefinition).getVariable(), termValue.getType());
			if(termValue.getType() != null)
				elementType = TermEvaluator.convertValueKindtoJavaType(termValue.getType());
			else
				elementType = "Object"; //it always has to be an Object

			termValue.insert(elementType+" "+((VariableDefinition)variableDefinition).getVariable().getName() + " = ");
			termValue.append(";\n");
			letRules.append(termValue.getTerm());
		}

		StringBuffer serializedBody = RuleInterpreter.evaluate(letBlockRule.getBody());
		serializedBody.insert(0, letRules);
		serializedBody.insert(0, "{");
		serializedBody.append("}\n");

		return serializedBody;
	}

	private StringBuffer interpretForallRulewithGTRule(ForallRule forallBlockRule) throws ViatraCompiledCompileTimeException{
	//	HashMap<Variable, ContainmentScope> parameterParameterScopes =getScopesFromContainmentConstraints(((ForallRule)ruleToBeInterpreted).getContainmentConstraints());

		//Get patternMatcher
//		try {
//			patternMatcher = getPatternMatcher(forallRuleExecutionEnvironment,
//					((GTPatternCall)((ForallRule)ruleToBeInterpreted).getCondition()).getCalledPattern());
//		} catch (PatternMatcherCompileTimeException e1) {
//			throw new GTASMCompiledException("Exception thrown when getting PatternMatcher:"+e1.getMessage(),ruleToBeInterpreted);
//		}
//
		// get matcher parameters && Update variables values from the matching
		//PatternInvocationContextData picd=getMatchParameters((ForallRule)ruleToBeInterpreted, parameterParameterScopes,PatternType.FORALL);

//		String forallString = AsmGTPatternwithForallTemplate.create("\n").generate(picd);
//		StringBuffer bodyRule = RuleInterpreter.evaluate(((ForallRule) blockRule).getBody());
//		bodyRule.insert(0, forallString);
//		bodyRule.append("\n} // end of forall rule with gt pattern "+ picd.getName()+"\n");
		return null;
	}

	private StringBuffer interpretForallRulewithASMFunction(ForallRule forallBlockRule) throws ViatraCompiledCompileTimeException{
		return null;
	}


	private StringBuffer interpretForallRulewithGTPattern(ForallRule forallBlockRule) throws ViatraCompiledCompileTimeException{
		return null;
	}


	private StringBuffer interpretChooseRulewithGTPattern(
				ChooseRule chooseBlockRule) throws ViatraCompiledCompileTimeException {
		return null;
	}

	private StringBuffer interpretChooseRulewithASMFunction(ChooseRule chooseBlockRule) throws ViatraCompiledCompileTimeException{
		return null;
	}

	private StringBuffer interpretChooseRulewithGTRule(ChooseRule chooseBlockRule ) throws ViatraCompiledCompileTimeException{
		return null;
	}

//	public static HashMap<Variable,ContainmentScope> getScopesFromContainmentConstraints(EList containmentConstraints) throws GTASMCompiledException
//	{
//		HashMap<Variable,ContainmentScope> parameterParameterScopes = new HashMap<Variable,ContainmentScope>();
//		for (Object containmentConstraint : containmentConstraints) {
//			//TODO correct Scopes of nonexistent references!!!!!!!
//			if(((ContainmentConstraint)containmentConstraint).getParent()!=null)
//			{
//				SerializedTerm parent = TermEvaluator.evaluate(((ContainmentConstraint)containmentConstraint).getParent());
//						if(parent.getType().equals(ValueKind.MODELELEMENT_LITERAL))
//						{
//							parameterParameterScopes.put(((ContainmentConstraint)containmentConstraint).getVariable(),
//								new ContainmentScope(((ContainmentConstraint)containmentConstraint).getMode().getValue(),
//										parent));
//						}
//						else
//						{
//							// ContainmentScope parent=UNDEF, the element the ContainmentScope references does not exist
//							throw new GTASMCompiledException(GTASMCompiledException.ASM_SCOPE_NOT_EXIST,(ContainmentConstraint)containmentConstraint);
//						}
//			}
//			else
//				parameterParameterScopes.put(((ContainmentConstraint)containmentConstraint).getVariable(), new ContainmentScope());
//		}
//		return parameterParameterScopes;
//	}

/*	public IPatternMatcher getPatternMatcher(ExecutionEnvironment executionEnvironment, GTPattern gtPattern) throws PatternMatcherCompileTimeException
	{
		IPatternMatcher patternMatcher=null;
		if(PatternMatchers.getInstance().containsKey(gtPattern))
			patternMatcher=PatternMatchers.getInstance().get(gtPattern);
			// We had it precompiled
		else
		{
			// We need to make a new patternmatcher
			// TODO An ItemHandler is needed
            TermHandler handler = new BasicTermHandler();
            PatternBuilder patternBuilder = new PatternBuilder(executionEnvironment.getFramework().getLogger(),executionEnvironment.getFramework().getTopmodel().getModelManager(), handler);

			patternMatcher = patternBuilder.construct(gtPattern);
			PatternMatchers.getInstance().put(gtPattern, patternMatcher);


		}
		return patternMatcher;
	}
*/

/*	private boolean evaulateDoPartofGTRuleInvocation(GTRule gtRule
			, Object[] match
			, BlockRule blockRule
			, ExecutionEnvironment blockRuleExecutionEnvironment)
	throws GTASMCompiledException
	{


	// the block rule does not have a do part
	if(blockRule.getBody() == null)
		return Boolean.TRUE;



	GTRuleInvocation gtRuleInvocation = (GTRuleInvocation)((CollectionIteratorRule) blockRule).getGtrule();

		for (int i=0;i<gtRule.getSymParameters().size();i++) {
			//If it is an input Parameter then it can not be changed
//			if(((SymbolicRuleParameter)gtRule.getSymParameters().get(i)).getDirection().equals(DirectionKind.IN_LITERAL) )
//			{
//				//TODO: have to check that an input parameter is not changed during the execution of the GTRule
//				if(!match[i]
//						.equals(
//								blockRuleExecutionEnvironment.getTermEvaluator().evaluate(blockRuleExecutionEnvironment
//										,(Term)((ChooseRule)blockRule).getGtrule().getActualParameters().get(i))) )
//				{
//					GTASMCompiledException e = new GTASMCompiledException(ErrorStrings.GTIN_OUTDIFFERENCE,(GTASMElement)((ChooseRule)blockRule).getGtrule().getActualParameters().get(i));
//					e.addNewStackElement(blockRule);
//					throw e;
//				}
//
//			}
//			else
			//INOUT or OUT parameter has to be changed
//			if(!((SymbolicRuleParameter)gtRule.getSymParameters().get(i)).getDirection().equals(DirectionKind.IN_LITERAL))
//			{
			try {
				blockRuleExecutionEnvironment.
					setVariableValue(
							((VariableReference)(gtRuleInvocation).getActualParameters().get(i)).getVariable(),
							match[i]);
				} catch (GTASMCompiledException e) {
					throw new GTASMCompiledException(ErrorStrings.RULE_INIT_VAR+e.getMessage(),blockRule);
				}
//			}
		}


			return blockRuleExecutionEnvironment.getRuleInterpreter().interpretRule(blockRuleExecutionEnvironment,blockRule.getBody());

	}


	*/

//	public IGTRuleMatcher getGtRuleMatcher(GTExecutionEnviroment gtEnviroment,GTRule gtRule ) throws GTASMCompiledException{
//		TermHandler handlerr = new BasicTermHandler();
//		GTRuleBuilder gtBuilder = new GTRuleBuilder(gtEnviroment.getFramework().getLogger(), gtEnviroment.getFramework().getTopmodel().getModelManager(), handlerr);
//		return gtBuilder.construct(gtRule, gtEnviroment);
//	}
//





/*	private PatternInvocationContextData getGTRuleMatchParameters(CollectionIteratorRule ruleToBeInterpreted
			, Map<Variable,ContainmentScope> parameterParameterScopes
			, GTRule gtRule
			, GTRuleInvocation invocation)
	throws GTASMCompiledException
	{

		Object[] patternParams = new Object[invocation.getActualParameters().size()];
		Integer[] quantificationOrder=new Integer[invocation.getActualParameters().size()];
		PatternCallSignature[] patternCallSignatures=new PatternCallSignature[invocation.getActualParameters().size()];
//		quantification order in case of forall rule
		int outerVariableCounterForQuantificationOrder=((CollectionIteratorRule) ruleToBeInterpreted).getLocalVariables().size();
		int localVariableCounterForQuantificationOrder=0;


		for (int i = 0 ; i< invocation.getActualParameters().size(); i++) {
			Object ActualTerm = invocation.getActualParameters().get(i);
			SymbolicRuleParameter symParam = (SymbolicRuleParameter) gtRule.getSymParameters().get(i);

			PatternCallSignature pcs=new PatternCallSignature();
			// the value of the actual parameter
			patternParams[i] = executionEnvironment.getTermEvaluator().evaluate(executionEnvironment , (Term)ActualTerm);

			//Its a variable type element
			if((Term)ActualTerm instanceof VariableReference
					// the CollectionIterator rule's actual parameter
					&& ((CollectionIteratorRule)ruleToBeInterpreted).getLocalVariables().contains(((VariableReference)ActualTerm).getVariable()))
				{
					if( ruleToBeInterpreted instanceof ForallRule)
						pcs.setExecutionMode(ExecutionMode.MULTIPLE_RESULTS);
					else
						pcs.setExecutionMode(ExecutionMode.SINGLE_RESULT);
				pcs.setParameterMode(ParameterMode.OUTPUT);	//the variable is quantified by the BlockRule
				}
			else
				{
				pcs.setExecutionMode(ExecutionMode.SINGLE_RESULT);
				}

			// Input / Output parameter handling

			if(symParam.getDirection().equals(DirectionKind.IN_LITERAL))	//INPUT
				{	if((patternParams[i]==null
							|| ValueKind.UNDEF_LITERAL.equals(patternParams[i])))
						{
						if(pcs.getParameterMode() == null) // Its is a quantificated variable if paramterMode != null
							{	GTASMCompiledException e  = new GTASMCompiledException(symParam.getVariable().getName()+ErrorStrings.GTINPUT_IN+gtRule.getName()+" gt rule.",gtRule);
								throw e.addNewStackElement(ruleToBeInterpreted);
							}
						}
					else
						{
							pcs.setParameterMode(ParameterMode.INPUT);
						}
				}
			else
			if(symParam.getDirection().equals(DirectionKind.OUT_LITERAL))		//OUTPUT
				{	if((patternParams[i]==null
							|| ValueKind.UNDEF_LITERAL.equals(patternParams[i])))
						{
						pcs.setParameterMode(ParameterMode.OUTPUT);
						}
					else
						{
						if(pcs.getParameterMode() == null) // Its is a quantificated variable if paramterMode != null
							{	GTASMCompiledException e = new GTASMCompiledException(symParam.getVariable().getName()+ErrorStrings.GTINPUT_OUT+gtRule.getName()+" gt rule.",gtRule);
								throw e.addNewStackElement(ruleToBeInterpreted);
							}
						}
				}
			else
			if(symParam.getDirection().equals(DirectionKind.INOUT_LITERAL))		//INOUT
					{
					// if it is an undef variable then it is only an output variable!!
				//TODO: have to ask is it possible to change the value if it is a ModelElement!!
					if((patternParams[i]==null
						|| ValueKind.UNDEF_LITERAL.equals(patternParams[i])))
						pcs.setParameterMode(ParameterMode.OUTPUT);
					else
						pcs.setParameterMode(ParameterMode.INPUT);
					}

			//ContainmentScope of the variables
			if(ActualTerm instanceof VariableReference) // varibale type element can be in the ContainmentScope order
			{
				if(parameterParameterScopes.containsKey(((VariableReference)ActualTerm).getVariable()))
					pcs.setParameterScope(parameterParameterScopes.get(((VariableReference)ActualTerm).getVariable()));
				else
					pcs.setParameterScope(new ContainmentScope());
			}
			else	//nonde varibaleReference type input parameter
			{
				pcs.setParameterScope(new ContainmentScope());
			}




			if(ruleToBeInterpreted instanceof ForallRule)
				if(ActualTerm instanceof VariableReference)
					{
						if((((CollectionIteratorRule) ruleToBeInterpreted).getLocalVariables().indexOf(((VariableReference)ActualTerm).getVariable()))==-1)
						{
							// an error occured, there is a variable which is quanitificated by the BlockRule but not used in the Gt rule
							if(invocation.getActualParameters().size() == outerVariableCounterForQuantificationOrder)
								getNoneUsedVariable(ruleToBeInterpreted);


							quantificationOrder[outerVariableCounterForQuantificationOrder]=i;
							outerVariableCounterForQuantificationOrder++;
						}
						else
						{
						quantificationOrder[(((CollectionIteratorRule) ruleToBeInterpreted).getLocalVariables().indexOf(((VariableReference)ActualTerm).getVariable()))]= i;
						localVariableCounterForQuantificationOrder++;
						}
					}
				else
					{
					quantificationOrder[outerVariableCounterForQuantificationOrder]=i;
					outerVariableCounterForQuantificationOrder++;
					}


		// the signature is assigned to the array
		patternCallSignatures[i]=pcs;

		}

	return new PatternInvocationContextData((SerializedTerm[]) patternParams,patternCallSignatures,quantificationOrder);

	}
	*/
	private static void getNoneUsedVariable(CollectionIteratorRule ruleToBeInterpreted)
	throws ViatraCompiledCompileTimeException
		{

	//Called from a gtPattern or a gtRule
		String name = ruleToBeInterpreted.getCondition() instanceof GTPatternCall?
				((GTPatternCall)ruleToBeInterpreted.getCondition()).getCalledPattern().getName()
				:ruleToBeInterpreted.getGtrule().getRule().getName();

		EList<Term> actualParamater = ruleToBeInterpreted.getCondition() instanceof GTPatternCall?
				((GTPatternCall)ruleToBeInterpreted.getCondition()).getActualParameters()
				:ruleToBeInterpreted.getGtrule().getActualParameters();



		for(int j=0; j< ruleToBeInterpreted.getLocalVariables().size(); j++)
		{// search for the errorful variable
		Variable locVar = ruleToBeInterpreted.getLocalVariables().get(j);
		boolean usedInRule = false;
		for(int k = 0; k < actualParamater.size(); k++)
			{//if it is used in the gtRule then the usedInRule is set to true
			if(actualParamater.get(k) instanceof VariableReference
					&& ((VariableReference)actualParamater.get(k)).getVariable().equals(locVar))
				usedInRule = true;
			}
		if(!usedInRule) //we have the errorful variable
			throw new ViatraCompiledCompileTimeException(locVar.getName() + ViatraCompiledCompileTimeException.AMS_BLOCKRULE_PARAMS_NOT_USED
					+ name,ruleToBeInterpreted);
		}

		throw new ViatraCompiledCompileTimeException("FATAL error during interpreting the blockrule with" +name ,ruleToBeInterpreted);
		}



//	private static StringBuffer generateOutputParemeters(BlockRule blockRule, PatternInvocationContextData picd) {
//
//		StringBuffer outputParameters = new StringBuffer();
//		for (int i=0;i<((GTPatternCall)((ForallRule) blockRule).getCondition()).getActualParameters().size();i++)
//		{
//			// OUTPUT checking
//			if(picd.getPatternCallSignatures()[i].getParameterMode().equals(ParameterMode.OUTPUT))
//			{
//
//				outputParameters.append(((VariableReference)((GTPatternCall)((ForallRule) blockRule).getCondition()).getActualParameters().get(i)).getVariable());
//			}
//		}
//
//		return outputParameters;
//		}




//	private static   PatternInvocationContextData getMatchParameters(
//			 CollectionIteratorRule ruleToBeInterpreted
//			, Map<Variable,ContainmentScope> parameterScopes,
//			PatternType type)
//	throws GTASMCompiledException
//	{
//
//
//		SerializedTerm[] patternParams = new SerializedTerm[((GTPatternCall)((CollectionIteratorRule)ruleToBeInterpreted).getCondition()).getActualParameters().size()];
//		Integer[] quantificationOrder=new Integer[((GTPatternCall)((CollectionIteratorRule)ruleToBeInterpreted).getCondition()).getActualParameters().size()];
//		PatternCallSignature[] patternCallSignatures=new PatternCallSignature[((GTPatternCall)((CollectionIteratorRule)ruleToBeInterpreted).getCondition()).getActualParameters().size()];
//
//		int outerVariableCounterForQuantificationOrder=((CollectionIteratorRule) ruleToBeInterpreted).getLocalVariables().size();
//		int localVariableCounterForQuantificationOrder=0;
//
//	//for (Object term : ((GTPatternCall)((CollectionIteratorRule)ruleToBeInterpreted).getCondition()).getActualParameters()) {
//
//		for (int i = 0 ; i< ((GTPatternCall)((CollectionIteratorRule)ruleToBeInterpreted).getCondition()).getActualParameters().size(); i++) {
//			Object ActualTerm = ((GTPatternCall)((CollectionIteratorRule)ruleToBeInterpreted).getCondition()).getActualParameters().get(i);
//
//
//			PatternCallSignature pcs=new PatternCallSignature();
//
//
//
//			if((Term)ActualTerm instanceof VariableReference
//					&& ruleToBeInterpreted instanceof ForallRule
//					&& ((CollectionIteratorRule)ruleToBeInterpreted).getLocalVariables().contains(((VariableReference)ActualTerm).getVariable())) pcs.setExecutionMode(ExecutionMode.MULTIPLE_RESULTS);
//			else pcs.setExecutionMode(ExecutionMode.SINGLE_RESULT);
//
//			//The mode of the parameter
//			//if it is not in the localVariables of the ruleTOBeinterpreted then it is an input parameter
//			//Scope information: if it is quantified -> OUTPUT else INPUT
//			if((Term)ActualTerm instanceof VariableReference
//					&& (!((CollectionIteratorRule)ruleToBeInterpreted).getLocalVariables().contains(((VariableReference)ActualTerm).getVariable())))
//				{pcs.setParameterMode(ParameterMode.INPUT);
//				patternParams[i] =TermEvaluator.evaluate((Term)ActualTerm);
//				}
//			else
//				{
//				if(ActualTerm instanceof VariableReference)
//					{
//					//the variable is initialized
//					GTASMCompiler.getInstance().getUsedVariables().put(((VariableReference)ActualTerm).getVariable(), ValueKind.MODELELEMENT_LITERAL);
//					SerializedTerm termValue = TermEvaluator.evaluate((Term)ActualTerm);
//					patternParams[i] =TermEvaluator.evaluate((Term)ActualTerm);
//					pcs.setParameterMode(ParameterMode.OUTPUT);}
//				else
//					throw new GTASMCompiledException(GTASMCompiledException.ASM_NONVARIABLE_OUPUT);
//				}
//
//
//
//			EList actualParamater = ((CollectionIteratorRule)ruleToBeInterpreted).getCondition() instanceof GTPatternCall?
//					((GTPatternCall)((CollectionIteratorRule)ruleToBeInterpreted).getCondition()).getActualParameters()
//					:((CollectionIteratorRule)ruleToBeInterpreted).getGtrule().getActualParameters();
//
//
//
//
////			for(int j=0; j< ruleToBeInterpreted.getLocalVariables().size(); j++)
////				{// search for the errorful variable
////				Variable locVar = (Variable) ruleToBeInterpreted.getLocalVariables().get(j);
////				boolean usedInRule = false;
////						if(ActualTerm instanceof VariableReference
////								&& ((VariableReference)ActualTerm).getVariable().equals(locVar))
////							usedInRule = true;
////
////				if(!usedInRule) //we have the errorful variable
////					pcs.setParameterMode(ParameterMode.OUTPUT);
////				else
////					pcs.setParameterMode(ParameterMode.INPUT);
////				}
//
//
//
//			//pcs.setParameterMode((patternParams[i]==null||ValueKind.UNDEF_LITERAL.equals(patternParams[i]))?ParameterMode.OUTPUT:ParameterMode.INPUT);
//
//			// Get ContainmentScope if local variable, get Below root if outer
//			if(ActualTerm instanceof VariableReference)
//			{
//				if(parameterScopes.containsKey(((VariableReference)ActualTerm).getVariable()))
//					pcs.setParameterScope(parameterScopes.get(((VariableReference)ActualTerm).getVariable()));
//				else
//					pcs.setParameterScope((new ContainmentScope()));
//			}
//			else
//			{
//				pcs.setParameterScope((new ContainmentScope()));
//			}
//
//			patternCallSignatures[i]=pcs;
//
//
////			quantification order in case of forall rule
//			if(ruleToBeInterpreted instanceof ForallRule)
//				if(ActualTerm instanceof VariableReference)
//				{
//					if((((CollectionIteratorRule) ruleToBeInterpreted).getLocalVariables().indexOf(((VariableReference)ActualTerm).getVariable()))==-1)
//					{
//						//TODO: pasztor: have to debug that it is working correctly!
////						 an error occured, there is a variable which is quanitificated by the BlockRule but not used in the Gt rule. Throws GTAsmException
//						if(((GTPatternCall)((CollectionIteratorRule)ruleToBeInterpreted).getCondition()).getActualParameters().size() == outerVariableCounterForQuantificationOrder)
//							getNoneUsedVariable(ruleToBeInterpreted);
//
//						quantificationOrder[outerVariableCounterForQuantificationOrder]=i;
//						outerVariableCounterForQuantificationOrder++;
//					}
//					else
//					{
//					quantificationOrder[(((CollectionIteratorRule) ruleToBeInterpreted).getLocalVariables().indexOf(((VariableReference)ActualTerm).getVariable()))]= i;
//					localVariableCounterForQuantificationOrder++;
//					}
//				}
//			else
//				{
//				quantificationOrder[outerVariableCounterForQuantificationOrder]=i;
//				outerVariableCounterForQuantificationOrder++;
//				}
//		}// end of for cycle
//
//		return 	GTASMCompiler.getInstance().getCurrentMachine().addUsedPattern(((GTPatternCall)((CollectionIteratorRule)ruleToBeInterpreted).getCondition()).getCalledPattern(),
//				patternParams,patternCallSignatures,quantificationOrder, type);
//	}

//
//	private ExecutionEnvironment getNewExecutionEnvironment(ExecutionEnvironment executionEnvironment,CollectionIteratorRule ruleToBeInterpreted)
//	{
//		// Needs a new Exec.env. the new Variables will be added.
//		ExecutionEnvironment forallRuleExecutionEnvironment = new BlockRuleExecutionEnvironment(executionEnvironment.getFramework(),ruleToBeInterpreted.getBody());
//		Map<Variable, Object> variables = new Hashtable<Variable, Object>(executionEnvironment.getVariableValues());
//		for (Object variable : ruleToBeInterpreted.getLocalVariables()) {
//			variables.put((Variable)variable, ValueKind.UNDEF_LITERAL);
//		}
//		forallRuleExecutionEnvironment.onBegin(variables);
//		return forallRuleExecutionEnvironment;
//	}
}
