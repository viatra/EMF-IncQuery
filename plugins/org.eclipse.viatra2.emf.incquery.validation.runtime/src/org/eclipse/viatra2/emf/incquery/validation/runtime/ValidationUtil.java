package org.eclipse.viatra2.emf.incquery.validation.runtime;

import org.eclipse.core.resources.IMarker;

public class ValidationUtil {

	public static int getSeverity(String severity) {
		if (severity != null) {
			if (severity.matches("error")) {
				return IMarker.SEVERITY_ERROR;
			}
			else if (severity.matches("warning")) {
				return IMarker.SEVERITY_WARNING;
			}
			else if (severity.matches("info")) {
				return IMarker.SEVERITY_INFO;
			}
		}
		return -1;
	}
	
}
