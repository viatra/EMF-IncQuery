package org.eclipse.viatra2.emf.incquery.base.itc.main;

import org.eclipse.viatra2.emf.incquery.base.itc.igraph.ITcObserver;

public class TestObserver implements ITcObserver<Integer> {

	@Override
	public void tupleInserted(Integer source, Integer target) {
		System.out.println("insert ("+source+","+target+")");	
	}

	@Override
	public void tupleDeleted(Integer source, Integer target) {
		System.out.println("delete ("+source+","+target+")");			
	}

}
