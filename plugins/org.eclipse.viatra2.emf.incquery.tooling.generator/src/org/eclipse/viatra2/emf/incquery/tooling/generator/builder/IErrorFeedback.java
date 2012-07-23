package org.eclipse.viatra2.emf.incquery.tooling.generator.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.diagnostics.Severity;

public interface IErrorFeedback {

	/**
	 * An error type for use in the JvmModelInferrer. It is differentiated from {@link #FRAGMENT_ERROR_TYPE}, as the
	 * two builds have different lifecycles, so cleaning has to be executed at different points.
	 */
	public static final String JVMINFERENCE_ERROR_TYPE = "org.eclipse.viatra2.emf.incquery.tooling.generator.marker.inference";
	/**
	 * An error type for use in the generator fragments. It is differentiated from {@link #JVMINFERENCE_ERROR_TYPE}, as the
	 * two builds have different lifecycles, so cleaning has to be executed at different points. 
	 */
	public static final String FRAGMENT_ERROR_TYPE = "org.eclipse.viatra2.emf.incquery.tooling.generator.marker.fragment";
	
	/**
	 * Clears all problem markers from the resource and all its descendants.
	 * @param resource a file, folder or project to clean all markers from
	 * @param markerType {@link #JVMINFERENCE_ERROR_TYPE} and {@link #FRAGMENT_ERROR_TYPE} are supported
	 */
	void clearMarkers(IResource resource, String markerType);
	
	/**
	 * Reports an error in a context object. The error marker only appears if the context object is contained in a workspace resource,
	 * and then it is associated with the location of the context object in the textual file.
	 * All runtime errors related to the creation of the marker are logged.
	 * @param ctx
	 * @param message
	 * @param errorCode an arbitrary error code - see {@link GeneratorIssueCodes} for already defined constants
	 * @param severity 
	 * @param markerType {@link #JVMINFERENCE_ERROR_TYPE} and {@link #FRAGMENT_ERROR_TYPE} are supported
	 */
	void reportError(EObject ctx, String message, String errorCode, Severity severity, String markerType);
	/**
	 * Reports an error in a context object. The error marker only appears if the context object is contained in a workspace resource,
	 * but it is <b>NOT</b> associated with the location of the context object in the textual file.
	 * All runtime errors related to the creation of the marker are logged.
	 * @param ctx
	 * @param message
	 * @param errorCode an arbitrary error code - see {@link GeneratorIssueCodes} for already defined constants
	 * @param severity 
	 * @param markerType {@link #JVMINFERENCE_ERROR_TYPE} and {@link #FRAGMENT_ERROR_TYPE} are supported
	 */
	void reportErrorNoLocation(EObject ctx, String message, String errorCode, Severity severity, String markerType);
	/**
	 * Reports an error in a file, but is not associated to any specific line.
	 * All runtime errors related to the creation of the marker are logged.
	 * @param file
	 * @param message
	 * @param errorCode an arbitrary error code - see {@link GeneratorIssueCodes} for already defined constants
	 * @param severity 
	 * @param markerType {@link #JVMINFERENCE_ERROR_TYPE} and {@link #FRAGMENT_ERROR_TYPE} are supported
	 */
	void reportError(IFile file, String message, String errorCode, Severity severity, String markerType);

}