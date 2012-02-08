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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguagePackage;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport;
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
		final Set<EPackage> importedPackages = new HashSet<EPackage>();
		for (PackageImport pi : patternModel.getImportPackages()) {
			if (importedPackages.contains(pi.getEPackage())) {
				warning(DUPLICATE_IMPORT + pi.getEPackage().getName(), pi, EMFPatternLanguagePackage.Literals.PACKAGE_IMPORT__EPACKAGE, 
						EMFIssueCodes.DUPLICATE_IMPORT, "");
			} else {
				importedPackages.add(pi.getEPackage());
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
