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

package org.eclipse.viatra2.patternlanguage.ui;

import org.eclipse.viatra2.emf.incquery.runtime.extensibility.IInjectorProvider;

import com.google.inject.Injector;

public class UiPluginInjectorProvider implements IInjectorProvider {

	@Override
	public Injector getInjector() {
		return EMFPatternLanguageUIActivator.getInstance().getInjector(EMFPatternLanguageUIActivator.ORG_ECLIPSE_VIATRA2_PATTERNLANGUAGE_EMFPATTERNLANGUAGE);
	}

}
