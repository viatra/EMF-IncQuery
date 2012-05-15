/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherRuntimeContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Network;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.remote.Address;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.FlatTuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;

/**
 * @author Bergmann Gábor
 *
 */
public abstract class Feeder {
	protected Address<? extends Receiver> receiver;
	protected IPatternMatcherRuntimeContext<?> context;
	protected Network network;
	protected ReteBoundary<?> boundary;

	/**
	 * @param receiver
	 * @param context
	 * @param network
	 * @param boundary
	 */
	public Feeder(Address<? extends Receiver> receiver,
			IPatternMatcherRuntimeContext<?> context, Network network,
			ReteBoundary<?> boundary) {
		super();
		this.receiver = receiver;
		this.context = context;
		this.network = network;
		this.boundary = boundary;
	}

	public abstract void feed();
	
	protected void emit(Tuple tuple) {
		network.sendConstructionUpdate(receiver, Direction.INSERT, tuple);
	}
	
	protected IPatternMatcherRuntimeContext.ModelElementCrawler unaryCrawler() {
		return new IPatternMatcherRuntimeContext.ModelElementCrawler() {
			public void crawl(Object element) {
				emit(new FlatTuple(boundary.wrapElement(element)));
			}
		};
	}

	protected IPatternMatcherRuntimeContext.ModelElementPairCrawler pairCrawler() {
		return new IPatternMatcherRuntimeContext.ModelElementPairCrawler() {
			public void crawl(Object first, Object second) {
				emit(new FlatTuple(boundary.wrapElement(first), boundary.wrapElement(second)));
			}
		};
	}
	
	protected IPatternMatcherRuntimeContext.ModelElementCrawler ternaryCrawler() {
		return new IPatternMatcherRuntimeContext.ModelElementCrawler() {
			public void crawl(Object element) {
				Object relation = element;
				Object from = context.ternaryEdgeSource(relation);					
				Object to = context.ternaryEdgeTarget(relation);				
				emit(new FlatTuple(
						boundary.wrapElement(relation),
						boundary.wrapElement(from), 
						boundary.wrapElement(to)));
			}
		};
	}

}
