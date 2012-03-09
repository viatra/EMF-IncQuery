/*******************************************************************************
 * Copyright (c) 2011 Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.patternlanguage;

import org.eclipse.viatra2.patternlanguage.core.scoping.MyAbstractDeclarativeScopeProvider;
import org.eclipse.viatra2.patternlanguage.scoping.EMFPatternLanguageDeclarativeScopeProvider;
import org.eclipse.viatra2.patternlanguage.scoping.EMFPatternLanguageLinkingService;
import org.eclipse.xtext.linking.ILinkingService;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.eclipse.xtext.xbase.scoping.XbaseImportedNamespaceScopeProvider;

import com.google.inject.Binder;
import com.google.inject.name.Names;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class EMFPatternLanguageRuntimeModule extends AbstractEMFPatternLanguageRuntimeModule {

	@Override
	public Class<? extends ILinkingService> bindILinkingService() {
		return EMFPatternLanguageLinkingService.class;
	}

	// contributed by org.eclipse.xtext.generator.xbase.XbaseGeneratorFragment
	@Override
	public void configureIScopeProviderDelegate(Binder binder) {
		binder.bind(IScopeProvider.class).annotatedWith(Names.named(AbstractDeclarativeScopeProvider.NAMED_DELEGATE)).to(EMFPatternLanguageDeclarativeScopeProvider.class);
		binder.bind(IScopeProvider.class).annotatedWith(Names.named(MyAbstractDeclarativeScopeProvider.NAMED_DELEGATE)).to(XbaseImportedNamespaceScopeProvider.class);
	}
	
	// contributed by org.eclipse.xtext.generator.xbase.XbaseGeneratorFragment
//	@Override
//	public Class<? extends ITypeProvider> bindITypeProvider() {
//			return EMFPatternTypeProvider.class;
//	}

}
