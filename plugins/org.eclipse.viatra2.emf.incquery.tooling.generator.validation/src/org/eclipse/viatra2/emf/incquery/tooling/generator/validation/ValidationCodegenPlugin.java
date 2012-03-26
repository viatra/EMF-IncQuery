package org.eclipse.viatra2.emf.incquery.tooling.generator.validation;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ValidationCodegenPlugin implements BundleActivator {

	public static BundleContext context;
	public static ValidationCodegenPlugin plugin;
	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.viatra2.emf.incquery.validation.codegen";

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		ValidationCodegenPlugin.context = bundleContext;
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		ValidationCodegenPlugin.context = null;
	}

}
