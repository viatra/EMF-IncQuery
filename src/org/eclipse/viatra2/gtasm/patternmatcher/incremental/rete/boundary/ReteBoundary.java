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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.Indexer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.IterableIndexer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.JoinNode;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext.GeneralizationQueryDirection;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherRuntimeContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Network;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Production;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Tunnel;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.remote.Address;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.single.TrimmerNode;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.FlatTuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMask;


/**
 * Responsible for the storage, maintenance and communication of the nodes of the network 
 * that are accessible form the outside for various reasons. 
 * 
 * @author Bergmann Gábor
 *
 * @param <PatternDescription>
 */
public class ReteBoundary<PatternDescription> {

	protected ReteEngine<PatternDescription> engine;
	protected Network network;
	protected ReteContainer headContainer;
	
	protected IPatternMatcherRuntimeContext<PatternDescription> context;
	IPatternMatcherContext.GeneralizationQueryDirection generalizationQueryDirection;

	/*
	 * arity:1
	 * used as simple entity constraints
	 * label is the object representing the type
	 * null label means all entities regardless of type (global supertype), if allowed
	 */
	protected Map<Object, Address<? extends Tunnel>> unaryRoots; 
	/*
	 * arity:3 (rel, from, to)
	 * used as VPM relation constraints
	 * null label means all relations regardless of type (global supertype)
	 */
	protected Map<Object, Address<? extends Tunnel>> ternaryEdgeRoots;
	/*
	 * arity:2 (from, to)
	 * not used over VPM; can be used as EMF references for instance
	 * label is the object representing the type
	 * null label means all entities regardless of type if allowed (global supertype), if allowed
	 */
	protected Map<Object, Address<? extends Tunnel>> binaryEdgeRoots; 

	protected Map<PatternDescription, Address<? extends Production>> productions;
	//protected Map<PatternDescription, Map<Map<Integer, Scope>, Address<? extends Production>>> productionsScoped; // (pattern, scopemap) -> production
	
	protected Address<? extends Tunnel> containmentRoot;
	protected Address<? extends Supplier> containmentTransitiveRoot;
	protected Address<? extends Tunnel> instantiationRoot;
	protected Address<? extends Supplier> instantiationTransitiveRoot;
	protected Address<? extends Tunnel> generalizationRoot;
	protected Address<? extends Supplier> generalizationTransitiveRoot;
	
	/**
	 * Stubs of parent nodes that have the key node as their child.
	 * For RETE --> Stub traceability, mainly at production nodes.
	 */
	protected Map<Address<? extends Receiver>, Set<Stub<Address<? extends Supplier>>>> parentStubsOfReceiver; 

	/**
	 * Prerequisite: engine has its network and framework fields initialized
	 * 
	 * @param headContainer
	 */
	public ReteBoundary(ReteEngine<PatternDescription> engine) {
		super();
		this.engine = engine;
		this.network = engine.getReteNet();
		this.headContainer = network.getHeadContainer();
		
		this.context = engine.getContext();
		this.generalizationQueryDirection = this.context.allowedGeneralizationQueryDirection();
		this.parentStubsOfReceiver = new HashMap<Address<? extends Receiver>, Set<Stub<Address<? extends Supplier>>>>();

		unaryRoots = new HashMap<Object, Address<? extends Tunnel>>();
		ternaryEdgeRoots = new HashMap<Object, Address<? extends Tunnel>>();
		binaryEdgeRoots = new HashMap<Object, Address<? extends Tunnel>>();		

		productions = new HashMap<PatternDescription, Address<? extends Production>>();
		//productionsScoped = new HashMap<GTPattern, Map<Map<Integer,Scope>,Address<? extends Production>>>();

		containmentRoot = null;
		containmentTransitiveRoot = null;
		instantiationRoot = null;
		generalizationRoot = null;
		generalizationTransitiveRoot = null;
	}

	/**
	 * Wraps the element into a form suitable for entering the network
	 * model element -> internal object
	 */
	public Object wrapElement(Object element) {
		return element;// .getID();
	}

	/**
	 * Unwraps the element into its original form
	 * internal object -> model element
	 */
	public Object unwrapElement(Object wrapper) {
		return wrapper;// modelManager.getElementByID((String)
										// wrapper);
	}

	/**
	 * Unwraps the tuple of elements into a form suitable for entering the
	 * network
	 */
	public Tuple wrapTuple(Tuple unwrapped) {
		// int size = unwrapped.getSize();
		// Object[] elements = new Object[size];
		// for (int i=0; i<size; ++i) elements[i] =
		// wrapElement(unwrapped.get(i));
		// return new FlatTuple(elements);
		return unwrapped;
	}

	/**
	 * Unwraps the tuple of elements into their original form
	 */
	public Tuple unwrapTuple(Tuple wrappers) {
		// int size = wrappers.getSize();
		// Object[] elements = new Object[size];
		// for (int i=0; i<size; ++i) elements[i] =
		// unwrapElement(wrappers.get(i));
		// return new FlatTuple(elements);
		return wrappers;
	}

	/**
	 * fetches the entity Root node under specified label; returns null if it
	 * doesn't exist yet
	 */
	public Address<? extends Tunnel> getUnaryRoot(Object label) {
		return unaryRoots.get(label);
	}

	/**
	 * fetches the relation Root node under specified label; returns null if it
	 * doesn't exist yet
	 */
	public Address<? extends Tunnel> getTernaryEdgeRoot(Object label) {
		return ternaryEdgeRoots.get(label);
	}

	/**
	 * accesses the entity Root node under specified label; creates the node if
	 * it doesn't exist yet
	 */
	public Address<? extends Tunnel> accessUnaryRoot(Object typeObject) {
		Address<? extends Tunnel> tn;
		tn = unaryRoots.get(typeObject);
		if (tn == null) {
			tn = headContainer.getLibrary().newUniquenessEnforcerNode(1, typeObject);
			unaryRoots.put(typeObject, tn);

			
			new EntityFeeder(tn, context, network, this, typeObject).feed();

			if (typeObject != null && generalizationQueryDirection == GeneralizationQueryDirection.BOTH) {
				Collection<? extends Object> subTypes = context.enumerateDirectUnarySubtypes(typeObject);

				for (Object subType : subTypes) {
					Address<? extends Tunnel> subRoot = accessUnaryRoot(subType);
					network.connectRemoteNodes(subRoot, tn, true);
				}
			}

		}
		return tn;
	}

	/**
	 * accesses the relation Root node under specified label; creates the node
	 * if it doesn't exist yet
	 */
	public Address<? extends Tunnel> accessTernaryEdgeRoot(Object typeObject) {
		Address<? extends Tunnel> tn;
		tn = ternaryEdgeRoots.get(typeObject);
		if (tn == null) {
			tn = headContainer.getLibrary().newUniquenessEnforcerNode(3, typeObject);
			ternaryEdgeRoots.put(typeObject, tn);

			new RelationFeeder(tn, context, network, this, typeObject).feed();

			if (typeObject != null && generalizationQueryDirection == GeneralizationQueryDirection.BOTH) {
				Collection<? extends Object> subTypes = context.enumerateDirectTernaryEdgeSubtypes(typeObject);

				for (Object subType : subTypes) {
					Address<? extends Tunnel> subRoot = accessTernaryEdgeRoot(subType);
					network.connectRemoteNodes(subRoot, tn, true);
				}
			}
		}
		return tn;
	}

	

	/**
	 * accesses the reference Root node under specified label; creates the node
	 * if it doesn't exist yet
	 */
	public Address<? extends Tunnel> accessBinaryEdgeRoot(Object typeObject) {
		Address<? extends Tunnel> tn;
		tn = binaryEdgeRoots.get(typeObject);
		if (tn == null) {
			tn = headContainer.getLibrary().newUniquenessEnforcerNode(2, typeObject);
			binaryEdgeRoots.put(typeObject, tn);

			new ReferenceFeeder(tn, context, network, this, typeObject).feed();

			if (typeObject != null && generalizationQueryDirection == GeneralizationQueryDirection.BOTH) {
				Collection<? extends Object> subTypes = context.enumerateDirectBinaryEdgeSubtypes(typeObject);

				for (Object subType : subTypes) {
					Address<? extends Tunnel> subRoot = accessBinaryEdgeRoot(subType);
					network.connectRemoteNodes(subRoot, tn, true);
				}
			}
		}
		return tn;
	}
	
	/**
	 * accesses the special direct containment relation Root node; creates the
	 * node if it doesn't exist yet
	 */
	public Address<? extends Tunnel> accessContainmentRoot() {
		if (containmentRoot == null) {
			// containment: relation quasi-type
			containmentRoot = headContainer.getLibrary()
					.newUniquenessEnforcerNode(2, "$containment");

			new ContainmentFeeder(containmentRoot, context, network, this).feed();
		}
		return containmentRoot;
	}

	/**
	 * accesses the special transitive containment relation Root node; creates
	 * the node if it doesn't exist yet
	 */
	public Address<? extends Supplier> accessContainmentTransitiveRoot() {
		if (containmentTransitiveRoot == null) {
			// transitive containment: derived
			Address<? extends Tunnel> containmentTransitiveRoot = headContainer
					.getLibrary().newUniquenessEnforcerNode(2, "$containmentTransitive");
			network.connectRemoteNodes(accessContainmentRoot(),
					containmentTransitiveRoot, true);

			final int[] actLI = { 1 };
			final int arcLIw = 2;
			final int[] actRI = { 0 };
			final int arcRIw = 2;
			Address<? extends IterableIndexer> jPrimarySlot = headContainer.getLibrary()
					.accessProjectionIndexer(accessContainmentRoot(),
							new TupleMask(actLI, arcLIw));
			Address<? extends IterableIndexer> jSecondarySlot = headContainer.getLibrary()
					.accessProjectionIndexer(containmentTransitiveRoot,
							new TupleMask(actRI, arcRIw));

			final int[] actRIcomp = { 1 };
			final int arcRIwcomp = 2;
			TupleMask complementerMask = new TupleMask(actRIcomp, arcRIwcomp);

			Address<JoinNode> andCT = headContainer.getLibrary()
					.accessJoinNode(jPrimarySlot, jSecondarySlot,
							complementerMask);

			final int[] mask = { 0, 2 };
			final int maskw = 3;
			Address<TrimmerNode> tr = headContainer.getLibrary()
					.accessTrimmerNode(andCT, new TupleMask(mask, maskw));
			network.connectRemoteNodes(tr, containmentTransitiveRoot, true);

			this.containmentTransitiveRoot = containmentTransitiveRoot; // cast
																		// back
																		// to
																		// org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.
																		// Supplier
		}
		return containmentTransitiveRoot;
	}

	/**
	 * accesses the special instantiation relation Root node; creates the node
	 * if it doesn't exist yet
	 */
	public Address<? extends Tunnel> accessInstantiationRoot() {
		if (instantiationRoot == null) {
			// instantiation: relation quasi-type
			instantiationRoot = headContainer.getLibrary()
					.newUniquenessEnforcerNode(2, "$instantiation");

			new InstantiationFeeder(instantiationRoot, context, network, this).feed();
		}
		return instantiationRoot;
	}

	/**
	 * accesses the special transitive instantiation relation Root node; creates
	 * the node if it doesn't exist yet InstantiationTransitive = Instantiation
	 * o (Generalization)^*
	 */
	public Address<? extends Supplier> accessInstantiationTransitiveRoot() {
		if (instantiationTransitiveRoot == null) {
			// transitive instantiation: derived
			Address<? extends Tunnel> instantiationTransitiveRoot = headContainer
					.getLibrary().newUniquenessEnforcerNode(2, "$instantiationTransitive");
			network.connectRemoteNodes(accessInstantiationRoot(),
					instantiationTransitiveRoot, true);

			final int[] actLI = { 1 };
			final int arcLIw = 2;
			final int[] actRI = { 0 };
			final int arcRIw = 2;
			Address<? extends IterableIndexer> jPrimarySlot = headContainer.getLibrary()
					.accessProjectionIndexer(accessGeneralizationRoot(),
							new TupleMask(actLI, arcLIw));
			Address<? extends Indexer> jSecondarySlot = headContainer.getLibrary()
					.accessProjectionIndexer(instantiationTransitiveRoot,
							new TupleMask(actRI, arcRIw));

			final int[] actRIcomp = { 1 };
			final int arcRIwcomp = 2;
			TupleMask complementerMask = new TupleMask(actRIcomp, arcRIwcomp);

			Address<JoinNode> andCT = headContainer.getLibrary()
					.accessJoinNode(jPrimarySlot, jSecondarySlot,
							complementerMask);

			final int[] mask = { 0, 2 };
			final int maskw = 3;
			Address<? extends TrimmerNode> tr = headContainer.getLibrary()
					.accessTrimmerNode(andCT, new TupleMask(mask, maskw));
			network.connectRemoteNodes(tr, instantiationTransitiveRoot, true);

			this.instantiationTransitiveRoot = instantiationTransitiveRoot; // cast
																			// back
																			// to
																			// org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network
																			// .
																			// Supplier
		}
		return instantiationTransitiveRoot;
	}

	/**
	 * accesses the special generalization relation Root node; creates the node
	 * if it doesn't exist yet
	 */
	public Address<? extends Tunnel> accessGeneralizationRoot() {
		if (generalizationRoot == null) {
			// generalization: relation quasi-type
			generalizationRoot = headContainer.getLibrary()
					.newUniquenessEnforcerNode(2, "$generalization");

			new GeneralizationFeeder(generalizationRoot, context, network, this).feed();
		}
		return generalizationRoot;
	}

	/**
	 * accesses the special transitive containment relation Root node; creates
	 * the node if it doesn't exist yet
	 */
	public Address<? extends Supplier> accessGeneralizationTransitiveRoot() {
		if (generalizationTransitiveRoot == null) {
			// transitive generalization: derived
			Address<? extends Tunnel> generalizationTransitiveRoot = headContainer
					.getLibrary().newUniquenessEnforcerNode(2, "$generalizationTransitive");
			network.connectRemoteNodes(accessGeneralizationRoot(),
					generalizationTransitiveRoot, true);

			final int[] actLI = { 1 };
			final int arcLIw = 2;
			final int[] actRI = { 0 };
			final int arcRIw = 2;
			Address<? extends IterableIndexer> jPrimarySlot = headContainer.getLibrary()
					.accessProjectionIndexer(accessGeneralizationRoot(),
							new TupleMask(actLI, arcLIw));
			Address<? extends Indexer> jSecondarySlot = headContainer.getLibrary()
					.accessProjectionIndexer(generalizationTransitiveRoot,
							new TupleMask(actRI, arcRIw));

			final int[] actRIcomp = { 1 };
			final int arcRIwcomp = 2;
			TupleMask complementerMask = new TupleMask(actRIcomp, arcRIwcomp);

			Address<JoinNode> andCT = headContainer.getLibrary()
					.accessJoinNode(jPrimarySlot, jSecondarySlot,
							complementerMask);

			final int[] mask = { 0, 2 };
			final int maskw = 3;
			Address<TrimmerNode> tr = headContainer.getLibrary()
					.accessTrimmerNode(andCT, new TupleMask(mask, maskw));
			network.connectRemoteNodes(tr, generalizationTransitiveRoot, true);

			this.generalizationTransitiveRoot = generalizationTransitiveRoot; // cast
																				// back
																				// to
																				// org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network
																				// .
																				// Supplier
		}
		return generalizationTransitiveRoot;
	}

	// /**
	// * Registers and publishes a supplier under specified label.
	// */
	// public void publishSupplier(Supplier s, Object label)
	// {
	// publishedSuppliers.put(label, s);
	// }
	//	
	// /**
	// * fetches the production node under specified label;
	// * returns null if it doesn't exist yet
	// */
	// public Production getProductionNode(Object label)
	// {
	// return productions.get(label);
	// }
	//	
	// /**
	// * fetches the published supplier under specified label;
	// * returns null if it doesn't exist yet
	// */
	// public Supplier getPublishedSupplier(Object label)
	// {
	// return publishedSuppliers.get(label);
	// }

	/**
	 * accesses the production node for specified pattern; builds pattern matcher if it doesn't exist yet
	 */
	public synchronized Address<? extends Production> accessProduction(PatternDescription gtPattern) throws RetePatternBuildException {
		Address<? extends Production> pn;
		pn = productions.get(gtPattern);
		if (pn == null) {
			construct(gtPattern);
			pn = productions.get(gtPattern);
			if (pn == null) {
				String[] args = {gtPattern.toString()};
				throw new RetePatternBuildException("Unsuccessful creation of production node for pattern {1}", args, gtPattern);
			}
		}
		return pn;
	}
	
	/**
	 * creates the production node for the specified pattern
	 * Contract: only call from the builder (through Buildable) responsible for building this pattern
	 * @throws PatternMatcherCompileTimeException if production node is already created
	 */
	public synchronized Address<? extends Production> createProductionInternal(PatternDescription gtPattern) 
	throws RetePatternBuildException {
		if (productions.containsKey(gtPattern)) {
			String[] args = {gtPattern.toString()};
			throw new RetePatternBuildException("Multiple creation attempts of production node for {1}", args, gtPattern);
		}
		
		HashMap<Object, Integer> posMapping = engine.getBuilder().getPosMapping(gtPattern);
		Address<? extends Production> pn = headContainer.getLibrary().newProductionNode(posMapping, gtPattern);
		productions.put(gtPattern, pn);
		context.reportPatternDependency(gtPattern);
	
		return pn;
	}

//	/**
//	 * accesses the production node for specified pattern and scope map; creates the node if
//	 * it doesn't exist yet
//	 */
//	public synchronized Address<? extends Production> accessProductionScoped(
//			GTPattern gtPattern, Map<Integer, Scope> additionalScopeMap) throws PatternMatcherCompileTimeException {
//		if (additionalScopeMap.isEmpty()) return accessProduction(gtPattern);
//		
//		Address<? extends Production> pn;
//		
//		Map<Map<Integer, Scope>, Address<? extends Production>> scopes = productionsScoped.get(gtPattern);
//		if (scopes == null) {
//			scopes = new HashMap<Map<Integer, Scope>, Address<? extends Production>>();
//			productionsScoped.put(gtPattern, scopes);
//		}	
//		
//		pn = scopes.get(additionalScopeMap);
//		if (pn == null) {
//			Address<? extends Production> unscopedProduction = accessProduction(gtPattern);
//
//			HashMap<Object, Integer> posMapping = headContainer.resolveLocal(unscopedProduction).getPosMapping();
//			pn = headContainer.getLibrary().newProductionNode(posMapping);
//			scopes.put(additionalScopeMap, pn);
//
//			constructScoper(unscopedProduction, additionalScopeMap, pn);
//		}
//		return pn;
//	}
	


	/**
	 * @pre: builder is set
	 */
	protected void construct(PatternDescription gtPattern)
			throws RetePatternBuildException {
		engine.getReteNet().waitForReteTermination();
		engine.getBuilder().construct(gtPattern);
		// production.setDirty(false);
	}

//	protected void constructScoper(
//			Address<? extends Production> unscopedProduction,
//			Map<Integer, Scope> additionalScopeMap,
//			Address<? extends Production> production)
//			throws PatternMatcherCompileTimeException {
//		engine.reteNet.waitForReteTermination();
//		engine.builder.constructScoper(unscopedProduction, additionalScopeMap, production);		
//	}
	
	// /**
	// * Invalidates the subnet constructed for the recognition of a given
	// pattern.
	// * The pattern matcher will have to be rebuilt.
	// * @param gtPattern the pattern whose matcher subnet should be invalidated
	// */
	// public void invalidatePattern(GTPattern gtPattern) {
	// Production production = null;
	// try {
	// production = accessProduction(gtPattern);
	// } catch (PatternMatcherCompileTimeException e) {
	// // this should not occur here, since we already have a production node
	// e.printStackTrace();
	// }
	//		
	// production.tearOff();
	// //production.setDirty(true);
	// }

	// updaters for change notification
	// if the corresponding rete input isn't created yet, call is ignored
	public void updateUnary(Direction direction, Object entity, Object typeObject) {
		Address<? extends Tunnel> root = unaryRoots.get(typeObject);
		if (root != null) {
			network.sendExternalUpdate(root, direction, new FlatTuple(wrapElement(entity)));
			if (!engine.isParallelExecutionEnabled())
				network.waitForReteTermination();
		}
		if (typeObject!=null && generalizationQueryDirection == GeneralizationQueryDirection.SUPERTYPE_ONLY) {
			for (Object superType: context.enumerateDirectUnarySupertypes(typeObject)) {
				updateUnary(direction, entity, superType);
			}
		}
	}
	
	public void updateTernaryEdge(Direction direction, Object relation,
			Object from, Object to, Object typeObject) {
		Address<? extends Tunnel> root = ternaryEdgeRoots.get(typeObject);
		if (root != null) {
			network.sendExternalUpdate(root, direction, new FlatTuple(
					wrapElement(relation), wrapElement(from), wrapElement(to)));	
			if (!engine.isParallelExecutionEnabled())
				network.waitForReteTermination();
		}
		if (typeObject!=null && generalizationQueryDirection == GeneralizationQueryDirection.SUPERTYPE_ONLY) {
			for (Object superType: context.enumerateDirectTernaryEdgeSupertypes(typeObject)) {
				updateTernaryEdge(direction, relation, from, to, superType);
			}
		}	
	}
	
	public void updateBinaryEdge(Direction direction,
			Object from, Object to, Object typeObject) {
		Address<? extends Tunnel> root = binaryEdgeRoots.get(typeObject);
		if (root != null) {
			network.sendExternalUpdate(root, direction, new FlatTuple(
					wrapElement(from), wrapElement(to)));
			if (!engine.isParallelExecutionEnabled())
				network.waitForReteTermination();
		}
		if (typeObject!=null && generalizationQueryDirection == GeneralizationQueryDirection.SUPERTYPE_ONLY) {
			for (Object superType: context.enumerateDirectBinaryEdgeSupertypes(typeObject)) {
				updateBinaryEdge(direction, from, to, superType);
			}
		}						
	}

	public void updateContainment(Direction direction, Object container,
			Object element) {
		if (containmentRoot != null) {
			network.sendExternalUpdate(containmentRoot, direction,
							new FlatTuple(wrapElement(container),
									wrapElement(element)));
			if (!engine.isParallelExecutionEnabled())
				network.waitForReteTermination();
		}
	}

	public void updateInstantiation(Direction direction, Object parent,
			Object child) {
		if (instantiationRoot != null) {
			network.sendExternalUpdate(instantiationRoot, direction,
					new FlatTuple(wrapElement(parent), wrapElement(child)));
			if (!engine.isParallelExecutionEnabled())
				network.waitForReteTermination();
		}
	}

	public void updateGeneralization(Direction direction, Object parent,
			Object child) {
		if (generalizationRoot != null) {
			network.sendExternalUpdate(generalizationRoot, direction,
					new FlatTuple(wrapElement(parent), wrapElement(child)));
			if (!engine.isParallelExecutionEnabled())
				network.waitForReteTermination();
		}
	}

	// no wrapping needed!
	public void notifyEvaluator(Address<? extends Receiver> receiver,
			Tuple tuple) {
		network.sendExternalUpdate(receiver, Direction.INSERT, tuple);
		if (!engine.isParallelExecutionEnabled())
			network.waitForReteTermination();
	}
	
	public void registerParentStubForReceiver(Address<? extends Receiver> receiver, Stub<Address<? extends Supplier>> parentStub) {
		Set<Stub<Address<? extends Supplier>>> parents = parentStubsOfReceiver.get(receiver);
		if (parents == null) {
			parents = new HashSet<Stub<Address<? extends Supplier>>>();
			parentStubsOfReceiver.put(receiver, parents);
		}
		parents.add(parentStub);
	}
	public Set<Stub<Address<? extends Supplier>>> getParentStubsOfReceiver(Address<? extends Receiver> receiver) {
		Set<Stub<Address<? extends Supplier>>> parents = parentStubsOfReceiver.get(receiver);
		if (parents == null) parents = Collections.emptySet();
		return parents;
	}
}
