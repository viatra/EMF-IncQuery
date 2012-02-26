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

import static org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage.Literals.PATTERN_COMPOSITION_CONSTRAINT__PATTERN_REF;
import static org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage.Literals.PATTERN__NAME;
import static org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage.Literals.PATTERN__PARAMETERS;
import static org.eclipse.xtext.util.Strings.equal;

import java.util.Iterator;

import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCompositionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.xtext.validation.Check;

/**
 * Validators for Core Pattern Language.
 * <p>
 * Validators implemented:
 * </p>
 * <ul>
 * <li>Duplicate parameter in pattern declaration</li>
 * <li>Duplicate pattern definition (name duplication only, better calculation is needed)</li>
 * <li>Pattern call parameter checking (only the number of the parameters, types not supported yet)</li>
 * <li>Empty PatternBody check</li>
 * </ul>
 * 
 * @author Mark Czotter
 * 
 */
public class PatternLanguageJavaValidator extends
		AbstractPatternLanguageJavaValidator {

	public static final String DUPLICATE_VARIABLE_MESSAGE = "Duplicate parameter ";
	public static final String DUPLICATE_PATTERN_DEFINITION_MESSAGE = "Duplicate pattern ";
	
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
	
	@Check
	public void checkPatternCompositionConstraintParameters(PatternCompositionConstraint constraint) {
		if (constraint.getPatternRef() != null && constraint.getParameters() != null) {
			final int definitionParameterSize = constraint.getPatternRef().getParameters().size();
			final int callParameterSize = constraint.getParameters().size();
			if (definitionParameterSize != callParameterSize) {
				error("The pattern " + getFormattedPattern(constraint.getPatternRef()) + " is not applicable for the arguments("+getFormattedArgumentsList(constraint)+")", PATTERN_COMPOSITION_CONSTRAINT__PATTERN_REF, IssueCodes.WRONG_NUMBER_PATTERNCALL_PARAMETER);
			}
		}
	}

	@Check
	public void checkPatterns(PatternModel model) {
		if (model.getPatterns() != null && !model.getPatterns().isEmpty()) {
			// TODO: more precise calculation is needed for duplicate patterns (number and type of pattern parameters)
			for (int i = 0; i < model.getPatterns().size(); ++i) {
				Pattern leftPattern = model.getPatterns().get(i);
				String leftPatternName = leftPattern.getName();
				for (int j = i + 1; j < model.getPatterns().size(); ++j) {
					Pattern rightPattern = model.getPatterns().get(j);
					String rightPatternName = rightPattern.getName();
					if (equal(leftPatternName, rightPatternName)) {
						error(DUPLICATE_PATTERN_DEFINITION_MESSAGE + leftPatternName, leftPattern,  PATTERN__NAME, IssueCodes.DUPLICATE_PATTERN_DEFINITION);
						error(DUPLICATE_PATTERN_DEFINITION_MESSAGE + rightPatternName, rightPattern,  PATTERN__NAME, IssueCodes.DUPLICATE_PATTERN_DEFINITION);
					}
				}
			}
		}
	}
	
	@Check
	public void checkPatternBody(PatternBody body) {
		if (body.getConstraints().isEmpty()) {
			String bodyName = getName(body);
			if (bodyName == null) {
				Pattern pattern = ((Pattern)body.eContainer());
				String patternName = pattern.getName();
				error("A patternbody of " + patternName + " is empty", body, PatternLanguagePackage.Literals.PATTERN_BODY__CONSTRAINTS, IssueCodes.PATTERN_BODY_EMPTY);
			} else {
				error("The patternbody "+bodyName+" cannot be empty", body, PatternLanguagePackage.Literals.PATTERN_BODY__NAME, IssueCodes.PATTERN_BODY_EMPTY);
			}
		}
	}

	private String getName(PatternBody body) {
		if (body.getName() != null && !body.getName().isEmpty()) {
			return "'" +body.getName()+ "'";
		}
		return null;
	}

	private String getFormattedPattern(Pattern pattern) {
		StringBuilder builder = new StringBuilder();
		builder.append(pattern.getName());
		builder.append("(");
		for (Iterator<Variable> iter = pattern.getParameters().iterator(); iter.hasNext();) {
			builder.append(iter.next().getName());
			if (iter.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append(")");
		return builder.toString();
	}
	
	protected String getFormattedArgumentsList(
			PatternCompositionConstraint constraint) {
		StringBuilder builder = new StringBuilder();
		for (Iterator<VariableReference> iter = constraint.getParameters().iterator(); iter.hasNext();) {
			builder.append(iter.next().getVar());
			if (iter.hasNext()) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}

}
