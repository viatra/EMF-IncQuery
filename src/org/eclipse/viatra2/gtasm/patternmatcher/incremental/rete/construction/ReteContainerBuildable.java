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

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.AbstractEvaluator;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.PredicateEvaluatorNode;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.ReteBoundary;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.DualInputNode;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.Indexer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.IterableIndexer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Library;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Network;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.remote.Address;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.single.EqualityFilterNode;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.single.InequalityFilterNode;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.single.TrimmerNode;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.FlatTuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.LeftInheritanceTuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMask;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.util.Options;


/**
 * The buildable interface of a rete container.
 * 
 * @author Bergmann Gábor
 *
 */
public class ReteContainerBuildable<PatternDescription> implements Buildable<PatternDescription, Address<? extends Supplier>, Address<? extends Receiver>> {

	protected Library library;
	protected ReteContainer targetContainer;
	protected Network reteNet;
	protected ReteBoundary<PatternDescription> boundary;
	protected ReteEngine<PatternDescription> engine;
	protected boolean headAttached = false;
	
	/**
	 * Constructs the builder attached to a specified container.
	 * Prerequisite: engine has its network and boundary fields initialized.
	 * @param targetContainer
	 */
	public ReteContainerBuildable(ReteEngine<PatternDescription> engine, ReteContainer targetContainer) {
		super();
		this.engine = engine;
		this.reteNet = engine.getReteNet();
		this.boundary = engine.getBoundary();
		this.targetContainer = targetContainer;
		this.library = targetContainer.getLibrary();
		this.headAttached = false;
	}
	/**
	 * Constructs the builder attached to the head container.
	 * Prerequisite: engine has its network and boundary fields initialized
	 */
	public ReteContainerBuildable(ReteEngine<PatternDescription> engine) {
		super();
		this.engine = engine;
		this.reteNet = engine.getReteNet();
		this.boundary = engine.getBoundary();
		this.targetContainer = reteNet.getHeadContainer();
		this.library = targetContainer.getLibrary();
		this.headAttached = true;
	}
	public void reinitialize() {
		this.reteNet = engine.getReteNet();
		this.boundary = engine.getBoundary();
		this.targetContainer = headAttached ? reteNet.getHeadContainer() : reteNet.getNextContainer();
		this.library = targetContainer.getLibrary();
	}
	
	public Stub<Address<? extends Supplier>> buildTrimmer(Stub<Address<? extends Supplier>> stub, TupleMask trimMask) { 
		Address<TrimmerNode> bodyTerminator = library.accessTrimmerNode(stub.getHandle(), trimMask); 
		return new Stub<Address<? extends Supplier>>(trimMask.transform(stub.getVariablesTuple()), bodyTerminator);
	}

	public void buildConnection(Stub<Address<? extends Supplier>> stub, Address<? extends Receiver> collector) {
		reteNet.connectRemoteNodes(stub.getHandle(), collector, true);
	}

	public Stub<Address<? extends Supplier>> buildStartStub(Object[] constantValues, Object[] constantNames) {
		return new Stub<Address<? extends Supplier>>(new FlatTuple(constantNames), 
			library.accessConstantNode(boundary.wrapTuple(new FlatTuple(constantValues))));
	}

	public Stub<Address<? extends Supplier>> buildEqualityChecker(Stub<Address<? extends Supplier>> stub, int[] indices) {
		Address<EqualityFilterNode> checker = library.accessEqualityFilterNode(stub.getHandle(), indices);
		return new Stub<Address<? extends Supplier>>(stub, checker);
	}

	public Stub<Address<? extends Supplier>> buildInjectivityChecker(Stub<Address<? extends Supplier>> stub, int subject, int[] inequalIndices) 
	{
		Address<InequalityFilterNode> checker = 
			library.accessInequalityFilterNode(stub.getHandle(), subject, 
					new TupleMask(inequalIndices, stub.getVariablesTuple().getSize()));
		return new Stub<Address<? extends Supplier>>(stub, checker);
	}

	public Stub<Address<? extends Supplier>> patternCallStub(Tuple nodes, PatternDescription supplierKey)
		throws RetePatternBuildException 
	{
		return new Stub<Address<? extends Supplier>>(nodes, boundary.accessProduction(supplierKey));
	}

	public Stub<Address<? extends Supplier>> instantiationTransitiveStub(Tuple nodes) {
		return new Stub<Address<? extends Supplier>>(nodes, boundary.accessInstantiationTransitiveRoot());
	}

	public Stub<Address<? extends Supplier>> instantiationDirectStub(Tuple nodes) {
		return new Stub<Address<? extends Supplier>>(nodes, boundary.accessInstantiationRoot());
	}

	public Stub<Address<? extends Supplier>> generalizationTransitiveStub(Tuple nodes) {
		return new Stub<Address<? extends Supplier>>(nodes, boundary.accessGeneralizationTransitiveRoot());
	}

	public Stub<Address<? extends Supplier>> generalizationDirectStub(Tuple nodes) {
		return new Stub<Address<? extends Supplier>>(nodes, boundary.accessGeneralizationRoot());
	}

	public Stub<Address<? extends Supplier>> containmentTransitiveStub(Tuple nodes) {
		return new Stub<Address<? extends Supplier>>(nodes, boundary.accessContainmentTransitiveRoot());
	}

	public Stub<Address<? extends Supplier>> containmentDirectStub(Tuple nodes) {
		return new Stub<Address<? extends Supplier>>(nodes, boundary.accessContainmentRoot());
	}

	public Stub<Address<? extends Supplier>> binaryEdgeTypeStub(Tuple nodes, Object supplierKey) {
		return new Stub<Address<? extends Supplier>>(nodes, boundary.accessBinaryEdgeRoot(supplierKey));
	}

	public Stub<Address<? extends Supplier>> ternaryEdgeTypeStub(Tuple nodes, Object supplierKey) {
		return new Stub<Address<? extends Supplier>>(nodes, boundary.accessTernaryEdgeRoot(supplierKey));
	}

	public Stub<Address<? extends Supplier>> unaryTypeStub(Tuple nodes, Object supplierKey) {
		return new Stub<Address<? extends Supplier>>(nodes, boundary.accessUnaryRoot(supplierKey));
	}

	public Stub<Address<? extends Supplier>> buildBetaNode(
			Stub<Address<? extends Supplier>> primaryStub, 
			Stub<Address<? extends Supplier>> sideStub, 
			TupleMask primaryMask,
			TupleMask sideMask, 
			TupleMask complementer, 
			boolean negative) 
			{
				Address<? extends IterableIndexer> primarySlot = library.accessProjectionIndexer(primaryStub.getHandle(),primaryMask);
				Address<? extends Indexer> sideSlot = library.accessProjectionIndexer(sideStub.getHandle(), sideMask);
			
				Address<? extends DualInputNode> checker = negative ? library
						.accessExistenceNode(primarySlot, sideSlot, true) : library
						.accessJoinNode(primarySlot, sideSlot, complementer);
						
				Tuple newCalibrationPattern = negative ? primaryStub.getVariablesTuple()
						: complementer.combine(primaryStub.getVariablesTuple(), sideStub.getVariablesTuple(), 
								Options.enableInheritance, true);
			
				Stub<Address<? extends Supplier>> result = new Stub<Address<? extends Supplier>>(newCalibrationPattern, checker);
			
				return result;
			}
	
	public Stub<Address<? extends Supplier>> buildCounterBetaNode(
			Stub<Address<? extends Supplier>> primaryStub, 
			Stub<Address<? extends Supplier>> sideStub, 
			TupleMask primaryMask,
			TupleMask originalSideMask,
			TupleMask complementer,
			Object aggregateResultCalibrationElement)  
			{
				Address<? extends IterableIndexer> primarySlot = library.accessProjectionIndexer(primaryStub.getHandle(),primaryMask);
				Address<? extends Indexer> sideSlot = library.accessCountOuterIndexer(sideStub.getHandle(), originalSideMask);
			
				Address<? extends DualInputNode> checker = library
						.accessJoinNode(primarySlot, sideSlot, 
								TupleMask.selectSingle(originalSideMask.indices.length, originalSideMask.indices.length+1));
						
				Object[] newCalibrationElement = {aggregateResultCalibrationElement}; 
				Tuple newCalibrationPattern = new LeftInheritanceTuple(primaryStub.getVariablesTuple(), newCalibrationElement);
			
				Stub<Address<? extends Supplier>> result = new Stub<Address<? extends Supplier>>(newCalibrationPattern, checker);
			
				return result;
			}	
	public Stub<Address<? extends Supplier>> buildCountCheckBetaNode(
			Stub<Address<? extends Supplier>> primaryStub, 
			Stub<Address<? extends Supplier>> sideStub, 
			TupleMask primaryMask,
			TupleMask originalSideMask,
			int resultPositionInSignature) 
			{
				Address<? extends IterableIndexer> primarySlot = library.accessProjectionIndexer(primaryStub.getHandle(),primaryMask);
				Address<? extends Indexer> sideSlot = library.accessCountOuterIdentityIndexer(sideStub.getHandle(), originalSideMask, resultPositionInSignature);
			
				Address<? extends DualInputNode> checker = library
						.accessJoinNode(primarySlot, sideSlot, TupleMask.empty(originalSideMask.indices.length+1));
						
				Tuple newCalibrationPattern = primaryStub.getVariablesTuple();
			
				Stub<Address<? extends Supplier>> result = new Stub<Address<? extends Supplier>>(newCalibrationPattern, checker);
			
				return result;
			}

	public Stub<Address<? extends Supplier>> buildPredicateChecker(
			AbstractEvaluator evaluator, 
			Integer rhsIndex, 
			int[] affectedIndices,
			Stub<Address<? extends Supplier>> stub
		) 
	{
		PredicateEvaluatorNode ten =  new PredicateEvaluatorNode(engine, targetContainer, 
				rhsIndex, affectedIndices, stub.getVariablesTuple().getSize(), evaluator);
		Address<PredicateEvaluatorNode> checker = Address.of(ten);
		
		reteNet.connectRemoteNodes(stub.getHandle(), checker, true);
		
		Stub<Address<? extends Supplier>> result = new Stub<Address<? extends Supplier>>(stub, checker);
		
		return result;
	}	
	
	/**
	 * @return a buildable that potentially acts on a separate container
	 */
	public ReteContainerBuildable<PatternDescription> getNextContainer() {
		return new ReteContainerBuildable<PatternDescription>(engine, reteNet.getNextContainer());
	}
	
	public Stub<Address<? extends Supplier>> buildScopeConstrainer(Stub<Address<? extends Supplier>> stub, boolean transitive,
			Object unwrappedContainer, int constrainedIndex) {
		Address<? extends Supplier> root = (transitive) ?
			boundary.accessContainmentTransitiveRoot() :
			boundary.accessContainmentRoot();
		// bind the container element
		Address<? extends Supplier> filteredRoot = 
			targetContainer.getLibrary().accessValueBinderFilterNode(root, 0/*container*/, boundary.wrapElement(unwrappedContainer));
		// build secondary indexer
		int[] secondaryIndices = {1/*contained element*/};
		Address<? extends Indexer> secondary = 
			targetContainer.getLibrary().accessProjectionIndexer(filteredRoot, new TupleMask(secondaryIndices, 2));
		// build primary indexer
		int[] primaryIndices = {constrainedIndex};
		TupleMask primaryMask = new TupleMask(primaryIndices, stub.getVariablesTuple().getSize());
		Address<? extends IterableIndexer> primary = targetContainer.getLibrary().accessProjectionIndexer(stub.getHandle(), primaryMask);
		// build checker
		stub = new Stub<Address<? extends Supplier>>(stub, targetContainer.getLibrary().accessExistenceNode(primary, secondary, false));
		return stub;
	}


	public Address<? extends Receiver> patternCollector(PatternDescription pattern) throws RetePatternBuildException {
		return engine.getBoundary().createProductionInternal(pattern);
	}
	
	/**
	 * No need to distinguish
	 */
	public ReteContainerBuildable<PatternDescription> putOnTab(PatternDescription effort) {
		return this;
	}
	
	
}
