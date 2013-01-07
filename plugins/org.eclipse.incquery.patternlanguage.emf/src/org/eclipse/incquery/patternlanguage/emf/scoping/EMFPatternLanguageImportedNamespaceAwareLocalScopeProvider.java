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
package org.eclipse.incquery.patternlanguage.emf.scoping;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.JavaImport;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternCall;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.scoping.impl.ImportNormalizer;
import org.eclipse.xtext.xbase.scoping.XbaseImportedNamespaceScopeProvider;

import com.google.common.collect.Iterables;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class EMFPatternLanguageImportedNamespaceAwareLocalScopeProvider extends XbaseImportedNamespaceScopeProvider {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.xtext.scoping.impl.ImportedNamespaceAwareLocalScopeProvider#internalGetImportedNamespaceResolvers
     * (org.eclipse.emf.ecore.EObject, boolean)
     */
    @Override
    protected List<ImportNormalizer> internalGetImportedNamespaceResolvers(EObject context, boolean ignoreCase) {
        List<ImportNormalizer> result = new ArrayList<ImportNormalizer>(super.internalGetImportedNamespaceResolvers(
                context, ignoreCase));
        if (context instanceof PatternCall) {
            PatternModel model = EcoreUtil2.getContainerOfType(context, PatternModel.class);
            if (model != null) {
                if (model.getPackageName() != null && !model.getPackageName().isEmpty()) {
                    result.add(createImportedNamespaceResolver(model.getPackageName() + ".*", ignoreCase));
                }
                for (JavaImport importDecl : Iterables.filter(model.getImportPackages(), JavaImport.class)) {
                    if (importDecl.getImportedNamespace() != null) {
                        result.add(createImportedNamespaceResolver(importDecl.getImportedNamespace() + ".*", ignoreCase));
                    }
                }
            }
        }
        return result;
    }

}
