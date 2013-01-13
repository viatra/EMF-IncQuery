/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.triggerengine.api;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;

/**
 * An {@link Activation} is a created for a {@link AbstractRule} when the preconditions (LHS) are fully satisfied with
 * some domain model elements and the rule becomes eligible for execution.
 * 
 * <p>
 * An Activation holds a state, a pattern match, the corresponding rule and whether it was fired yet. The state of the
 * Activation can be either Inactive, Appeared, Disappeared, Upgraded or Fired. Upon {@link AbstractRule} instantiation,
 * one may set whether the Disappeared and Upgraded states will be used during the lifecycle of the Activation. If
 * multiple firing is allowed for the Activation then only the Appeared state will be used.
 * 
 * TODO rewrite documentation
 * 
 * @author Tamas Szabo
 * 
 * @param <Match>
 *            the type of the pattern match
 */
public class Activation<Match extends IPatternMatch> {

    private Match patternMatch;
    private ActivationState state;
    private RuleInstance<Match, ? extends IncQueryMatcher<Match>> rule;
    private int cachedHash = -1;

    protected <Matcher extends IncQueryMatcher<Match>> Activation(RuleInstance<Match, Matcher> rule, Match patternMatch) {
        this.patternMatch = patternMatch;
        this.state = ActivationState.INACTIVE;
        this.rule = rule;
    }

    public Match getPatternMatch() {
        return patternMatch;
    }

    public ActivationState getState() {
        return state;
    }
    
    /**
     * @return the rule
     */
    public RuleInstance<Match, ? extends IncQueryMatcher<Match>> getRule() {
        return rule;
    }

    /**
     * Should be only set through {@link RuleInstance#activationStateTransition}
     * 
     * @param state
     */
    protected void setState(ActivationState state) {
        this.state = state;
    }

    /**
     * The activation will be fired; the appropriate job of the rule will be executed based on the activation state.
     */
    public void fire() {
        rule.fire(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Activation) {
            Activation<Match> other = (Activation<Match>) obj;
            return (other.rule.equals(this.rule)) && (other.patternMatch.equals(this.patternMatch))
                    && (other.state == this.state);
        } else {
            return false;
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
