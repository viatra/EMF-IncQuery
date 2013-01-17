package org.eclipse.incquery.runtime.patternregistry.listeners;

import org.eclipse.incquery.runtime.patternregistry.PatternInfo;


public interface IPatternRegistryListener {

    public void patternAdded(PatternInfo patternInfo);

    public void patternRemoved(PatternInfo patternInfo);

}
