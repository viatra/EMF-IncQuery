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

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Buildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IReteLayoutStrategy;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.helpers.BuildHelper;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.helpers.LayoutHelper;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.DeferredPConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.EnumerablePConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PSystem;
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
		IPatternMatcherContext<PatternDescription> context = pSystem.getContext();
		Buildable<PatternDescription, StubHandle, Collector> buildable = pSystem.getBuildable();
		try {
			
			context.logDebug(getClass().getSimpleName() + ": patternbody build started");
			
			// UNIFICATION AND WEAK INEQUALITY ELMINATION
			LayoutHelper.unifyVariablesAlongEqualities(pSystem);
			LayoutHelper.eliminateWeakInequalities(pSystem);
		
			// UNARY ELIMINATION WITH TYPE INFERENCE
			if (Options.calcImpliedTypes) {
				LayoutHelper.eliminateInferrableUnaryTypes(pSystem, context);
			}
			
			// PREVENTIVE CHECKS
			LayoutHelper.checkSanity(pSystem);
					
			// STARTING THE LINE
			Stub<StubHandle> stub = buildable.buildStartStub(new Object[]{}, new Object[]{});
			
//			Set<ConstantValue> constants = pSystem.getConstraintsOfType(ConstantValue.class);
//			for (ConstantValue<PatternDescription, StubHandle> pConstraint : constants) {
//				Stub<StubHandle> sideStub = pConstraint.doCreateStub();
//				stub = BuildHelper.naturalJoin(buildable, stub, sideStub);
//			}
			
			Set<PConstraint> pQueue = new HashSet<PConstraint>(pSystem.getConstraints()); //TreeSet<PConstraint>(new OrderingHeuristics());
//			pQueue.addAll(pSystem.getConstraintsOfType(EnumerablePConstraint.class));
//			pQueue.addAll(pSystem.getConstraintsOfType(DeferredPConstraint.class));
//			// omitted: symbolic & equality -- not anymore
			
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
			LayoutHelper.finalCheck(pSystem, stub);
			

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

			context.logDebug(getClass().getSimpleName() + ": patternbody build concluded");
	
			return stub;
			
		} catch (RetePatternBuildException ex) {
			ex.setPatternDescription(pattern);
			throw ex;
		}	
	}




}
