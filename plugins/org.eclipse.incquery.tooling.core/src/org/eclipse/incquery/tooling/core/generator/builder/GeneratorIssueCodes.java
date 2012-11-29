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

package org.eclipse.incquery.tooling.core.generator.builder;

import org.eclipse.incquery.tooling.core.generator.IncQueryGeneratorPlugin;

public final class GeneratorIssueCodes {
	
	private GeneratorIssueCodes() {}
	
    public static final String INVALID_PATTERN_MODEL_CODE = IncQueryGeneratorPlugin.BUNDLE_ID + ".invalid.patternmodel";
    public static final String INVALID_TYPEREF_CODE = IncQueryGeneratorPlugin.BUNDLE_ID + ".invalid.typeref";
}
