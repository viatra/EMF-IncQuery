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
package org.eclipse.viatra2.emf.incquery.runtime;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.viatra2.emf.incquery.runtime.derived.WellbehavingDerivedFeatureRegistry;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.MatcherFactoryRegistry;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageRuntimeModule;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * The activator class controls the plug-in life cycle
 */
public class IncQueryRuntimePlugin extends Plugin {

	// The shared instance
	private static IncQueryRuntimePlugin plugin;
	private Injector injector;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		// TODO Builder regisry may be used later
		//BuilderRegistry.initRegistry();
		MatcherFactoryRegistry.initRegistry();
		WellbehavingDerivedFeatureRegistry.initRegistry();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
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
		return new org.eclipse.viatra2.patternlanguage.EMFPatternLanguageRuntimeModule();
	}

}
