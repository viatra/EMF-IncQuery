/*******************************************************************************
 * Copyright (c) 2010-2012, istvanrath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   istvanrath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.rete.index;

import java.lang.ref.WeakReference;

import org.eclipse.incquery.runtime.rete.network.Node;

/**
 * @author istvanrath
 *
 */
public abstract class DefaultIndexerListener implements IndexerListener {

	WeakReference<Node> owner;
	
	public DefaultIndexerListener(Node owner) {
		this.owner = new WeakReference<Node>(owner);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.IndexerListener#getOwner()
	 */
	@Override
	public Node getOwner() {
		return owner.get();
	}

}
