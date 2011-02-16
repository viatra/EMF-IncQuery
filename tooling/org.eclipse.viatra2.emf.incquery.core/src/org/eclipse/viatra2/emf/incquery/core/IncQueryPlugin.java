package org.eclipse.viatra2.emf.incquery.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class IncQueryPlugin implements BundleActivator {
	
	public BundleContext context;
	public static IncQueryPlugin plugin;
	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.viatra2.emf.incquery.core";

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		plugin = this;

	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;

	}

}
