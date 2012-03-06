package org.eclipse.viatra2.emf.incquery.tooling.generator.fragments;

import org.eclipse.viatra2.emf.incquery.tooling.generator.GeneratorModule;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * An interface for collecting code generation fragments for specific patterns.
 * The concrete value is injected using the {@link GeneratorModule}-based
 * injectors.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public interface IGenerationFragmentProvider {

	/**
	 * Collects the generation fragments applicable for a selected pattern.
	 * @param pattern
	 * @return a non-null collection of code generation fragments. May be empty.
	 */
	public Iterable<IGenerationFragment> getFragmentsForPattern(Pattern pattern);
}
