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

package org.eclipse.viatra2.emf.incquery.core;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class IncQueryPlugin implements BundleActivator {
	
	public BundleContext context;
	public static IncQueryPlugin plugin;
	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.viatra2.emf.incquery.core";

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		plugin = this;

	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;

	}

	public static File convertURLtoFile(URL url) throws IllegalArgumentException {
		URI uri;
		try 
		{ 
			uri = url.toURI(); 
		} 
		catch (URISyntaxException e) {
			try 
			{
				uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
			} catch (URISyntaxException e1) {
				throw new IllegalArgumentException("The URL cannot be converted: " + url);
			}
		}
		return new File(uri);
	}

	public static File getFileFromBundle(String bundleId, String path) {
		// ugly hack: switch /->\ on windoze
		if ("\\".equals(System.getProperty("file.separator"))) {
			// switch / to \ on windoze
			path = path.replace('/', '\\');
		} else if ("/".equals(System.getProperty("file.separator"))) {
			path = path.replace('\\', '/');
		}

		File f = null;
		// if the bundle is not ready then there is no file, however we may
		// assume its perfectly ready
		Bundle bundle = Platform.getBundle(bundleId);
		if (bundle == null)// || !BundleUtility.isReady(bundle))
			return null;

		// look for the image (this will check both the plugin and fragment
		// folders
		java.net.URL fullPathString = null;
		try {
			// fullPathString = BundleUtility.find(bundle, path);
			fullPathString = FileLocator.toFileURL(FileLocator.find(bundle,
					new Path(path), null));
			f = convertURLtoFile(fullPathString);
		} catch (IllegalArgumentException e) {
			f = new File(fullPathString.getPath());
		} // impossible
		catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}
}
