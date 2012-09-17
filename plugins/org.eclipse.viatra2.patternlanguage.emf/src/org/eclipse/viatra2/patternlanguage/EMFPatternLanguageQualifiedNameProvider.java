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
package org.eclipse.viatra2.patternlanguage;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.AnnotationParameter;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.scoping.XbaseQualifiedNameProvider;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class EMFPatternLanguageQualifiedNameProvider extends
		XbaseQualifiedNameProvider {

	@Inject
	IQualifiedNameConverter nameConverter;
	
	@Override
	public QualifiedName getFullyQualifiedName(EObject obj) {
		if (obj instanceof PackageImport) {
			return nameConverter.toQualifiedName("import.nsUri." + ((PackageImport)obj).getEPackage().getNsURI());
		} else if (obj instanceof Annotation) {
			return nameConverter.toQualifiedName("annotation." + ((Annotation) obj).getName());
		} else if (obj instanceof AnnotationParameter) {
			AnnotationParameter parameter = (AnnotationParameter) obj;
			Annotation annotation = (Annotation) parameter.eContainer();
			getFullyQualifiedName(annotation).append(parameter.getName());
		} else if (obj instanceof VariableReference) {
			VariableReference variableRef = (VariableReference) obj;
			QualifiedName containerName = getFullyQualifiedName(variableRef.eContainer());
			String name = variableRef.getVariable().getName();
			if (containerName == null) {
				return nameConverter.toQualifiedName(name);
			} else {
				return containerName.append(name);
			}
		}
		return super.getFullyQualifiedName(obj);
	}

}
