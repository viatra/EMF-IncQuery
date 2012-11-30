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

package org.eclipse.incquery.tooling.core.generator.jvmmodel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.incquery.tooling.core.generator.builder.IErrorFeedback;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.resource.DerivedStateAwareResource;
import org.eclipse.xtext.xbase.jvmmodel.JvmModelAssociator;

import com.google.inject.Inject;

/**
 * This subClass is needed for local variable scoping. PatternBody not associated with any Inferred classes.
 * 
 * @author Mark Czotter
 * 
 */
@SuppressWarnings("restriction")
public class EMFPatternJvmModelAssociator extends JvmModelAssociator {

    @Inject
    private IErrorFeedback feedback;

    @Override
    public JvmIdentifiableElement getLogicalContainer(EObject object) {
        if (object instanceof PatternBody) {
            return null;
        }
        return super.getLogicalContainer(object);
    }

    @Override
    public void installDerivedState(DerivedStateAwareResource resource, boolean preIndexingPhase) {
        if (!resource.getURI().isEmpty() && resource.getURI().isPlatformResource()) {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IFile file = root.getFile(new Path(resource.getURI().toPlatformString(true)));
            if (file.exists()) {
                feedback.clearMarkers(file, IErrorFeedback.JVMINFERENCE_ERROR_TYPE);
            }
        }
        super.installDerivedState(resource, preIndexingPhase);
    }

}
