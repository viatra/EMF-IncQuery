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
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

/**
 * The job is used to remove a problem marker from the Problems View of Eclipse
 * when the constraint violation is no longer present. 
 * It is associated to the rule that is created for the constraint.  
 * 
 * @author Tamas Szabo
 *
 */
public class MarkerEraserJob implements IMatchProcessor<IPatternMatch> {

	private Logger logger;
	private ConstraintAdapter adapter;
	
	public MarkerEraserJob(ConstraintAdapter adapter, Logger logger) {
		this.logger = logger;
		this.adapter = adapter;
	}
	
	@Override
	public void process(IPatternMatch match) {
		IMarker marker = adapter.removeMarker(match);
		if (marker != null) {
			try {
				marker.delete();
			} catch (CoreException e) {
				logger.error("Could not delete marker!", e);
			}
		}
	}
}
