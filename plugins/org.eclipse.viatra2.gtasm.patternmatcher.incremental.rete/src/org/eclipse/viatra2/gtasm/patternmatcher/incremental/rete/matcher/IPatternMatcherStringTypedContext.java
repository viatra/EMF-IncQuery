/*******************************************************************************
 * Copyright (c) 2004-2009 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;

/**
 * 
 * @author Bergmann Gábor
 *
 */
public interface IPatternMatcherStringTypedContext<PatternDescription> extends
		IPatternMatcherContext<PatternDescription> {

	//	String retrieveUnaryTypeFQN(Object typeObject);
	//	String retrieveTernaryEdgeTypeFQN(Object typeObject);	
	//	String retrieveBinaryEdgeTypeFQN(Object typeObject);
		
	Object resolveConstant(String fullyQualifiedName) throws RetePatternBuildException; //Type? Instance? Entity? Relation? Who knows?

	Object retrieveBinaryEdgeType(String fullyQualifiedName) throws RetePatternBuildException;

	Object retrieveTernaryEdgeType(String fullyQualifiedName) throws RetePatternBuildException;

	Object retrieveUnaryType(String fullyQualifiedName) throws RetePatternBuildException;

}
