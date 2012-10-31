package org.eclipse.viatra2.emf.incquery.tooling.retevis.views;

import java.util.Vector;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.ReteBoundary;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.Indexer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.IndexerListener;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.StandardIndexer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Node;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.remote.Address;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

public class ZestReteContentProvider extends ArrayContentProvider implements
		IGraphEntityContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof ReteContainer) {
			return super.getElements(((ReteContainer)inputElement).getAllNodes());
		}
		else if (inputElement instanceof ReteBoundary) {
			ReteBoundary rb = (ReteBoundary) inputElement;
			Vector<Node> r = new Vector<Node>();
			for (Object a : rb.getAllUnaryRoots()) {
				r.add(rb.getHeadContainer().resolveLocal( (Address)a )); // access all unary constraints
			}
			for (Object a : rb.getAllTernaryEdgeRoots()) {
				r.add(rb.getHeadContainer().resolveLocal( (Address)a )); // access all ternary constraints	
			}
			return r.toArray();
		}
		return super.getElements(inputElement);
	}

	@Override
	public Object[] getConnectedTo(Object entity) {
		if (entity instanceof Node) {
			Vector<Node> r = new Vector<Node>();
			if (entity instanceof Supplier) {
				r.addAll( ((Supplier)entity).getReceivers() );
			}
			if (entity instanceof Indexer) {
				if (entity instanceof StandardIndexer) {
					for (IndexerListener il : ((StandardIndexer)entity).getListeners()){
						r.add(il.getOwner());
					}
				}
			}
			return r.toArray();
		}
		return null;
	}
	
	
}
