/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.remote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.single.SingleInputNode;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;


/**
 * This node delivers updates to a remote recipient; no updates are propagated
 * further in this network.
 * 
 * @author Gabor Bergmann
 * 
 */
public class RemoteReceiver extends SingleInputNode {

	List<Address<? extends Receiver>> targets;

	public RemoteReceiver(ReteContainer reteContainer) {
		super(reteContainer);
		targets = new ArrayList<Address<? extends Receiver>>();
	}

	public void addTarget(Address<? extends Receiver> target) {
		targets.add(target);
	}

	public void pullInto(Collection<Tuple> collector) {
		propagatePullInto(collector);
	}

	public Collection<Tuple> remotePull() {
		return reteContainer.pullContents(this);
	}

	public void update(Direction direction, Tuple updateElement) {
		for (Address<? extends Receiver> ad : targets)
			reteContainer.sendUpdateToRemoteAddress(ad, direction,
					updateElement);
	}

}
