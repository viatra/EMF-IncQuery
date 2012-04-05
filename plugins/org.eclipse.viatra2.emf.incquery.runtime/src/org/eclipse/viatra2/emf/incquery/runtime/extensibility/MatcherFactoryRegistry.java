/*******************************************************************************
 * Copyright (c) 2004-2011 Abel Hegedus and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.runtime.extensibility;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.viatra2.emf.incquery.runtime.IExtensions;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Registry for accessing matcher factory instances based on Pattern or pattern ID
 * 
 * @author Abel Hegedus
 *
 */
@SuppressWarnings("rawtypes")
public class MatcherFactoryRegistry {
	private static Map<String, IMatcherFactory> contributedMatcherFactories = new HashMap<String, IMatcherFactory>();

	/**
	 * Called by Activator.
	 */
	public static void initRegistry()
	{
		contributedMatcherFactories.clear();
		
		IExtensionRegistry reg = Platform.getExtensionRegistry();	
		IExtensionPoint poi;

		poi = reg.getExtensionPoint(IExtensions.MATCHERFACTORY_EXTENSION_POINT_ID);	
		if (poi != null) 
		{		
			IExtension[] exts = poi.getExtensions();
			
			for (IExtension ext: exts)
			{
				
				IConfigurationElement[] els = ext.getConfigurationElements();
				for (IConfigurationElement el : els)
				{
					if (el.getName().equals("matcher")) {
						try
						{
							String id = el.getAttribute("id");
							IMatcherFactory matcherFactory = (IMatcherFactory)el.createExecutableExtension("factory");
							String fullyQualifiedName = matcherFactory.getPatternFullyQualifiedName();
							if(id.equals(fullyQualifiedName)) {
							  contributedMatcherFactories.put(fullyQualifiedName, matcherFactory);
							} else {
								throw new UnsupportedOperationException(
										"Id attribute value " + id + " does not equal pattern FQN of factory " + fullyQualifiedName + " in plugin.xml of "
										+ el.getDeclaringExtension().getUniqueIdentifier());
							}
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


	/**
	 * Puts the factory in the registry, unless it already contains a factory for the given pattern FQN 
	 * 
	 * @param factory
	 */
	public static void registerMatcherFactory(IMatcherFactory factory) {
	
		if(!contributedMatcherFactories.containsKey(factory.getPatternFullyQualifiedName())) {
			contributedMatcherFactories.put(factory.getPatternFullyQualifiedName(), factory);
		}
	}

	/**
	 * @return the contributedMatcherFactories
	 */
	public static Map<String, IMatcherFactory> getContributedMatcherFactories() {
		return contributedMatcherFactories;
	}
	
	/**
	 * Returns the specific pattern matcher factory, if it is registered, null otherwise
	 * @param patternFqn
	 * @return
	 */
	public static IMatcherFactory getMatcherFactory(String patternFqn) {
		if(contributedMatcherFactories.containsKey(patternFqn)) {
			return contributedMatcherFactories.get(patternFqn);
		}
		return null;
	}
	
	/**
	 * Returns the specific pattern matcher factory, if it is registered, null otherwise
	 * 
	 * @param pattern
	 * @return
	 */
	public static IMatcherFactory getMatcherFactory(Pattern pattern) {
		String fullyQualifiedName = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
		if(contributedMatcherFactories.containsKey(fullyQualifiedName)) {
			return contributedMatcherFactories.get(fullyQualifiedName);
		}
		return null;
	}
	
	/**
	 * Returns a generic pattern matcher factory if a specific factory is not registered
	 * @param pattern
	 * @return
	 */
	public static IMatcherFactory getOrCreateMatcherFactory(Pattern pattern) {
		String fullyQualifiedName = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
		if(contributedMatcherFactories.containsKey(fullyQualifiedName)) {
			return contributedMatcherFactories.get(fullyQualifiedName);
		} 
		return new GenericMatcherFactory(pattern);
	}
	
}
