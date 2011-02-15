/*******************************************************************************
 * Copyright (c) 2004-2009 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.StandardNode;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.LeftInheritanceTuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMask;

/**
 * A special node depending on a projection indexer to aggregate tuple groups with the same projection.
 * Only propagates the aggregates of non-empty groups. Use the outer indexers to circumvent.
 * @author Bergmann Gábor
 *
 */
public abstract class AggregatorNode extends StandardNode {

	ProjectionIndexer projection;
	AggregatorNode me;
	
	AggregatorOuterIndexer aggregatorOuterIndexer = null;
	AggregatorOuterIdentityIndexer[] aggregatorOuterIdentityIndexers = null; 

	/**
	 * @param reteContainer
	 * @param projection the projection indexer whose tuple groups should be aggregated
	 */
	public AggregatorNode(ReteContainer reteContainer, ProjectionIndexer projection) {
		super(reteContainer);
		this.me = this;
		this.projection = projection;
		projection.attachListener(new IndexerListener(){
			
			public void notifyIndexerUpdate(Direction direction, Tuple updateElement, Tuple signature, boolean change) {
				aggregateUpdate(direction, updateElement, signature, change);
			}
		});		
	}

	/**
	 * Aggregates (reduces) a group of tuples.
	 * The group can be null.
	 */
	public abstract Object aggregateGroup(Tuple signature, Collection<Tuple> group);

	/**
	 * Aggregates (reduces) a group of tuples, while also returning the previous aggregated value (before the update).
	 * @return an array of length 2: {previousAggregate, currentAggregate}
	 */	
	public abstract Object[] aggregateGroupBeforeAndNow(Tuple signature, Collection<Tuple> currentGroup, Direction direction, Tuple updateElement, boolean change);

	
	protected Tuple aggregateAndPack(Tuple signature, Collection<Tuple> group) {
		return packResult(signature, aggregateGroup(signature, group));
	}
	
	public AggregatorOuterIndexer getAggregatorOuterIndexer() {
		if (aggregatorOuterIndexer==null) 
			aggregatorOuterIndexer = new AggregatorOuterIndexer();
		return aggregatorOuterIndexer;
	}	
	
	public AggregatorOuterIdentityIndexer getAggregatorOuterIdentityIndexer(int resultPositionInSignature) {
		if (aggregatorOuterIdentityIndexers==null) 
			aggregatorOuterIdentityIndexers = new AggregatorOuterIdentityIndexer[projection.getMask().indices.length + 1];
		if (aggregatorOuterIdentityIndexers[resultPositionInSignature]==null)
			aggregatorOuterIdentityIndexers[resultPositionInSignature] = new AggregatorOuterIdentityIndexer(resultPositionInSignature);
		return aggregatorOuterIdentityIndexers[resultPositionInSignature];
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier#pullInto(java.util.Collection)
	 */
	public void pullInto(Collection<Tuple> collector) {
		reteContainer.flushUpdates();
		
		for (Tuple signature: projection.getSignatures()) {
			collector.add(aggregateAndPack(signature, projection.get(signature)));
		}
	}
	
	protected Tuple packResult(Tuple signature, Object result) {
		Object[] resultArray = {result};
		return new LeftInheritanceTuple(signature, resultArray);
	}
	
	protected void aggregateUpdate(Direction direction, Tuple updateElement, Tuple signature, boolean change) {
		Collection<Tuple> currentGroup = projection.get(signature);
		Object[] aggregatedResults = aggregateGroupBeforeAndNow(signature, currentGroup, direction, updateElement, change);
		propagate(Direction.REVOKE, packResult(signature, aggregatedResults[0]), signature);
		propagate(Direction.INSERT, packResult(signature, aggregatedResults[1]), signature);
	}
	
	protected void propagate(Direction direction, Tuple packResult, Tuple signature) {
		propagateUpdate(direction, packResult);
		if (aggregatorOuterIndexer!=null) aggregatorOuterIndexer.propagate(direction, packResult, signature);
		if (aggregatorOuterIdentityIndexers!=null) 
			for (AggregatorOuterIdentityIndexer aggregatorOuterIdentityIndexer : aggregatorOuterIdentityIndexers)
				aggregatorOuterIdentityIndexer.propagate(direction, packResult, signature);
	}
	
	/**
	 * A special non-iterable index that retrieves the aggregated, packed result (signature+aggregate) for the original signature.
	 * @author Bergmann Gábor
	 */
	class AggregatorOuterIndexer extends StandardIndexer {
		public AggregatorOuterIndexer() {
			super(me.reteContainer, projection.getMask());
			this.parent = me.projection.getParent();
		}

		public Collection<Tuple> get(Tuple signature) {	
			return Collections.singleton(aggregateAndPack(signature, projection.get(signature)));
		}

		public void propagate(Direction direction, Tuple packResult, Tuple signature) {
			propagate(direction, packResult, signature, true);
		}
	}
	/**
	 * A special non-iterable index that checks a suspected aggregate value for a given signature. 
	 * The signature for this index is the original signature of the projection index, 
	 * 	with the suspected result inserted at position resultPositionInSignature.
	 * @author Bergmann Gábor
	 */
	
	class AggregatorOuterIdentityIndexer extends StandardIndexer {
		int resultPositionInSignature;
		TupleMask pruneResult;
		TupleMask reorder; 
		
		public AggregatorOuterIdentityIndexer(int resultPositionInSignature) {
			super(me.reteContainer, projection.getMask());
			this.parent = me.projection.getParent();
			this.resultPositionInSignature = resultPositionInSignature;
			int sizeWithResult = projection.getMask().indices.length + 1;
			this.pruneResult = TupleMask.omit(resultPositionInSignature, sizeWithResult);
			if (resultPositionInSignature == sizeWithResult - 1)
				this.reorder = null;
			else 
				this.reorder = TupleMask.displace(sizeWithResult-1, resultPositionInSignature, sizeWithResult);
		}

		public Collection<Tuple> get(Tuple signatureWithResult) {
			Tuple prunedSignature = pruneResult.transform(signatureWithResult);
			Object result = aggregateGroup(prunedSignature, projection.get(prunedSignature));
			if (signatureWithResult.get(resultPositionInSignature).equals(result))
				return Collections.singleton(signatureWithResult);
			else 
				return null;
		}

		public void propagate(Direction direction, Tuple packResult, Tuple signature) {
			Tuple updateElement;
			if (reorder == null)
				updateElement = packResult;
			else 
				updateElement = reorder.transform(packResult);
			propagate(direction, updateElement, updateElement, true);
		}		
	}

}
