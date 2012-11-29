/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.incquery.runtime.extensibility.IInjectorProvider;
import org.eclipse.incquery.runtime.internal.XtextInjectorProvider;
import org.osgi.framework.BundleContext;

import com.google.inject.Injector;

/**
 * The activator class controls the plug-in life cycle
 */
public class IncQueryRuntimePlugin extends Plugin {

	// The shared instance
	private static IncQueryRuntimePlugin plugin;
	
	public static final String PLUGIN_ID="org.eclipse.incquery.runtime";

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		// TODO Builder registry may be used later
		//BuilderRegistry.initRegistry();
		XtextInjectorProvider.INSTANCE.setInjector(createInjector());
		//MatcherFactoryRegistry.initRegistry();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		XtextInjectorProvider.INSTANCE.setInjector(null);
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static IncQueryRuntimePlugin getDefault() {
		return plugin;
	}

	private Injector createInjector() throws CoreException {
		IConfigurationElement[] providers = Platform.getExtensionRegistry().getConfigurationElementsFor(IExtensions.INJECTOREXTENSIONID);
		if (providers.length > 0) {
			IConfigurationElement provider = providers[0]; //XXX multiple providers not supported
			IInjectorProvider injectorProvider = (IInjectorProvider) provider.createExecutableExtension("injector");
			return injectorProvider.getInjector();
		} else {
			return new EMFPatternLanguageStandaloneSetup().createInjector();
		}
	}

}
