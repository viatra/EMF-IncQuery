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
import org.eclipse.viatra2.emf.incquery.triggerengine.api.ActivationState;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.IAgenda;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.IRule;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.RuleEngine;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.AutomaticFiringStrategy;

/**
 * The constraint adapter class is used to collect the constraints and deal with their 
 * maintenance for a given EMF instance model. If the validation framework is 
 * initialized an instance of this class will be created which handles the creation 
 * of the appropriate rules and their jobs. 
 *  
 * @author Tamas Szabo
 */
public class ConstraintAdapter {
	
	private Map<IPatternMatch, IMarker> markerMap;
	private IAgenda agenda;
	
	public ConstraintAdapter(IEditorPart editorPart, Notifier notifier, Logger logger) {
		this.markerMap = new HashMap<IPatternMatch, IMarker>();
		
		this.agenda = RuleEngine.getInstance().getOrCreateAgenda(notifier);
		
		for (Constraint<IPatternMatch> constraint : ValidationUtil.getConstraintsForEditorId(editorPart.getSite().getId())) {
			IRule<IPatternMatch> rule = agenda.createRule(constraint.getMatcherFactory(), true, true);
			rule.setStateChangeProcessor(ActivationState.APPEARED, new MarkerPlacerJob(this, constraint, logger));
			rule.setStateChangeProcessor(ActivationState.DISAPPEARED, new MarkerEraserJob(this, logger));
			rule.setStateChangeProcessor(ActivationState.UPDATED, new MarkerUpdaterJob(this, constraint, logger));
		}
		
		AutomaticFiringStrategy firingStrategy = new AutomaticFiringStrategy(agenda.newActivationMonitor(true));
		agenda.addUpdateCompleteListener(firingStrategy, true);
	}
	
	public void dispose() {
		for (IMarker marker : markerMap.values()) {
			try {
				marker.delete();
			} 
			catch (CoreException e) {
				agenda.getLogger().error(String.format("Exception occured when removing a marker on dispose: %s", e.getMessage()), e);
			}
		}
		agenda.dispose();
	}
	
	public IMarker getMarker(IPatternMatch match) {
		return this.markerMap.get(match);
	}
	
	public IMarker addMarker(IPatternMatch match, IMarker marker) {
		return this.markerMap.put(match, marker);
	}
	
	public IMarker removeMarker(IPatternMatch match) {
		return this.markerMap.remove(match);
	}
}
