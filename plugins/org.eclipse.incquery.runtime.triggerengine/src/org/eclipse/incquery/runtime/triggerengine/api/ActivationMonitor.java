package org.eclipse.incquery.runtime.triggerengine.api;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;

/**
 * Instances of this class are used by clients to
 * monitor and process the collection of activations within an {@link Agenda} on an
 * individual basis. This means that while the {@link Agenda} always provides an
 * up-to-date view of the applicable activations, an {@link ActivationMonitor} instance
 * accumulates all the activations and the user can clear the collection in the
 * monitor when it is needed (for example after those have been processed). Upon
 * instantiation it can be set to be filled with the initial collection of
 * activations.
 * 
 * @author Tamas Szabo
 *
 */
public class ActivationMonitor {

	private Set<Activation<? extends IPatternMatch>> activations;
	
	public ActivationMonitor() {
		activations = new HashSet<Activation<? extends IPatternMatch>>();
	}
	
	public Set<Activation<? extends IPatternMatch>> getActivations() {
		return Collections.unmodifiableSet(activations);
	}
	
	protected void addActivation(Activation<? extends IPatternMatch> activation) {
		this.activations.add(activation);
	}
	
	protected void removeActivation(Activation<? extends IPatternMatch> activation) {
		this.activations.remove(activation);
	}
	
	public void clear() {
		this.activations.clear();
	}
	
}
