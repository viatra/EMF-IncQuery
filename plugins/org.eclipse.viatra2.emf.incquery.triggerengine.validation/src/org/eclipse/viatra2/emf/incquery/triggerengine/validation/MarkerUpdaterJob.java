package org.eclipse.viatra2.emf.incquery.triggerengine.validation;

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.DatabindingAdapterUtil;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

public class MarkerUpdaterJob implements IMatchProcessor<IPatternMatch> {

	private Constraint<IPatternMatch> constraint;
	private Logger logger;
	private Map<IPatternMatch, IMarker> markerMap;
	
	public MarkerUpdaterJob(Map<IPatternMatch, IMarker> markerMap, Constraint<IPatternMatch> constraint, Logger logger) {
		this.constraint = constraint;
		this.logger = logger;
		this.markerMap = markerMap;
	}
	
	@Override
	public void process(IPatternMatch match) {
		IMarker marker = markerMap.get(match);
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
