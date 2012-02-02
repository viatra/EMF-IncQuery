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
package org.eclipse.viatra2.emf.incquery.runtime.derived;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.emf.incquery.runtime.IExtensions;

/**
 * @author Abel Hegedus
 *
 */
public class WellbehavingDerivedFeatureRegistry {
	private static Collection<EStructuralFeature> contributedWellbehavingDerivedFeatures = new ArrayList<EStructuralFeature>();;

	/**
	 * Called by Activator.
	 */
	public static void initRegistry()
	{
		getContributedWellbehavingDerivedFeatures().clear();
		
		IExtensionRegistry reg = Platform.getExtensionRegistry();	
		IExtensionPoint poi;

		poi = reg.getExtensionPoint(IExtensions.WELLBEHAVING_DERIVED_FEATURE_EXTENSION_POINT_ID);	
		if (poi != null) 
		{		
			IExtension[] exts = poi.getExtensions();
			
			for (IExtension ext: exts)
			{
				
				IConfigurationElement[] els = ext.getConfigurationElements();
				for (IConfigurationElement el : els)
				{
					if (el.getName().equals("wellbehaving-derived-feature")) {
						try
						{
							String packageUri = el.getAttribute("package-nsUri");
							String classifierName = el.getAttribute("classifier-name");
							String featureName = el.getAttribute("feature-name");
							if(packageUri != null) {
								EPackage pckg = EPackage.Registry.INSTANCE.getEPackage(packageUri);
								if(pckg != null && classifierName != null) {
									EClassifier clsr = pckg.getEClassifier(classifierName);
									if(clsr instanceof EClass && featureName != null) {
										EClass cls = (EClass) clsr;
										EStructuralFeature feature = cls.getEStructuralFeature(featureName);
										if(feature != null) {
											contributedWellbehavingDerivedFeatures.add(feature);
										}
									}
								}
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
	 * @param feature
	 */
	public static void registerWellbehavingDerivedFeature(EStructuralFeature feature) {
		contributedWellbehavingDerivedFeatures.add(feature);
	}

	/**
	 * @return the contributedWellbehavingDerivedFeatures
	 */
	public static Collection<EStructuralFeature> getContributedWellbehavingDerivedFeatures() {
		return contributedWellbehavingDerivedFeatures;
	}
}
