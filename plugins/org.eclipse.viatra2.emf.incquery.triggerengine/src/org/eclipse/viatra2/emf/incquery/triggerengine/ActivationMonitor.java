package org.eclipse.viatra2.emf.incquery.triggerengine;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

/**
 * Instances of this class are used by clients to
 * monitor and process the collection of activations within an agenda on an
 * individual basis. This means that while the agenda always provides an
 * up-to-date view of the applicable activations, an ActivationMonitor instance
 * accumulates all the activations and the user can clear the collection in the
 * monitor when it is needed (for example after those have been processed). Upon
 * instantiation it can be set to be filled with the initial collection of
 * activations.
 * 
 * @author Tamas Szabo
 *
 */
public class ActivationMonitor {

	private Collection<Activation<? extends IPatternMatch>> activations;
	
	public ActivationMonitor() {
		activations = new HashSet<Activation<? extends IPatternMatch>>();
	}
	
	public Collection<Activation<? extends IPatternMatch>> getActivations() {
		return Collections.unmodifiableCollection(activations);
	}
	
	protected void addActivations(Collection<Activation<? extends IPatternMatch>> activations) {
		this.activations.addAll(activations);
	}
	
	public void clear() {
		this.activations.clear();
	}
	
}
