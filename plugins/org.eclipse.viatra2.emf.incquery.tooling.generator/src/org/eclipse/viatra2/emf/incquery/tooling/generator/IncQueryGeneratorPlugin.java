/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

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
	public void start(BundleContext bundleContext) {
		INSTANCE = this;
		IncQueryGeneratorPlugin.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) {
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
