package org.eclipse.incquery.runtime.patternregistry.listeners;

import org.eclipse.incquery.runtime.patternregistry.PatternInfo;

public abstract class PatternRegistryAdapter implements IPatternRegistryListener {

    @Override
    public void patternAdded(PatternInfo patternInfo) {
        // Empty implementation
    }

    @Override
    public void patternRemoved(PatternInfo patternInfo) {
        // Empty implementation
    }

}
