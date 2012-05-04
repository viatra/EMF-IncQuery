package org.eclipse.viatra2.emf.incquery.validation.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

public class ValidationUtil {

	/**
	 * The list of registered constraints (cached at first call)
	 */
	private static List<Constraint<IPatternMatch>> constraints;

	
	
	/**
	 * Maps the registered validation adapter instances to a given editor
	 * instance
	 */
	private static Map<IEditorPart, Set<ConstraintAdapter<IPatternMatch>>> adapterMap = new HashMap<IEditorPart, Set<ConstraintAdapter<IPatternMatch>>>();

	public static ModelEditorPartListener editorPartListener = new ModelEditorPartListener();
	
	public static Map<IEditorPart, Set<ConstraintAdapter<IPatternMatch>>> getAdapterMap() {
		return adapterMap;
	}

	/**
	 * Returns the appropriate IMarker enum value of severity for the given
	 * literal
	 * 
	 * @param severity
	 *            the literal of the severity
	 * @return the IMarker severity enum value (info is the default)
	 */
	public static int getSeverity(String severity) {
		if (severity != null) {
			if (severity.matches("error")) {
				return IMarker.SEVERITY_ERROR;
			} else if (severity.matches("warning")) {
				return IMarker.SEVERITY_WARNING;
			}
		}
		return IMarker.SEVERITY_INFO;
	}

	public static List<Constraint<IPatternMatch>> getConstraints() {
		if (constraints == null) {
			constraints = _getConstraints();
		}
		return constraints;
	}

	@SuppressWarnings("unchecked")
	private static List<Constraint<IPatternMatch>> _getConstraints() {
		List<Constraint<IPatternMatch>> result = new ArrayList<Constraint<IPatternMatch>>();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg
				.getExtensionPoint("org.eclipse.viatra2.emf.incquery.validation.runtime.constraint");

		for (IExtension extension : ep.getExtensions()) {
			for (IConfigurationElement ce : extension
					.getConfigurationElements()) {
				if (ce.getName().matches("constraint")) {
					try {
						Object o = ce.createExecutableExtension("class");
						if (o instanceof Constraint<?>) {
							result.add((Constraint<IPatternMatch>) o);
						}
					} catch (CoreException e) {
						ValidationRuntimeActivator
								.getDefault()
								.logException(
										"Error loading EMF-IncQuery Validation Constraint",
										e);
					}
				}
			}
		}
		return result;
	}
}
