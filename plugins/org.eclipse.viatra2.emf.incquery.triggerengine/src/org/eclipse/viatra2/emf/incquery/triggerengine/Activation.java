package org.eclipse.viatra2.emf.incquery.triggerengine;

import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

/**
 * An {@link Activation} is a created for a {@link Rule} when the 
 * preconditions (LHS) are fully satisfied with some domain model
 * elements and the rule becomes eligible for execution.
 * <br/><br/>
 * An Activation holds a state, a pattern match, the corresponding rule 
 * and whether it was fired yet. The state of the Activation can be 
 * either Inactive, Appeared, Disappeared, Upgraded or Fired. Upon 
 * {@link Rule} instantiation, one may set whether the Disappeared and 
 * Upgraded states will be used during the lifecycle of the Activation.  
 * If multiple firing is allowed for the Activation then only
 * the 'Appeared' state will be used. 
 * 
 * @author Tamas Szabo
 *
 * @param <MatchType> the type of the pattern match
 */
public abstract class Activation<MatchType extends IPatternMatch> {

	protected MatchType patternMatch;
	protected boolean fired;
	protected ActivationState state;
	protected Rule<MatchType> rule;
	protected boolean allowMultipleFiring;
	private int cachedHash = -1;
	
	public Activation(Rule<MatchType> rule, MatchType patternMatch, boolean allowMultipleFiring) {
		this.patternMatch = patternMatch;
		this.fired = false;
		this.rule = rule;
		this.allowMultipleFiring = allowMultipleFiring;
	}
	
	public void setFired(boolean fired) {
		this.fired = fired;
	}
	
	public boolean isFired() {
		return this.fired;
	}
	
	public MatchType getPatternMatch() {
		return patternMatch;
	}
	
	public ActivationState getState() {
		return state;
	}
	
	public void setState(ActivationState state) {
		this.state = state;
	}
	
	/**
	 * The activation will be fired; the appropriate job of the rule will be executed based on the activation state.
	 */
	public void fire() {		
		if (this.state == ActivationState.APPEARED && rule.afterAppearanceJob != null) {
			this.rule.afterAppearanceJob.process(patternMatch);
		}
		else if (this.state == ActivationState.DISAPPEARED && rule.afterDisappearanceJob != null) {
			this.rule.afterDisappearanceJob.process(patternMatch);
		}
		else if (this.state == ActivationState.UPDATED && rule.afterModificationJob != null) {
			this.rule.afterModificationJob.process(patternMatch);	
		}
		if (!allowMultipleFiring) {
			this.fired = true;
			this.rule.activationFired(this);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj == null || !(obj instanceof Activation)) {
			return false;
		}
		else {
			Activation<MatchType> other = (Activation<MatchType>) obj;
			return (other.fired == this.fired) && (other.rule.equals(this.rule)) && (other.patternMatch.equals(this.patternMatch)) && (other.state == this.state);
		}
	}
	
	@Override
	public int hashCode() {
		if (cachedHash == -1) {
			final int prime = 31;
			cachedHash = 1;
			cachedHash = prime * cachedHash + state.hashCode(); 
			cachedHash = prime * cachedHash + patternMatch.hashCode();
		}
		return cachedHash; 
	}
}
