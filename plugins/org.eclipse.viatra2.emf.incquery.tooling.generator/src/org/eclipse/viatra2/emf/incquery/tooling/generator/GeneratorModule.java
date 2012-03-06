package org.eclipse.viatra2.emf.incquery.tooling.generator;

import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.ExtensionBasedGenerationFragmentProvider;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragmentProvider;
import org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel.EMFPatternLanguageJvmModelInferrer;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageRuntimeModule;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelInferrer;

public class GeneratorModule extends EMFPatternLanguageRuntimeModule {

	public Class<? extends IGenerationFragmentProvider> bindIGenerationFragmentProvider() {
		return ExtensionBasedGenerationFragmentProvider.class;
	}
	
	// contributed by org.eclipse.xtext.generator.xbase.XbaseGeneratorFragment
	public Class<? extends IJvmModelInferrer> bindIJvmModelInferrer() {
		return EMFPatternLanguageJvmModelInferrer.class;
	}
	
	// contributed by org.eclipse.xtext.generator.xbase.XbaseGeneratorFragment
	@Override
	public Class<? extends org.eclipse.xtext.generator.IGenerator> bindIGenerator() {
		return IncQueryGenerator.class;
	}
}
