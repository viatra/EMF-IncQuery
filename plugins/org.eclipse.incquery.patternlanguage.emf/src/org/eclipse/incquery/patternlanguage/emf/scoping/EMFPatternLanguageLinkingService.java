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

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageScopeHelper;
import org.eclipse.incquery.patternlanguage.emf.ResolutionException;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EMFPatternLanguagePackage;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EnumValue;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PackageImport;
import org.eclipse.incquery.patternlanguage.patternLanguage.PathExpressionHead;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.linking.impl.DefaultLinkingService;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;

import com.google.inject.Inject;

public class EMFPatternLanguageLinkingService extends DefaultLinkingService {

    @Inject
    private Logger logger;
    @Inject
    private IValueConverterService valueConverterService;
    @Inject
    private IMetamodelProvider metamodelProvider;

    @Override
    public List<EObject> getLinkedObjects(EObject context, EReference ref, INode node) {
        if (ref == EMFPatternLanguagePackage.eINSTANCE.getPackageImport_EPackage() && context instanceof PackageImport
                && node instanceof ILeafNode) {
            return getPackage((PackageImport) context, (ILeafNode) node);
        } else if (ref == EMFPatternLanguagePackage.eINSTANCE.getEnumValue_Literal() && context instanceof EnumValue
                && node instanceof ILeafNode) {
            return getEnumLiteral((EnumValue) context, node);
        }
        return super.getLinkedObjects(context, ref, node);
    }

    private List<EObject> getEnumLiteral(EnumValue value, INode node) {
        try {
            EEnum type = null;
            if (value.getEnumeration() != null) {
                type = value.getEnumeration();
            } else if (value.eContainer() instanceof PathExpressionHead) {
                type = EMFPatternLanguageScopeHelper.calculateEnumerationType(getExpressionHead(value.eContainer()));
            } else {
                return Collections.emptyList();
            }
            String typename = ((ILeafNode) node).getText();
            EEnumLiteral literal = type.getEEnumLiteralByLiteral(typename);
            if (literal == null) {
                literal = type.getEEnumLiteral(typename);
            }
            if (literal != null) {
                return Collections.<EObject> singletonList(literal);
            } else {
                return Collections.emptyList();
            }
        } catch (ResolutionException e) {
            return Collections.emptyList();
        }
    }

    private PathExpressionHead getExpressionHead(EObject obj) {
        if (obj instanceof PathExpressionHead) {
            return (PathExpressionHead) obj;
        } else if (obj.eContainer() != null) {
            return getExpressionHead(obj.eContainer());
        } else {
            return null;
        }
    }

    private List<EObject> getPackage(PackageImport context, ILeafNode text) {
        String nsUri = getMetamodelNsURI(text);
        if (nsUri == null) {
            return Collections.emptyList();
        }
        EPackage pack = metamodelProvider.loadEPackage(nsUri, context.eResource().getResourceSet());
        if (pack != null) {
            return Collections.<EObject> singletonList(pack);
        }
        return Collections.emptyList();
    }

    private String getMetamodelNsURI(ILeafNode text) {
        try {
            return (String) valueConverterService.toValue(text.getText(),
                    getLinkingHelper().getRuleNameFrom(text.getGrammarElement()), text);
        } catch (ValueConverterException e) {
            logger.debug("Exception on leaf '" + text.getText() + "'", e);
            return null;
        }
    }
}
