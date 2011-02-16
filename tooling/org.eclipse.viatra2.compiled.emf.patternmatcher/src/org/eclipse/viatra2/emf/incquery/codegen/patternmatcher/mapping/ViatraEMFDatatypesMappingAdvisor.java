/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.codegen.patternmatcher.mapping;

import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

public class ViatraEMFDatatypesMappingAdvisor implements ViatraEMFMetamodelAdvisor {

	@Override
	public void addEClassifiersByFQN(Map<String, EClassifier> eClassifiersByFQN) {
		eClassifiersByFQN.put("datatypes.Integer", EcorePackage.Literals.EINT); // EINTEGER_OBJECT vagy EINT? az a fotipus...
		eClassifiersByFQN.put("datatypes.Boolean", EcorePackage.Literals.EBOOLEAN); // EBOOLEAN_OBJECT vagy EBOOLEAN? az a fotipus...
		eClassifiersByFQN.put("datatypes.Double", EcorePackage.Literals.EDOUBLE); // EDOUBLE_OBJECT vagy EDOUBLE? az a fotipus...
		eClassifiersByFQN.put("datatypes.String", EcorePackage.Literals.ECHAR); // ESTRING vagy ECHAR vagy ECHARACTER_OBJECT
		
		// TODO this is shit
	}

	@Override
	public void addEStructuralFeaturesByFQN(Map<String, EStructuralFeature> structuralFeaturesByFQN) {
		
	}

}
