package org.eclipse.viatra2.emf.incquery.tooling.retevis.views;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.ReteBoundary;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PConstraint;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem.PVariable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.IndexerWithMemory;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Network;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Node;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Production;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.remote.Address;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.single.UniquenessEnforcerNode;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;

public class ZestReteLabelProvider extends LabelProvider implements IEntityStyleProvider 
{
	
	ReteBoundary rb;
	
	public ReteBoundary getRb() {
		return rb;
	}

	public void setRb(ReteBoundary rb) {
		this.rb = rb;
		// initialize reverse traceability information
		resetReverseMap();
		for (Object _o : rb.getAllProductionNodes()) {
			Node productionNode = rb.getHeadContainer().resolveLocal((Address<?>)_o);
			if (productionNode!=null && productionNode instanceof Production) {
				initalizeReverseMap((Production)productionNode);		
			}
		}
	}

	
	@Override
	public String getText(Object element) {
		if (element instanceof Node) {
			Node n = (Node) element;
			String s = ""+n.getClass().getSimpleName();
			if (n instanceof UniquenessEnforcerNode) {
				// print tuplememory statistics
				s+="["+((UniquenessEnforcerNode)n).getMemory().size()+"]";
			}
			if (n instanceof IndexerWithMemory) {
				s+="["+((IndexerWithMemory)n).getMemory().getSize()+"]";
			}
			return s;
		}
		return "!";
//		return s+super.getText(element);
	}
	
//	@Override
	public IFigure getTooltip(Object entity) {
		if (entity instanceof Node) {
			Node n = (Node)entity;
			String s="";
			
			for (Stub st : getStubsForNode(n)) {
				s+=getEnforcedConstraints(st);
			}
			
			FlowPage fp = new FlowPage();
			
			TextFlow nameTf = new TextFlow();
//			nameTf.setFont(fontRegistry.get("default"));
			TextFlow infoTf = new TextFlow();
//			infoTf.setFont(fontRegistry.get("code"));
			
			//nameTf.setText(n.toString());
			String info="";//"\n";
			info+="Stubs:\n"+s;//+"\n";
			infoTf.setText(info);
//			fp.add(nameTf);
			fp.add(infoTf);
			return fp;
		}
		return null;
	}
	
	// useful only for production nodes
	private static String getEnforcedConstraints(Stub st) {
		String s="";
		for (Object _pc : st.getAllEnforcedConstraints()) {
			PConstraint pc = (PConstraint) _pc;
			s+="\t["+pc.getClass().getSimpleName()+"]:";
			for (Object _v : pc.getAffectedVariables()) {
				PVariable v = (PVariable) _v;
				s+="{"+v.getName()+"}";
			}
			s+="\n";
		}
		return s;
	}
	
	private Collection<Stub<Address<?>>> getStubsForNode(Node n) {
		Collection<Stub<Address<?>>> r = reverseMap.get(n);
		if (r!=null) return r;
		else return Collections.EMPTY_SET;
	}
	
	Map<Node, Collection<Stub<Address<?>>>> reverseMap;// = new HashMap<Node, Collection<Stub<Address<?>>>>();
	
	private void resetReverseMap() {
		reverseMap = new HashMap<Node, Collection<Stub<Address<?>>>>();
	}
	
	private void initalizeReverseMap(Production prod) {
		for (Object _stubOfProd : rb.getParentStubsOfReceiver(new Address<Node>(prod))) {
			Stub stubOfProd = (Stub) _stubOfProd;
			for (Stub<Address<?>> s : getAllParentStubs(stubOfProd)) {
				Address<Node> address = (Address<Node>) s.getHandle();
				Node n = rb.getHeadContainer().resolveLocal(address);
				Collection<Stub<Address<?>>> t = reverseMap.get(n);
				if (t==null) {
					t=new HashSet<Stub<Address<?>>>();
				}
				t.add(s);
				reverseMap.put(n, t);
			}
		}
	}
	
	private static Collection<Stub<Address<?>>> getAllParentStubs(Stub<Address<?>> st) {
		if (st!=null) {
			Vector<Stub<Address<?>>> v = new Vector<Stub<Address<?>>>();
			v.add(st);
			v.addAll(getAllParentStubs( st.getPrimaryParentStub() ) );
			v.addAll(getAllParentStubs( st.getSecondaryParentStub() ) );
			return v;
		} else return Collections.EMPTY_LIST;
	}

	@Override
	public Color getNodeHighlightColor(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getBorderColor(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getBorderHighlightColor(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBorderWidth(Object entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Color getBackgroundColour(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getForegroundColour(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean fisheyeNode(Object entity) {
		// TODO Auto-generated method stub
		return false;
	}

}
