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
import org.eclipse.jface.text.IRegion;
import org.eclipse.viatra2.emf.incquery.tooling.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport;
import org.eclipse.xtext.xbase.ui.hover.XbaseHoverProvider;

import com.google.inject.Inject;

@SuppressWarnings("restriction")
public class EMFPatternLanguageHoverProvider extends XbaseHoverProvider {

	@Inject
	IEiqGenmodelProvider genmodelProvider;
	
	@Override
	protected String getHoverInfoAsHtml(EObject call, EObject objectToView, IRegion hoverRegion) {
		if (objectToView instanceof PackageImport) {
			PackageImport packageImport = (PackageImport) objectToView;
			GenPackage genPackage = genmodelProvider.findGenPackage(packageImport, packageImport.getEPackage());
			if (genPackage != null) {
				return String.format(
					"<b>Genmodel found</b>: %s<br/><b>Package uri</b>: %s",
					genPackage.eResource().getURI().toString(), genPackage
							.getEcorePackage().eResource().getURI().toString());
			} 
		}
		return super.getHoverInfoAsHtml(call, objectToView, hoverRegion);
	}

	@Override
	protected boolean hasHover(EObject o) {
		return (o instanceof PackageImport) || super.hasHover(o);
	}

}
