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

import org.eclipse.viatra2.patternlanguage.core.annotations.IPatternAnnotationValidator;
import org.eclipse.viatra2.patternlanguage.core.annotations.PatternAnnotationProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.AnnotationParameter;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.BoolValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CompareConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.DoubleValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.IntValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ListValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCompositionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.StringValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ValueReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableValue;
import org.eclipse.xtext.validation.Check;

import com.google.inject.Inject;

/**
 * Validators for Core Pattern Language.
 * <p>
 * Validators implemented:
 * </p>
 * <ul>
 * <li>Duplicate parameter in pattern declaration</li>
 * <li>Duplicate pattern definition (name duplication only, better calculation
 * is needed)</li>
 * <li>Pattern call parameter checking (only the number of the parameters, types
 * not supported yet)</li>
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
	public static final String UNKNOWN_ANNOTATION_ATTRIBUTE = "Undefined annotation attribute ";
	public static final String MISSING_ANNOTATION_ATTRIBUTE = "Required attribute missing ";
	public static final String ANNOTATION_PARAMETER_TYPE_ERROR = "Invalid parameter type %s. Expected %s";

	@Inject
	PatternAnnotationProvider annotationProvider;

	@Check
	public void checkPatternParameters(Pattern pattern) {
		for (int i = 0; i < pattern.getParameters().size(); ++i) {
			String leftParameterName = pattern.getParameters().get(i).getName();
			for (int j = i + 1; j < pattern.getParameters().size(); ++j) {
				if (equal(leftParameterName, pattern.getParameters().get(j)
						.getName())) {
					error(DUPLICATE_VARIABLE_MESSAGE + leftParameterName,
							PATTERN__PARAMETERS, i,
							IssueCodes.DUPLICATE_PATTERN_PARAMETER_NAME);
					error(DUPLICATE_VARIABLE_MESSAGE + leftParameterName,
							PATTERN__PARAMETERS, j,
							IssueCodes.DUPLICATE_PATTERN_PARAMETER_NAME);
				}
			}
		}
	}

	@Check
	public void checkPatternCompositionConstraintParameters(
			PatternCompositionConstraint constraint) {
		if (constraint.getPatternRef() != null
				&& constraint.getParameters() != null) {
			final int definitionParameterSize = constraint.getPatternRef()
					.getParameters().size();
			final int callParameterSize = constraint.getParameters().size();
			if (definitionParameterSize != callParameterSize) {
				error("The pattern "
						+ getFormattedPattern(constraint.getPatternRef())
						+ " is not applicable for the arguments("
						+ getFormattedArgumentsList(constraint) + ")",
						PATTERN_COMPOSITION_CONSTRAINT__PATTERN_REF,
						IssueCodes.WRONG_NUMBER_PATTERNCALL_PARAMETER);
			}
		}
	}

	@Check
	public void checkPatterns(PatternModel model) {
		if (model.getPatterns() != null && !model.getPatterns().isEmpty()) {
			// TODO: more precise calculation is needed for duplicate patterns
			// (number and type of pattern parameters)
			for (int i = 0; i < model.getPatterns().size(); ++i) {
				Pattern leftPattern = model.getPatterns().get(i);
				String leftPatternName = leftPattern.getName();
				for (int j = i + 1; j < model.getPatterns().size(); ++j) {
					Pattern rightPattern = model.getPatterns().get(j);
					String rightPatternName = rightPattern.getName();
					if (equal(leftPatternName, rightPatternName)) {
						error(DUPLICATE_PATTERN_DEFINITION_MESSAGE
								+ leftPatternName, leftPattern, PATTERN__NAME,
								IssueCodes.DUPLICATE_PATTERN_DEFINITION);
						error(DUPLICATE_PATTERN_DEFINITION_MESSAGE
								+ rightPatternName, rightPattern,
								PATTERN__NAME,
								IssueCodes.DUPLICATE_PATTERN_DEFINITION);
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
				Pattern pattern = ((Pattern) body.eContainer());
				String patternName = pattern.getName();
				error("A patternbody of " + patternName + " is empty",
						body,
						PatternLanguagePackage.Literals.PATTERN_BODY__CONSTRAINTS,
						IssueCodes.PATTERN_BODY_EMPTY);
			} else {
				error("The patternbody " + bodyName + " cannot be empty", body,
						PatternLanguagePackage.Literals.PATTERN_BODY__NAME,
						IssueCodes.PATTERN_BODY_EMPTY);
			}
		}
	}

	@Check
	public void checkAnnotation(Annotation annotation) {
		if (annotationProvider.hasValidator(annotation.getName())) {
			IPatternAnnotationValidator validator = annotationProvider
					.getValidator(annotation.getName());
			// Check for unknown annotation attributes
			for (AnnotationParameter unknownParameter : validator
					.getUnknownAttributes(annotation)) {
				error(UNKNOWN_ANNOTATION_ATTRIBUTE + unknownParameter.getName(),
						unknownParameter,
						PatternLanguagePackage.Literals.ANNOTATION_PARAMETER__NAME,
						annotation.getParameters().indexOf(unknownParameter),
						IssueCodes.UNKNOWN_ANNOTATION_PARAMETER);
			}
			// Check for missing mandatory attributes
			for (String missingAttribute : validator
					.getMissingMandatoryAttributes(annotation)) {
				error(MISSING_ANNOTATION_ATTRIBUTE + missingAttribute,
						annotation,
						PatternLanguagePackage.Literals.ANNOTATION__PARAMETERS,
						IssueCodes.MISSING_REQUIRED_ANNOTATION_PARAMETER);
			}
			// Check for annotation parameter types
			for (AnnotationParameter parameter : annotation.getParameters()) {
				Class<? extends ValueReference> expectedParameterType = validator
						.getExpectedParameterType(parameter);
				if (expectedParameterType != null 
						&& parameter.getValue() != null
						&& !expectedParameterType.isAssignableFrom(parameter
								.getValue().getClass())) {
					error(String.format(ANNOTATION_PARAMETER_TYPE_ERROR,
							getTypeName(parameter.getValue().getClass()),
							getTypeName(expectedParameterType)),
							parameter,
							PatternLanguagePackage.Literals.ANNOTATION_PARAMETER__NAME,
							annotation.getParameters().indexOf(parameter),
							IssueCodes.MISTYPED_ANNOTATION_PARAMETER);
				}
			}
		} else {
			warning("Unknown annotation " + annotation.getName(),
					PatternLanguagePackage.Literals.ANNOTATION__NAME,
					IssueCodes.UNKNOWN_ANNOTATION);
		}
	}
	
	@Check
	public void checkCompareConstraints(CompareConstraint constraint) { 
		//If none of the operands are variables, issues a warning
		boolean op1Variable = PatternLanguagePackage.Literals.VARIABLE_VALUE.isSuperTypeOf(constraint.getLeftOperand().eClass());
		boolean op2Variable = PatternLanguagePackage.Literals.VARIABLE_VALUE.isSuperTypeOf(constraint.getRightOperand().eClass());
		if (!op1Variable && !op2Variable) {
			warning("Both operands are constants - constraint is always true or always false.",
					PatternLanguagePackage.Literals.COMPARE_CONSTRAINT__LEFT_OPERAND,
					IssueCodes.CONSTANT_COMPARE_CONSTRAINT);
			warning("Both operands are constants - constraint is always true or always false.",
					PatternLanguagePackage.Literals.COMPARE_CONSTRAINT__RIGHT_OPERAND,
					IssueCodes.CONSTANT_COMPARE_CONSTRAINT);
		}
		//If both operands are the same, issues a warning
		if (op1Variable && op2Variable) {
			VariableValue op1 = (VariableValue) constraint.getLeftOperand();
			VariableValue op2 = (VariableValue) constraint.getRightOperand();
			if (op1.getValue().getVar().equals(op2.getValue().getVar())) {
				warning("Comparing a variable with itself.",
						PatternLanguagePackage.Literals.COMPARE_CONSTRAINT__LEFT_OPERAND,
						IssueCodes.SELF_COMPARE_CONSTRAINT);
				warning("Comparing a variable with itself.",
						PatternLanguagePackage.Literals.COMPARE_CONSTRAINT__RIGHT_OPERAND,
						IssueCodes.SELF_COMPARE_CONSTRAINT);
			}
		}
	}

	private String getName(PatternBody body) {
		if (body.getName() != null && !body.getName().isEmpty()) {
			return "'" + body.getName() + "'";
		}
		return null;
	}

	private String getTypeName(Class<? extends ValueReference> typeClass) {
		if (IntValue.class.isAssignableFrom(typeClass)) {
			return "Integer";
		} else if (DoubleValue.class.isAssignableFrom(typeClass)) {
			return "Double";
		} else if (BoolValue.class.isAssignableFrom(typeClass)) {
			return "Boolean";
		} else if (StringValue.class.isAssignableFrom(typeClass)) {
			return "String";
		} else if (ListValue.class.isAssignableFrom(typeClass)) {
			return "List";
		} else if (VariableValue.class.isAssignableFrom(typeClass)) {
			return "Variable";
		}
		return "UNDEFINED";
	}

	private String getConstantAsString(ValueReference ref) {
		if (ref instanceof IntValue) {
			return Integer.toString(((IntValue) ref).getValue());
		} else if (ref instanceof DoubleValue) {
			return Double.toString(((DoubleValue) ref).getValue());
		} else if (ref instanceof BoolValue) {
			return Boolean.toString(((BoolValue) ref).isValue());
		} else if (ref instanceof StringValue) {
			return "\"" + ((StringValue) ref).getValue() + "\"";
		} else if (ref instanceof ListValue) {
			StringBuilder sb = new StringBuilder();
			sb.append("{ ");
			for (Iterator<ValueReference> iter = ((ListValue) ref).getValues()
					.iterator(); iter.hasNext();) {
				sb.append(getConstantAsString(iter.next()));
				if (iter.hasNext()) {
					sb.append(", ");
				}
			}
			sb.append("}");
			return sb.toString();
		} else if (ref instanceof VariableValue) {
			return ((VariableValue) ref).getValue().getVar();
		}
		return "UNDEFINED";
	}

	private String getFormattedPattern(Pattern pattern) {
		StringBuilder builder = new StringBuilder();
		builder.append(pattern.getName());
		builder.append("(");
		for (Iterator<Variable> iter = pattern.getParameters().iterator(); iter
				.hasNext();) {
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
		for (Iterator<ValueReference> iter = constraint.getParameters()
				.iterator(); iter.hasNext();) {
			ValueReference parameter = iter.next();
			builder.append(getConstantAsString(parameter));
			if (iter.hasNext()) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}

}
