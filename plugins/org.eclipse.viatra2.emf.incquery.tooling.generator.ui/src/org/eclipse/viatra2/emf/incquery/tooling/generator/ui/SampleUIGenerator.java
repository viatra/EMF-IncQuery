/**
 * 
 */
package org.eclipse.viatra2.emf.incquery.tooling.generator.ui;

import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.generator.IFileSystemAccess;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * A generator fragment for the sample UI project
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class SampleUIGenerator implements IGenerationFragment {

	@Inject
	Injector injector;
	
	SampleUIJvmModelInferrer inferrer = new SampleUIJvmModelInferrer();

	@Override
	public String getProjectPostfix() {
		return "ui";
	}

	@Override
	public String[] getProjectDependencies() {
		return new String[] { "org.eclipse.core.runtime", "org.eclipse.ui",
				"org.eclipse.emf.ecore", "org.eclipse.pde.core"};
	}

	@Override
	public void generateFiles(Pattern pattern, IFileSystemAccess fsa) {
		
	}

	@Override
	public void generateFiles(Pattern patternm, Annotation annotation,
			IFileSystemAccess fsa) {}
	
	@Override
	public Iterable<JvmGenericType> inferFiles(Pattern pattern) {
		injector.injectMembers(inferrer);
		return inferrer.infer(pattern);
	}
	
	@Override
	public Iterable<JvmGenericType> inferFiles(Pattern pattern, Annotation annotation) {
		return null;
	}
	
}
