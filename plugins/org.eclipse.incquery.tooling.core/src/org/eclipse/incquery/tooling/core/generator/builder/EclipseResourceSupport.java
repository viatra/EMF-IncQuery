/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.core.generator.builder;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.xtext.builder.EclipseOutputConfigurationProvider;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.generator.OutputConfiguration;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class EclipseResourceSupport {

    private final static class EclipseResourceFileCallback implements EclipseResourceFileSystemAccess2.IFileCallback {
        public boolean beforeFileDeletion(IFile file) {
            return true;
        }

        public void afterFileUpdate(IFile file) {
            handleFileAccess(file);
        }

        public void afterFileCreation(IFile file) {
            handleFileAccess(file);
        }

        protected void handleFileAccess(IFile file) {
        }
    }

    @Inject
    private Provider<EclipseResourceFileSystemAccess2> fileSystemAccessProvider;

    @Inject
    private EclipseOutputConfigurationProvider outputConfigurationProvider;

    /**
     * Calculates a file system access component for the selected target project. This is required for code generation
     * API.
     * 
     * @param targetProject
     * @return an initialized file system access component for the
     */
    public EclipseResourceFileSystemAccess2 createProjectFileSystemAccess(IProject targetProject) {
        EclipseResourceFileSystemAccess2 fsa = fileSystemAccessProvider.get();
        fsa.setProject(targetProject);
        fsa.setMonitor(new NullProgressMonitor());
        Map<String, OutputConfiguration> outputs = new HashMap<String, OutputConfiguration>();
        for (OutputConfiguration conf : outputConfigurationProvider.getOutputConfigurations(targetProject)) {
            outputs.put(conf.getName(), conf);
        }
        fsa.setOutputConfigurations(outputs);
        fsa.setPostProcessor(new EclipseResourceFileCallback());
        return fsa;
    }

}
