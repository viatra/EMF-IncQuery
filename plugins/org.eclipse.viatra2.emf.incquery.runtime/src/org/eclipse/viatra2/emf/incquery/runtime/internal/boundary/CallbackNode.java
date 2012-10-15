/*******************************************************************************
 * Copyright (c) 2010-2012, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.runtime.internal.boundary;


import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.IncQueryCallbackHandle;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.misc.SimpleReceiver;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;

/**
 * @author Bergmann Gabor
 *
 */
public abstract class CallbackNode<Match extends IPatternMatch> extends SimpleReceiver implements IncQueryCallbackHandle {

	IncQueryEngine engine;
	IMatchProcessor<Match> callbackAppear;
	IMatchProcessor<Match> callbackDisappear;
	
    public abstract Match statelessConvert(Tuple t);

	public CallbackNode(ReteContainer reteContainer, IncQueryEngine engine,
			IMatchProcessor<Match> callbackAppear,
			IMatchProcessor<Match> callbackDisappear) {
		super(reteContainer);
		this.engine = engine;
		this.callbackAppear = callbackAppear;
		this.callbackDisappear = callbackDisappear;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver#update(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple)
	 */
	@Override
	public void update(Direction direction, Tuple updateElement) {
		IMatchProcessor<Match> callback = direction == Direction.INSERT ? 
				callbackAppear : callbackDisappear;
		
		if (callback != null) {
			Match match = statelessConvert(updateElement);
			try {
				callback.process(match);
			} catch (Throwable e) { //NOPMD
				if (e instanceof Error) throw (Error)e;
				engine.getLogger().warn(String.format(
					"The incremental pattern matcher encountered an error during executing a callback on %s of match %s of pattern %s. Error message: %s. (Developer note: %s in %s called from CallbackNode)", 
					direction == Direction.INSERT ? "insertion" : "removal",
					match.prettyPrint(),
					match.patternName(),
					e.getMessage(),
					e.getClass().getSimpleName(),
					callback
					), e);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.extensibility.IncQueryCallbackHandle#removeCallback()
	 */
	@Override
	public void removeCallback() {
		this.disconnectFromNetwork();
	}


}
