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

package org.eclipse.incquery.runtime.rete.construction.quasitree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.incquery.runtime.rete.construction.Buildable;
import org.eclipse.incquery.runtime.rete.construction.IReteLayoutStrategy;
import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;
import org.eclipse.incquery.runtime.rete.construction.Stub;
import org.eclipse.incquery.runtime.rete.construction.helpers.BuildHelper;
import org.eclipse.incquery.runtime.rete.construction.helpers.LayoutHelper;
import org.eclipse.incquery.runtime.rete.construction.psystem.DeferredPConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.EnumerablePConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.PSystem;
import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherContext;
import org.eclipse.incquery.runtime.rete.util.Options;

/**
 * Layout ideas
 * 
 * Order of preference
 *  - trivial joins (stub a = stub b)
 *  - high selectivity filters
 *   - supressed by joincandidate if already checked on other side
 *   - apply first to narrower stubs
 *  - aggregator quasijoins, let-expressions (?)
 *  - true joins 
 *   - except cartesians 
 *   - TBD: ranking based on what constraints the join will enable
 *  - low-selectivity filters (injectivity)
 *  - cartesian joins
 * 
 * @author Bergmann Gábor
 *
 */
public class QuasiTreeLayout<PatternDescription, StubHandle, Collector>
		implements
		IReteLayoutStrategy<PatternDescription, StubHandle, Collector> {

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IReteLayoutStrategy#layout(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PSystem)
	 */
	@Override
	public Stub<StubHandle> layout(PSystem<PatternDescription, StubHandle, Collector> pSystem) throws RetePatternBuildException {
		return new Scaffold(pSystem).run();
	}
	
	public class Scaffold {
		PSystem<PatternDescription, StubHandle, Collector> pSystem;
		PatternDescription pattern;
		IPatternMatcherContext<PatternDescription> context;
		Buildable<PatternDescription, StubHandle, Collector> buildable;

		Set<DeferredPConstraint> deferredConstraints = null;
		Set<Stub<StubHandle>> forefront = new LinkedHashSet<Stub<StubHandle>>();

		Scaffold(PSystem<PatternDescription, StubHandle, Collector> pSystem) {
			this.pSystem = pSystem;
			pattern = pSystem.getPattern();
			context = pSystem.getContext();
			buildable = pSystem.getBuildable();
		}

		/**
		 * @return
		 */
		public Stub<StubHandle> run() throws RetePatternBuildException {
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
				
				// PROCESS CONSTRAINTS
				deferredConstraints = pSystem.getConstraintsOfType(DeferredPConstraint.class);
				Set<EnumerablePConstraint> enumerables = 
					pSystem.getConstraintsOfType(EnumerablePConstraint.class);
				for (EnumerablePConstraint<PatternDescription, StubHandle> enumerable : enumerables) {
					Stub<StubHandle> stub = enumerable.getStub();
					admitStub(stub);
				}
				if (enumerables.isEmpty()) { // EXTREME CASE
					Stub<StubHandle> stub = buildable.buildStartStub(new Object[]{}, new Object[]{});
					admitStub(stub);
				}
	
				// JOIN FOREFRONT STUBS WHILE POSSIBLE
				while (forefront.size() > 1) {
					// TODO QUASI-TREE TRIVIAL JOINS?
					
					List<JoinCandidate<StubHandle>> candidates = generateJoinCandidates();				 
					JoinOrderingHeuristics<PatternDescription, StubHandle, Collector> ordering = 
						new JoinOrderingHeuristics<PatternDescription, StubHandle, Collector>();
					JoinCandidate<StubHandle> selectedJoin = Collections.min(candidates, ordering);
					doJoin(selectedJoin.getPrimary(), selectedJoin.getSecondary());
				}
				
				// FINAL CHECK, whether all exported variables are present
				assert(forefront.size() == 1);
				Stub<StubHandle> finalStub = forefront.iterator().next();
				LayoutHelper.finalCheck(pSystem, finalStub);
				
				context.logDebug(getClass().getSimpleName() + ": patternbody build concluded");
				return finalStub;
			} catch (RetePatternBuildException ex) {
				ex.setPatternDescription(pattern);
				throw ex;
			}	
		}

		public List<JoinCandidate<StubHandle>> generateJoinCandidates() {
			List<JoinCandidate<StubHandle>> candidates = new ArrayList<JoinCandidate<StubHandle>>();
			int bIndex = 0;
			for (Stub<StubHandle> b : forefront) {
				int aIndex = 0;
				for (Stub<StubHandle> a : forefront) {
					if (aIndex++ >= bIndex) break; 
					candidates.add(new JoinCandidate<StubHandle>(a, b));
				}	
				bIndex++;
			}
			return candidates;
		}


		private void admitStub(Stub<StubHandle> stub) throws RetePatternBuildException {
			for (DeferredPConstraint<PatternDescription, StubHandle> deferred : deferredConstraints) {
				if (!stub.getAllEnforcedConstraints().contains(deferred)) {
					if (deferred.isReadyAt(stub)) {
						admitStub(deferred.checkOn(stub));
						return;
					}			
				}
			}
			forefront.add(stub);
		}
		
		private void doJoin(Stub<StubHandle> primaryStub, Stub<StubHandle> secondaryStub) throws RetePatternBuildException {
			Stub<StubHandle> joinedStub = BuildHelper.naturalJoin(buildable, primaryStub, secondaryStub);
			forefront.remove(primaryStub);
			forefront.remove(secondaryStub);
			admitStub(joinedStub);
		}
		
	}
	


}
