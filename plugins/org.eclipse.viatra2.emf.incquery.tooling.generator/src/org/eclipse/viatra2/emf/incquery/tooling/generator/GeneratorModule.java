package org.eclipse.viatra2.emf.incquery.tooling.generator;

import org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel.EMFPatternLanguageJvmModelInferrer;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageRuntimeModule;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelInferrer;

public class GeneratorModule extends EMFPatternLanguageRuntimeModule{

	// contributed by org.eclipse.xtext.generator.xbase.XbaseGeneratorFragment
		public Class<? extends IJvmModelInferrer> bindIJvmModelInferrer() {
			return EMFPatternLanguageJvmModelInferrer.class;
		}
}
