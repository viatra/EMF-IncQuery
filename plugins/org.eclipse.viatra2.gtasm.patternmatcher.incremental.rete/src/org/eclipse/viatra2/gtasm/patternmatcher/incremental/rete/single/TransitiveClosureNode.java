package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.single;

import java.util.Collection;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.dred.DRedAlg;
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
	
	private Graph<Object> gds;
	private ITcDataSource<Object> tcAlg;
	public static byte alg = 1;
	
	/**
	 * Create a new transitive closure rete node.
	 * Initializes the graph data source with the given collection of tuples.
	 * 
	 * @param reteContainer the rete container of the node
	 * @param tuples the initial collection of tuples
	 */
	public TransitiveClosureNode(ReteContainer reteContainer, Collection<org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple> tuples) {
		super(reteContainer);
		gds = new Graph<Object>();
		
		for (org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple t : tuples) {
			Object source = t.get(0);
			Object target = t.get(1);
			gds.insertNode(source);
			gds.insertNode(target);
			gds.insertEdge(source, target);
		}
		
		if (alg == 0) {
			tcAlg = new IncSCCAlg<Object>(gds);
		}
		else {
			tcAlg = new DRedAlg<Object>(gds);
		}
		
		tcAlg.attachObserver(this);
		reteContainer.registerClearable(this);
	}
	
	public TransitiveClosureNode(ReteContainer reteContainer) {
		super(reteContainer);
		gds = new Graph<Object>();
		if (alg == 0) {
			tcAlg = new IncSCCAlg<Object>(gds);
		}
		else {
			tcAlg = new DRedAlg<Object>(gds);
		}
		tcAlg.attachObserver(this);
		reteContainer.registerClearable(this);
	}

	@Override
	public void pullInto(Collection<org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple> collector) {
		if (alg == 0) {
			for (Tuple<Object> tuple : ((IncSCCAlg<Object>) tcAlg).getTcRelation()) {
				collector.add(new FlatTuple(tuple.getSource(), tuple.getTarget()));
			}
		}
		else {
			DRedAlg<Object> dred = (DRedAlg<Object>) tcAlg;
			
			for (Object s : dred.getTcRelation().getTupleStarts()) {
				for (Object t : dred.getTcRelation().getTupleEnds(s)) {
					collector.add(new FlatTuple(s,t));
				}
			}
		}
		
	}

	@Override
	public void update(Direction direction, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple updateElement) {
		if (updateElement.getSize() == 2) {
			Object source = updateElement.get(0);
			Object target = updateElement.get(1);
			
			if (direction == Direction.INSERT) {
				//Nodes are stored in a set
				gds.insertNode(source);
				gds.insertNode(target);
				gds.insertEdge(source, target);
			}
			if (direction == Direction.REVOKE) {
				gds.deleteEdge(source, target);
				
				if (alg == 0) {
					IncSCCAlg<Object> incscc = (IncSCCAlg<Object>) tcAlg;
					
					if (incscc.isIsolated(source)) {
						gds.deleteNode(source);
					}
					if (incscc.isIsolated(target)) {
						gds.deleteNode(target);
					}
				}
			}
		}
	}
	
	@Override
	public void clear() {
		gds = new Graph<Object>();
		if (alg == 0) {
			tcAlg = new IncSCCAlg<Object>(gds);
		}
		else {
			tcAlg = new DRedAlg<Object>(gds);
		}
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
