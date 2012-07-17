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
package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.single;

import java.util.Collection;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.incscc.IncSCCAlg;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.Tuple;
import org.eclipse.viatra2.emf.incquery.base.itc.graphimpl.Graph;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.ITcDataSource;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.ITcObserver;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Clearable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.FlatTuple;

// TODO egyelore (i,j) elek, majd helyette mask megoldas
// TODO bemeneti index
/**
 * This class represents a transitive closure node in the rete net. 
 * 
 * @author Tamas Szabo
 *
 */
public class TransitiveClosureNode extends SingleInputNode implements Clearable, ITcObserver<Object> {
	
	private Graph<Object> graphDataSource;
	private ITcDataSource<Object> transitiveClosureAlgorithm;
	
	/**
	 * Create a new transitive closure rete node.
	 * Initializes the graph data source with the given collection of tuples.
	 * 
	 * @param reteContainer the rete container of the node
	 * @param tuples the initial collection of tuples
	 */
	public TransitiveClosureNode(ReteContainer reteContainer, Collection<org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple> tuples) {
		super(reteContainer);
		graphDataSource = new Graph<Object>();
		
		for (org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple t : tuples) {
			Object source = t.get(0);
			Object target = t.get(1);
			graphDataSource.insertNode(source);
			graphDataSource.insertNode(target);
			graphDataSource.insertEdge(source, target);
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
	public void pullInto(Collection<org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple> collector) {
		for (Tuple<Object> tuple : ((IncSCCAlg<Object>) transitiveClosureAlgorithm).getTcRelation()) {
			collector.add(new FlatTuple(tuple.getSource(), tuple.getTarget()));
		}	
	}

	@Override
	public void update(Direction direction, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple updateElement) {
		if (updateElement.getSize() == 2) {
			Object source = updateElement.get(0);
			Object target = updateElement.get(1);
			
			if (direction == Direction.INSERT) {
				//Nodes are stored in a set
				graphDataSource.insertNode(source);
				graphDataSource.insertNode(target);
				graphDataSource.insertEdge(source, target);
			}
			if (direction == Direction.REVOKE) {
				graphDataSource.deleteEdge(source, target);
				IncSCCAlg<Object> incscc = (IncSCCAlg<Object>) transitiveClosureAlgorithm;
					
				if (incscc.isIsolated(source)) {
					graphDataSource.deleteNode(source);
				}
				if (incscc.isIsolated(target)) {
					graphDataSource.deleteNode(target);
				}
			}
		}
	}
	
	@Override
	public void clear() {
		graphDataSource = new Graph<Object>();
		transitiveClosureAlgorithm = new IncSCCAlg<Object>(graphDataSource);
	}

	@Override
	public void tupleInserted(Object source, Object target) {
		org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple tuple = new FlatTuple(source, target);
		propagateUpdate(Direction.INSERT, tuple);
	}

	@Override
	public void tupleDeleted(Object source, Object target) {
		org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple tuple = new FlatTuple(source, target);
		propagateUpdate(Direction.REVOKE, tuple);	
	}
	

	@Override
	protected void propagateUpdate(Direction direction,	org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple updateElement) 
	{
		super.propagateUpdate(direction, updateElement);
	}
}
