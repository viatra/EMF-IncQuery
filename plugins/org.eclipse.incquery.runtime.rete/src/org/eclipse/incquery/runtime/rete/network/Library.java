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

package org.eclipse.incquery.runtime.rete.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.incquery.runtime.rete.collections.CollectionsFactory;
import org.eclipse.incquery.runtime.rete.index.CountNode;
import org.eclipse.incquery.runtime.rete.index.ExistenceNode;
import org.eclipse.incquery.runtime.rete.index.Indexer;
import org.eclipse.incquery.runtime.rete.index.IterableIndexer;
import org.eclipse.incquery.runtime.rete.index.JoinNode;
import org.eclipse.incquery.runtime.rete.index.OnetimeIndexer;
import org.eclipse.incquery.runtime.rete.index.ProjectionIndexer;
import org.eclipse.incquery.runtime.rete.misc.ConstantNode;
import org.eclipse.incquery.runtime.rete.remote.Address;
import org.eclipse.incquery.runtime.rete.remote.RemoteReceiver;
import org.eclipse.incquery.runtime.rete.remote.RemoteSupplier;
import org.eclipse.incquery.runtime.rete.single.DefaultProductionNode;
import org.eclipse.incquery.runtime.rete.single.EqualityFilterNode;
import org.eclipse.incquery.runtime.rete.single.InequalityFilterNode;
import org.eclipse.incquery.runtime.rete.single.TransitiveClosureNode;
import org.eclipse.incquery.runtime.rete.single.TransparentNode;
import org.eclipse.incquery.runtime.rete.single.TrimmerNode;
import org.eclipse.incquery.runtime.rete.single.UniquenessEnforcerNode;
import org.eclipse.incquery.runtime.rete.single.ValueBinderFilterNode;
import org.eclipse.incquery.runtime.rete.tuple.FlatTuple;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.tuple.TupleMask;
import org.eclipse.incquery.runtime.rete.util.Options;

/**
 * Stores the internal parts of a rete network. Nodes are stored according to type and parameters.
 * 
 * @author Gabor Bergmann
 */
public class Library {

    // boolean activeStorage = true;

    ReteContainer reteContainer;

    Map<Tuple, ProjectionIndexer> projectionIndexers; // Tuple<supplierId, mask>
    Map<Tuple, CountNode> countNodes; // Tuple<supplierId, mask>
    Map<Tuple, JoinNode> joinNodes; // Tuple<Indexer primarySlot, Indexer
                                    // secondarySlot, TupleMask complementer>
    Map<Tuple, ExistenceNode> existenceNodes; // Tuple<Indexer primarySlot,
                                              // Indexer secondarySlot,
                                              // boolean negative>
    Map<Tuple, InequalityFilterNode> ineqFilters; // Tuple<Supplier parent,
                                                  // Integer subject,
                                                  // inequalityMask>
    Map<Tuple, EqualityFilterNode> eqFilters; // Tuple<Supplier parent,
                                              // Tuple~int[] indices>
    Map<Tuple, ValueBinderFilterNode> valueBinderFilters; // Tuple<supplierId, bindingIndex, bindingValue>
    Map<Tuple, TrimmerNode> trimmers; // Tuple<supplierId, mask>
    Map<Supplier, TransparentNode> transparentNodes; // Tuple<supplierId, mask>
    Map<Tuple, ConstantNode> constantNodes; // Tuple constants
    Map<Tuple, TransitiveClosureNode> tcNodes;

    Map<Supplier, RemoteReceiver> remoteReceivers;
    Map<Address<? extends Supplier>, RemoteSupplier> remoteSuppliers;

    /**
     * @param reteContainer
     *            the ReteNet whose interior is to be mapped.
     */
    public Library(ReteContainer reteContainer) {
        super();
        this.reteContainer = reteContainer;

        projectionIndexers = //new HashMap<Tuple, ProjectionIndexer>();
                CollectionsFactory.getMap();
        joinNodes = CollectionsFactory.getMap();//new HashMap<Tuple, JoinNode>();
        existenceNodes = CollectionsFactory.getMap();//new HashMap<Tuple, ExistenceNode>();
        ineqFilters = CollectionsFactory.getMap();//new HashMap<Tuple, InequalityFilterNode>();
        eqFilters = CollectionsFactory.getMap();//new HashMap<Tuple, EqualityFilterNode>();
        valueBinderFilters = CollectionsFactory.getMap();//new HashMap<Tuple, ValueBinderFilterNode>();
        trimmers = CollectionsFactory.getMap();//new HashMap<Tuple, TrimmerNode>();
        transparentNodes = CollectionsFactory.getMap();//new HashMap<Supplier, TransparentNode>();
        constantNodes = CollectionsFactory.getMap();//new HashMap<Tuple, ConstantNode>();
        countNodes = CollectionsFactory.getMap();//new HashMap<Tuple, CountNode>();
        tcNodes = CollectionsFactory.getMap();//new HashMap<Tuple, TransitiveClosureNode>();

        remoteReceivers = CollectionsFactory.getMap();//new HashMap<Supplier, RemoteReceiver>();
        remoteSuppliers = CollectionsFactory.getMap();//new HashMap<Address<? extends Supplier>, RemoteSupplier>();
    }

    synchronized RemoteReceiver accessRemoteReceiver(Address<? extends Supplier> address) {
        if (!reteContainer.isLocal(address))
            return address.getContainer().getLibrary().accessRemoteReceiver(address);
        Supplier localSupplier = reteContainer.resolveLocal(address);
        RemoteReceiver result = remoteReceivers.get(localSupplier);
        if (result == null) {
            result = new RemoteReceiver(reteContainer);
            reteContainer.connect(localSupplier, result); // stateless node, no
                                                          // synch required

            if (Options.nodeSharingOption != Options.NodeSharingOption.NEVER)
                remoteReceivers.put(localSupplier, result);
        }
        return result;
    }

    /**
     * @pre: address is NOT local
     */
    synchronized RemoteSupplier accessRemoteSupplier(Address<? extends Supplier> address) {
        RemoteSupplier result = remoteSuppliers.get(address);
        if (result == null) {
            result = new RemoteSupplier(reteContainer, address.getContainer().getLibrary()
                    .accessRemoteReceiver(address));
            // network.connectAndSynchronize(supplier, result);

            if (Options.nodeSharingOption != Options.NodeSharingOption.NEVER)
                remoteSuppliers.put(address, result);
        }
        return result;
    }

    /**
     * The powerful method for accessing any (supplier) Address as a local supplier.
     */
    public Supplier asSupplier(Address<? extends Supplier> address) {
        if (!reteContainer.isLocal(address))
            return accessRemoteSupplier(address);
        else
            return reteContainer.resolveLocal(address);
    }

    public Address<ProjectionIndexer> accessProjectionIndexer(Address<? extends Supplier> supplierAddress,
            TupleMask mask) {
        Supplier supplier = asSupplier(supplierAddress);
        return reteContainer.makeAddress(accessProjectionIndexer(supplier, mask));
    }

    public Address<CountNode> accessCountNode(Address<? extends Supplier> supplierAddress, TupleMask mask) {
        Supplier supplier = asSupplier(supplierAddress);
        return reteContainer.makeAddress(accessCountNode(supplier, mask));
    }

    public Address<? extends Indexer> accessCountOuterIndexer(Address<? extends Supplier> supplierAddress,
            TupleMask mask) {
        Supplier supplier = asSupplier(supplierAddress);
        return reteContainer.makeAddress(accessCountNode(supplier, mask).getAggregatorOuterIndexer());
    }

    public Address<? extends Indexer> accessCountOuterIdentityIndexer(Address<? extends Supplier> supplierAddress,
            TupleMask mask, int resultPositionInSignature) {
        Supplier supplier = asSupplier(supplierAddress);
        return reteContainer.makeAddress(accessCountNode(supplier, mask).getAggregatorOuterIdentityIndexer(
                resultPositionInSignature));
    }

    // local version
    public synchronized ProjectionIndexer accessProjectionIndexer(Supplier supplier, TupleMask mask) {
        Object[] paramsArray = { supplier.getNodeId(), mask };
        Tuple params = new FlatTuple(paramsArray);
        ProjectionIndexer result = projectionIndexers.get(params);
        if (result == null) {
            result = supplier.constructIndex(mask);
            if (Options.nodeSharingOption != Options.NodeSharingOption.NEVER)
                projectionIndexers.put(params, result);
        }
        return result;
    }

    // local version
    public synchronized CountNode accessCountNode(Supplier supplier, TupleMask mask) {
        Object[] paramsArray = { supplier.getNodeId(), mask };
        Tuple params = new FlatTuple(paramsArray);
        CountNode result = countNodes.get(params);
        if (result == null) {
            result = new CountNode(reteContainer, accessProjectionIndexer(supplier, mask));

            if (Options.nodeSharingOption != Options.NodeSharingOption.NEVER)
                countNodes.put(params, result);
        }
        return result;
    }

    // local version
    public synchronized ProjectionIndexer accessProjectionIndexerOnetime(Supplier supplier, TupleMask mask) {
        if (Options.nodeSharingOption != Options.NodeSharingOption.NEVER)
            return accessProjectionIndexer(supplier, mask);

        reteContainer.flushUpdates();
        OnetimeIndexer result = new OnetimeIndexer(reteContainer, mask);
        reteContainer.sendConstructionUpdates(result, Direction.INSERT, reteContainer.pullContents(supplier));
        reteContainer.flushUpdates();

        return result;
    }

    // local, read-only version
    public synchronized ProjectionIndexer peekProjectionIndexer(Supplier supplier, TupleMask mask) {
        Object[] paramsArray = { supplier.getNodeId(), mask };
        Tuple params = new FlatTuple(paramsArray);
        return projectionIndexers.get(params);
    }

    /**
     * @pre: both projectionIndexers must be local to this container.
     */
    public synchronized Address<JoinNode> accessJoinNode(Address<? extends IterableIndexer> primaryIndexer,
            Address<? extends Indexer> secondaryIndexer, TupleMask complementer) {
        Slots slots = avoidActiveNodeConflict(reteContainer.resolveLocal(primaryIndexer),
                reteContainer.resolveLocal(secondaryIndexer));
        IterableIndexer primarySlot = slots.primary;
        Indexer secondarySlot = slots.secondary;

        Object[] paramsArray = { primarySlot.getNodeId(), secondarySlot.getNodeId(), complementer };
        Tuple params = new FlatTuple(paramsArray);
        JoinNode result = joinNodes.get(params);
        if (result == null) {
            result = new JoinNode(reteContainer, primarySlot, secondarySlot, complementer);
            // network.connectAndSynchronize(supplier, result);

            if (Options.nodeSharingOption == Options.NodeSharingOption.ALL)
                joinNodes.put(params, result);
        }
        return reteContainer.makeAddress(result);
    }

    /**
     * If two indexers share their active node, joining them via DualInputNode is error-prone. Exception: coincidence of
     * the two indexers is supported.
     * 
     * @return a replacement for the secondary Indexers, if needed
     */
    private Slots avoidActiveNodeConflict(final IterableIndexer primarySlot, final Indexer secondarySlot) {
        Slots result = new Slots() {
            {
                primary = primarySlot;
                secondary = secondarySlot;
            }
        };
        if (activeNodeConflict(primarySlot, secondarySlot))
            if (secondarySlot instanceof IterableIndexer)
                result.secondary = accessActiveIndexer((IterableIndexer) secondarySlot);
            else
                result.primary = accessActiveIndexer(primarySlot);
        return result;
    }

    private static class Slots {
        IterableIndexer primary;
        Indexer secondary;
    }

    /**
     * Returns a copy of the given indexer that is an active node by itself (created if does not exist). (Convention:
     * attached with same mask to a transparent node that is attached to parent node.) Node is created if it does not
     * exist yet.
     * 
     * @return an identical but active indexer
     */
    private ProjectionIndexer accessActiveIndexer(IterableIndexer indexer) {
        TransparentNode transparent = accessTransparentNodeInternal(indexer.getParent());
        return accessProjectionIndexer(transparent, indexer.getMask());
    }

    // /**
    // * Read-only-check for a copy of the given indexer that is an active node by itself.
    // * (Expected convention: attached with same mask to a transparent node that is attached to parent node.)
    // * @return an identical but active indexer, if such exists, null otherwise
    // */
    // private ProjectionIndexer peekActiveIndexer(Indexer indexer) {
    // TransparentNode transparent = transparentNodes.get(indexer.getParent());
    // if (transparent != null) {
    // return peekProjectionIndexer(transparent, indexer.getMask());
    // }
    // return null;
    // }

    /**
     * If two indexers share their active node, joining them via DualInputNode is error-prone. Exception: coincidence of
     * the two indexers is supported.
     * 
     * @return true if there is a conflict of active nodes.
     */
    private boolean activeNodeConflict(Indexer primarySlot, Indexer secondarySlot) {
        return !primarySlot.equals(secondarySlot) && primarySlot.getActiveNode().equals(secondarySlot.getActiveNode());
    }

    /**
     * @pre: both projectionIndexers must be local to this container.
     */
    public synchronized Address<ExistenceNode> accessExistenceNode(Address<? extends IterableIndexer> primaryIndexer,
            Address<? extends Indexer> secondaryIndexer, boolean negative) {
        Slots slots = avoidActiveNodeConflict(reteContainer.resolveLocal(primaryIndexer),
                reteContainer.resolveLocal(secondaryIndexer));
        IterableIndexer primarySlot = slots.primary;
        Indexer secondarySlot = slots.secondary;

        Object[] paramsArray = { primarySlot.getNodeId(), secondarySlot.getNodeId(), negative };
        Tuple params = new FlatTuple(paramsArray);
        ExistenceNode result = existenceNodes.get(params);
        if (result == null) {
            result = new ExistenceNode(reteContainer, primarySlot, secondarySlot, negative);
            // network.connectAndSynchronize(supplier, result);

            if (Options.nodeSharingOption == Options.NodeSharingOption.ALL)
                existenceNodes.put(params, result);
        }
        return reteContainer.makeAddress(result);
    }

    public synchronized Address<InequalityFilterNode> accessInequalityFilterNode(
            Address<? extends Supplier> supplierAddress, int subject, TupleMask inequalityMask) {
        Supplier supplier = asSupplier(supplierAddress);
        Object[] paramsArray = { supplier.getNodeId(), subject, inequalityMask };
        Tuple params = new FlatTuple(paramsArray);
        InequalityFilterNode result = ineqFilters.get(params);
        if (result == null) {
            result = new InequalityFilterNode(reteContainer, subject, inequalityMask);
            reteContainer.connect(supplier, result); // stateless node, no synch
                                                     // required

            if (Options.nodeSharingOption == Options.NodeSharingOption.ALL)
                ineqFilters.put(params, result);
        }
        return reteContainer.makeAddress(result);
    }

    public synchronized Address<ValueBinderFilterNode> accessValueBinderFilterNode(
            Address<? extends Supplier> supplierAddress, int bindingIndex, Object bindingValue) {
        Supplier supplier = asSupplier(supplierAddress);
        Object[] paramsArray = { supplier.getNodeId(), bindingIndex, bindingValue };
        Tuple params = new FlatTuple(paramsArray);
        ValueBinderFilterNode result = valueBinderFilters.get(params);
        if (result == null) {
            result = new ValueBinderFilterNode(reteContainer, bindingIndex, bindingValue);
            reteContainer.connect(supplier, result); // stateless node, no synch
                                                     // required

            if (Options.nodeSharingOption == Options.NodeSharingOption.ALL)
                valueBinderFilters.put(params, result);
        }
        return reteContainer.makeAddress(result);
    }

    public synchronized Address<EqualityFilterNode> accessEqualityFilterNode(
            Address<? extends Supplier> supplierAddress, int[] indices) {
        Supplier supplier = asSupplier(supplierAddress);
        Object[] paramsArray = { supplier.getNodeId(), new FlatTuple(indices) };
        Tuple params = new FlatTuple(paramsArray);
        EqualityFilterNode result = eqFilters.get(params);
        if (result == null) {
            result = new EqualityFilterNode(reteContainer, indices);
            reteContainer.connect(supplier, result); // stateless node, no synch
                                                     // required

            if (Options.nodeSharingOption == Options.NodeSharingOption.ALL)
                eqFilters.put(params, result);
        }
        return reteContainer.makeAddress(result);
    }

    public synchronized Address<TrimmerNode> accessTrimmerNode(Address<? extends Supplier> supplierAddress,
            TupleMask mask) {
        Supplier supplier = asSupplier(supplierAddress);
        Object[] paramsArray = { supplier.getNodeId(), mask };
        Tuple params = new FlatTuple(paramsArray);
        TrimmerNode result = trimmers.get(params);
        if (result == null) {
            result = new TrimmerNode(reteContainer, mask);
            reteContainer.connect(supplier, result); // stateless node, no synch
                                                     // required

            if (Options.nodeSharingOption == Options.NodeSharingOption.ALL)
                trimmers.put(params, result);
        }
        return reteContainer.makeAddress(result);
    }

    public synchronized Address<TransparentNode> accessTransparentNode(Address<? extends Supplier> supplierAddress) {
        Supplier supplier = asSupplier(supplierAddress);
        TransparentNode result = accessTransparentNodeInternal(supplier);
        return reteContainer.makeAddress(result);
    }

    private TransparentNode accessTransparentNodeInternal(Supplier supplier) {
        Supplier params = supplier;
        TransparentNode result = transparentNodes.get(params);
        if (result == null) {
            result = new TransparentNode(reteContainer);
            reteContainer.connect(supplier, result); // stateless node, no synch
                                                     // required

            if (Options.nodeSharingOption == Options.NodeSharingOption.ALL)
                transparentNodes.put(params, result);
        }
        return result;
    }

    public synchronized Address<ConstantNode> accessConstantNode(Tuple constants) {
        // Object[] paramsArray = {supplier.getNodeId(), mask};
        Tuple params = constants;// new FlatTuple(paramsArray);
        ConstantNode result = constantNodes.get(params);
        if (result == null) {
            result = new ConstantNode(reteContainer, constants);
            // network.connectAndSynchronize(supplier, result)

            if (Options.nodeSharingOption == Options.NodeSharingOption.ALL)
                constantNodes.put(params, result);
        }
        return reteContainer.makeAddress(result);
    }

    // public synchronized void registerSpecializedProjectionIndexer(Node node, ProjectionIndexer indexer) {
    // if (Options.nodeSharingOption != Options.NodeSharingOption.NEVER) {
    // Object[] paramsArray = { node.getNodeId(), indexer.getMask() };
    // Tuple params = new FlatTuple(paramsArray);
    // projectionIndexers.put(params, indexer);
    // }
    // }

    public synchronized Address<UniquenessEnforcerNode> newUniquenessEnforcerNode(int tupleWidth, Object tag) {
        UniquenessEnforcerNode node = new UniquenessEnforcerNode(reteContainer, tupleWidth);
        node.setTag(tag);
        Address<UniquenessEnforcerNode> address = reteContainer.makeAddress(node);
        return address;
    }

    public synchronized Address<? extends Production> newProductionNode(Map<Object, Integer> posMapping, Object tag) {
        DefaultProductionNode node = new DefaultProductionNode(reteContainer, posMapping);
        node.setTag(tag);
        Address<? extends Production> address = reteContainer.makeAddress(node);
        return address;
    }

    public synchronized Address<TransitiveClosureNode> accessTransitiveClosureNode(
            Address<? extends Supplier> supplierAddress) {
        Supplier supplier = asSupplier(supplierAddress);
        Object[] paramsArray = { supplier.getNodeId() };
        Tuple params = new FlatTuple(paramsArray);
        TransitiveClosureNode result = tcNodes.get(params);
        if (result == null) {
            Collection<Tuple> tuples = new ArrayList<Tuple>();
            supplier.pullInto(tuples);
            result = new TransitiveClosureNode(reteContainer, tuples);
            // reteContainer.connectAndSynchronize(supplier, result);
            reteContainer.connect(supplier, result);

            if (Options.nodeSharingOption == Options.NodeSharingOption.ALL)
                tcNodes.put(params, result);
        }
        return reteContainer.makeAddress(result);
    }
}
