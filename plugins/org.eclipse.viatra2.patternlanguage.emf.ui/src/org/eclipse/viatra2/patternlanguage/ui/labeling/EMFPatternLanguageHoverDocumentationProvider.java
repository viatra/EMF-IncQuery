/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.patternlanguage.ui.labeling;

import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.emf.incquery.tooling.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.viatra2.patternlanguage.core.annotations.PatternAnnotationProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.AnnotationParameter;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport;
import org.eclipse.xtext.xbase.ui.hover.XbaseHoverDocumentationProvider;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class EMFPatternLanguageHoverDocumentationProvider extends
		XbaseHoverDocumentationProvider {

	@Inject
	private IEiqGenmodelProvider genmodelProvider;
	
	@Override
	public String computeDocumentation(EObject object) {
		if (object instanceof PackageImport) {
			PackageImport packageImport = (PackageImport) object;
			GenPackage genPackage = genmodelProvider.findGenPackage(
					packageImport, packageImport.getEPackage());
			if (genPackage != null) {
				return String.format(
						"<b>Genmodel found</b>: %s<br/><b>Package uri</b>: %s",
						genPackage.eResource().getURI().toString(), genPackage
								.getEcorePackage().eResource().getURI()
								.toString());
			}
		}
		return super.computeDocumentation(object);
	}

}
