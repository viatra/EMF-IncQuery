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
package org.eclipse.incquery.querybasedfeatures.tooling;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.incquery.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author Abel Hegedus
 *
 */
public final class ProjectLocator {
  
  public static IJavaProject locateProject(String path, Logger logger) {
    IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(path);
    if(project.exists()) {
      ArrayList<String> dependencies = new ArrayList<String>();
      dependencies.add("org.eclipse.viatra2.emf.incquery.runtime");
      dependencies.add("org.eclipse.viatra2.emf.incquery.derived");
      try {
        ProjectGenerationHelper.ensureBundleDependencies(project, dependencies);
      } catch (CoreException e) {
        logger.error("Could not add required dependencies to model project.", e);
      }
      return JavaCore.create(project);
    } else {
      return null;
    }
  }
  
  private ProjectLocator() {
  }
  
}
