/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.patternlanguage.core.helper;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;

/**
 * Simple helper class to support processing pattern models.
 * @author Bergmann Gábor
 *
 */
public class CorePatternLanguageHelper {

	/**
	 * Returns the name of the pattern, qualified by package name.
	 */
	public static String getFullyQualifiedName(Pattern p) {
		PatternModel patternModel = (PatternModel) p.eContainer();
		return patternModel.getPackageName() + "." + p.getName();
		// TODO ("local pattern?")
	}
	
	/** Compiles a map for name-based lookup of symbolic parameter positions. */
	public static Map<String, Integer> getParameterPositionsByName(Pattern pattern) {
		HashMap<String, Integer> posMapping = new HashMap<String, Integer>();
		int parameterPosition = 0;
		for (Variable parameter : pattern.getParameters()) {
			posMapping.put(parameter.getName(), parameterPosition++);
		}
		return posMapping;
	}

}
