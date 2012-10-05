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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.triggerengine.ActivationMonitor;
import org.eclipse.viatra2.emf.incquery.triggerengine.Agenda;
import org.eclipse.viatra2.emf.incquery.triggerengine.Rule;
import org.eclipse.viatra2.emf.incquery.triggerengine.TriggerEngine;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.AutomaticFiringStrategy;

public class ConstraintAdapter<T extends IPatternMatch> {
	
	private Map<IPatternMatch, IMarker> markerMap;
	private Agenda agenda;
	
	@SuppressWarnings("unchecked")
	public ConstraintAdapter(IEditorPart editorPart, Notifier notifier, Logger logger) {
		this.markerMap = new HashMap<IPatternMatch, IMarker>();
		
		this.agenda = TriggerEngine.getInstance().createAgenda(notifier);
		
		for (Constraint<IPatternMatch> constraint : ValidationUtil.getConstraintsForEditorId(editorPart.getSite().getId())) {
			Rule<IPatternMatch> rule = (Rule<IPatternMatch>) agenda.createRule(constraint.getMatcherFactory().getPattern(), true, true);
			rule.afterAppearanceJob = new MarkerPlacerJob(markerMap, constraint, logger);
			rule.afterDisappearanceJob = new MarkerEraserJob(markerMap, logger);
			rule.afterModificationJob = new MarkerUpdaterJob(markerMap, constraint, logger);
			
			ActivationMonitor monitor = agenda.newActivationMonitor(true);
			AutomaticFiringStrategy firingStrategy = new AutomaticFiringStrategy(monitor);
			agenda.addCallbackAfterUpdates(firingStrategy);
		}
	}
	
	public void dispose() {
		for (IMarker marker : markerMap.values()) {
			try {
				marker.delete();
			} 
			catch (CoreException e) {
				e.printStackTrace();
			}
		}
		
		agenda.dispose();
	}
}
