package org.eclipse.incquery.runtime.patternregistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.patternregistry.listeners.IPatternRegistryListener;

public enum PatternRegistry {

    INSTANCE;

    private final List<IPatternRegistryListener> listeners = new ArrayList<IPatternRegistryListener>();

    private final List<PatternInfo> patternInfos = new ArrayList<PatternInfo>();

    private final Map<String, PatternInfo> idToPatternInfoMap = new HashMap<String, PatternInfo>();

    public List<PatternInfo> getAllPatternInfosInAspect() {
        return Collections.unmodifiableList(patternInfos);
    }

    public PatternInfo addPatternToRegistry(Pattern pattern) {
        // Returns the PatterInfo if it is already registered
        String id = PatternRegistryUtil.getUniquePatternIdentifier(pattern);
        if (idToPatternInfoMap.containsKey(id)) {
            return idToPatternInfoMap.get(id);
        }
        
        // Registers new pattern
        PatternInfo patternInfo = new PatternInfo(pattern);
        patternInfos.add(patternInfo);
        idToPatternInfoMap.put(id, patternInfo);
        for (IPatternRegistryListener patternRegistryListener : listeners) {
            patternRegistryListener.patternAdded(patternInfo);
        }
        return patternInfo;
    }

    // public void removePatternFromRegistry(Pattern pattern) {
    // patterns.remove(PatternRegistryUtil.getUniquePatternIdentifier(pattern));
    // for (IPatternRegistryListener patternRegistryListener : listeners) {
    // patternRegistryListener.patternRemoved(pattern);
    // }
    // }
    //
    // public boolean isPatternContainedInRegistry(Pattern pattern) {
    // return patterns.containsKey(PatternRegistryUtil.getUniquePatternIdentifier(pattern));
    // }

    public void registerListener(IPatternRegistryListener patternRegistryListener) {
        listeners.add(patternRegistryListener);
    }

    public void unregisterListener(IPatternRegistryListener patternRegistryListener) {
        listeners.remove(patternRegistryListener);
    }

}
