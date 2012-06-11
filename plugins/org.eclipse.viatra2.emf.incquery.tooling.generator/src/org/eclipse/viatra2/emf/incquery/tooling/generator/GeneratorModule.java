package org.eclipse.viatra2.emf.incquery.tooling.generator;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.ExtensionBasedGenerationFragmentProvider;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragmentProvider;
import org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel.EMFPatternLanguageJvmModelInferrer;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageRuntimeModule;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider;
import org.eclipse.xtext.common.types.access.jdt.JdtTypeProviderFactory;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelInferrer;

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
}
