/*******************************************************************************
 * Copyright (c) 2010-2012, Csicsely Attila, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Csicsely Attila - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.tooling.generator.mavenbuilder;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.emf.incquery.tooling.generator.builder.IErrorFeedback;
import org.eclipse.xtext.diagnostics.Severity;

import com.google.inject.Inject;

/**
 * @author Csicsely Attila
 *
 */
public class LoggerFeedback implements IErrorFeedback {
	
	@Inject
	Logger logger;

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.tooling.generator.builder.IErrorFeedback#clearMarkers(org.eclipse.core.resources.IResource, java.lang.String)
	 */
	@Override
	public void clearMarkers(IResource resource, String markerType) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.tooling.generator.builder.IErrorFeedback#reportError(org.eclipse.emf.ecore.EObject, java.lang.String, java.lang.String, org.eclipse.xtext.diagnostics.Severity, java.lang.String)
	 */
	@Override
	public void reportError(EObject ctx, String message, String errorCode,
			Severity severity, String markerType) {
		switch (severity) {
		case INFO: 
			logger.info(message);
			break;
		case WARNING:
			logger.warn(message);
			break;
		case ERROR:
			logger.error(message);
			break;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.tooling.generator.builder.IErrorFeedback#reportErrorNoLocation(org.eclipse.emf.ecore.EObject, java.lang.String, java.lang.String, org.eclipse.xtext.diagnostics.Severity, java.lang.String)
	 */
	@Override
	public void reportErrorNoLocation(EObject ctx, String message,
			String errorCode, Severity severity, String markerType) {
		switch (severity) {
		case INFO: 
			logger.info(message);
			break;
		case WARNING:
			logger.warn(message);
			break;
		case ERROR:
			logger.error(message);
			break;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.tooling.generator.builder.IErrorFeedback#reportError(org.eclipse.core.resources.IFile, java.lang.String, java.lang.String, org.eclipse.xtext.diagnostics.Severity, java.lang.String)
	 */
	@Override
	public void reportError(IFile file, String message, String errorCode,
			Severity severity, String markerType) {
		switch (severity) {
		case INFO: 
			logger.info(message);
			break;
		case WARNING:
			logger.warn(message);
			break;
		case ERROR:
			logger.error(message);
			break;
		}
	}

}
