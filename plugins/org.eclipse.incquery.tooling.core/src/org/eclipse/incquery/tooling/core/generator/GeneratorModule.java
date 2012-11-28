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

package org.eclipse.incquery.tooling.core.generator;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageRuntimeModule;
import org.eclipse.incquery.patternlanguage.emf.scoping.IMetamodelProvider;
import org.eclipse.incquery.tooling.core.generator.fragments.ExtensionBasedGenerationFragmentProvider;
import org.eclipse.incquery.tooling.core.generator.fragments.IGenerationFragmentProvider;
import org.eclipse.incquery.tooling.core.generator.genmodel.GenModelMetamodelProviderService;
import org.eclipse.incquery.tooling.core.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.incquery.tooling.core.generator.types.GenModelBasedTypeProvider;
import org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel.EMFPatternLanguageJvmModelInferrer;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider;
import org.eclipse.xtext.common.types.access.jdt.JdtTypeProviderFactory;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelInferrer;
import org.eclipse.xtext.xbase.typing.ITypeProvider;

public class GeneratorModule extends EMFPatternLanguageRuntimeModule {

	public Class<? extends IGenerationFragmentProvider> bindIGenerationFragmentProvider() {
		return ExtensionBasedGenerationFragmentProvider.class;
	}

	// contributed by org.eclipse.xtext.generator.xbase.XbaseGeneratorFragment
	public Class<? extends IJvmModelInferrer> bindIJvmModelInferrer() {
		return EMFPatternLanguageJvmModelInferrer.class;
	}

	// contributed by org.eclipse.xtext.generator.generator.GeneratorFragment
	public IWorkspaceRoot bindIWorkspaceRootToInstance() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
	// contributed by org.eclipse.xtext.generator.types.TypesGeneratorFragment
	public Class<? extends IJvmTypeProvider.Factory> bindIJvmTypeProvider$Factory() {
		return JdtTypeProviderFactory.class;
	}
	
	public Class<? extends IMetamodelProvider> bindIMetamodelProvider() {
		return GenModelMetamodelProviderService.class;
	}
	
	public Class<? extends IEiqGenmodelProvider> bindIEiqGenmodelProvider() {
		return GenModelMetamodelProviderService.class;
	}
	
	@Override
	public Class<? extends ITypeProvider> bindITypeProvider() {
		return GenModelBasedTypeProvider.class;
	}
}
