package org.eclipse.incquery.tooling.core.project;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * An incremental project builder for IncQuery projects. Currently it is an empty builder implementation - registered
 * for future use.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class IncQueryProjectBuilder extends IncrementalProjectBuilder {

    @Override
    protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
        return null;
    }

}
