package org.eclipse.viatra2.emf.incquery.tooling.generator.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.diagnostics.Severity;

public interface IErrorFeedback {

	public static final String JVMINFERENCE_ERROR_TYPE = "org.eclipse.viatra2.emf.incquery.tooling.generator.marker.inference";
	public static final String FRAGMENT_ERROR_TYPE = "org.eclipse.viatra2.emf.incquery.tooling.generator.marker.fragment";
	
	void clearMarkers(IResource resource, String markerType);
	
	void reportError(EObject ctx, String message, String errorCode, Severity severity, String markerType);
	void reportErrorNoLocation(EObject ctx, String message, String errorCode, Severity severity, String markerType);

	void reportError(IFile file, String message, String errorCode, Severity severity, String markerType);

}