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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.basiclinear;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.BuildHelper;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Buildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IReteLayoutStrategy;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.DeferredPConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.EnumerablePConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.ITypeInfoProviderConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PSystem;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicdeferred.Inequality;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicenumerables.TypeUnary;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.basicmisc.ExportedSymbolicParameter;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.util.Options;

/**
 * Basic layout that builds a linear RETE net based on a heuristic ordering of constraints. 
 * @author Bergmann Gábor
 *
 */
public class BasicLinearLayout<PatternDescription, StubHandle, Collector> implements IReteLayoutStrategy<PatternDescription, StubHandle, Collector> {

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IReteLayoutStrategy#layout(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PSystem)
	 */
	@Override
	public Stub<StubHandle> layout(final PSystem<PatternDescription, StubHandle, Collector> pSystem)
		throws RetePatternBuildException 
	{
		PatternDescription pattern = pSystem.getPattern();
		IRetePatternBuilder<PatternDescription, StubHandle, Collector> builder = pSystem.getBuilder();
		IPatternMatcherContext<PatternDescription> context = builder.getContext();
		Buildable<PatternDescription, StubHandle, Collector> buildable = pSystem.getBuildable();
		try {
			
			context.logDebug(getClass().getSimpleName() + ": patternbody build started");
			
			// UNIFICATION AND WEAK INEQUALITY ELMINATION
			pSystem.unifyVariablesAlongEqualities();
			for (Inequality inequality : pSystem.getConstraintsOfType(Inequality.class)) inequality.eliminateWeak();
		
			// UNARY ELIMINATION WITH TYPE INFERENCE
			if (Options.calcImpliedTypes) {
				Set<TypeUnary> constraintsOfType = pSystem.getConstraintsOfType(TypeUnary.class);
				for (TypeUnary<PatternDescription, StubHandle> typeUnary : constraintsOfType) {
					PVariable var = (PVariable) typeUnary.getVariablesTuple().get(0);
					Object expressedType = typeUnary.getTypeInfo(var);
					Set<ITypeInfoProviderConstraint> typeRestrictors = var.getReferringConstraintsOfType(ITypeInfoProviderConstraint.class);
					typeRestrictors.remove(typeUnary);
					for (ITypeInfoProviderConstraint iTypeRestriction : typeRestrictors) {
						Object typeInfo = iTypeRestriction.getTypeInfo(var);
						if (typeInfo != ITypeInfoProviderConstraint.TypeInfoSpecials.NO_TYPE_INFO_PROVIDED) {
							Set<Object> typeClosure = 
								BuildHelper.typeClosure(Collections.singleton(typeInfo), context);
							if (typeClosure.contains(expressedType)) {
								typeUnary.delete();								
								break;
							}
						}
					}
				}
			}
			
			// PREVENTIVE CHECKS
			for (PConstraint pConstraint : pSystem.getConstraints()) pConstraint.checkSanity();
		
			Set<ExportedSymbolicParameter> exports = pSystem.getConstraintsOfType(ExportedSymbolicParameter.class);
			for (ExportedSymbolicParameter<PatternDescription, StubHandle> export : exports) {
				PVariable var = export.getParameterVariable();
				if (!var.isDeducable()){
					String[] args = {var.toString()};
					String msg = "Exported pattern variable {1} can not be determined based on the pattern constraints. "
						+ "HINT: certain constructs (e.g. negative patterns or check expressions) cannot output symbolic parameters.";
//						+ "pattern variable {1} in pattern {2} could not be reached";
					throw new RetePatternBuildException(msg, args, pattern);
				}					
			}
			
			// STARTING THE LINE
			Stub<StubHandle> stub = buildable.buildStartStub(new Object[]{}, new Object[]{});
			
//			Set<ConstantValue> constants = pSystem.getConstraintsOfType(ConstantValue.class);
//			for (ConstantValue<PatternDescription, StubHandle> pConstraint : constants) {
//				Stub<StubHandle> sideStub = pConstraint.doCreateStub();
//				stub = BuildHelper.naturalJoin(buildable, stub, sideStub);
//			}
			
			Set<PConstraint> pQueue = new HashSet<PConstraint>(); //TreeSet<PConstraint>(new OrderingHeuristics());
			pQueue.addAll(pSystem.getConstraintsOfType(EnumerablePConstraint.class));
			pQueue.addAll(pSystem.getConstraintsOfType(DeferredPConstraint.class));
			// omitted: symbolic & equality
			
			// MAIN LOOP
			while (!pQueue.isEmpty()) {
				PConstraint pConstraint = 
					Collections.min(pQueue, 
						new OrderingHeuristics<PatternDescription, StubHandle, Collector>(stub)); //pQueue.iterator().next();
				pQueue.remove(pConstraint);

				if (pConstraint instanceof EnumerablePConstraint<?, ?>) {
					EnumerablePConstraint<PatternDescription,StubHandle> enumerable = 
						(EnumerablePConstraint<PatternDescription,StubHandle>) pConstraint;
					Stub<StubHandle> sideStub = enumerable.getStub();
					stub = BuildHelper.naturalJoin(buildable, stub, sideStub);
				} else {
					DeferredPConstraint<PatternDescription, StubHandle> deferred = 
						(DeferredPConstraint<PatternDescription, StubHandle>) pConstraint;
					if (deferred.isReadyAt(stub)) {
						stub = deferred.checkOn(stub);
					} else {
						deferred.raiseForeverDeferredError(stub);
					}
				}
			}
			
			// FINAL CHECK, whether all exported variables are present
			for (ExportedSymbolicParameter<PatternDescription, StubHandle> export : exports) {
				PVariable var = export.getParameterVariable();
				if (!stub.getVariablesIndex().containsKey(var)){
					String[] args = {var.toString()};
					String msg = "Pattern Graph Search terminated incompletely, "
						+ "exported pattern variable {1} could not be reached. "
						+ "HINT: certain constructs (e.g. negative patterns or check expressions) cannot output symbolic parameters.";
;
//						+ "pattern variable {1} in pattern {2} could not be reached";
					throw new RetePatternBuildException(msg, args, pattern);
				}					
			}
//			for (PVariable var : pSystem.getVariables())
//				if (
//						var.getDirectUnifiedInto()==null && 
//						(var.isDeducable()) && 
//						!stub.getVariablesIndex().containsKey(var)
//					) 


//			// output
//			int paramNum = patternScaffold.gtPattern.getSymParameters().size();
//			int[] tI = new int[paramNum];
//			int tiW = stub.getVariablesTuple().getSize();
//			for (int i = 0; i < paramNum; i++) {
//				PatternVariable variable = patternScaffold.gtPattern.getSymParameters().get(i);
//				// for (Object o : variable.getElementInPattern()) // in all bodies
//				// {
//				PatternNodeBase pNode = pGraph.getPNode(variable);
//				// if (stub.calibrationIndex.containsKey(pNode))
//				tI[i] = stub.getVariablesIndex().get(pNode);
//				// }
//			}
//			TupleMask trim = new TupleMask(tI, tiW);
//			Stub<StubHandle> trimmer = buildable.buildTrimmer(stub, trim);
//			buildable.buildConnection(trimmer, collector);
	
			return stub;
			
		} catch (RetePatternBuildException ex) {
			ex.setPatternDescription(pattern);
			throw ex;
		}	
	}


}
