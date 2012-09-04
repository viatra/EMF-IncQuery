/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Istvan Rath - initial API and implementation
 *******************************************************************************/


package org.eclipse.viatra2.emf.incquery.runtime;

/**
 * Interface for storing string constants related to IncQuery's extension points.
 * @author Istvan Rath
 *
 */
public interface IExtensions {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.viatra2.emf.incquery.runtime";
	// The extension point ID
	//public static final String EXTENSION_POINT_ID = "org.eclipse.viatra2.emf.incquery.patternmatcher.builder";
	public static final String MATCHERFACTORY_EXTENSION_POINT_ID = "org.eclipse.viatra2.emf.incquery.matcherfactory";
	
	// Extension point for registering the generated java codes from the xbase xexpressions
	public static final String XEXPRESSIONEVALUATOR_EXTENSION_POINT_ID = "org.eclipse.viatra2.emf.incquery.xexpressionevaluator";
}
