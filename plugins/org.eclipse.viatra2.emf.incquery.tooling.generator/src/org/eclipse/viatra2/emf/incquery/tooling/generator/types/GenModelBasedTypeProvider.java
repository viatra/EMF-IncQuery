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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra2.emf.incquery.tooling.generator.builder.GeneratorIssueCodes;
import org.eclipse.viatra2.emf.incquery.tooling.generator.builder.IErrorFeedback;
import org.eclipse.viatra2.emf.incquery.tooling.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.types.EMFPatternTypeProvider;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.util.Primitives;
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.diagnostics.Severity;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * FIXME do it, write something meaningful here
 */
@Singleton
@SuppressWarnings("restriction")
public class GenModelBasedTypeProvider extends EMFPatternTypeProvider {

	@Inject
	private IEiqGenmodelProvider genModelProvider;

	@Inject
	private IErrorFeedback errorFeedback;

	@Inject
	private TypeReferences typeReferences;

	@Inject
	private Primitives primitives;

	@Override
	protected JvmTypeReference getTypeReferenceForVariableWithEClassifier(EClassifier classifier, Variable variable) {
		JvmTypeReference typeRef = super.getTypeReferenceForVariableWithEClassifier(classifier, variable);
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
	protected JvmTypeReference resolve(GenPackage genPackage, EClassifier classifier, Variable variable) {
		GenClass genClass = findGenClass(genPackage, classifier);
		if (genClass != null) {
			JvmTypeReference typeReference = getTypeReferenceForTypeName(genClass.getQualifiedInterfaceName(), variable);
			if (typeReference == null) {
				EObject context = variable;
				if (variable.eContainer() instanceof PatternBody && variable.getReferences().size() > 0) {
					context = variable.getReferences().get(0);
				}
				errorFeedback.reportError(context, String.format(
						"Cannot resolve corresponding Java type for variable %s. Are the required bundle dependencies set?",
						variable.getName()), GeneratorIssueCodes.INVALID_TYPEREF_CODE, Severity.WARNING,
						IErrorFeedback.JVMINFERENCE_ERROR_TYPE);
			}
			return typeReference;
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

	private JvmTypeReference getTypeReferenceForTypeName(String typeName, Variable variable) {
		JvmTypeReference typeRef = typeReferences.getTypeForName(typeName, variable);
		return primitives.asWrapperTypeIfPrimitive(typeRef);
	}

}
