/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.patternlanguage.core.validation;

import static org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage.Literals.PATTERN__NAME;
import static org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage.Literals.PATTERN__PARAMETERS;
import static org.eclipse.xtext.util.Strings.equal;

import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.core.annotations.IPatternAnnotationValidator;
import org.eclipse.viatra2.patternlanguage.core.annotations.PatternAnnotationProvider;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.AnnotationParameter;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.BoolValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CheckConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CompareConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.DoubleValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.IntValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ListValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCall;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCompositionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.StringValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ValueReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableValue;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.xbase.typing.ITypeProvider;

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
	public static final String TRANSITIVE_CLOSURE_ARITY_IN_PATTERNCALL = 
			"The pattern %s is not of binary arity (it has %d parameters), therefore transitive closure is not supported.";
	public static final String TRANSITIVE_CLOSURE_ONLY_IN_POSITIVE_COMPOSITION = 
			"Transitive closure of %s is currently only allowed in simple positive pattern calls (no negation or aggregation).";
	public static final String UNUSED_PRIVATE_PATTERN_MESSAGE = "The pattern '%s' is never used locally.";
	
	@Inject
	private PatternAnnotationProvider annotationProvider;
	
	@Inject
	private ITypeProvider typeProvider;
	
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
	public void checkPrivatePatternUsage(Pattern pattern) {
		if (CorePatternLanguageHelper.isPrivate(pattern) && !isLocallyUsed(pattern, pattern.eContainer())) {
			String message = String.format(UNUSED_PRIVATE_PATTERN_MESSAGE, pattern.getName());
			warning(message, PatternLanguagePackage.Literals.PATTERN__NAME, IssueCodes.UNUSED_PRIVATE_PATTERN);
		}
	}

	@Check
	public void checkPatternCallParameters(
			PatternCall call) {
		if (call.getPatternRef() != null &&  call.getPatternRef().getName() != null
				&& call.getParameters() != null) {
			final int definitionParameterSize = call.getPatternRef()
					.getParameters().size();
			final int callParameterSize = call.getParameters().size();
			if (definitionParameterSize != callParameterSize) {
				error("The pattern "
						+ getFormattedPattern(call.getPatternRef())
						+ " is not applicable for the arguments("
						+ getFormattedArgumentsList(call) + ")",
						PatternLanguagePackage.Literals.PATTERN_CALL__PATTERN_REF,
						IssueCodes.WRONG_NUMBER_PATTERNCALL_PARAMETER);
			}
		}
	}
	
	@Check
	public void checkApplicabilityOfTransitiveClosureInPatternCall(PatternCall call) {
		final Pattern patternRef = call.getPatternRef();
		final EObject eContainer = call.eContainer();
		if (patternRef != null && call.isTransitive()) {
			if (patternRef.getParameters() != null) {
				final int arity = patternRef.getParameters().size();
				if (2 != arity) {
					error(String.format(TRANSITIVE_CLOSURE_ARITY_IN_PATTERNCALL, 
							getFormattedPattern(patternRef), arity),
						PatternLanguagePackage.Literals.PATTERN_CALL__TRANSITIVE,
						IssueCodes.TRANSITIVE_PATTERNCALL_ARITY);						
				}
			} 
			if (eContainer != null) {
				if(! (eContainer instanceof PatternCompositionConstraint) || 
					((PatternCompositionConstraint)eContainer).isNegative()) 
				{
					error(String.format(TRANSITIVE_CLOSURE_ONLY_IN_POSITIVE_COMPOSITION, 
							getFormattedPattern(patternRef)),
						PatternLanguagePackage.Literals.PATTERN_CALL__TRANSITIVE,
						IssueCodes.TRANSITIVE_PATTERNCALL_NOT_APPLICABLE);						
				}	
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
		ValueReference op1 = constraint.getLeftOperand();
		ValueReference op2 = constraint.getRightOperand();
		if (op1 == null || op2 == null) {
			return;
		}
			
		boolean op1Constant = PatternLanguagePackage.Literals.LITERAL_VALUE_REFERENCE.isSuperTypeOf(op1.eClass());
		boolean op2Constant = PatternLanguagePackage.Literals.LITERAL_VALUE_REFERENCE.isSuperTypeOf(op2.eClass());
		boolean op1Variable = PatternLanguagePackage.Literals.VARIABLE_VALUE.isSuperTypeOf(op1.eClass());
		boolean op2Variable = PatternLanguagePackage.Literals.VARIABLE_VALUE.isSuperTypeOf(op2.eClass());
		
		//If both operands are constant literals, issue a warning
		if (op1Constant && op2Constant) {
			warning("Both operands are constants - constraint is always true or always false.",
					PatternLanguagePackage.Literals.COMPARE_CONSTRAINT__LEFT_OPERAND,
					IssueCodes.CONSTANT_COMPARE_CONSTRAINT);
			warning("Both operands are constants - constraint is always true or always false.",
					PatternLanguagePackage.Literals.COMPARE_CONSTRAINT__RIGHT_OPERAND,
					IssueCodes.CONSTANT_COMPARE_CONSTRAINT);
		}
		//If both operands are the same, issues a warning
		if (op1Variable && op2Variable) {
			VariableValue op1v = (VariableValue) op1;
			VariableValue op2v = (VariableValue) op2;
			if (op1v.getValue().getVar().equals(op2v.getValue().getVar())) {
				warning("Comparing a variable with itself.",
						PatternLanguagePackage.Literals.COMPARE_CONSTRAINT__LEFT_OPERAND,
						IssueCodes.SELF_COMPARE_CONSTRAINT);
				warning("Comparing a variable with itself.",
						PatternLanguagePackage.Literals.COMPARE_CONSTRAINT__RIGHT_OPERAND,
						IssueCodes.SELF_COMPARE_CONSTRAINT);
			}
		}
	}
	
	@Check
	public void checkCheckConstraintReturnValue(CheckConstraint checkConstraint) { 
		JvmTypeReference jvmTypeReference = typeProvider.getType(checkConstraint.getExpression());
		if (!"boolean".equals(jvmTypeReference.getSimpleName()) && !"Boolean".equals(jvmTypeReference.getSimpleName())) {
			error("The check constraint expression must return a boolean value!", checkConstraint, 
					PatternLanguagePackage.Literals.CHECK_CONSTRAINT__EXPRESSION, IssueCodes.WRONG_CHECK_CONSTRAINT_RETURN);
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
			PatternCall call) {
		StringBuilder builder = new StringBuilder();
		for (Iterator<ValueReference> iter = call.getParameters()
				.iterator(); iter.hasNext();) {
			ValueReference parameter = iter.next();
			builder.append(getConstantAsString(parameter));
			if (iter.hasNext()) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}

	@Check
	public void checkPackageDeclaration(PatternModel model) {
		String packageName = model.getPackageName();
		if (packageName!= null && !packageName.equalsIgnoreCase(packageName)) {
			error("Only lowercase package names supported",
					PatternLanguagePackage.Literals.PATTERN_MODEL__PACKAGE_NAME,
					IssueCodes.LOWERCASE_PATTERN_NAME);
		}
	}
}
