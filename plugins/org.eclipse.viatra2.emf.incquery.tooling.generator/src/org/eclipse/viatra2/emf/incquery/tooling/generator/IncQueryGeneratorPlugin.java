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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.viatra2.emf.incquery.runtime.IncQueryRuntimePlugin;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.IInjectorProvider;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageRuntimeModule;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageStandaloneSetup;
import org.eclipse.xtext.ui.shared.SharedStateModule;
import org.eclipse.xtext.util.Modules2;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class IncQueryGeneratorPlugin extends AbstractUIPlugin {

	private static BundleContext context;
	private Injector injector;
	public static IncQueryGeneratorPlugin INSTANCE;
	private static final String INJECTOREXTENSIONID = "org.eclipse.viatra2.emf.incquery.runtime.injectorprovider";
	
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
		super.start(bundleContext);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		super.stop(bundleContext);
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
			org.eclipse.xtext.resource.IResourceFactory resourceFactory = injector.getInstance(org.eclipse.xtext.resource.IResourceFactory.class);
			org.eclipse.xtext.resource.IResourceServiceProvider serviceProvider = injector.getInstance(org.eclipse.xtext.resource.IResourceServiceProvider.class);
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("eiq", resourceFactory);
			org.eclipse.xtext.resource.IResourceServiceProvider.Registry.INSTANCE.getExtensionToFactoryMap().put("eiq", serviceProvider);
		}
		return injector;
	}
	
	protected Injector createInjector() {
		IConfigurationElement[] providers = Platform.getExtensionRegistry().getConfigurationElementsFor(INJECTOREXTENSIONID);
		if (providers.length > 0) {
			try {
				IConfigurationElement provider = providers[0]; // XXX multiple
																// providers not
																// supported
				IInjectorProvider injectorProvider = (IInjectorProvider) provider
						.createExecutableExtension("injector");
				return injectorProvider.getInjector();
			} catch (CoreException e) {
				// TODO real exception handling and logging needed!
				System.out.println("Cannot initialize injector.");
				e.printStackTrace();
			}
		}
		Module runtimeModule = new EMFPatternLanguageRuntimeModule();
		Module sharedStateModule = new SharedStateModule();
		Module genModule = getRuntimeModule();
		Module mergedModule = Modules2.mixin(runtimeModule, sharedStateModule, genModule);
		
		return Guice.createInjector(mergedModule);
	}

	/**
	 * Return the runtime module for the pattern language project
	 * @return
	 */
	public EMFPatternLanguageRuntimeModule getRuntimeModule() {
		return new GeneratorModule();
	}
}
