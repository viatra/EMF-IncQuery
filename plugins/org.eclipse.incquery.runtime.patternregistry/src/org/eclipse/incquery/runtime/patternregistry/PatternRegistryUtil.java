package org.eclipse.incquery.runtime.patternregistry;

import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternModel;

public class PatternRegistryUtil {

    public static String getFQN(Pattern pattern) {
        PatternModel patternModel = (PatternModel) pattern.eContainer();
        return patternModel.getPackageName() + "." + pattern.getName();
    }

    public static String getUniquePatternIdentifier(Pattern pattern) {
        return pattern.getFileName() + "//" + getFQN(pattern);
    }

}
