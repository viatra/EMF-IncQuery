/*******************************************************************************
 * Copyright (c) 2010-2012, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.base;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.incquery.runtime.base.comprehension.WellbehavingDerivedFeatureRegistry;
import org.osgi.framework.BundleContext;

public class IncQueryBasePlugin extends Plugin {

	// The shared instance
	private static IncQueryBasePlugin plugin;
	
	public static final String PLUGIN_ID="org.eclipse.viatra2.emf.incquery.base";
	public static final String WELLBEHAVING_DERIVED_FEATURE_EXTENSION_POINT_ID = "org.eclipse.viatra2.emf.incquery.base.wellbehaving.derived.features";

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		WellbehavingDerivedFeatureRegistry.initRegistry();
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
	public static IncQueryBasePlugin getDefault() {
		return plugin;
	}

}
