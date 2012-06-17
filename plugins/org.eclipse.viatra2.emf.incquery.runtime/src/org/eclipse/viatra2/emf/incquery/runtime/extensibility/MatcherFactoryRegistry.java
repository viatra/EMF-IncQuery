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
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.viatra2.emf.incquery.runtime.IExtensions;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
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
	private static final Map<String, IMatcherFactory> contributedMatcherFactories = new HashMap<String, IMatcherFactory>();
	// NOTE pattern group management is relegated to PatternGroup classes
	//private static Map<String, Set<IMatcherFactory>> matcherFactoryGroups = null;
	//private static Map<String, Set<IMatcherFactory>> matcherFactorySubTrees = null;

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
							IncQueryEngine.getDefaultLogger().logError("[MatcherFactoryRegistry] Exception during matcher factory registry initialization " + e.getMessage(),e);
						}
					} else {
						IncQueryEngine.getDefaultLogger().logError("[MatcherFactoryRegistry] Unknown configuration element " + el.getName() + " in plugin.xml of "
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
	
		String qualifiedName = factory.getPatternFullyQualifiedName();
		if(!contributedMatcherFactories.containsKey(qualifiedName)) {
			contributedMatcherFactories.put(qualifiedName, factory);
		  // NOTE pattern group management is relegated to PatternGroup classes
			/*/if(matcherFactoryGroups != null) {
				for (Entry<String, Set<IMatcherFactory>> groupEntry : matcherFactoryGroups.entrySet()) {
					addPatternToGroup(groupEntry.getKey(), groupEntry.getValue(), qualifiedName, factory, false);
				}
			}
			if(matcherFactorySubTrees != null) {
				for (Entry<String, Set<IMatcherFactory>> groupEntry : matcherFactorySubTrees.entrySet()) {
					addPatternToGroup(groupEntry.getKey(), groupEntry.getValue(), qualifiedName, factory, true);
				}
			}*/
		}
	}

	/**
	 * @return a copy of the set of contributed matcher factories
	 */
	public static Set<IMatcherFactory> getContributedMatcherFactories() {
		return new HashSet<IMatcherFactory>(contributedMatcherFactories.values());
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
	
	/**
	 * Returns the set of matcher factories in a given package. Only matcher factories with the exact package fully qualified name are returned. 
	 * 
	 * @param packageFQN the fully qualified name of the package
	 * @return the set of matcher factories inside the given package, empty set otherwise.
	 */
	public static Set<IMatcherFactory> getPatternGroup(String packageFQN) {
		return getPatternGroupOrSubTree(packageFQN, false);
	}
	
	/**
	 * Returns the set of matcher factories in a given package. Matcher factories with package names starting with the given package are returned. 
	 * 
	 * @param packageFQN the fully qualified name of the package
	 * @return the set of matcher factories in the given package subtree, empty set otherwise.
	 */
	public static Set<IMatcherFactory> getPatternSubTree(String packageFQN) {
		return getPatternGroupOrSubTree(packageFQN, true);
	}

	/**
	 * Returns a pattern group for the given package
	 * 
	 * @param packageFQN the fully qualified name of the package
	 * @param includeSubPackages if true, the pattern is added if it is in the package hierarchy,
	 *  if false, the pattern is added only if it is in the given package
	 * @return the matcher factories in the group
	 */
	private static Set<IMatcherFactory> getPatternGroupOrSubTree(String packageFQN, boolean includeSubPackages) {
		Map<String, Set<IMatcherFactory>> map = null;
	  // NOTE pattern group management is relegated to PatternGroup classes
		/*if(includeSubPackages) {
			map = matcherFactorySubTrees;
		} else {
			map = matcherFactoryGroups;
		}*/
		if(map == null) {
			map = new HashMap<String, Set<IMatcherFactory>>();
		}
		if(map.containsKey(packageFQN)) {
			return map.get(packageFQN);
		} else {
			Set<IMatcherFactory> group = new HashSet<IMatcherFactory>();
			for (Entry<String, IMatcherFactory> entry : contributedMatcherFactories.entrySet()) {
				addPatternToGroup(packageFQN, group, entry.getKey(), entry.getValue(), includeSubPackages);
			}
			if(group.size() > 0) {
				map.put(packageFQN, group);
			}
			return group;
		}
	}

	/**
	 * Adds the factory to an existing group if the package of the factory's pattern matches the given package name.
	 * 
	 * @param packageFQN the fully qualified name of the package
	 * @param group the group to add the factory to
	 * @param patternFQN the fully qualified name of the pattern
	 * @param factory the matcher factory of the pattern
	 * @param includeSubPackages if true, the pattern is added if it is in the package hierarchy,
	 *  if false, the pattern is added only if it is in the given package
	 */
	private static void addPatternToGroup(String packageFQN, Set<IMatcherFactory> group, String patternFQN, IMatcherFactory factory, boolean includeSubPackages) {
		if(packageFQN.length() + 1 < patternFQN.length()) {
			if(includeSubPackages) {
				if(patternFQN.startsWith(packageFQN+'.')) {
					group.add(factory);
				}
			} else {
				String name = patternFQN.substring(patternFQN.lastIndexOf('.')+1,patternFQN.length());
				if(patternFQN.equals(packageFQN+'.'+name)) {
					group.add(factory);
				}
			}
		}
	}
}
