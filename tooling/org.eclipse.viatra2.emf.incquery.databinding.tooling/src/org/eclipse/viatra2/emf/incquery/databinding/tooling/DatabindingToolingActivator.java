package org.eclipse.viatra2.emf.incquery.databinding.tooling;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class DatabindingToolingActivator implements BundleActivator {

	public static BundleContext context;
	public static DatabindingToolingActivator plugin;
	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.viatra2.emf.incquery.databinding.core";

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		DatabindingToolingActivator.context = bundleContext;
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		DatabindingToolingActivator.context = null;
	}
}
