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

import org.eclipse.emf.ecore.EPackage;
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
	
	@Override
	protected List<EPackage> getEPackages() {
		// PatternLanguagePackage must be added to the defaults, otherwise the core language validators not used in the validation process
		List<EPackage> result = super.getEPackages();
		result.add(org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage.eINSTANCE);
		return result;
	}

}
