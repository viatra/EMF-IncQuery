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

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.util.DatabindingAdapterUtil;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternMatch;

public class MarkerPlacerJob implements IMatchProcessor<IPatternMatch> {

	private Constraint<IPatternMatch> constraint;
	private Logger logger;
	private Map<IPatternMatch, IMarker> markerMap;
	
	public MarkerPlacerJob(Map<IPatternMatch, IMarker> markerMap, Constraint<IPatternMatch> constraint, Logger logger) {
		this.constraint = constraint;
		this.logger = logger;
		this.markerMap = markerMap;
	}
	
	@Override
	public void process(IPatternMatch match) {
		EObject location = constraint.getLocationObject(match);
		if (location != null && location.eResource() != null) {
			URI uri = location.eResource().getURI();
			String platformString = uri.toPlatformString(true);
			IResource markerLoc = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);
			try {
				IMarker marker = markerLoc.createMarker(EValidator.MARKER);
				marker.setAttribute(IMarker.SEVERITY, constraint.getSeverity());
				marker.setAttribute(IMarker.TRANSIENT, true);
				String locationString = String.format("%1$s %2$s", location.eClass().getName(), BasePatternMatch.prettyPrintValue(location));
				marker.setAttribute(IMarker.LOCATION, locationString);
				marker.setAttribute(EValidator.URI_ATTRIBUTE, EcoreUtil.getURI(location).toString());
				marker.setAttribute(IMarker.MESSAGE, DatabindingAdapterUtil.getMessage(match, constraint.getMessage()));
				markerMap.put(match, marker);
			}
			catch (CoreException e) {
				logger.error("Error during marker initialization!", e);
			}
		}
	}
}