/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - initial API and implementation
 *******************************************************************************/

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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class ValidationUtil {
	
	/**
	 * The list of registered constraints (cached at first call)
	 */
	private static List<Constraint<IPatternMatch>> constraints;
	// TODO change to multimap<EditorId,Constraint>
	private static Multimap<String, Constraint<IPatternMatch>> editorConstraintMap;
	// TODO activate only constraints with proper id

	private ValidationUtil() {}
	
	/**
	 * Maps the registered validation adapter instances to a given editor
	 * instance
	 */
	private static Map<IEditorPart, Set<ConstraintAdapter<IPatternMatch>>> adapterMap = new HashMap<IEditorPart, Set<ConstraintAdapter<IPatternMatch>>>();

	public static final ModelEditorPartListener editorPartListener = new ModelEditorPartListener();
	
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

	/*public synchronized static List<Constraint<IPatternMatch>> getConstraints() {
		if (constraints == null) {
			constraints = loadConstraintsFromExtensions();
		}
		return constraints;
	}*/
	
	public synchronized static List<Constraint<IPatternMatch>> getConstraints() {
	  if (editorConstraintMap == null) {
      editorConstraintMap = loadConstraintsFromExtensions();
    }
    return new ArrayList<Constraint<IPatternMatch>>(editorConstraintMap.values());
  }
	
	public synchronized static List<Constraint<IPatternMatch>> getConstraintsForEditorId(String editorId) {
    if (editorConstraintMap == null) {
      editorConstraintMap = loadConstraintsFromExtensions();
    }
    ArrayList<Constraint<IPatternMatch>> list = new ArrayList<Constraint<IPatternMatch>>(editorConstraintMap.get(editorId));
    list.addAll(editorConstraintMap.get("*"));
    return list;
  }
	
	public synchronized static boolean constraintsRegisteredForEditorId(String editorId) {
    if (editorConstraintMap == null) {
      editorConstraintMap = loadConstraintsFromExtensions();
    }
    return editorConstraintMap.containsKey(editorId);
  }

	@SuppressWarnings("unchecked")
	private static Multimap<String, Constraint<IPatternMatch>> loadConstraintsFromExtensions() {
	  Multimap<String, Constraint<IPatternMatch>> result = HashMultimap.create();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint("org.eclipse.viatra2.emf.incquery.validation.runtime.constraint");
		
		for (IExtension extension : ep.getExtensions()) {
			for (IConfigurationElement ce : extension
					.getConfigurationElements()) {
				if (ce.getName().equals("constraint")) {
					try {
					  List<String> ids = new ArrayList<String>();
					  for (IConfigurationElement child : ce.getChildren()) {
					    if(child.getName().equals("enabledForEditor")) {
					      String id = child.getAttribute("editorId");
					      if(id != null && !id.equals("")) {
					        ids.add(id);
					      }
					    }
					  }
					  
						Object o = ce.createExecutableExtension("class");
						if (o instanceof Constraint<?>) {
						  if(ids.isEmpty()) {
						    ids.add("*");
						  }
						  for(String id : ids) {
						    result.put(id,(Constraint<IPatternMatch>) o);
						  }
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
