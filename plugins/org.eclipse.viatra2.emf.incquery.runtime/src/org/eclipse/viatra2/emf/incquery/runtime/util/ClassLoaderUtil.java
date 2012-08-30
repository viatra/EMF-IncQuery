/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Mark Czotter - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.runtime.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * 
 * @author Mark Czotter
 * 
 */
public class ClassLoaderUtil {

	/**
	 * Returns a {@link ClassLoader} that is capable of loading classes defined
	 * in the project of the input file, or in any dependencies of that project.
	 * 
	 * @param file
	 * @return {@link ClassLoader}
	 * @throws CoreException
	 * @throws MalformedURLException
	 */
	public static ClassLoader getClassLoader(IFile file) throws CoreException,
			MalformedURLException {
		IProject project = file.getProject();
		IJavaProject jp = JavaCore.create(project);
		String[] classPathEntries = JavaRuntime
				.computeDefaultRuntimeClassPath(jp);
		List<URL> classURLs = getClassesAsURLs(classPathEntries);
		URL[] urls = (URL[]) classURLs.toArray(new URL[classURLs.size()]);
		URLClassLoader loader = URLClassLoader.newInstance(urls, jp.getClass()
				.getClassLoader());
		return loader;
	}

	private static List<URL> getClassesAsURLs(String[] classPathEntries)
			throws MalformedURLException {
		List<URL> urlList = new ArrayList<URL>();
		for (int i = 0; i < classPathEntries.length; i++) {
			String entry = classPathEntries[i];
			IPath path = new Path(entry);
			URL url = path.toFile().toURI().toURL();
			urlList.add(url);
		}
		return urlList;
	}

}