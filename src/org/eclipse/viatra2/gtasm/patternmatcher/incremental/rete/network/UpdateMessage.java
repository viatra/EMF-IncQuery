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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;

class UpdateMessage {
	public Receiver receiver;
	public Direction direction;
	public Tuple updateElement;

	public UpdateMessage(Receiver receiver, Direction direction,
			Tuple updateElement) {
		this.receiver = receiver;
		this.direction = direction;
		this.updateElement = updateElement;
	}

}
