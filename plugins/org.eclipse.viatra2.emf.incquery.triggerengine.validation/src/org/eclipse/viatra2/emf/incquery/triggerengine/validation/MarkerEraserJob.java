package org.eclipse.viatra2.emf.incquery.triggerengine.validation;

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

public class MarkerEraserJob implements IMatchProcessor<IPatternMatch> {

	private Logger logger;
	private Map<IPatternMatch, IMarker> markerMap;
	
	public MarkerEraserJob(Map<IPatternMatch, IMarker> markerMap, Logger logger) {
		this.logger = logger;
		this.markerMap = markerMap;
	}
	
	@Override
	public void process(IPatternMatch match) {
		IMarker marker = markerMap.remove(match);
		if (marker != null) {
			try {
				marker.delete();
			} catch (CoreException e) {
				logger.error("Could not delete marker!", e);
			}
		}
	}
}
