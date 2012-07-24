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
package org.eclipse.viatra2.emf.incquery.tooling.generator.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.viatra2.emf.incquery.tooling.generator.validation.GeneratorModelJavaValidator;
import org.eclipse.xtext.service.SingletonBinding;

/**
 * Use this class to register components to be used within the IDE.
 */
public class GeneratorModelUiModule extends org.eclipse.viatra2.emf.incquery.tooling.generator.ui.AbstractGeneratorModelUiModule {
	public GeneratorModelUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}

	@SingletonBinding(eager = true)
	public Class<? extends GeneratorModelJavaValidator> bindGeneratorModelJavaValidator() {
		return GenmodelProjectBasedValidation.class;
	}
}
