/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.urlclassloader;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.cl.ClassLoaderUtil;

/**
 * 
 * @author Mark Czotter
 *
 */
public class URLClassLoaderTestHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IFile file = getFileFromSelection(event);
		if (file != null) {
			try {
				// Change to the FQN of the class you want to load
				String classToLoad = "org.eclipse.viatr2a2.emf.incquery.runtime.api.IPatternMatch";
				ClassLoader loader = ClassLoaderUtil.getClassLoader(file);
				Class<?> clazz = loader.loadClass(classToLoad);
				System.out.println("Successfully loaded class: " + clazz.getCanonicalName());
			} catch (Exception e) {
				throw new ExecutionException("Cannot load the specified class: " , e);
			}
		}
		return null;
	}

	private IFile getFileFromSelection(ExecutionEvent event) {
		ISelection s = HandlerUtil.getCurrentSelection(event);
		if (s instanceof IStructuredSelection) {
			Object o = ((IStructuredSelection) s).getFirstElement();
			if (o instanceof IFile) {
				return (IFile) o;
			}
		}
		return null;
	}

}
