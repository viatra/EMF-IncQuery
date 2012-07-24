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
package org.eclipse.viatra2.emf.incquery.tooling.generator;

import org.eclipse.viatra2.emf.incquery.tooling.generator.scoping.GeneratorModelCrossRefSerializer;
import org.eclipse.viatra2.emf.incquery.tooling.generator.scoping.GeneratorModelLinkingService;
import org.eclipse.xtext.linking.ILinkingService;
import org.eclipse.xtext.serializer.tokens.ICrossReferenceSerializer;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class GeneratorModelRuntimeModule extends AbstractGeneratorModelRuntimeModule {

	@Override
	public Class<? extends ILinkingService> bindILinkingService() {
		return GeneratorModelLinkingService.class;
	}

	public Class<? extends ICrossReferenceSerializer> bindICrossReferenceSerializer() {
		return GeneratorModelCrossRefSerializer.class;
	}
}
