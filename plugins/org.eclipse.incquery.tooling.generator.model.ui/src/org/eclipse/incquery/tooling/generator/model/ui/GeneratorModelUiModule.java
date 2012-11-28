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
package org.eclipse.incquery.tooling.generator.model.ui;

import org.apache.log4j.Logger;
import org.eclipse.incquery.tooling.generator.model.validation.GeneratorModelJavaValidator;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.service.SingletonBinding;

import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Use this class to register components to be used within the IDE.
 */
public class GeneratorModelUiModule extends AbstractGeneratorModelUiModule {
	public GeneratorModelUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}

	private static final String loggerRoot = "org.eclipse.viatra2.emf.incquery";

	@Provides
	@Singleton
	Logger provideLoggerImplementation() {
		return Logger.getLogger(loggerRoot);
	}

	@SingletonBinding(eager = true)
	public Class<? extends GeneratorModelJavaValidator> bindGeneratorModelJavaValidator() {
		return GenmodelProjectBasedValidation.class;
	}
}
