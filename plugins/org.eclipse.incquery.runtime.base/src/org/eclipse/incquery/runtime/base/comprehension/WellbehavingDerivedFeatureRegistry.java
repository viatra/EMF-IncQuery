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
package org.eclipse.incquery.runtime.base.comprehension;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.base.IncQueryBasePlugin;

/**
 * @author Abel Hegedus
 * 
 */
public class WellbehavingDerivedFeatureRegistry {
    private static Collection<EStructuralFeature> contributedWellbehavingDerivedFeatures = new ArrayList<EStructuralFeature>();;
    private static Collection<EClass> contributedWellbehavingDerivedClasses = new ArrayList<EClass>();;
    private static Collection<EPackage> contributedWellbehavingDerivedPackages = new ArrayList<EPackage>();;

    /**
	 * 
	 */
    private WellbehavingDerivedFeatureRegistry() {
    }

    /**
     * Called by IncQueryBasePlugin.
     */
    public static void initRegistry() {
        getContributedWellbehavingDerivedFeatures().clear();
        getContributedWellbehavingDerivedClasses().clear();
        getContributedWellbehavingDerivedPackages().clear();

        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IExtensionPoint poi;

        poi = reg.getExtensionPoint(IncQueryBasePlugin.WELLBEHAVING_DERIVED_FEATURE_EXTENSION_POINT_ID);
        if (poi != null) {
            IExtension[] exts = poi.getExtensions();

            for (IExtension ext : exts) {

                IConfigurationElement[] els = ext.getConfigurationElements();
                for (IConfigurationElement el : els) {
                    if (el.getName().equals("wellbehaving-derived-feature")) {
                        processWellbehavingExtension(el);
                    } else {
                        throw new UnsupportedOperationException("Unknown configuration element " + el.getName()
                                + " in plugin.xml of " + el.getDeclaringExtension().getUniqueIdentifier());
                    }
                }
            }
        }
    }

    /**
     * @param el
     */
    private static void processWellbehavingExtension(IConfigurationElement el) {
        try {
            String packageUri = el.getAttribute("package-nsUri");
            String classifierName = el.getAttribute("classifier-name");
            String featureName = el.getAttribute("feature-name");
            if (packageUri != null) {
                EPackage pckg = EPackage.Registry.INSTANCE.getEPackage(packageUri);
                if (pckg != null) {
                    if (classifierName != null) {
                        EClassifier clsr = pckg.getEClassifier(classifierName);
                        if (clsr instanceof EClass) {
                            if (featureName != null) {
                                EClass cls = (EClass) clsr;
                                EStructuralFeature feature = cls.getEStructuralFeature(featureName);
                                if (feature != null) {
                                    contributedWellbehavingDerivedFeatures.add(feature);
                                }
                            } else {
                                contributedWellbehavingDerivedClasses.add((EClass) clsr);
                            }
                        }
                    } else {
                        contributedWellbehavingDerivedPackages.add(pckg);
                    }
                }
            }
        } catch (Exception e) {
            final Logger logger = Logger.getLogger(WellbehavingDerivedFeatureRegistry.class);
            logger.error("Well-behaving feature registration failed", e);
        }
    }

    /**
     * @param feature
     */
    public static void registerWellbehavingDerivedFeature(EStructuralFeature feature) {
        contributedWellbehavingDerivedFeatures.add(feature);
    }

    /**
     * @param feature
     */
    public static void registerWellbehavingDerivedClass(EClass cls) {
        contributedWellbehavingDerivedClasses.add(cls);
    }

    /**
     * @param feature
     */
    public static void registerWellbehavingDerivedPackage(EPackage pkg) {
        contributedWellbehavingDerivedPackages.add(pkg);
    }

    /**
     * @return the contributedWellbehavingDerivedFeatures
     */
    public static Collection<EStructuralFeature> getContributedWellbehavingDerivedFeatures() {
        return contributedWellbehavingDerivedFeatures;
    }

    public static Collection<EClass> getContributedWellbehavingDerivedClasses() {
        return contributedWellbehavingDerivedClasses;
    }

    public static Collection<EPackage> getContributedWellbehavingDerivedPackages() {
        return contributedWellbehavingDerivedPackages;
    }

    public static boolean isWellbehavingFeature(EStructuralFeature feature) {
        if (contributedWellbehavingDerivedFeatures.contains(feature)) {
            return true;
        } else if (contributedWellbehavingDerivedClasses.contains(feature.getEContainingClass())) {
            return true;
        } else if (contributedWellbehavingDerivedPackages.contains(feature.getEContainingClass().getEPackage())) {
            return true;
        }
        return false;
    }
}
