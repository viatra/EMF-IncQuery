package org.eclipse.viatra2.emf.incquery.databinding.runtime;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class DatabindingRuntimeActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		DatabindingRuntimeActivator.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		DatabindingRuntimeActivator.context = null;
	}

}
