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
package org.eclipse.incquery.patternlanguage.emf;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PackageImport;
import org.eclipse.incquery.patternlanguage.naming.PatternNameProvider;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.AnnotationParameter;
import org.eclipse.incquery.patternlanguage.patternLanguage.VariableReference;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 * 
 */
public class EMFPatternLanguageQualifiedNameProvider extends PatternNameProvider {

    @Inject
    private IQualifiedNameConverter nameConverter;

    @Override
    public QualifiedName getFullyQualifiedName(EObject obj) {
        if (obj instanceof PackageImport) {
            PackageImport packageImport = (PackageImport) obj;
            String nsURI = (packageImport.getEPackage() != null) ? packageImport.getEPackage().getNsURI() : "<none>";
            return nameConverter.toQualifiedName("import.nsUri." + nsURI);
        } else if (obj instanceof Annotation) {
            Annotation annotation = (Annotation) obj;
            String name = annotation.getName();
            return nameConverter.toQualifiedName("annotation." + name);
        } else if (obj instanceof AnnotationParameter) {
            AnnotationParameter parameter = (AnnotationParameter) obj;
            Annotation annotation = (Annotation) parameter.eContainer();
            return getFullyQualifiedName(annotation).append(parameter.getName());
        } else if (obj instanceof VariableReference) {
            VariableReference variableRef = (VariableReference) obj;
            QualifiedName containerName = getFullyQualifiedName(variableRef.eContainer());
            String name = variableRef.getVar();
            if (name == null) {
                return nameConverter.toQualifiedName("<none>");
            } else if (containerName == null) {
                return nameConverter.toQualifiedName(name);
            } else {
                return containerName.append(name);
            }
        }
        return super.getFullyQualifiedName(obj);
    }

}
