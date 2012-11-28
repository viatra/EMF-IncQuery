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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.adapter.DatabindingAdapterUtil;

/**
 * The job is used to update a problem marker in the Problems View of Eclipse. 
 * It is associated to the rule that is created for the constraint.  
 * 
 * @author Tamas Szabo
 *
 */
public class MarkerUpdaterJob implements IMatchProcessor<IPatternMatch> {

	private Constraint<IPatternMatch> constraint;
	private Logger logger;
	private ConstraintAdapter adapter;
	
	public MarkerUpdaterJob(ConstraintAdapter adapter, Constraint<IPatternMatch> constraint, Logger logger) {
		this.constraint = constraint;
		this.logger = logger;
		this.adapter = adapter;
	}
	
	@Override
	public void process(IPatternMatch match) {
		IMarker marker = adapter.getMarker(match);
		if (marker != null) {
			try {
				marker.setAttribute(IMarker.MESSAGE, DatabindingAdapterUtil.getMessage(match, constraint.getMessage()));
			} 
			catch (CoreException e) {
				logger.error("Error during marker update!", e);
			}
		}
	}
}
