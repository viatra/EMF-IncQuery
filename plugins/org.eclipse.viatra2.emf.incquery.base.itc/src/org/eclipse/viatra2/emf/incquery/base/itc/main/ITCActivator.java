/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.base.itc.main;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.incscc.IncSCCAlg;
import org.eclipse.viatra2.emf.incquery.base.itc.graphimpl.Graph;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ITCActivator implements BundleActivator {
		
	public void start(BundleContext context) throws Exception {
		//System.out.println("org.eclipse.viatra2.emf.incquery.base.itc bundle started");
	}
	
	public void stop(BundleContext context) throws Exception {
		//System.out.println("org.eclipse.viatra2.emf.incquery.base.itc bundle stopped");
	}
	
	public static void main(String[] args) {
		Graph<Integer> g = new Graph<Integer>();
		IncSCCAlg<Integer> incScc = new IncSCCAlg<Integer>(g);
		incScc.attachObserver(new TestObserver());
		Integer n1 = Integer.valueOf(1);
		Integer n2 = Integer.valueOf(2);
		Integer n3 = Integer.valueOf(3);
		Integer n4 = Integer.valueOf(4);
		Integer n5 = Integer.valueOf(5);
		Integer n6 = Integer.valueOf(6);
		Integer n7 = Integer.valueOf(7);
		Integer n8 = Integer.valueOf(8);
		
		g.insertNode(n1);
		g.insertNode(n2);
		g.insertNode(n3);
		g.insertNode(n4);
		g.insertNode(n5);
		g.insertNode(n6);
		g.insertNode(n7);
		g.insertNode(n8);
		
		g.insertEdge(n1, n4);
		g.insertEdge(n2, n4);
		g.insertEdge(n3, n4);
		System.out.println("--");
		g.insertEdge(n4, n5);
		System.out.println("--");
		g.insertEdge(n5, n6);
		g.insertEdge(n5, n7);
		g.insertEdge(n5, n8);
		
		System.out.println("--");
		g.insertEdge(n8, n8);
		g.insertEdge(n8, n8);
		g.insertEdge(n8, n8);
		
		System.out.println("--");
		g.insertEdge(n8, n4);
	}
}
