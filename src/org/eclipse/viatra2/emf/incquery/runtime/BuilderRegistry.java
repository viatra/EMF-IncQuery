/*******************************************************************************
 * Copyright (c) 2004-2009 Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Istvan Rath - initial API and implementation
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
import org.eclipse.viatra2.emf.incquery.runtime.internal.ViatraEMFPatternmatcherBuildAdvisor;

/**
 * Registry class for registering IncQuery pattern builders.
 * @author Istvan Rath
 *
 */
public class BuilderRegistry {

	private static Collection<ViatraEMFPatternmatcherBuildAdvisor> contributedPatternBuildAdvisors = new ArrayList<ViatraEMFPatternmatcherBuildAdvisor>();;

	private static Map<String, IStatelessGeneratedRetePatternBuilder> contributedStatelessPatternBuilders = new HashMap<String, IStatelessGeneratedRetePatternBuilder>();

	public static void initRegistry()
	{
		contributedPatternBuildAdvisors.clear();
		contributedStatelessPatternBuilders.clear();
		
		IExtensionRegistry reg = Platform.getExtensionRegistry();	
		IExtensionPoint poi;

		poi = reg.getExtensionPoint(IExtensions.EXTENSION_POINT_ID);	
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
							BuilderRegistry.contributedPatternBuildAdvisors.add(o);
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

	public static Map<String, IStatelessGeneratedRetePatternBuilder> getContributedStatelessPatternBuilders() {
		return contributedStatelessPatternBuilders;
	}	
	
	public static Collection<ViatraEMFPatternmatcherBuildAdvisor> getContributedPatternBuildAdvisors() {
		return contributedPatternBuildAdvisors;
	}
}
