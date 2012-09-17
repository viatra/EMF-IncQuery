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
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.emf.incquery.tooling.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.viatra2.patternlanguage.core.annotations.PatternAnnotationProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.AnnotationParameter;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra2.patternlanguage.types.IEMFTypeProvider;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.xbase.typing.ITypeProvider;
import org.eclipse.xtext.xbase.ui.hover.XbaseHoverDocumentationProvider;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 *
 */
@SuppressWarnings("restriction")
public class EMFPatternLanguageHoverDocumentationProvider extends
		XbaseHoverDocumentationProvider {

	@Inject
	private IEiqGenmodelProvider genmodelProvider;
	@Inject
	private PatternAnnotationProvider annotationProvider;
	@Inject
	private ITypeProvider typeProvider;
	@Inject
	private IEMFTypeProvider emfTypeProvider;
	
	@Override
	public String computeDocumentation(EObject object) {
		if (object instanceof Annotation) {
			String description = annotationProvider.getDescription((Annotation) object);
			if (annotationProvider.isDeprecated((Annotation) object)) {
				return "<b>@deprecated</b></p></p>" + description;
			} else {
				return description;
			}
		} else if (object instanceof AnnotationParameter) {
			String description = annotationProvider.getDescription((AnnotationParameter)object);
			if (annotationProvider.isDeprecated((AnnotationParameter) object)) {
				return "<b>@deprecated</b></p></p>" + description;
			} else {
				return description;
			}
		} else if (object instanceof PackageImport) {
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
		} else if (object instanceof Variable) {
			Variable variable = (Variable) object;
			return calculateVariableHover(variable);
		} else if (object instanceof VariableReference) {
			VariableReference reference = (VariableReference) object;
			return calculateVariableHover(reference.getVariable());
		}
		return super.computeDocumentation(object);
	}

	/**
	 * @param variable
	 * @return
	 */
	private String calculateVariableHover(Variable variable) {
		JvmTypeReference type = typeProvider.getTypeForIdentifiable(variable);
		EClassifier emfType = emfTypeProvider
				.getClassifierForVariable(variable);
		String javaTypeString = type.getQualifiedName();
		String emfTypeString;
		if (emfType == null) {
			emfTypeString = "Not applicable";
		} else {
			emfTypeString = String.format("%s (<i>%s</i>)", emfType.getName(),
					emfType.getEPackage().getNsURI());
		}
		return String.format("<b>EMF Type</b>: %s<br /><b>Java Type</b>: %s",
				emfTypeString, javaTypeString);
	}

}
