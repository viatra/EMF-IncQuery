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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Node;
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
 	int sourceWidth;
	Map<Tuple,Object> mainAggregates;
	
	AggregatorOuterIndexer aggregatorOuterIndexer = null;
	AggregatorOuterIdentityIndexer[] aggregatorOuterIdentityIndexers = null; 

	/**
	 * @param reteContainer
	 * @param projection the projection indexer whose tuple groups should be aggregated
	 */
	public AggregatorNode(ReteContainer reteContainer, ProjectionIndexer projection) {
		super(reteContainer);
		this.me = this;
		mainAggregates = new HashMap<Tuple, Object>();
		this.projection = projection;
		this.sourceWidth = projection.getMask().indices.length;
		for (Tuple signature : projection.getSignatures()) {
			mainAggregates.put(signature, aggregateGroup(signature, projection.get(signature)));
		}
		projection.attachListener(new DefaultIndexerListener(this){		
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
	 * Aggregates (reduces) a group of tuples, having access to the previous aggregated value (before the update) and the update definition.
	 * Defaults to aggregateGroup(). Override to increase performance.
	 */	
	public Object aggregateGroupAfterUpdate(Tuple signature, Collection<Tuple> currentGroup, Object oldAggregate, Direction direction, Tuple updateElement, boolean change) {
		return aggregateGroup(signature, currentGroup);
	}

	
	protected Tuple aggregateAndPack(Tuple signature, Collection<Tuple> group) {
		return packResult(signature, aggregateGroup(signature, group));
	}
	
	public AggregatorOuterIndexer getAggregatorOuterIndexer() {
		if (aggregatorOuterIndexer==null) {
			aggregatorOuterIndexer = new AggregatorOuterIndexer();
			//reteContainer.connectAndSynchronize(this, aggregatorOuterIndexer);
		}
		return aggregatorOuterIndexer;
	}	
	
	public AggregatorOuterIdentityIndexer getAggregatorOuterIdentityIndexer(int resultPositionInSignature) {
		if (aggregatorOuterIdentityIndexers==null) 
			aggregatorOuterIdentityIndexers = new AggregatorOuterIdentityIndexer[sourceWidth + 1];
		if (aggregatorOuterIdentityIndexers[resultPositionInSignature]==null) {
			aggregatorOuterIdentityIndexers[resultPositionInSignature] = new AggregatorOuterIdentityIndexer(resultPositionInSignature);
			//reteContainer.connectAndSynchronize(this, aggregatorOuterIdentityIndexers[resultPositionInSignature]);
		}
		return aggregatorOuterIdentityIndexers[resultPositionInSignature];
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier#pullInto(java.util.Collection)
	 */
	public void pullInto(Collection<Tuple> collector) {
		for (Tuple signature: mainAggregates.keySet()) {
			collector.add(packResult(signature, mainAggregates.get(signature)));
		}
	}
	
	protected Tuple packResult(Tuple signature, Object result) {
		Object[] resultArray = {result};
		return new LeftInheritanceTuple(signature, resultArray);
	}
	
	protected void aggregateUpdate(Direction direction, Tuple updateElement, Tuple signature, boolean change) {
		Collection<Tuple> currentGroup = projection.get(signature);
		// these will be null if group is empty
		Object oldAggregate = mainAggregates.get(signature);
		Object safeOldAggregate = oldAggregate == null? aggregateGroup(signature, null) : oldAggregate;
		boolean empty = currentGroup==null || currentGroup.isEmpty();
		Object newAggregate = empty ? null :
				aggregateGroupAfterUpdate(signature, currentGroup, safeOldAggregate/*non-null*/, direction, updateElement, change);
		if (!empty) mainAggregates.put(signature, newAggregate); else mainAggregates.remove(signature);
		Tuple oldTuple = packResult(signature, safeOldAggregate);
		Tuple newTuple = packResult(signature, newAggregate == null? aggregateGroup(signature, null) : newAggregate);
		if (oldAggregate != null) propagateUpdate(Direction.REVOKE, oldTuple); // direct outputs lack non-empty groups
		if (newAggregate != null) propagateUpdate(Direction.INSERT, newTuple); // direct outputs lack non-empty groups
		if (aggregatorOuterIndexer!=null) aggregatorOuterIndexer.propagate(signature, oldTuple, newTuple);
		if (aggregatorOuterIdentityIndexers!=null) 
			for (AggregatorOuterIdentityIndexer aggregatorOuterIdentityIndexer : aggregatorOuterIdentityIndexers)
				if (aggregatorOuterIdentityIndexer != null) 
					aggregatorOuterIdentityIndexer.propagate(signature, oldTuple, newTuple);
	}
	
//	protected void propagate(Direction direction, Tuple packResult, Tuple signature) {
//		propagateUpdate(direction, packResult);
//		if (aggregatorOuterIndexer!=null) aggregatorOuterIndexer.propagate(direction, packResult, signature);
//		if (aggregatorOuterIdentityIndexers!=null) 
//			for (AggregatorOuterIdentityIndexer aggregatorOuterIdentityIndexer : aggregatorOuterIdentityIndexers)
//				aggregatorOuterIdentityIndexer.propagate(direction, packResult, signature);
//	}

	private Object getAggregate(Tuple signature) {
		Object aggregate = mainAggregates.get(signature);
		return aggregate == null? aggregateGroup(signature, null) : aggregate;
	}
	
	/**
	 * A special non-iterable index that retrieves the aggregated, packed result (signature+aggregate) for the original signature.
	 * @author Bergmann Gábor
	 */
	class AggregatorOuterIndexer extends StandardIndexer {
		//private Map<Tuple,Tuple> localAggregates;
		
		public AggregatorOuterIndexer() {
			super(me.reteContainer, TupleMask.omit(sourceWidth, sourceWidth+1));
			this.parent = me;
			//this.localAggregates = new HashMap<Tuple, Tuple>();
//			
//			for (Tuple signature: projection.getSignatures()) {
//				localAggregates.put(signature, aggregateGroup(signature, projection.get(signature)));
//			}		
			
		}

		public Collection<Tuple> get(Tuple signature) {	
			return Collections.singleton(packResult(signature, getAggregate(signature)));
		}
		
		public void propagate(Tuple signature, Tuple oldTuple, Tuple newTuple) {
			propagate(Direction.INSERT, newTuple, signature, false);
			propagate(Direction.REVOKE, oldTuple, signature, false);							
		}

//		@Override
//		public void update(Direction direction, Tuple updateElement) {
//			Tuple signature = mask.transform(updateElement);
//			Tuple neutral = aggregateAndPack(signature, null);
//			if (direction == Direction.INSERT) {
//				propagate(Direction.INSERT, updateElement, signature, false);
//				propagate(Direction.REVOKE, neutral, signature, false);				
//				localAggregates.put(signature, updateElement);
//			} else {
//				localAggregates.remove(signature); 
//				propagate(Direction.INSERT, neutral, signature, false);				
//				propagate(Direction.REVOKE, updateElement, signature, false);
//			} 
//		}

//		private Object getLocalAggregate(Tuple signature) {
//			Tuple resultTuple = localAggregates.get(signature);
//			return resultTuple == null? aggregateGroup(signature, null) : resultTuple.get(sourceWidth);
//		}

		@Override
		public Node getActiveNode() {
			return projection.getActiveNode();
		}

	}
	/**
	 * A special non-iterable index that checks a suspected aggregate value for a given signature. 
	 * The signature for this index is the original signature of the projection index, 
	 * 	with the suspected result inserted at position resultPositionInSignature.
	 * @author Bergmann Gábor
	 */
	
	class AggregatorOuterIdentityIndexer extends StandardIndexer /*implements Receiver*/ {
//		private Map<Tuple,Tuple> localAggregates;
		int resultPositionInSignature;
		TupleMask pruneResult;
		TupleMask reorder; 
		
		public AggregatorOuterIdentityIndexer(int resultPositionInSignature) {
			super(me.reteContainer, TupleMask.displace(sourceWidth, resultPositionInSignature, sourceWidth+1));
			this.parent = me;
//			this.localAggregates = new HashMap<Tuple, Tuple>();
			this.resultPositionInSignature = resultPositionInSignature;
			this.pruneResult = TupleMask.omit(resultPositionInSignature, sourceWidth+1);
			if (resultPositionInSignature == sourceWidth)
				this.reorder = null;
			else 
				this.reorder = mask;
		}

		public Collection<Tuple> get(Tuple signatureWithResult) {
			Tuple prunedSignature = pruneResult.transform(signatureWithResult);
			Object result = getAggregate(prunedSignature);
			if (signatureWithResult.get(resultPositionInSignature).equals(result))
				return Collections.singleton(signatureWithResult);
			else 
				return null;
		}
		
		public void propagate(Tuple signature, Tuple oldTuple, Tuple newTuple) {
			propagate(Direction.INSERT, reorder(newTuple), signature, true);
			propagate(Direction.REVOKE, reorder(oldTuple), signature, true);							
		}

//		@Override
//		public void update(Direction direction, Tuple signatureWithResult) {
//			Tuple prunedSignature = pruneResult.transform(signatureWithResult);
//			if (direction == Direction.INSERT) 
//				localAggregates.put(prunedSignature, signatureWithResult);
//			else
//				localAggregates.remove(prunedSignature);
//			Tuple transformed = reorder(signatureWithResult);
//			propagate(direction, transformed, transformed, true);
//		}


		private Tuple reorder(Tuple signatureWithResult) {
			Tuple transformed;
			if (reorder == null)
				transformed = signatureWithResult;
			else 
				transformed = reorder.transform(signatureWithResult);
			return transformed;
		}

//		private Object getLocalAggregate(Tuple signature) {
//			Tuple resultTuple = localAggregates.get(signature);
//			return resultTuple == null? aggregateGroup(signature, null) : resultTuple.get(resultPositionInSignature);
//		}
		
		@Override
		public Node getActiveNode() {
			return projection.getActiveNode();
		}		
	}

}
