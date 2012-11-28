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
package org.eclipse.incquery.patternlanguage.emf.validation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.xtext.diagnostics.AbstractDiagnostic;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.validation.IDiagnosticConverter;
import org.eclipse.xtext.validation.IResourceValidator;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 * 
 */
public class PatternSetValidator {

    @Inject
    private Diagnostician diagnostician;
    @Inject
    private IResourceValidator resourceValidator;
    @Inject
    private IDiagnosticConverter converter;

    public PatternSetValidationDiagnostics validate(Collection<Pattern> patternSet) {
        BasicDiagnostic chain = new BasicDiagnostic();
        PatternSetValidationDiagnostics collectedIssues = new PatternSetValidationDiagnostics();
        Set<Resource> containerResources = new HashSet<Resource>();
        for (Pattern pattern : patternSet) {
            Resource resource = pattern.eResource();
            if (resource != null) {
                containerResources.add(resource);
            }
        }
        for (Resource resource : containerResources) {
            for (Diagnostic diag : resource.getErrors()) {
                if (diag instanceof AbstractDiagnostic) {
                    AbstractDiagnostic abstractDiagnostic = (AbstractDiagnostic) diag;
                    URI uri = abstractDiagnostic.getUriToProblem();
                    EObject obj = resource.getEObject(uri.fragment());
                    if (EcoreUtil.isAncestor(patternSet, obj)) {
                        converter.convertResourceDiagnostic(abstractDiagnostic, Severity.ERROR, collectedIssues);
                    }
                }
            }
        }
        for (Pattern pattern : patternSet) {
            diagnostician.validate(pattern, chain);
        }
        converter.convertValidatorDiagnostic(chain, collectedIssues);
        return collectedIssues;
    }

}
