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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassifierConstraint;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguagePackage;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.validation.Check;

/**
 * Validators for EMFPattern Language.
 * <p>
 * Validators implemented:
 * </p>
 * <ul>
 * <li>Duplicate import of EPackage</li>
 * </ul>
 * 
 * @author Mark Czotter
 * 
 */
public class EMFPatternLanguageJavaValidator extends
		AbstractEMFPatternLanguageJavaValidator {

	public static final String DUPLICATE_IMPORT = "Duplicate import of ";
	public static final String UNUSED_VARIABLE_MESSAGE = "Variable %s is used only once";

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
	public void checkUnusedVariables(VariableReference variableReference) {
		EObject container = variableReference.eContainer();
		
		while (!Constraint.class.isAssignableFrom(container.getClass())) {
			container = container.eContainer();
		}
		
		Constraint containingConstraint = (Constraint)container;
		
		while (!PatternBody.class.isAssignableFrom(container.getClass())) {
			container = container.eContainer();
		}
		
		PatternBody body = (PatternBody)container;
		
		Pattern pattern = (Pattern)body.eContainer();
		
		for (Variable var : pattern.getParameters()) {
			if (var.getName().equals(variableReference.getVar())) {
				return;
			}
		}
		
		for (Constraint constraint : body.getConstraints()) {
			if (constraint != containingConstraint) {
				if (EClassifierConstraint.class.isAssignableFrom(constraint.getClass())) {
					if (((EClassifierConstraint)constraint).getVar().getVar().equals(variableReference.getVar())) {
						return;
					}
				}
				else if (PathExpressionConstraint.class.isAssignableFrom(constraint.getClass()))
				{
					if (((PathExpressionConstraint)constraint).getHead().getSrc().getVar().equals(variableReference.getVar())) {
						return;
					}
				}
			}
		}
		
		warning(String.format(UNUSED_VARIABLE_MESSAGE, variableReference.getVar()), PatternLanguagePackage.Literals.VARIABLE_REFERENCE__VAR, EMFIssueCodes.UNUSED_VARIABLE);
	}

	@Override
	protected List<EPackage> getEPackages() {
		// PatternLanguagePackage must be added to the defaults, otherwise the core language validators not used in the validation process
		List<EPackage> result = super.getEPackages();
		result.add(org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage.eINSTANCE);
		return result;
	}

}
