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

import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionHead;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableValue;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageScopeHelper;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassifierConstraint;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguagePackage;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EnumValue;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.ResolutionException;
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
	public static final String UNUSED_LOCAL_VARIABLE_MESSAGE = "Local variable '%s' is used only once.";
	public static final String UNUSED_PARAM_VARIABLE_MESSAGE = "Pattern parameter '%s' is never used in body '%s'.";

	@Check
	public void checkPatternModelPackageImports(PatternModel patternModel) {
		for (int i = 0; i < patternModel.getImportPackages().size(); ++i) {
			EPackage leftPackage = patternModel.getImportPackages().get(i).getEPackage();
			for (int j = i + 1; j < patternModel.getImportPackages().size(); ++j) {
				EPackage rightPackage = patternModel.getImportPackages().get(j).getEPackage();
				if (leftPackage.equals(rightPackage)) {
					warning(DUPLICATE_IMPORT + leftPackage.getNsURI(), EMFPatternLanguagePackage.Literals.PATTERN_MODEL__IMPORT_PACKAGES, i, EMFIssueCodes.DUPLICATE_IMPORT);
					warning(DUPLICATE_IMPORT + rightPackage.getNsURI(), EMFPatternLanguagePackage.Literals.PATTERN_MODEL__IMPORT_PACKAGES, j, EMFIssueCodes.DUPLICATE_IMPORT);
				}
			}
		}
	}
	
	@Check
	public void checkUnusedVariables(PatternBody patternBody) {
		// Check number of references for local variables.
		// NOTE: This step has to come first, because the getVariables() function finds/initializes the references.
		for (Variable var : patternBody.getVariables()) {
			if (var.getReferences().size() == 1) {
				warning(String.format(UNUSED_LOCAL_VARIABLE_MESSAGE, var.getName()), var.getReferences().get(0), null, EMFIssueCodes.UNUSED_VARIABLE);
			}
		}

		// Create and initialize reference counter for pattern parameter variables.
		java.util.Map<Variable, Integer> referenceCount = new java.util.HashMap<Variable, Integer>();
		for (Variable var : ((Pattern)patternBody.eContainer()).getParameters()) {
			referenceCount.put(var, 0);
		}
		
		// Iterate through all contents of the pattern body and increment the number of references referring to any pattern parameter variable.
		java.util.Iterator<EObject> it = patternBody.eAllContents();
		while(it.hasNext()) {
			EObject obj = it.next();
			if (obj instanceof VariableReference) {
				Variable referencedVar = ((VariableReference)obj).getVariable();
				Integer count = referenceCount.get(referencedVar);
				if (count != null) {
					referenceCount.put(referencedVar, count + 1);
				}
			}
		}
		
		// Check the number of local references for pattern parameter variables.
		for (Entry<Variable, Integer> entry : referenceCount.entrySet()) {
			if (entry.getValue() == 0) {
				String patternBodyName = (patternBody.getName() != null) ? patternBody.getName() : String.format("#%d", ((Pattern)patternBody.eContainer()).getBodies().indexOf(patternBody));
				warning(String.format(UNUSED_PARAM_VARIABLE_MESSAGE, entry.getKey().getName(), patternBodyName), entry.getKey(), null, EMFIssueCodes.UNUSED_VARIABLE);
			}
		}
	}
	
	@Override
	protected List<EPackage> getEPackages() {
		// PatternLanguagePackage must be added to the defaults, otherwise the core language validators not used in the validation process
		List<EPackage> result = super.getEPackages();
		result.add(org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage.eINSTANCE);
		return result;
	}

	@Check
	public void checkEnumValues(EnumValue value) {
		if (value.eContainer() instanceof PathExpressionHead) {
			//If container is PathExpression check for enum type assignability
			EEnum enumType = value.getEnumeration();
			PathExpressionHead expression = (PathExpressionHead) value
					.eContainer();
			try {
				EEnum expectedType = EMFPatternLanguageScopeHelper
						.calculateEnumerationType(expression);
				if (!expectedType.equals(enumType)) {
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
		} //else {
			//If container is not a PathExpression, the entire enum type has to be specified
			//However, the it is checked during reference resolution
		//}
	}
}
