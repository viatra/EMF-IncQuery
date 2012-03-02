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
package org.eclipse.viatra2.emf.incquery.runtime.internal;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.viatra2.emf.incquery.runtime.derived.WellbehavingDerivedFeatureRegistry;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.BuilderRegistry;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The shared instance
	private static Activator plugin;
	private Injector injector;
	

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		BuilderRegistry.initRegistry();
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
	public static Activator getDefault() {
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
	
	public Injector createInjector() {
		return Guice.createInjector(new org.eclipse.viatra2.patternlanguage.EMFPatternLanguageRuntimeModule());
	}

}
