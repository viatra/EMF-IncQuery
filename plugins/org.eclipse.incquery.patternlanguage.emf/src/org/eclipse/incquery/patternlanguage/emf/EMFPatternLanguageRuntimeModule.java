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
package org.eclipse.incquery.patternlanguage.emf;

import org.apache.log4j.Logger;
import org.eclipse.incquery.patternlanguage.emf.scoping.EMFPatternLanguageDeclarativeScopeProvider;
import org.eclipse.incquery.patternlanguage.emf.scoping.EMFPatternLanguageImportedNamespaceAwareLocalScopeProvider;
import org.eclipse.incquery.patternlanguage.emf.scoping.EMFPatternLanguageLinkingService;
import org.eclipse.incquery.patternlanguage.emf.scoping.EMFPatternLanguageScopeProvider;
import org.eclipse.incquery.patternlanguage.emf.scoping.IMetamodelProvider;
import org.eclipse.incquery.patternlanguage.emf.scoping.MetamodelProviderService;
import org.eclipse.incquery.patternlanguage.emf.serializer.EMFPatternLanguageCrossRefSerializer;
import org.eclipse.incquery.patternlanguage.emf.types.EMFPatternTypeProvider;
import org.eclipse.incquery.patternlanguage.emf.types.IEMFTypeProvider;
import org.eclipse.incquery.patternlanguage.emf.validation.EMFPatternLanguageSyntaxErrorMessageProvider;
import org.eclipse.incquery.patternlanguage.scoping.MyAbstractDeclarativeScopeProvider;
import org.eclipse.incquery.patternlanguage.scoping.PatternLanguageResourceDescriptionStrategy;
import org.eclipse.xtext.linking.ILinkingService;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.parser.antlr.ISyntaxErrorMessageProvider;
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy;
import org.eclipse.xtext.resource.IGlobalServiceProvider;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.eclipse.xtext.serializer.tokens.ICrossReferenceSerializer;
import org.eclipse.xtext.xbase.typing.ITypeProvider;

import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.name.Names;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class EMFPatternLanguageRuntimeModule extends AbstractEMFPatternLanguageRuntimeModule {

    @Provides
    Logger provideLoggerImplementation() {
        return Logger.getLogger(EMFPatternLanguageRuntimeModule.class);
    }

    @Override
    public Class<? extends ILinkingService> bindILinkingService() {
        return EMFPatternLanguageLinkingService.class;
    }

    // contributed by org.eclipse.xtext.generator.xbase.XbaseGeneratorFragment
    @Override
    public void configureIScopeProviderDelegate(Binder binder) {
        binder.bind(IScopeProvider.class).annotatedWith(Names.named(AbstractDeclarativeScopeProvider.NAMED_DELEGATE))
                .to(EMFPatternLanguageDeclarativeScopeProvider.class);
        binder.bind(IScopeProvider.class).annotatedWith(Names.named(MyAbstractDeclarativeScopeProvider.NAMED_DELEGATE))
                .to(EMFPatternLanguageImportedNamespaceAwareLocalScopeProvider.class);
    }

    @Override
    public Class<? extends IDefaultResourceDescriptionStrategy> bindIDefaultResourceDescriptionStrategy() {
        return PatternLanguageResourceDescriptionStrategy.class;
    }

    @Override
    public Class<? extends IScopeProvider> bindIScopeProvider() {
        return EMFPatternLanguageScopeProvider.class;
    }

    // contributed by org.eclipse.xtext.generator.xbase.XbaseGeneratorFragment
    @Override
    public Class<? extends ITypeProvider> bindITypeProvider() {
        return EMFPatternTypeProvider.class;
    }

    public Class<? extends IEMFTypeProvider> bindIEMFTypeProvider() {
        return EMFPatternTypeProvider.class;
    }

    public Class<? extends IMetamodelProvider> bindIMetamodelProvider() {
        return MetamodelProviderService.class;
    }

    public Class<? extends ICrossReferenceSerializer> bindICrossReferenceSerializer() {
        return EMFPatternLanguageCrossRefSerializer.class;
    }

    public Class<? extends ISyntaxErrorMessageProvider> bindISyntaxErrorMessageProvider() {
        return EMFPatternLanguageSyntaxErrorMessageProvider.class;
    }

    @Override
    public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
        return EMFPatternLanguageQualifiedNameProvider.class;
    }

    public Class<? extends IGlobalServiceProvider> bindIGlobalServiceProvider() {
        return EMFPatternLanguageServiceProvider.class;
    }
}
