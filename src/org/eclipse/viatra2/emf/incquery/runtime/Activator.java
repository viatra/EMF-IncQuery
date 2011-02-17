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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.viatra2.emf.incquery.runtime";

	// The extension point ID
	public static final String EXTENSION_POINT_ID = "org.eclipse.viatra2.emf.incquery.patternmatcher.builder";
	
	// The shared instance
	private static Activator plugin;
	
	private static Collection<ViatraEMFPatternmatcherBuildAdvisor> contributedPatternBuildAdvisors = null;
	private static Map<String, IStatelessGeneratedRetePatternBuilder> contributedStatelessPatternBuilders = null;

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
		initExtensions();
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

	public void initExtensions()
	{
		contributedPatternBuildAdvisors = new ArrayList<ViatraEMFPatternmatcherBuildAdvisor>();
		contributedStatelessPatternBuilders = new HashMap<String, IStatelessGeneratedRetePatternBuilder>();
		
		IExtensionRegistry reg = Platform.getExtensionRegistry();	
		IExtensionPoint poi;

		poi = reg.getExtensionPoint(EXTENSION_POINT_ID);	
		if (poi != null) 
		{		
			IExtension[] exts = poi.getExtensions();
			
			for (IExtension ext: exts)
			{
				
				IConfigurationElement[] els = ext.getConfigurationElements();
				for (IConfigurationElement el : els)
				{
					if (el.getName().equals("patternmatcher-builder")) {
						try
						{
							ViatraEMFPatternmatcherBuildAdvisor o = (ViatraEMFPatternmatcherBuildAdvisor)el.createExecutableExtension("build-class");
							contributedPatternBuildAdvisors.add(o);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					} else if (el.getName().equals("pattern-builder")) {
						try
						{
							IStatelessGeneratedRetePatternBuilder o = (IStatelessGeneratedRetePatternBuilder)el.createExecutableExtension("build-class");
							contributedStatelessPatternBuilders.put(el.getAttribute("pattern-fqn"), o);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					} else {
						throw new UnsupportedOperationException(
								"Unknown configuration element " + el.getName() + " in plugin.xml of "
								+ el.getDeclaringExtension().getUniqueIdentifier());
					}
				}
			}
		}
	}

	public Map<String, IStatelessGeneratedRetePatternBuilder> getContributedStatelessPatternBuilders() {
		return contributedStatelessPatternBuilders;
	}	
	
	public Collection<ViatraEMFPatternmatcherBuildAdvisor> getContributedPatternBuildAdvisors() {
		return contributedPatternBuildAdvisors;
	}
}
