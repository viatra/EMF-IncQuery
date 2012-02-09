/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMask;

/**
 * @author Bergmann Gábor
 *
 * An implicit common parameter is the "effort" PatternDescription. This indicates that the build request is part
 * of an effort to build the matcher of the given pattern; it it important to record this during code generation
 * so that the generated code can be separated according to patterns. 
 *  
 * @param <PatternDescription> the description of a pattern
 * @param <StubHandle> the handle of a continuable supplier-like RETE ending with associated semantics
 * @param <Collector> the handle of a receiver-like RETE ending to which stubs can be connected
 */
public interface Buildable<PatternDescription, StubHandle, Collector> {
	
	public Collector patternCollector(PatternDescription pattern) throws RetePatternBuildException;
	public Stub<StubHandle> patternCallStub(Tuple nodes, PatternDescription supplierKey) throws RetePatternBuildException;

	public Stub<StubHandle> instantiationTransitiveStub(Tuple nodes);
	public Stub<StubHandle> instantiationDirectStub(Tuple nodes);
	public Stub<StubHandle> generalizationTransitiveStub(Tuple nodes);
	public Stub<StubHandle> generalizationDirectStub(Tuple nodes);
	public Stub<StubHandle> containmentTransitiveStub(Tuple nodes);
	public Stub<StubHandle> containmentDirectStub(Tuple nodes);
	public Stub<StubHandle> binaryEdgeTypeStub(Tuple nodes, Object supplierKey);
	public Stub<StubHandle> ternaryEdgeTypeStub(Tuple nodes, Object supplierKey);
	public Stub<StubHandle> unaryTypeStub(Tuple nodes, Object supplierKey);
	
	public void buildConnection(Stub<StubHandle> stub, Collector collector);
	
	public Stub<StubHandle> buildStartStub(Object[] constantValues, Object[] constantNames);
	public Stub<StubHandle> buildEqualityChecker(Stub<StubHandle> stub, int[] indices);
	public Stub<StubHandle> buildInjectivityChecker(Stub<StubHandle> stub, int subject, int[] inequalIndices);
	public Stub<StubHandle> buildTransitiveClosure(Stub<StubHandle> stub);
	public Stub<StubHandle> buildTrimmer(Stub<StubHandle> stub, TupleMask trimMask);
	public Stub<StubHandle> buildBetaNode(Stub<StubHandle> primaryStub, Stub<StubHandle> sideStub, TupleMask primaryMask,
			TupleMask sideMask, TupleMask complementer, boolean negative);
	public Stub<StubHandle> buildCounterBetaNode(Stub<StubHandle> primaryStub, Stub<StubHandle> sideStub, 
			TupleMask primaryMask, TupleMask originalSideMask, TupleMask complementer, Object aggregateResultCalibrationElement);  
	public Stub<StubHandle> buildCountCheckBetaNode(Stub<StubHandle> primaryStub, Stub<StubHandle> sideStub, 
			TupleMask primaryMask, TupleMask originalSideMask, int resultPositionInSignature);
	public Stub<StubHandle> buildScopeConstrainer(Stub<StubHandle> stub, boolean transitive, Object unwrappedContainer, int constrainedIndex);
	
	
	/**
	 * @return a buildable that potentially acts on a separate container
	 */
	public Buildable<PatternDescription, StubHandle, Collector> getNextContainer();
	/**
	 * @return a buildable that puts build actions on the tab of the given pattern 
	 */
	public Buildable<PatternDescription, StubHandle, Collector> putOnTab(PatternDescription effort);
	
	public void reinitialize();
}