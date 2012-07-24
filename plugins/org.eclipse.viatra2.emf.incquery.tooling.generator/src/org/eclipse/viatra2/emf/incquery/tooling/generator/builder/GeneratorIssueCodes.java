/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.tooling.generator.builder;

public final class GeneratorIssueCodes {
	
	private GeneratorIssueCodes() {}

	private static final String GENERATOR_ROOT_CODE = "org.eclipse.viatra2.emf.incquery.tooling.generator.";
	
	public static final String INVALID_PATTERN_MODEL_CODE = GENERATOR_ROOT_CODE + "invalid.patternmodel";
	public static final String INVALID_TYPEREF_CODE = GENERATOR_ROOT_CODE + "invalid.typeref";
}
