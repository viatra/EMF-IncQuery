package org.eclipse.viatra2.emf.incquery.codegen.gtasm.asm;

import org.eclipse.viatra2.emf.incquery.codegen.gtasm.GTASMCompiler;
import org.eclipse.viatra2.emf.incquery.codegen.term.SerializedTerm;
import org.eclipse.viatra2.emf.incquery.codegen.term.TermEvaluator;
import org.eclipse.viatra2.emf.incquery.codegen.term.exception.ViatraCompiledCompileTimeException;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.enums.ValueKind;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.simpleRules.ASMRuleInvocation;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.VariableReference;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.copymove.ModelCopyRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.copymove.MoveRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.creation.CreateInstanceOf;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.creation.CreateSupertypeOf;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.creation.ElementCreateRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.creation.EntityCreateRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.creation.RelationCreateRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.deletion.DeleteInstanceOf;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.deletion.DeleteSupertypeOf;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.deletion.ElementDeleteRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.update.RenameRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.update.SetAggregationRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.update.SetAnySourceRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.update.SetAnyTargetRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.update.SetInverseRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.update.SetMultiplicityRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.update.SetRelationFrom;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.update.SetRelationTo;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.update.SetRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.modelmanagement.manipulationRules.update.SetValueRule;


public class ModelManipulationRuleInterpreter extends RuleInterpreter {

	
 /**
 * Model Manipulation rules.
 * 
 * @author: akinator
 */
	
	public static StringBuffer evaluate(ASMRuleInvocation ruleToBeInterpreted) throws ViatraCompiledCompileTimeException
	{
		
		
/*******************SET RULES****************************/		
		if(ruleToBeInterpreted instanceof RenameRule)
		{
			
			SerializedTerm newName= TermEvaluator.evaluate(((SetRule)ruleToBeInterpreted).getValue(),
					GTASMCompiler.getInstance().getUsedVariables());
			SerializedTerm modelElement=TermEvaluator.evaluate(((SetRule)ruleToBeInterpreted).getElement(),
					GTASMCompiler.getInstance().getUsedVariables());
			
			modelElement.append(".sSet("+modelElement.getTerm() +".eClass().getEStructuralFeature(\"name\"),");
			modelElement.append(newName);
			modelElement.append(");\n");
			return modelElement.getTerm();
				
		}
		else if(ruleToBeInterpreted instanceof SetValueRule)
		{
			//What to do if the EObject does not have a value EAttribute or multiplicity is greater than 1
			SerializedTerm newName= TermEvaluator.evaluate(((SetRule)ruleToBeInterpreted).getValue(),
					GTASMCompiler.getInstance().getUsedVariables());
			SerializedTerm modelElement=TermEvaluator.evaluate(((SetRule)ruleToBeInterpreted).getElement(),
					GTASMCompiler.getInstance().getUsedVariables());
			
			modelElement.append(".sSet("+modelElement.getTerm() +".eClass().getEStructuralFeature(\"value\"),");
			modelElement.append(newName);
			modelElement.append(");\n");
			return modelElement.getTerm();
			
		}
		//TODO: which is which parameter in the syntax??
		else if(ruleToBeInterpreted instanceof SetRelationTo)
		{
			SerializedTerm relation = TermEvaluator.evaluate(
					((SetRule)ruleToBeInterpreted).getElement(),
					GTASMCompiler.getInstance().getUsedVariables());
			relation.insert("(IRelationWrapper)");
			
			SerializedTerm modelElement=TermEvaluator.evaluate(
					((SetRule)ruleToBeInterpreted).getValue(),
					GTASMCompiler.getInstance().getUsedVariables());
			
			relation.append(".setTarget(");
			relation.append(modelElement);
			relation.append(");\n");
						
			return relation.getTerm();
			
		}
		else if(ruleToBeInterpreted instanceof SetRelationFrom)
		{
			SerializedTerm relation = TermEvaluator.evaluate(
					((SetRule)ruleToBeInterpreted).getElement(),
					GTASMCompiler.getInstance().getUsedVariables());
			relation.insert("(IRelationWrapper)");
			
			SerializedTerm modelElement=TermEvaluator.evaluate(
					((SetRule)ruleToBeInterpreted).getValue(),
					GTASMCompiler.getInstance().getUsedVariables());
			
			relation.append(".setSource(");
			relation.append(modelElement);
			relation.append(");\n");
						
			return relation.getTerm();
			
		}
		else if(ruleToBeInterpreted instanceof SetMultiplicityRule)
		{
			throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_MODELMAN_UNIMP, ruleToBeInterpreted);
		}
		else if(ruleToBeInterpreted instanceof SetInverseRule)
		{
			throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_MODELMAN_UNIMP, ruleToBeInterpreted);
		}
		else if(ruleToBeInterpreted instanceof SetAnyTargetRule)
		{
			throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_MODELMAN_UNIMP, ruleToBeInterpreted);
		}
		else if(ruleToBeInterpreted instanceof SetAnySourceRule)
		{
			throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_MODELMAN_UNIMP, ruleToBeInterpreted);
		}
		else if(ruleToBeInterpreted instanceof SetAggregationRule)
		{
			throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_MODELMAN_UNIMP, ruleToBeInterpreted);
		}
		
	
/*************************CREATE RULES****************************/	
		else if(ruleToBeInterpreted instanceof CreateSupertypeOf)
		{
			throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_MODELMAN_UNIMP, ruleToBeInterpreted);
		}
		else if(ruleToBeInterpreted instanceof CreateInstanceOf)
		{
			throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_MODELMAN_UNIMP, ruleToBeInterpreted);
		}
		else if(ruleToBeInterpreted instanceof RelationCreateRule){
			
			
			//TODO: at this moment there is no idea how to implement this
			return null;
			//no type information is available
//			if(((RelationCreateRule)ruleToBeInterpreted).getType() == null)
//				throw new GTASMCompiledException(GTASMCompiledException.ASM_NO_TYPE,ruleToBeInterpreted); 
//			
//			SerializedTerm targetVar = TermEvaluator.evaluate(((RelationCreateRule)ruleToBeInterpreted).getTargetVariable());
//			
//			SerializedTerm source = TermEvaluator.evaluate(((RelationCreateRule)ruleToBeInterpreted).getSource());
//			SerializedTerm target = TermEvaluator.evaluate(((RelationCreateRule)ruleToBeInterpreted).getTarget());
//			SerializedTerm type = TermEvaluator.evaluate(((RelationCreateRule)ruleToBeInterpreted).getType());
//			
//			
//			targetVar.insert("(IRelationWrapper) ");
//			targetVar.append("= (new IRelationWrapper(");
//			targetVar.append(source.getTerm()+","+target.getTerm()+","+ type.getTerm()+")).create();\n");
//			return targetVar.getTerm();
		}
		else if(ruleToBeInterpreted instanceof EntityCreateRule){
			// as viatra.gtasm.compiled.emf.internal handles containment with aggregation edges the parent parameter is not allowed in 
			if((((EntityCreateRule)ruleToBeInterpreted).getParent())==null)
			      	throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_PARENT,ruleToBeInterpreted);
			if((((EntityCreateRule)ruleToBeInterpreted).getType())!=null)
					throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_NO_TYPE,ruleToBeInterpreted);
			
			SerializedTerm targetVar = TermEvaluator.evaluate(((EntityCreateRule)ruleToBeInterpreted).getTargetVariable(),
					GTASMCompiler.getInstance().getUsedVariables());
			if(!targetVar.getType().equals(ValueKind.MODELELEMENT_LITERAL))
				throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_VAR_TERM_TYPEMISMATCH,((RelationCreateRule)ruleToBeInterpreted).getTargetVariable());
						
			SerializedTerm type = TermEvaluator.evaluate(((ElementCreateRule)ruleToBeInterpreted).getType(),
					GTASMCompiler.getInstance().getUsedVariables());
			
			targetVar.append(" = EcoreUtil.create(");
			targetVar.append(type);
			targetVar.append(".eClass());\n");
		}
		
		
/************************* DELETION RULES**********************************/
		
				
		else if(ruleToBeInterpreted instanceof DeleteInstanceOf)
		{
			throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_MODELMAN_UNIMP, ruleToBeInterpreted);
		}
		else if(ruleToBeInterpreted instanceof DeleteSupertypeOf)
		{
			throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_MODELMAN_UNIMP, ruleToBeInterpreted);
		}
		else if(ruleToBeInterpreted instanceof ElementDeleteRule){
			
//			if(((ElementDeleteRule)ruleToBeInterpreted).getSemantics().equals(DeleteSemantics.MOVE_CONTENT_LITERAL))
//			{
//			}
			SerializedTerm  elementToBeDeleted = TermEvaluator.evaluate(((ElementDeleteRule)ruleToBeInterpreted).getElement(),
					GTASMCompiler.getInstance().getUsedVariables());
			
			if(((ElementDeleteRule)ruleToBeInterpreted).getElement() instanceof VariableReference)
			{
				String element = elementToBeDeleted.getTerm().toString();
				elementToBeDeleted.insert("EcoreUtil.delete((EObject)");
				elementToBeDeleted.append(");\n");
				elementToBeDeleted.append(element+" = null;\n");
				return elementToBeDeleted.getTerm();
			}
			else
			{
				elementToBeDeleted.insert("EcoreUtil.delete((EObject)");
				elementToBeDeleted.append(");\n");
				return elementToBeDeleted.getTerm();
				
			}
			
		}
		/*
		 * End of Deletion Rules
		 * 
		 * 
		 * 
		 * Copy/move Rules 
		 */
		else if(ruleToBeInterpreted instanceof MoveRule){
			
//			SerializedTerm srcRoot = TermEvaluator.evaluate(((MoveRule)ruleToBeInterpreted).getSrcRoot());
//			SerializedTerm trgContainer = TermEvaluator.evaluate(((MoveRule)ruleToBeInterpreted).getTrgContainer());
//			Can not be used in the compiled version as we do not know the EStructuralFeatures which represent the containment hierarchy			
			
			throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_MODELMAN_UNIMP, ruleToBeInterpreted);	
			
		}
		else if(ruleToBeInterpreted instanceof ModelCopyRule){
			throw new ViatraCompiledCompileTimeException(ViatraCompiledCompileTimeException.ASM_MODELMAN_UNIMP, ruleToBeInterpreted);
			
//			Object srcRoot = (executionEnvironment.getTermEvaluator().evaluate(executionEnvironment, ((MoveRule)ruleToBeInterpreted).getSrcRoot()));
//			Object trgContainer = (executionEnvironment.getTermEvaluator().evaluate(executionEnvironment, ((MoveRule)ruleToBeInterpreted).getTrgContainer()));
//						
//			
//			if(!(srcRoot instanceof IModelElement))
//			{
//				if(ValueKind.UNDEF_LITERAL.equals(srcRoot))
//					throw new GTASMCompiledException(ErrorStrings.REF_NOT_EXIST,ruleToBeInterpreted);
//				else throw new GTASMCompiledException(ErrorStrings.PARAM_EXCPECTED_MODELELEMENT+"1",ruleToBeInterpreted);
//			}
//			if(!(trgContainer instanceof IModelElement))
//			{
//				if(ValueKind.UNDEF_LITERAL.equals(srcRoot))
//					throw new GTASMCompiledException(ErrorStrings.REF_NOT_EXIST,ruleToBeInterpreted);
//				else throw new GTASMCompiledException(ErrorStrings.PARAM_EXCPECTED_MODELELEMENT+"2",ruleToBeInterpreted);
//			}
//			
//			try {
//				executionEnvironment.
//					setVariableValue(
//							((ModelCopyRule)ruleToBeInterpreted).getTargetVariable().getVariable(),
//							executionEnvironment.getFramework().getTopmodel().getModelManager().
//								copyModelElement(
//										(IModelElement)srcRoot,
//										(IModelElement)trgContainer,
//										((ModelCopyRule)ruleToBeInterpreted).getSemantics()==CopySemantics.DROP_OUTER_EDGES_LITERAL));
//			} catch (VPMCoreException e) {
//				throw new GTASMCompiledException(ErrorStrings.VPM_CORE+e.getMessage(),ruleToBeInterpreted);
//			} catch (GTASMCompiledException e) {
//				throw new GTASMCompiledException(ErrorStrings.SET_VAR_VALUE+e.getMessage(),((ModelCopyRule)ruleToBeInterpreted).getTargetVariable());
//			}
//			return Boolean.TRUE;
		}
		return null;
	}
	
	
	
}
