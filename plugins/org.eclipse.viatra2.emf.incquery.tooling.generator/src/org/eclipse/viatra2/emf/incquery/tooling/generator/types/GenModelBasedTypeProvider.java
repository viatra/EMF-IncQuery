/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.tooling.generator.types;

import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra2.emf.incquery.tooling.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.types.EMFPatternTypeProvider;
import org.eclipse.xtext.common.types.JvmTypeReference;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Mark Czotter
 *
 */
@Singleton
public class GenModelBasedTypeProvider extends EMFPatternTypeProvider {

	@Inject 
	private IEiqGenmodelProvider genModelProvider;
	
	@Override
	protected JvmTypeReference resolve(EClassifier classifier,
			Variable variable) {
		JvmTypeReference typeRef = super.resolve(classifier, variable);
		if (typeRef == null) {
			EPackage ePackage = classifier.getEPackage();
			if (ePackage != null) {
				GenPackage genPackage = genModelProvider.findGenPackage(variable, ePackage);
				if (genPackage != null) {
					typeRef = resolve(genPackage, classifier, variable);
				}				
			}
		}
		return typeRef;
	}
	
	/**
	 * Resolves the {@link Variable} using information from the
	 * {@link GenPackage}. Tries to find an appropriate {@link GenClass} for the
	 * {@link EClassifier}. If one is found, then returns a
	 * {@link JvmTypeReference} for it's qualified interface name.
	 * 
	 * @param genPackage
	 * @param classifier
	 * @param variable
	 * @return
	 */
	protected JvmTypeReference resolve(GenPackage genPackage,
			EClassifier classifier, Variable variable) {
		GenClass genClass = findGenClass(genPackage, classifier);
		if (genClass != null) {
			return typeReference(genClass.getQualifiedInterfaceName(), variable);
		}
		return null;
	}
	
	private GenClass findGenClass(GenPackage genPackage, EClassifier classifier) {
		for (GenClass genClass : genPackage.getGenClasses()) {
			if (classifier.equals(genClass.getEcoreClassifier())) {
				return genClass;
			}
		}
		return null;
	}
	
}
