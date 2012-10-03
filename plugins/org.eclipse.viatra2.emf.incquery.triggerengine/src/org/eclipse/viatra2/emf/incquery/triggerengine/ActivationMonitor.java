package org.eclipse.viatra2.emf.incquery.triggerengine;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

public class ActivationMonitor {

	private Collection<Activation<? extends IPatternMatch>> activations;
	
	public ActivationMonitor() {
		activations = new HashSet<Activation<? extends IPatternMatch>>();
	}
	
	public Collection<Activation<? extends IPatternMatch>> getActivations() {
		return Collections.unmodifiableCollection(activations);
	}
	
	void addActivations(Collection<Activation<? extends IPatternMatch>> activations) {
		this.activations.addAll(activations);
	}
	
	public void clear() {
		this.activations.clear();
	}
	
}
