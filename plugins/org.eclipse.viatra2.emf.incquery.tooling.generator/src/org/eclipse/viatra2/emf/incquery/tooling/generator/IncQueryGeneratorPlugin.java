package org.eclipse.viatra2.emf.incquery.tooling.generator;

import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageRuntimeModule;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class IncQueryGeneratorPlugin implements BundleActivator {

	private static BundleContext context;
	private Injector injector;
	public static IncQueryGeneratorPlugin INSTANCE;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		INSTANCE = this;
		IncQueryGeneratorPlugin.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		IncQueryGeneratorPlugin.context = null;
		INSTANCE = null;
	}

	/**
	 * Returns injector for the EMFPatternLanguage.
	 * @return
	 */
	public Injector getInjector() {
		if (injector == null) {
			injector = createInjector();
		}
		return injector;
//		return EMFPatternLanguageActivator.getInstance().getInjector("org.eclipse.viatra2.patternlanguage.EMFPatternLanguage");
	}
	
	protected Injector createInjector() {
		return Guice.createInjector(getRuntimeModule());
	}

	/**
	 * Return the runtime module for the pattern language project
	 * @return
	 */
	public EMFPatternLanguageRuntimeModule getRuntimeModule() {
		return new GeneratorModule();
	}
}
