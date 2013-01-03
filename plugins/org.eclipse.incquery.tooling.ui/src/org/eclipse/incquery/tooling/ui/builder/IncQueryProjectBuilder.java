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
package org.eclipse.incquery.tooling.ui.builder;

import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.incquery.tooling.core.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.incquery.tooling.core.project.IncQueryNature;
import org.eclipse.xtext.builder.IXtextBuilderParticipant;
import org.eclipse.xtext.builder.IXtextBuilderParticipant.BuildType;
import org.eclipse.xtext.builder.builderState.IBuilderState;
import org.eclipse.xtext.builder.impl.BuildContext;
import org.eclipse.xtext.builder.impl.BuildData;
import org.eclipse.xtext.builder.impl.QueuedBuildData;
import org.eclipse.xtext.builder.impl.ToBeBuilt;
import org.eclipse.xtext.builder.impl.XtextBuilder;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

/**
 * An incremental project builder for IncQuery projects. Based on the {@link XtextBuilder} class, but simplified for
 * IncQuery generation.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class IncQueryProjectBuilder extends XtextBuilder {

    /**
     * Visitor to check for specific resources (by path) in a resource delta.
     * 
     * @author Zoltan Ujhelyi
     * 
     */
    private final static class ChangeDetector implements IResourceDeltaVisitor {
        /**
         * Sets the path to look for genmodel 
         * @param path
         */
        public ChangeDetector(IPath path) {
            super();
            this.path = path;
        }

        private final IPath path;
        private boolean changeFound = false;

        public boolean visit(IResourceDelta delta) throws CoreException {
            if (path.equals(delta.getFullPath())) {
                changeFound = true;
            }
            return !changeFound;
        }

        public boolean isChangeFound() {
            return changeFound;
        }
    }

    public static final String BUILDER_ID = IncQueryNature.BUILDER_ID;

    @Inject
    private IEiqGenmodelProvider genmodelProvider;

    @Inject
    private IXtextBuilderParticipant participant;

    @Inject
    private QueuedBuildData queuedBuildData;

    @Inject
    private IBuilderState builderState;

    /**
     * @param monitor
     *            the progress monitor to use for reporting progress to the user. It is the caller's responsibility to
     *            call done() on the given monitor. Accepts null, indicating that no progress should be reported and
     *            that the operation cannot be cancelled.
     */
    @Override
    protected void incrementalBuild(IResourceDelta delta, final IProgressMonitor monitor) throws CoreException {
        ChangeDetector visitor = new ChangeDetector(
                genmodelProvider.getGeneratorModelPath(getProject()));
        delta.accept(visitor);
        if (visitor.isChangeFound()) {
            super.fullBuild(monitor, false);
        } else {
            super.incrementalBuild(delta, monitor);
        }

    }

    /**
     * Overridden to not search for @link{IXtextBuilderParticipant} but use only the IncQuery-specified one.
     */
    @Override
    protected void doBuild(ToBeBuilt toBeBuilt, IProgressMonitor monitor, BuildType type) throws CoreException {
        SubMonitor progress = SubMonitor.convert(monitor, 2);

        ResourceSet resourceSet = getResourceSetProvider().get(getProject());
        resourceSet.getLoadOptions().put(ResourceDescriptionsProvider.NAMED_BUILDER_SCOPE, Boolean.TRUE);
        if (resourceSet instanceof ResourceSetImpl) {
            ((ResourceSetImpl) resourceSet).setURIResourceMap(Maps.<URI, Resource> newHashMap());
        }
        BuildData buildData = new BuildData(getProject().getName(), resourceSet, toBeBuilt, queuedBuildData);
        if (!buildData.isEmpty()) {
            ImmutableList<Delta> deltas = builderState.update(buildData, progress.newChild(1));
            if (participant != null) {
                participant.build(new BuildContext(this, resourceSet, deltas, type), progress.newChild(1));
                getProject().getWorkspace().checkpoint(false);
            } else {
                progress.worked(1);
            }
        } else {
            progress.worked(2);
        }
        resourceSet.eSetDeliver(false);
        resourceSet.getResources().clear();
        resourceSet.eAdapters().clear();
    }

    /**
     * Overridden to not search for @link{IXtextBuilderParticipant} but use only the IncQuery-specified one.
     * 
     */
    @Override
    protected void doClean(ToBeBuilt toBeBuilt, IProgressMonitor monitor) throws CoreException {
        SubMonitor progress = SubMonitor.convert(monitor, 2);
        ImmutableList<Delta> deltas = builderState.clean(toBeBuilt.getToBeDeleted(), progress.newChild(1));
        if (participant != null) {
            participant.build(new BuildContext(this, getResourceSetProvider().get(getProject()), deltas,
                    BuildType.CLEAN), progress.newChild(1));
        } else {
            progress.worked(1);
        }
    }
}
