/*******************************************************************************
 * Copyright (c) 2011 Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.patternlanguage.core.validation;

import static org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage.Literals.PATTERN__PARAMETERS;
import static org.eclipse.xtext.util.Strings.equal;

import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.xtext.validation.Check;

/**
 * Validators for Core Pattern Language.
 * <p>
 * Validators implemented:
 * </p>
 * <ul>
 * <li>Duplicate parameter in pattern declaration</li>
 * </ul>
 * 
 * @author Mark Czotter
 * 
 */
public class PatternLanguageJavaValidator extends
		AbstractPatternLanguageJavaValidator {

	public static final String DUPLICATE_VARIABLE_MESSAGE = "Duplicate parameter ";

	@Check
	public void checkPatternParameters(Pattern pattern) {
		for (int i = 0; i < pattern.getParameters().size(); ++i) {
			String leftParameterName = pattern.getParameters().get(i).getName();
			for (int j = i + 1; j < pattern.getParameters().size(); ++j) {
				if (equal(leftParameterName, pattern.getParameters().get(j).getName())) {
					error(DUPLICATE_VARIABLE_MESSAGE + leftParameterName, PATTERN__PARAMETERS, i, IssueCodes.DUPLICATE_PATTERN_PARAMETER_NAME);
					error(DUPLICATE_VARIABLE_MESSAGE + leftParameterName, PATTERN__PARAMETERS, j, IssueCodes.DUPLICATE_PATTERN_PARAMETER_NAME);
				}
			}
		}
	}

}
