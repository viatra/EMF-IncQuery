package org.eclipse.viatra2.emf.incquery.tooling.generator.fragments;

import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

public interface IGenerationFragmentProvider {

	public Iterable<IGenerationFragment> getFragmentsForPattern(Pattern pattern);
}
