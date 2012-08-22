/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.tooling.generator.derived;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.viatra2.emf.incquery.core.project.ProjectGenerationHelper;

/**
 * @author Abel Hegedus
 *
 */
public final class ProjectLocator {
  
  public static IJavaProject locateProject(String path) {
    IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(path);
    if(project.exists()) {
      ArrayList<String> dependencies = new ArrayList<String>();
      dependencies.add("org.eclipse.viatra2.emf.incquery.runtime");
      try {
        ProjectGenerationHelper.ensureBundleDependencies(project, dependencies);
      } catch (CoreException e) {
        e.printStackTrace();
      }
      return JavaCore.create(project);
    } else return null;
  }
  
  private ProjectLocator() {
  }
  
}
