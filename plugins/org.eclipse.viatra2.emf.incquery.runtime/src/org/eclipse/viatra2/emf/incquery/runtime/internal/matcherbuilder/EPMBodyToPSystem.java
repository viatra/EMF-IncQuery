/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.runtime.internal.matcherbuilder;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Buildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PSystem;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicdeferred.Equality;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicdeferred.Inequality;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicenumerables.BinaryTransitiveClosure;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicenumerables.TypeBinary;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicenumerables.TypeTernary;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicenumerables.TypeUnary;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext.EdgeInterpretation;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.FlatTuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.AggregatedValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.AggregatorExpression;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.BoolValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CheckConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CompareConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CountAggregator;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.DoubleValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.IntValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionHead;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionTail;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCall;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCompositionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.StringValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Type;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ValueReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableValue;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassifierConstraint;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EnumValue;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ReferenceType;
import org.eclipse.xtext.xbase.XExpression;

/**
 * @author Bergmann Gábor
 *
 */
public class EPMBodyToPSystem<StubHandle, Collector> {
	
	protected Pattern pattern;
	protected PatternBody body;
	protected IPatternMatcherContext<Pattern> context;
	protected Buildable<Pattern, StubHandle, Collector> buildable;
	
	protected PSystem<Pattern, StubHandle, Collector> pSystem;
	
	String patternFQN;

	/**
	 * @param pattern
	 * @param body
	 * @param builder
	 * @param buildable
	 */
	public EPMBodyToPSystem(Pattern pattern, PatternBody body,
			IPatternMatcherContext<Pattern> context,
			Buildable<Pattern, StubHandle, Collector> buildable) {
		super();
		this.pattern = pattern;
		this.body = body;
		this.context = context;
		this.buildable = buildable;
		
		patternFQN = CorePatternLanguageHelper.getFullyQualifiedName(pattern).toString();
	}
	
	public PSystem<Pattern, StubHandle, Collector> toPSystem() throws RetePatternBuildException {
		try {
			if (this.pSystem == null) {
				this.pSystem = new PSystem<Pattern, StubHandle, Collector>(context, buildable, pattern);
				
				// TODO
	//			preProcessAssignments();
				preProcessParameters();
				gatherBodyConstraints();
			}
			return pSystem;
		} catch (RetePatternBuildException e) {
			e.setPatternDescription(pattern);
			throw e;
		}
	}

	public PVariable[] symbolicParameterArray() throws RetePatternBuildException {
		toPSystem();
		
		EList<Variable> symParameters = pattern.getParameters();
		int arity = symParameters.size();
		PVariable[] result = new PVariable[arity];
		for (int i=0; i<arity; ++i) result[i] = getPNode(symParameters.get(i));
		return result;
	}
	protected PVariable getPNode(String name) {
		return pSystem.getOrCreateVariableByName(name);
	}

	protected PVariable getPNode(Variable variable) {
		return getPNode(variable.getName());
	}
	protected PVariable getPNode(VariableReference variable) {
		// Warning! variable.getVar() does not differentiate between 
		// multiple anonymous variables ('_')
		return getPNode(variable.getVariable().getName());
	}
	protected Tuple getPNodeTuple(List<? extends ValueReference> variables) throws RetePatternBuildException {
		PVariable[] pNodeArray = getPNodeArray(variables);
		return new FlatTuple(pNodeArray);
	}
	public PVariable[] getPNodeArray(List<? extends ValueReference> variables) throws RetePatternBuildException {
		int k = 0;
		PVariable[] pNodeArray = new PVariable[variables.size()];
		for (ValueReference varRef : variables) {
			pNodeArray[k++] = getPNode(varRef);
		}
		return pNodeArray;
	}
	protected PVariable getPNode(ValueReference reference) throws RetePatternBuildException {
		if (reference instanceof VariableValue)
			return getPNode(((VariableValue) reference).getValue());
		else if (reference instanceof AggregatedValue)
			return aggregate((AggregatedValue) reference);
		else if (reference instanceof IntValue)
			return pSystem.newConstantVariable(((IntValue) reference).getValue());
		else if (reference instanceof StringValue)
			return pSystem.newConstantVariable(((StringValue) reference).getValue());
		else if (reference instanceof EnumValue) // EMF-specific
			return pSystem.newConstantVariable(((EnumValue) reference).getLiteral().getInstance());
		else if (reference instanceof DoubleValue) {
			return pSystem.newConstantVariable(((DoubleValue) reference).getValue());
		}
		else if (reference instanceof BoolValue) {
			Boolean b = ((BoolValue) reference).isValue();
			return pSystem.newConstantVariable(b);
		}
		else throw new RetePatternBuildException(
				"Unsupported value reference of type {1} from EPackage {2} currently unsupported by pattern builder in pattern {3}.",
				new String[]{
						reference != null? reference.eClass().getName() : "(null)",
						reference != null? reference.eClass().getEPackage().getNsURI() : "(null)",
						pattern.getName()
				}, "Unsupported value expression", pattern);
	}
	

	protected PVariable newVirtual() {
		return pSystem.newVirtualVariable();
	}
//	protected Tuple getPNodeTuple(List<? extends ValueReference> references) throws RetePatternBuildException {
//		PVariable[] pNodeArray = getPNodeArray(references);
//		return new FlatTuple(pNodeArray);
//	}
//	public PVariable[] getPNodeArray(List<? extends ValueReference> references) throws RetePatternBuildException {
//		int k = 0;
//		PVariable[] pNodeArray = new PVariable[references.size()];
//		for (ValueReference varRef : references) {
//			pNodeArray[k++] = getPNode(varRef);
//		}
//		return pNodeArray;
//	}	
	private void preProcessParameters() {
		EList<Variable> parameters = pattern.getParameters();
		for (Variable variable : parameters) {
			new ExportedParameter<Pattern, StubHandle>(pSystem, getPNode(variable), variable.getName());
			if (variable.getType() != null && variable.getType() instanceof ClassType) {
				EClassifier classname = ((ClassType)variable.getType()).getClassname();
				PVariable pNode = getPNode(variable);
				new TypeUnary<Pattern, StubHandle>(pSystem, pNode, classname);
			}
		}
	}
	private void gatherBodyConstraints() throws RetePatternBuildException {
		EList<Constraint> constraints = body.getConstraints();
		for (Constraint constraint : constraints) {
			gatherConstraint(constraint);
		}
	}

	/**
	 * @param constraint
	 * @throws RetePatternBuildException
	 */
	protected void gatherConstraint(Constraint constraint)
			throws RetePatternBuildException {
		if (constraint instanceof EClassifierConstraint) {  // EMF-specific
			EClassifierConstraint constraint2 = (EClassifierConstraint) constraint;
			gatherClassifierConstraint(constraint2);
		} else if (constraint instanceof PatternCompositionConstraint) {
			PatternCompositionConstraint constraint2 = (PatternCompositionConstraint) constraint;
			gatherCompositionConstraint(constraint2);
		} else if (constraint instanceof CompareConstraint) {
			CompareConstraint compare = (CompareConstraint) constraint;
			gatherCompareConstraint(compare);
		} else if (constraint instanceof PathExpressionConstraint) {
			// TODO advanced features here?
			PathExpressionConstraint pathExpression = (PathExpressionConstraint) constraint;			
			gatherPathExpression(pathExpression);
		} else if (constraint instanceof CheckConstraint) {
			final CheckConstraint check = (CheckConstraint) constraint;
			gatherCheckConstraint(check);
		// TODO OTHER CONSTRAINT TYPES
		} else {
			throw new RetePatternBuildException(
					"Unsupported constraint type {1} in pattern {2}.", 
					new String[]{constraint.eClass().getName(), patternFQN}, 
					"Unsupported constraint type", pattern);
		}
	}

	/**
	 * @param check
	 */
	protected void gatherCheckConstraint(final CheckConstraint check) {
		XExpression expression = check.getExpression();
		new XBaseCheck<StubHandle>(this, expression, pattern);
	}

	/**
	 * @param pathExpression
	 * @throws RetePatternBuildException
	 */
	protected void gatherPathExpression(PathExpressionConstraint pathExpression)
			throws RetePatternBuildException {
		PathExpressionHead head = pathExpression.getHead();
		PVariable currentSrc = getPNode(head.getSrc());
		PVariable finalDst = getPNode(head.getDst());
		PathExpressionTail currentTail = head.getTail();
		
		// type constraint on source
		Type headType = head.getType(); 
		if (headType instanceof ClassType) {
			EClassifier headClassname =  ((ClassType)headType).getClassname();
			new TypeUnary<Pattern, StubHandle>(pSystem, currentSrc, headClassname);
		} else {
			throw new RetePatternBuildException(
				"Unsupported path expression head type {1} in pattern {2}: {3}", 
				new String[]{headType.eClass().getName(), patternFQN, typeStr(headType)}, 
				"Unsupported navigation source", pattern);
		}

		// process each segment			
		while (currentTail != null) {
			Type currentPathSegmentType = currentTail.getType();
			currentTail = currentTail.getTail();
			
			PVariable intermediate = newVirtual();
			gatherPathSegment(currentPathSegmentType, currentSrc, intermediate);
			
			currentSrc = intermediate;
		}
		// link the final step to the overall destination
		new Equality<Pattern, StubHandle>(pSystem, currentSrc, finalDst);
	}

	/**
	 * @param compare
	 * @throws RetePatternBuildException
	 */
	protected void gatherCompareConstraint(CompareConstraint compare)
			throws RetePatternBuildException {
		PVariable left = getPNode(compare.getLeftOperand()); 
		PVariable right = getPNode(compare.getRightOperand()); 
		switch(compare.getFeature()) {
		case EQUALITY:
			new Equality<Pattern, StubHandle>(pSystem, left, right);
			break;
		case INEQUALITY:
			new Inequality<Pattern, StubHandle>(pSystem, left, right, false);
		}
	}

	/**
	 * @param constraint
	 * @throws RetePatternBuildException
	 */
	protected void gatherCompositionConstraint(
			PatternCompositionConstraint constraint)
			throws RetePatternBuildException {
		PatternCall call = constraint.getCall();
		Pattern patternRef = call.getPatternRef();
		Tuple pNodeTuple = getPNodeTuple(call.getParameters());
		if (!call.isTransitive()) {
			if (constraint.isNegative()) 
				new NegativePatternCall<Pattern, StubHandle>(pSystem, pNodeTuple, patternRef);
			else 
				new PositivePatternCall<Pattern, StubHandle>(pSystem, pNodeTuple, patternRef);
		} else {
			if (pNodeTuple.getSize() != 2)
				throw new RetePatternBuildException(
						"Transitive closure of {1} in pattern {2} is unsupported because called pattern is not binary.", 
						new String[]{CorePatternLanguageHelper.getFullyQualifiedName(patternRef), patternFQN},
						"Transitive closure only supported for binary patterns.", 
						pattern); 
			else if (constraint.isNegative()) 
				throw new RetePatternBuildException(
						"Unsupported negated transitive closure of {1} in pattern {2}", 
						new String[]{CorePatternLanguageHelper.getFullyQualifiedName(patternRef), patternFQN}, 
						"Unsupported negated transitive closure", pattern);
			else 
				new BinaryTransitiveClosure<Pattern, StubHandle>(pSystem, pNodeTuple, patternRef);
//				throw new RetePatternBuildException(
//						"Unsupported positive transitive closure of {1} in pattern {2}", 
//						new String[]{CorePatternLanguageHelper.getFullyQualifiedName(patternRef), patternFQN}, 
//						pattern);
		}
	}

	/**
	 * @param constraint
	 */
	protected void gatherClassifierConstraint(EClassifierConstraint constraint) {
		EClassifier classname = ((ClassType)constraint.getType()).getClassname();
		PVariable pNode = getPNode(constraint.getVar());
		new TypeUnary<Pattern, StubHandle>(pSystem, pNode, classname);
	}

	protected void gatherPathSegment(Type segmentType, PVariable src, PVariable trg) throws RetePatternBuildException {
		if (segmentType instanceof ReferenceType) {  // EMF-specific
			EStructuralFeature typeObject = ((ReferenceType) segmentType).getRefname();
			if (context.edgeInterpretation() == EdgeInterpretation.TERNARY) {
				new TypeTernary<Pattern, StubHandle>(pSystem, context, newVirtual(), src, trg, typeObject);
			} else {
				new TypeBinary<Pattern, StubHandle>(pSystem, context, src, trg, typeObject);
			}
		} else throw new RetePatternBuildException(
				"Unsupported path segment type {1} in pattern {2}: {3}", 
				new String[]{segmentType.eClass().getName(), patternFQN, typeStr(segmentType)}, 
				"Unsupported navigation step", pattern);		
	}


	protected PVariable aggregate(AggregatedValue reference) throws RetePatternBuildException {
		PVariable result = newVirtual();
		
		PatternCall call = reference.getCall();
		Pattern patternRef = call.getPatternRef();
		Tuple pNodeTuple = getPNodeTuple(call.getParameters());
		
		AggregatorExpression aggregator = reference.getAggregator();
		if (aggregator instanceof CountAggregator) {
			new PatternMatchCounter<Pattern, StubHandle>(pSystem, pNodeTuple, patternRef, result);
		} else throw new RetePatternBuildException(
				"Unsupported aggregator expression type {1} in pattern {2}.", 
				new String[]{aggregator.eClass().getName(), patternFQN}, 
				"Unsupported aggregator expression", pattern);	
		
		
		return result;
	}
	
	/**
	 * @return the string describing a metamodel type, for debug / exception purposes
	 */
	private String typeStr(Type type) {
		return type.getTypename() == null ? "(null)" : type.getTypename();
	}

}
