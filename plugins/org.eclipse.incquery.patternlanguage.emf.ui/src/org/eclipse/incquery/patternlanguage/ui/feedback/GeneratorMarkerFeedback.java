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

package org.eclipse.incquery.patternlanguage.ui.feedback;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.incquery.tooling.core.generator.builder.IErrorFeedback;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.resource.ILocationInFileProvider;
import org.eclipse.xtext.ui.editor.validation.MarkerCreator;
import org.eclipse.xtext.util.ITextRegion;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.Issue;

import com.google.inject.Inject;

public class GeneratorMarkerFeedback implements IErrorFeedback {
	
	@Inject
	private MarkerCreator markerCreator;
	@Inject
	private ILocationInFileProvider locationProvider;
	@Inject
	private Logger logger;

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.patternlanguage.ui.feedback.IErrorFeedback#reportError(org.eclipse.emf.ecore.EObject, java.lang.String, java.lang.String)
	 */
	@Override
	public void reportError(EObject ctx, String message, String errorCode, Severity severity, String markerType) {
		try {
			Resource res = ctx.eResource();
			if (res != null && res.getURI().isPlatformResource()) {
				ITextRegion region = locationProvider
						.getSignificantTextRegion(ctx);
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				IFile file = root.getFile(new Path(res.getURI()
						.toPlatformString(true)));
				createMarker(message, errorCode, markerType, file, region, severity);
			}
		} catch (CoreException e) {
			logger.error("Error while creating error marker", e);
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.patternlanguage.ui.feedback.IErrorFeedback#reportErrorNoLocation(org.eclipse.emf.ecore.EObject, java.lang.String, java.lang.String)
	 */
	@Override
	public void reportErrorNoLocation(EObject ctx, String message, String errorCode, Severity severity, String markerType) {
		try {
			Resource res = ctx.eResource();
			if (res != null && res.getURI().isPlatformResource()) {
				ITextRegion region = ITextRegion.EMPTY_REGION;
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				IFile file = root.getFile(new Path(res.getURI()
						.toPlatformString(true)));
				createMarker(message, errorCode, markerType, file, region, severity);
			}
		} catch (CoreException e) {
			logger.error("Error while creating error marker", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.patternlanguage.ui.feedback.IErrorFeedback#reportError(org.eclipse.core.resources.IFile, java.lang.String, java.lang.String)
	 */
	@Override
	public void reportError(IFile file, String message, String errorCode, Severity severity, String markerType) {
		try {
			ITextRegion region = ITextRegion.EMPTY_REGION;
			createMarker(message, errorCode, markerType, file, region, severity);
		} catch (CoreException e) {
			logger.error("Error while creating error marker", e);
		}
	}
	
	private void createMarker(String message, String errorCode, String markerType, IFile file,
			ITextRegion region, Severity severity) throws CoreException {
		Issue.IssueImpl issue = new Issue.IssueImpl();
		issue.setOffset(region.getOffset());
		issue.setLength(region.getLength());
		issue.setMessage(message);
		issue.setCode(errorCode);
		issue.setSeverity(severity);
		issue.setType(CheckType.EXPENSIVE);
		markerCreator.createMarker(issue, file, markerType);
	}
	@Override
	public void clearMarkers(IResource resource, String markerType) {
		try {
			resource.deleteMarkers(markerType, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			logger.error("Error while clearing markers", e);
		}
	}

}
