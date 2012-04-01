package org.eclipse.viatra2.emf.incquery.validation.runtime;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

public class ValidationUtil {

	public static List<Constraint<IPatternMatch>> constraints = getConstraints();
	
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
	
	@SuppressWarnings("unchecked")
	private static List<Constraint<IPatternMatch>> getConstraints() {
		List<Constraint<IPatternMatch>> result = new ArrayList<Constraint<IPatternMatch>>();
		
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint("org.eclipse.viatra2.emf.incquery.validation.runtime.constraint");
		
		for (IExtension extension : ep.getExtensions()) {
			for (IConfigurationElement ce : extension.getConfigurationElements()) {
				if (ce.getName().matches("constraint")) {
					try {
						Object o = ce.createExecutableExtension("class");
						if (o instanceof Constraint<?>) {
							result.add((Constraint<IPatternMatch>) o);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}
}
