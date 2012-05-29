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
package org.eclipse.viatra2.patternlanguage.validation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageScopeHelper;
import org.eclipse.viatra2.patternlanguage.ResolutionException;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CheckConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CompareConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CompareFeature;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionHead;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCompositionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassifierConstraint;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguagePackage;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EnumValue;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.validation.Check;

/**
 * Validators for EMFPattern Language.
 * <p>
 * Validators implemented:
 * </p>
 * <ul>
 * <li>Duplicate import of EPackage</li>
 * <li>Enum type validators</li>
 * </ul>
 * 
 * @author Mark Czotter, Zoltan Ujhelyi
 * 
 */
public class EMFPatternLanguageJavaValidator extends
		AbstractEMFPatternLanguageJavaValidator {

	public static final String DUPLICATE_IMPORT = "Duplicate import of ";

	@Check
	public void checkPatternModelPackageImports(PatternModel patternModel) {
		for (int i = 0; i < patternModel.getImportPackages().size(); ++i) {
			EPackage leftPackage = patternModel.getImportPackages().get(i)
					.getEPackage();
			for (int j = i + 1; j < patternModel.getImportPackages().size(); ++j) {
				EPackage rightPackage = patternModel.getImportPackages().get(j)
						.getEPackage();
				if (leftPackage.equals(rightPackage)) {
					warning(DUPLICATE_IMPORT + leftPackage.getNsURI(),
							EMFPatternLanguagePackage.Literals.PATTERN_MODEL__IMPORT_PACKAGES,
							i, EMFIssueCodes.DUPLICATE_IMPORT);
					warning(DUPLICATE_IMPORT + rightPackage.getNsURI(),
							EMFPatternLanguagePackage.Literals.PATTERN_MODEL__IMPORT_PACKAGES,
							j, EMFIssueCodes.DUPLICATE_IMPORT);
				}
			}
		}
	}

	private enum VariableReferenceClass {
		PositiveExistential, NegativeExistential, ReadOnly
	}

	private class ClassifiedVariableReferences {
		private Variable referredVariable;
		private boolean isLocalVariable;

		private Map<VariableReferenceClass, Integer> classifiedReferenceCount;

		public Variable getReferredVariable() {
			return referredVariable;
		}

		public int getReferenceCount(VariableReferenceClass forClass) {
			Integer count = classifiedReferenceCount.get(forClass);
			return count == null ? 0 : count;
		}

		public int getReferenceCountSum() {
			int sum = 0;

			for (Integer val : classifiedReferenceCount.values()) {
				sum += val;
			}

			return sum;
		}

		public boolean isVariableLocal() {
			return isLocalVariable;
		}

		public ClassifiedVariableReferences(Variable referredVariable,
				boolean isLocal) {
			this.referredVariable = referredVariable;
			this.isLocalVariable = isLocal;

			classifiedReferenceCount = new HashMap<VariableReferenceClass, Integer>();
		}

		public void incrementCounter(VariableReferenceClass forClass) {
			Integer count = classifiedReferenceCount.get(forClass);
			classifiedReferenceCount.put(forClass, count == null ? 1
					: count + 1);
		}
	}

	private VariableReferenceClass classifyVariableReference(
			VariableReference varRef) {
		EObject container = varRef.eContainer();

		while (container != null && !(container instanceof Constraint)) {
			container = container.eContainer();
		}

		if (container instanceof EClassifierConstraint) {
			return VariableReferenceClass.PositiveExistential;
		} else if (container instanceof CheckConstraint) {
			return VariableReferenceClass.ReadOnly;
		} else if (container instanceof CompareConstraint) {
			CompareConstraint constraint = (CompareConstraint) container;

			if (constraint.getFeature() == CompareFeature.EQUALITY) {
				// TODO: check this optimistic approximation again later.
				return VariableReferenceClass.PositiveExistential;
			} else if (constraint.getFeature() == CompareFeature.INEQUALITY) {
				// TODO: check this again later. this might not be true.
				return VariableReferenceClass.ReadOnly;
			} else {
				throw new UnsupportedOperationException(
						"Unrecognised compare feature.");
			}
		} else if (container instanceof PathExpressionConstraint) {
			if (((PathExpressionConstraint) container).isNegative()) {
				return VariableReferenceClass.NegativeExistential;
			} else {
				return VariableReferenceClass.PositiveExistential;
			}
		} else if (container instanceof PatternCompositionConstraint) {
			if (((PatternCompositionConstraint) container).isNegative()) {
				return VariableReferenceClass.NegativeExistential;
			} else {
				return VariableReferenceClass.PositiveExistential;
			}
		} else {
			throw new UnsupportedOperationException("Unrecognised constraint.");
		}
	}

	private Collection<ClassifiedVariableReferences> processVariableReferences(
			PatternBody inBody) {
		Map<Variable, ClassifiedVariableReferences> classifiedVariableReferencesCollection = new HashMap<Variable, ClassifiedVariableReferences>();

		// NOTE: This is also a work-around to fill in connections between
		// variables and their references.
		inBody.getVariables();

		Pattern pattern = (Pattern) inBody.eContainer();

		for (Variable var : pattern.getParameters()) {
			final ClassifiedVariableReferences varRefs = new ClassifiedVariableReferences(var, false);
			classifiedVariableReferencesCollection.put(var,varRefs);
			if (var.getType() != null) { // type assertion on parameter
				varRefs.incrementCounter(VariableReferenceClass.PositiveExistential);
			}
		}

		TreeIterator<EObject> iter = inBody.eAllContents();
		while (iter.hasNext()) {
			EObject obj = iter.next();
			if (obj instanceof VariableReference) {
				VariableReference varRef = (VariableReference) obj;
				ClassifiedVariableReferences classifiedVariableReferences = classifiedVariableReferencesCollection
						.get(varRef.getVariable());
				if (classifiedVariableReferences == null) {
					classifiedVariableReferences = new ClassifiedVariableReferences(
							varRef.getVariable(), true); // All symbolic
															// variables are
															// already added.
				}
				classifiedVariableReferences
						.incrementCounter(classifyVariableReference(varRef));
				classifiedVariableReferencesCollection.put(
						classifiedVariableReferences.getReferredVariable(),
						classifiedVariableReferences);
			} else if (obj instanceof CheckConstraint) {
				Set<Variable> vars = CorePatternLanguageHelper
						.getReferencedPatternVariablesOfXExpression(((CheckConstraint) obj)
								.getExpression());
				for (Variable var : vars) {
					ClassifiedVariableReferences classifiedVariableReferences = classifiedVariableReferencesCollection
							.get(var);
					if (classifiedVariableReferences == null) {
						classifiedVariableReferences = new ClassifiedVariableReferences(
								var, true); // All symbolic variables are
											// already added.
					}
					classifiedVariableReferences
							.incrementCounter(VariableReferenceClass.ReadOnly);
					classifiedVariableReferencesCollection.put(
							classifiedVariableReferences.getReferredVariable(),
							classifiedVariableReferences);

				}
			}
		}
		return classifiedVariableReferencesCollection.values();
	}

	private String getPatternBodyName(PatternBody patternBody) {
		return (patternBody.getName() != null) ? patternBody.getName() : String
				.format("#%d", ((Pattern) patternBody.eContainer()).getBodies()
						.indexOf(patternBody) + 1);
	}

	@Check
	public void checkUnusedVariables(PatternBody patternBody) {
		Collection<ClassifiedVariableReferences> classifiedVariableReferencesCollection = processVariableReferences(patternBody);

		for (ClassifiedVariableReferences classifiedVariableReferences : classifiedVariableReferencesCollection) {
			if (classifiedVariableReferences.isVariableLocal()) {
				if (classifiedVariableReferences
						.getReferenceCount(VariableReferenceClass.PositiveExistential) == 1
						&& classifiedVariableReferences.getReferenceCountSum() == 1) {
					warning(String.format(
							"Local variable '%s' is referenced only once.",
							classifiedVariableReferences.getReferredVariable()
									.getName()), classifiedVariableReferences
							.getReferredVariable().getReferences().get(0),
							null, EMFIssueCodes.LOCAL_VARIABLE_REFERENCED_ONCE);
				} else if (classifiedVariableReferences
						.getReferenceCount(VariableReferenceClass.PositiveExistential) == 0) {
					if (classifiedVariableReferences
							.getReferenceCount(VariableReferenceClass.NegativeExistential) == 0
							&& classifiedVariableReferences
									.getReferenceCountSum() != 0) {
						error(String.format(
								"Local variable '%s' has no quantifying reference.",
								classifiedVariableReferences
										.getReferredVariable().getName()),
								classifiedVariableReferences
										.getReferredVariable().getReferences()
										.get(0),
								null,
								EMFIssueCodes.LOCAL_VARIABLE_NO_QUANTIFYING_REFERENCE);
					} else if (classifiedVariableReferences
							.getReferenceCountSum() > 1) {
						error(String.format(
								"Local variable '%s' has no positive reference.",
								classifiedVariableReferences
										.getReferredVariable().getName()),
								classifiedVariableReferences
										.getReferredVariable().getReferences()
										.get(0),
								null,
								EMFIssueCodes.LOCAL_VARIABLE_NO_POSITIVE_REFERENCE);
					}
				}
			} else { // Symbolic variable:
				if (classifiedVariableReferences.getReferenceCountSum() == 0) {
					error(String
							.format("Symbolic variable '%s' is never referenced in body '%s'.",
									classifiedVariableReferences
											.getReferredVariable().getName(),
									getPatternBodyName(patternBody)),
							classifiedVariableReferences.getReferredVariable(),
							null,
							EMFIssueCodes.SYMBOLIC_VARIABLE_NEVER_REFERENCED);
				} else if (classifiedVariableReferences
						.getReferenceCount(VariableReferenceClass.PositiveExistential) == 0) {
					error(String
							.format("Symbolic variable '%s' has no positive reference in body '%s'.",
									classifiedVariableReferences
											.getReferredVariable().getName(),
									getPatternBodyName(patternBody)),
							classifiedVariableReferences.getReferredVariable(),
							null,
							EMFIssueCodes.SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE);
				}
			}
		}
	}

	@Override
	protected List<EPackage> getEPackages() {
		// PatternLanguagePackage must be added to the defaults, otherwise the
		// core language validators not used in the validation process
		List<EPackage> result = super.getEPackages();
		result.add(org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage.eINSTANCE);
		return result;
	}

	@Check
	public void checkEnumValues(EnumValue value) {
		if (value.eContainer() instanceof PathExpressionHead) {
			// If container is PathExpression check for enum type assignability
			EEnum enumType = value.getEnumeration();
			if (enumType == null && value.getLiteral() != null) {
				enumType = value.getLiteral().getEEnum();
			}
			PathExpressionHead expression = (PathExpressionHead) value
					.eContainer();
			try {
				EEnum expectedType = EMFPatternLanguageScopeHelper
						.calculateEnumerationType(expression);
				if (enumType != null && !expectedType.equals(enumType)) {
					error(String
							.format("Inconsistent enumeration types: found %s but expected %s",
									enumType.getName(), expectedType.getName()),
							value,
							EMFPatternLanguagePackage.Literals.ENUM_VALUE__ENUMERATION,
							EMFIssueCodes.INVALID_ENUM_LITERAL);
				}
			} catch (ResolutionException e) {
				// EClassifier type =
				// EMFPatternLanguageScopeHelper.calculateExpressionType(expression);
				error(String.format("Invalid enumeration constant %s",
						enumType.getName()),
						value,
						EMFPatternLanguagePackage.Literals.ENUM_VALUE__ENUMERATION,
						EMFIssueCodes.INVALID_ENUM_LITERAL);
			}
		} // else {
			// If container is not a PathExpression, the entire enum type has to
			// be specified
			// However, the it is checked during reference resolution
		// }
	}
}
