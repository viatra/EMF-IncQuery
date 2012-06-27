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

package org.eclipse.viatra2.emf.incquery.tooling.generator.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;
import org.eclipse.viatra2.patternlanguage.scoping.IMetamodelProvider;

import com.google.inject.Inject;

/**
 * Helper class for loading XMI resources using information available in 
 *
 */
public class XMIResourceURIHandler extends URIHandlerImpl {

	@Inject
	IMetamodelProvider metamodelProvider;
	private ResourceSet set;
	
	public XMIResourceURIHandler(ResourceSet set) {
		this.set = set;
	}
	
	@Override
	public URI resolve(URI uri) {
		if (uri.isRelative()) {
			return super.resolve(uri);
		}
		EPackage epackage = metamodelProvider.loadEPackage(uri.trimFragment().toString(), set);
		if (epackage != null) {
			return epackage.eResource().getURI().appendFragment(uri.fragment());
		}
		return super.resolve(uri);
	}
	
}
