package org.eclipse.viatra2.emf.incquery.tooling.generator.fragments;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * A provider for {@link IGenerationFragment} classes - the fragment list is populated using the
 * registered extensions for the {@value #EXTENSIONID} extension point.
 * @author Zoltan Ujhelyi
 *
 */
public class ExtensionBasedGenerationFragmentProvider implements
		IGenerationFragmentProvider {
	
	Logger logger = Logger.getLogger(getClass());

	static final String EXTENSIONID = "org.eclipse.viatra2.emf.incquery.tooling.generator.generatorFragment";
	static final String GENERIC_ATTRIBUTE = "";
	Multimap<String, IGenerationFragment> fragments;

	protected void initializeFragments() {
		fragments = ArrayListMultimap.create();
		final IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EXTENSIONID);
		for (IConfigurationElement e : config) {
			final String annotationName = e.getAttribute("annotation") != null ? e.getAttribute("annotation") : GENERIC_ATTRIBUTE;
			try {
				IGenerationFragment fragment = (IGenerationFragment) e.createExecutableExtension("fragment");
				fragments.put(annotationName, fragment);
			} catch (CoreException e1) {
				logger.warn("Cannot load generator fragment from " + e.getContributor().getName(), e1);
			}
		}
	}	
	
	@Override
	public Iterable<IGenerationFragment> getFragmentsForPattern(Pattern pattern) {
		if (fragments == null) {
			initializeFragments();
		}
		HashSet<IGenerationFragment> fragmentSet = new HashSet<IGenerationFragment>(fragments.get(GENERIC_ATTRIBUTE));
		for (Annotation annotation : pattern.getAnnotations()) {
			fragmentSet.addAll(fragments.get(annotation.getName()));
		}
		return fragmentSet;
	}

}
