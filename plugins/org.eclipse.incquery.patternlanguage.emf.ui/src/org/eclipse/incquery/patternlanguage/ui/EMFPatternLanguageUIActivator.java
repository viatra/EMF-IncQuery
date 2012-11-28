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

package org.eclipse.incquery.patternlanguage.ui;

import org.eclipse.incquery.patternlanguage.ui.internal.EMFPatternLanguageActivator;
import org.eclipse.incquery.tooling.core.generator.IncQueryGeneratorPlugin;

import com.google.inject.Module;

public class EMFPatternLanguageUIActivator extends EMFPatternLanguageActivator {

	@Override
	protected Module getRuntimeModule(String grammar) {
		if (ORG_ECLIPSE_VIATRA2_PATTERNLANGUAGE_EMFPATTERNLANGUAGE.equals(grammar)) {
			return IncQueryGeneratorPlugin.INSTANCE.getRuntimeModule();
		}
		
		throw new IllegalArgumentException(grammar);
	}

}
