package org.eclipse.viatra2.emf.incquery.tooling.generator;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragmentProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.xbase.compiler.JvmModelGenerator;

import com.google.inject.Inject;

/**
 * A custom generator for EMF-IncQuery projects that is based on the JVM Model
 * Inferrers, but allows extensions based on an injected
 * {@link IGenerationFragmentProvider} instance.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class IncQueryGenerator extends JvmModelGenerator {

	@Inject
	IGenerationFragmentProvider fragmentProvider;

	@Override
	public void doGenerate(Resource input, IFileSystemAccess fsa) {
		super.doGenerate(input, fsa);
		TreeIterator<EObject> it = input.getAllContents();
		while (it.hasNext()) {
			EObject obj = it.next();
			if (obj instanceof Pattern) {
				executeGeneratorFragments((Pattern) obj);
			}
		}
	}

	private void executeGeneratorFragments(Pattern obj) {
		for (IGenerationFragment fragment : fragmentProvider
				.getFragmentsForPattern(obj)) {
			System.out.println(obj.getName() + ": "
					+ fragment.getClass().getCanonicalName());
		}
	}

}
