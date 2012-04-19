/*******************************************************************************
 * Copyright (c) 2011 Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.patternlanguage.scoping;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageScopeHelper;
import org.eclipse.viatra2.patternlanguage.ResolutionException;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionHead;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguagePackage;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EnumValue;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.linking.impl.DefaultLinkingService;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;

import com.google.inject.Inject;

public class EMFPatternLanguageLinkingService extends DefaultLinkingService {
	private static final Logger LOG = Logger.getLogger(EMFPatternLanguageLinkingService.class);
	
	@Inject
	private IValueConverterService valueConverterService;
	
	@Override
	public List<EObject> getLinkedObjects(EObject context, EReference ref, INode node) {
		if (ref == EMFPatternLanguagePackage.eINSTANCE.getPackageImport_EPackage() && context instanceof PackageImport) {
			return getPackage((PackageImport)context, (ILeafNode) node);
		} else if (ref == EMFPatternLanguagePackage.eINSTANCE.getEnumValue_Literal() && context instanceof EnumValue) {
			try {
				EnumValue value = (EnumValue) context;
				EEnum type = null;
				if (value.getEnumeration() != null) {
					type = value.getEnumeration();
				} else if (value.eContainer() instanceof PathExpressionHead) {
					type = EMFPatternLanguageScopeHelper
						.calculateEnumerationType(getExpressionHead(context
								.eContainer()));
				} else {
					return Collections.emptyList();
				}
				String typename = ((ILeafNode)node).getText();
				EEnumLiteral literal = type.getEEnumLiteral(typename);
				if (literal != null) {
					return Collections.<EObject>singletonList(literal);
				} else return Collections.emptyList();
			} catch (ResolutionException e) {
				return Collections.emptyList();
			}
		}
		return super.getLinkedObjects(context, ref, node);
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
		EPackage pack = loadEPackage(nsUri, context.eResource().getResourceSet());
		if (pack != null) {
			return Collections.<EObject>singletonList(pack);
		}
		return Collections.emptyList();
	}

	private String getMetamodelNsURI(ILeafNode text) {
		try {
			return (String) valueConverterService.toValue(text.getText(), getLinkingHelper().getRuleNameFrom(text
					.getGrammarElement()), text);
		} catch (ValueConverterException e) {
			LOG.debug("Exception on leaf '" + text.getText() + "'", e);
			return null;
		}
	}
	
	private EPackage loadEPackage(String resourceOrNsURI, ResourceSet resourceSet) {
		if (EPackage.Registry.INSTANCE.containsKey(resourceOrNsURI)) {
			return EPackage.Registry.INSTANCE.getEPackage(resourceOrNsURI);
		}
		URI uri = URI.createURI(resourceOrNsURI);
		try {
			if (uri.fragment() == null) {
				Resource resource = resourceSet.getResource(uri, true);
				return (EPackage) resource.getContents().get(0);
			}
			return (EPackage) resourceSet.getEObject(uri, true);
		} catch(RuntimeException ex) {
			if (uri.isPlatformResource()) {
				String platformString = uri.toPlatformString(true);
				URI platformPluginURI = URI.createPlatformPluginURI(platformString, true);
				return loadEPackage(platformPluginURI.toString(), resourceSet);
			}
			LOG.trace("Cannot load package with URI '" + resourceOrNsURI + "'", ex);
			return null;
		}
	}
}
