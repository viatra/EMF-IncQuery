package org.eclipse.viatra2.gtasm.scoping;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra2.gtasm.patternLanguage.Import;
import org.eclipse.viatra2.gtasm.patternLanguage.PatternLanguagePackage;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.linking.impl.DefaultLinkingService;
import org.eclipse.xtext.linking.impl.IllegalNodeException;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;

import com.google.inject.Inject;

public class PatternLanguageLinkingService extends DefaultLinkingService {
	private static final Logger log = Logger.getLogger(PatternLanguageLinkingService.class);
	
	@Inject
	private IValueConverterService valueConverterService;
	
	@Override
	public List<EObject> getLinkedObjects(EObject context, EReference ref, INode node) throws IllegalNodeException {
		if (ref == PatternLanguagePackage.eINSTANCE.getImport_EPackage() && context instanceof Import)
			return getPackage((Import)context, (ILeafNode) node);
		return super.getLinkedObjects(context, ref, node);
	}
	
	private List<EObject> getPackage(Import context, ILeafNode text) {
		String nsUri = getMetamodelNsURI(text);
		if (nsUri == null)
			return Collections.emptyList();
		EPackage pack = loadEPackage(nsUri, context.eResource().getResourceSet());
		if (pack != null)
			return Collections.<EObject>singletonList(pack);
		return Collections.emptyList();
	}

	private String getMetamodelNsURI(ILeafNode text) {
		try {
			return (String) valueConverterService.toValue(text.getText(), getLinkingHelper().getRuleNameFrom(text
					.getGrammarElement()), text);
		} catch (ValueConverterException e) {
			log.debug("Exception on leaf '" + text.getText() + "'", e);
			return null;
		}
	}
	
	private EPackage loadEPackage(String resourceOrNsURI, ResourceSet resourceSet) {
		if (EPackage.Registry.INSTANCE.containsKey(resourceOrNsURI))
			return EPackage.Registry.INSTANCE.getEPackage(resourceOrNsURI);
		URI uri = URI.createURI(resourceOrNsURI);
		try {
			if (uri.fragment() == null) {
				Resource resource = resourceSet.getResource(uri, true);
				EPackage result = (EPackage) resource.getContents().get(0);
				return result;
			}
			EPackage result = (EPackage) resourceSet.getEObject(uri, true);
			return result;
		} catch(RuntimeException ex) {
			if (uri.isPlatformResource()) {
				String platformString = uri.toPlatformString(true);
				URI platformPluginURI = URI.createPlatformPluginURI(platformString, true);
				return loadEPackage(platformPluginURI.toString(), resourceSet);
			}
			log.trace("Cannot load package with URI '" + resourceOrNsURI + "'", ex);
			return null;
		}
	}
}
