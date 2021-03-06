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
package org.eclipse.incquery.runtime.extensibility;

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
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.IExtensions;
import org.eclipse.incquery.runtime.api.GenericMatcherFactory;
import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;

/**
 * Registry for accessing matcher factory instances based on Pattern or pattern ID
 * 
 * @author Abel Hegedus
 * 
 */
public final class MatcherFactoryRegistry {
    private static final Map<String, IMatcherFactory<?>> MATCHER_FACTORIES = createMatcherFactories();

    /**
     * Utility class constructor hidden
     */
    private MatcherFactoryRegistry() {
    }

    private static Map<String, IMatcherFactory<?>> createMatcherFactories() {
        final Map<String, IMatcherFactory<?>> factories = new HashMap<String, IMatcherFactory<?>>();
        initRegistry(factories);
        return factories;
    }

    // Does not use the field MATCHER_FACTORIES as it may still be uninitialized
    private static void initRegistry(Map<String, IMatcherFactory<?>> factories) {
        factories.clear();

        IExtensionRegistry reg = Platform.getExtensionRegistry();
        if (reg == null) {
            return;
        }

        IExtensionPoint poi = reg.getExtensionPoint(IExtensions.MATCHERFACTORY_EXTENSION_POINT_ID);
        if (poi != null) {
            IExtension[] exts = poi.getExtensions();

            Set<String> duplicates = new HashSet<String>();

            for (IExtension ext : exts) {

                IConfigurationElement[] els = ext.getConfigurationElements();
                for (IConfigurationElement el : els) {
                    if (el.getName().equals("matcher")) {
                        prepareMatcherFactory(factories, duplicates, el);
                    } else {
                        IncQueryEngine.getDefaultLogger().error(
                                "[MatcherFactoryRegistry] Unknown configuration element " + el.getName()
                                        + " in plugin.xml of " + el.getDeclaringExtension().getUniqueIdentifier());
                    }
                }
            }
            if (!duplicates.isEmpty()) {
                StringBuilder duplicateSB = new StringBuilder(
                        "[MatcherFactoryRegistry] Trying to register patterns with the same FQN multiple times. Check your plug-in configuration!\n");
                duplicateSB.append("The following pattern FQNs appeared multiple times:\n");
                for (String fqn : duplicates) {
                    duplicateSB.append(String.format("\t%s%n", fqn));
                }
                IncQueryEngine.getDefaultLogger().warn(duplicateSB.toString());
            }
        }
    }

    private static void prepareMatcherFactory(Map<String, IMatcherFactory<?>> factories, Set<String> duplicates,
            IConfigurationElement el) {
        try {
            String id = el.getAttribute("id");
            @SuppressWarnings("unchecked")
            IMatcherFactoryProvider<IMatcherFactory<IncQueryMatcher<IPatternMatch>>> provider = (IMatcherFactoryProvider<IMatcherFactory<IncQueryMatcher<IPatternMatch>>>) el
                    .createExecutableExtension("factoryProvider");
            IMatcherFactory<IncQueryMatcher<IPatternMatch>> matcherFactory = provider.get();
            String fullyQualifiedName = matcherFactory.getPatternFullyQualifiedName();
            if (id.equals(fullyQualifiedName)) {
                if (factories.containsKey(fullyQualifiedName)) {
                    duplicates.add(fullyQualifiedName);
                } else {
                    factories.put(fullyQualifiedName, matcherFactory);
                }
            } else {
                throw new UnsupportedOperationException("Id attribute value " + id
                        + " does not equal pattern FQN of factory " + fullyQualifiedName + " in plugin.xml of "
                        + el.getDeclaringExtension().getUniqueIdentifier());
            }
        } catch (Exception e) {
            IncQueryEngine.getDefaultLogger().error(
                    "[MatcherFactoryRegistry] Exception during matcher factory registry initialization "
                            + e.getMessage(), e);
        }
    }

    /**
     * Puts the factory in the registry, unless it already contains a factory for the given pattern FQN
     * 
     * @param factory
     */
    public static void registerMatcherFactory(IMatcherFactory<?> factory) {
        String qualifiedName = factory.getPatternFullyQualifiedName();
        if (!MATCHER_FACTORIES.containsKey(qualifiedName)) {
            MATCHER_FACTORIES.put(qualifiedName, factory);
        } else {
            IncQueryEngine
                    .getDefaultLogger()
                    .warn(String
                            .format("[MatcherFactoryRegistry] Trying to register duplicate FQN (%s). Check your plug-in configuration!",
                                    qualifiedName));
        }
    }

    /**
     * @return a copy of the set of contributed matcher factories
     */
    public static Set<IMatcherFactory<?>> getContributedMatcherFactories() {
        return new HashSet<IMatcherFactory<?>>(MATCHER_FACTORIES.values());
    }

    /**
     * Returns the specific pattern matcher factory, if it is registered, null otherwise
     * 
     * @param patternFqn
     * @return
     */
    public static IMatcherFactory<?> getMatcherFactory(String patternFqn) {
        if (MATCHER_FACTORIES.containsKey(patternFqn)) {
            return MATCHER_FACTORIES.get(patternFqn);
        }
        return null;
    }

    /**
     * Returns the specific pattern matcher factory, if it is registered, null otherwise
     * 
     * @param pattern
     * @return
     */
    public static IMatcherFactory<?> getMatcherFactory(Pattern pattern) {
        String fullyQualifiedName = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
        if (MATCHER_FACTORIES.containsKey(fullyQualifiedName)) {
            return MATCHER_FACTORIES.get(fullyQualifiedName);
        }
        return null;
    }

    /**
     * Returns a generic pattern matcher factory if a specific factory is not registered
     * 
     * @param pattern
     * @return
     */
    public static IMatcherFactory<?> getOrCreateMatcherFactory(Pattern pattern) {
        String fullyQualifiedName = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
        if (MATCHER_FACTORIES.containsKey(fullyQualifiedName)) {
            return MATCHER_FACTORIES.get(fullyQualifiedName);
        }
        return new GenericMatcherFactory(pattern);
    }

    /**
     * Returns the set of matcher factories in a given package. Only matcher factories with the exact package fully
     * qualified name are returned.
     * 
     * @param packageFQN
     *            the fully qualified name of the package
     * @return the set of matcher factories inside the given package, empty set otherwise.
     */
    public static Set<IMatcherFactory<?>> getPatternGroup(String packageFQN) {
        return getPatternGroupOrSubTree(packageFQN, false);
    }

    /**
     * Returns the set of matcher factories in a given package. Matcher factories with package names starting with the
     * given package are returned.
     * 
     * @param packageFQN
     *            the fully qualified name of the package
     * @return the set of matcher factories in the given package subtree, empty set otherwise.
     */
    public static Set<IMatcherFactory<?>> getPatternSubTree(String packageFQN) {
        return getPatternGroupOrSubTree(packageFQN, true);
    }

    /**
     * Returns a pattern group for the given package
     * 
     * @param packageFQN
     *            the fully qualified name of the package
     * @param includeSubPackages
     *            if true, the pattern is added if it is in the package hierarchy, if false, the pattern is added only
     *            if it is in the given package
     * @return the matcher factories in the group
     */
    private static Set<IMatcherFactory<?>> getPatternGroupOrSubTree(String packageFQN, boolean includeSubPackages) {
        Map<String, Set<IMatcherFactory<?>>> map = new HashMap<String, Set<IMatcherFactory<?>>>();
        if (map.containsKey(packageFQN)) {
            return map.get(packageFQN);
        } else {
            Set<IMatcherFactory<?>> group = new HashSet<IMatcherFactory<?>>();
            for (Entry<String, IMatcherFactory<?>> entry : MATCHER_FACTORIES.entrySet()) {
                addPatternToGroup(packageFQN, group, entry.getKey(), entry.getValue(), includeSubPackages);
            }
            if (group.size() > 0) {
                map.put(packageFQN, group);
            }
            return group;
        }
    }

    /**
     * Adds the factory to an existing group if the package of the factory's pattern matches the given package name.
     * 
     * @param packageFQN
     *            the fully qualified name of the package
     * @param group
     *            the group to add the factory to
     * @param patternFQN
     *            the fully qualified name of the pattern
     * @param factory
     *            the matcher factory of the pattern
     * @param includeSubPackages
     *            if true, the pattern is added if it is in the package hierarchy, if false, the pattern is added only
     *            if it is in the given package
     */
    private static void addPatternToGroup(String packageFQN, Set<IMatcherFactory<?>> group, String patternFQN,
            IMatcherFactory<?> factory, boolean includeSubPackages) {
        if (packageFQN.length() + 1 < patternFQN.length()) {
            if (includeSubPackages) {
                if (patternFQN.startsWith(packageFQN + '.')) {
                    group.add(factory);
                }
            } else {
                String name = patternFQN.substring(patternFQN.lastIndexOf('.') + 1, patternFQN.length());
                if (patternFQN.equals(packageFQN + '.' + name)) {
                    group.add(factory);
                }
            }
        }
    }
}
