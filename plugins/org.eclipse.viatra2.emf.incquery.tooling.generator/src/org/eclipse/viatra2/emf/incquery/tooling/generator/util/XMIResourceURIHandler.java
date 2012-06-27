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
		System.out.println(uri.toString());
		if (uri.isRelative()) {
			return super.resolve(uri);
		}
		
		String uriBase = uri.trimFragment().toString();
		// XXX this seems to work but is an ugly HACK!!!
		if(!uriBase.endsWith(".genmodel")) {
			EPackage epackage = metamodelProvider.loadEPackage(uriBase, set);
			if (epackage != null) {
				return epackage.eResource().getURI().appendFragment(uri.fragment());
			}
		}
		return super.resolve(uri);
	}
	
}
