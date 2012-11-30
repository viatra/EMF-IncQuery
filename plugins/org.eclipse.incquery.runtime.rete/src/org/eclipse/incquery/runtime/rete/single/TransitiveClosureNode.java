/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.rete.single;

import java.util.Collection;

import org.eclipse.incquery.runtime.base.itc.alg.incscc.IncSCCAlg;
import org.eclipse.incquery.runtime.base.itc.alg.misc.Tuple;
import org.eclipse.incquery.runtime.base.itc.graphimpl.Graph;
import org.eclipse.incquery.runtime.base.itc.igraph.ITcDataSource;
import org.eclipse.incquery.runtime.base.itc.igraph.ITcObserver;
import org.eclipse.incquery.runtime.rete.network.Direction;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.tuple.Clearable;
import org.eclipse.incquery.runtime.rete.tuple.FlatTuple;

// TODO egyelore (i,j) elek, majd helyette mask megoldas
// TODO bemeneti index
/**
 * This class represents a transitive closure node in the rete net.
 * 
 * @author Gabor Bergmann
 * 
 */
public class TransitiveClosureNode extends SingleInputNode implements Clearable, ITcObserver<Object> {

    private Graph<Object> graphDataSource;
    private ITcDataSource<Object> transitiveClosureAlgorithm;

    /**
     * Create a new transitive closure rete node. Initializes the graph data source with the given collection of tuples.
     * 
     * @param reteContainer
     *            the rete container of the node
     * @param tuples
     *            the initial collection of tuples
     */
    public TransitiveClosureNode(ReteContainer reteContainer,
            Collection<org.eclipse.incquery.runtime.rete.tuple.Tuple> tuples) {
        super(reteContainer);
        graphDataSource = new Graph<Object>();

        for (org.eclipse.incquery.runtime.rete.tuple.Tuple t : tuples) {
            graphDataSource.insertNode(t.get(0));
            graphDataSource.insertNode(t.get(1));
            graphDataSource.insertEdge(t.get(0), t.get(1));
        }
        transitiveClosureAlgorithm = new IncSCCAlg<Object>(graphDataSource);
        transitiveClosureAlgorithm.attachObserver(this);
        reteContainer.registerClearable(this);
    }

    public TransitiveClosureNode(ReteContainer reteContainer) {
        super(reteContainer);
        graphDataSource = new Graph<Object>();
        transitiveClosureAlgorithm = new IncSCCAlg<Object>(graphDataSource);
        transitiveClosureAlgorithm.attachObserver(this);
        reteContainer.registerClearable(this);
    }

    @Override
    public void pullInto(Collection<org.eclipse.incquery.runtime.rete.tuple.Tuple> collector) {
        for (Tuple<Object> tuple : ((IncSCCAlg<Object>) transitiveClosureAlgorithm).getTcRelation()) {
            collector.add(new FlatTuple(tuple.getSource(), tuple.getTarget()));
        }
    }

    @Override
    public void update(Direction direction, org.eclipse.incquery.runtime.rete.tuple.Tuple updateElement) {
        if (updateElement.getSize() == 2) {
            Object source = updateElement.get(0);
            Object target = updateElement.get(1);

            if (direction == Direction.INSERT) {
                graphDataSource.insertNode(source);
                graphDataSource.insertNode(target);
                graphDataSource.insertEdge(source, target);
            }
            if (direction == Direction.REVOKE) {
                graphDataSource.deleteEdge(source, target);

                if (((IncSCCAlg<Object>) transitiveClosureAlgorithm).isIsolated(source)) {
                    graphDataSource.deleteNode(source);
                }
                if (!source.equals(target) && ((IncSCCAlg<Object>) transitiveClosureAlgorithm).isIsolated(target)) {
                    graphDataSource.deleteNode(target);
                }
            }
        }
    }

    @Override
    public void clear() {
        transitiveClosureAlgorithm.dispose();
        graphDataSource = new Graph<Object>();
        transitiveClosureAlgorithm = new IncSCCAlg<Object>(graphDataSource);
    }

    @Override
    public void tupleInserted(Object source, Object target) {
        org.eclipse.incquery.runtime.rete.tuple.Tuple tuple = new FlatTuple(source, target);
        propagateUpdate(Direction.INSERT, tuple);
    }

    @Override
    public void tupleDeleted(Object source, Object target) {
        org.eclipse.incquery.runtime.rete.tuple.Tuple tuple = new FlatTuple(source, target);
        propagateUpdate(Direction.REVOKE, tuple);
    }

    @Override
    protected void propagateUpdate(Direction direction, org.eclipse.incquery.runtime.rete.tuple.Tuple updateElement) {
        super.propagateUpdate(direction, updateElement);
    }
}
