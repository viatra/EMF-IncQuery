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
package org.eclipse.viatra2.compiled.emf.patternmatcher;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.viatra2.compiled.emf.patternmatcher.mapping.ViatraEMFMetamodelAdvisor;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.viatra2.compiled.emf.patternmatcher";

	// The shared instance
	private static Activator plugin;

	private static Collection<ViatraEMFMetamodelAdvisor> contributedMetamodelAdvisors = null;
	
	/**
	 * The constructor
	 */
	public Activator() {
		initExtensions();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public Collection<ViatraEMFMetamodelAdvisor> getMetamodelAdvisors() {
		return contributedMetamodelAdvisors;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	public void initExtensions() {
		contributedMetamodelAdvisors = new ArrayList<ViatraEMFMetamodelAdvisor>();
		
		IExtensionRegistry reg = Platform.getExtensionRegistry();	
		IExtensionPoint poi;

		poi = reg.getExtensionPoint("org.eclipse.viatra2.compiled.emf.metainfo");	
		if (poi != null) 
		{		
			IExtension[] exts = poi.getExtensions();
			
			for (IExtension ext: exts)
			{
				
				IConfigurationElement[] els = ext.getConfigurationElements();
				for (IConfigurationElement el : els)
				{
					try
					{
						ViatraEMFMetamodelAdvisor o = (ViatraEMFMetamodelAdvisor)el.createExecutableExtension("advisor-class");
						contributedMetamodelAdvisors.add(o);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

}
