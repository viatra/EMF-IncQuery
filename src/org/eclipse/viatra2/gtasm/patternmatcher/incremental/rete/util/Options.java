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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.util;

public class Options {

	public enum NodeSharingOption {
		NEVER, // not recommended, patternmatcher leaks possible
		INDEXER_AND_REMOTEPROXY, ALL
	}

	public final static NodeSharingOption nodeSharingOption = NodeSharingOption.ALL;
	public static final boolean releaseOnetimeIndexers = true; // effective only
																// with
																// nodesharing
																// ==NEVER

	public enum InjectivityStrategy {
		EAGER, LAZY
	}

	public final static InjectivityStrategy injectivityStrategy = InjectivityStrategy.EAGER;

	public final static boolean enableInheritance = true;

	//public final static boolean useComplementerMask = true;
	
	public static final boolean calcImpliedTypes = true; // if true, shrinks the net by avoiding unnecessary typechecks
	public static final boolean employTrivialIndexers = true;


	// public final static boolean synchronous = false;

	public final static int numberOfLocalContainers = 1;
	public final static int firstFreeContainer = 0; // 0 if head container is
													// free to contain pattern
													// bodies, 1 otherwise
}
