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

package org.eclipse.incquery.validation.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class ValidationUtil {
    
    /**
     * Constructor hidden for utility class
     */
    private ValidationUtil() {
        
    }


	private static Logger logger = Logger.getLogger(ValidationUtil.class);
	
	private static Map<IWorkbenchPage, Set<IEditorPart>> pageMap = new HashMap<IWorkbenchPage, Set<IEditorPart>>();
	
	private static Set<String> genericEditorIds = Sets.newHashSet("org.eclipse.emf.ecore.presentation.XMLReflectiveEditorID",
	        "org.eclipse.emf.ecore.presentation.ReflectiveEditorID", "org.eclipse.emf.genericEditor");
	
	private static Map<IEditorPart, ConstraintAdapter> adapterMap = new HashMap<IEditorPart, ConstraintAdapter>();
	public static synchronized Map<IEditorPart, ConstraintAdapter> getAdapterMap() {
		return adapterMap;
	}

	private static Multimap<String, Constraint<IPatternMatch>> editorConstraintMap;
	public static synchronized Multimap<String, Constraint<IPatternMatch>> getEditorConstraintMap() {
	    if(editorConstraintMap == null) {
	        editorConstraintMap = loadConstraintsFromExtensions();
	    }
		return editorConstraintMap;
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

	public static synchronized boolean isConstraintsRegisteredForEditorId(String editorId) {
		return getEditorConstraintMap().containsKey(editorId);
	}
	
	public static synchronized Set<Constraint<IPatternMatch>> getConstraintsForEditorId(String editorId) {
	    if(genericEditorIds.contains(editorId)) {
            return ImmutableSet.copyOf(getEditorConstraintMap().values());
        }
		Set<Constraint<IPatternMatch>> set = new HashSet<Constraint<IPatternMatch>>(getEditorConstraintMap().get(editorId));
		set.addAll(getEditorConstraintMap().get("*"));
		return set;
	}

	private static synchronized Multimap<String, Constraint<IPatternMatch>> loadConstraintsFromExtensions() {
		Multimap<String, Constraint<IPatternMatch>> result = HashMultimap.create();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint("org.eclipse.incquery.validation.runtime.constraint");

		for (IExtension extension : ep.getExtensions()) {
			for (IConfigurationElement ce : extension.getConfigurationElements()) {
				if (ce.getName().equals("constraint")) {
					processConstraintConfigurationElement(result, ce);
				}
			}
		}
		return result;
	}

    /**
     * @param result
     * @param ce
     */
	@SuppressWarnings("unchecked")
    private static void processConstraintConfigurationElement(Multimap<String, Constraint<IPatternMatch>> result,
            IConfigurationElement ce) {
        try {
        	List<String> ids = new ArrayList<String>();
        	for (IConfigurationElement child : ce.getChildren()) {
        		if (child.getName().equals("enabledForEditor")) {
        			String id = child.getAttribute("editorId");
        			if (id != null && !id.equals("")) {
        				ids.add(id);
        			}
        		}
        	}

        	Object o = ce.createExecutableExtension("class");
        	if (o instanceof Constraint<?>) {
        		if (ids.isEmpty()) {
        			ids.add("*");
        		}
        		for (String id : ids) {
        			result.put(id, (Constraint<IPatternMatch>) o);
        		}
        	}
        } catch (CoreException e) {
        	logger.error("Error loading EMF-IncQuery Validation Constraint", e);
        }
    }

	public static synchronized void addNotifier(IEditorPart editorPart, Notifier notifier) {
		adapterMap.put(editorPart, new ConstraintAdapter(editorPart, notifier, logger));
	}
	
	public static void registerEditorPart(IEditorPart editorPart) {
		IWorkbenchPage page = editorPart.getSite().getPage();
		if (pageMap.containsKey(page)) {
			pageMap.get(page).add(editorPart);
		}
		else {
			Set<IEditorPart> editorParts = new HashSet<IEditorPart>();
			editorParts.add(editorPart);
			pageMap.put(page, editorParts);
			page.addPartListener(ValidationPartListener.getInstance());
		}
	}
	
	public static void unregisterEditorPart(IEditorPart editorPart) {
		IWorkbenchPage page = editorPart.getSite().getPage();
		if (pageMap.containsKey(page)) {
			pageMap.get(page).remove(editorPart);
			if (pageMap.get(page).size() == 0) {
				pageMap.remove(page);
				page.removePartListener(ValidationPartListener.getInstance());
			}
		}
	}
}
