package org.eclipse.viatra2.patternlanguage.serializer;

import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.serializer.diagnostic.ISerializationDiagnostic.Acceptor;
import org.eclipse.xtext.serializer.tokens.CrossReferenceSerializer;

public class EMFPatternLanguageCrossRefSerializer extends CrossReferenceSerializer {

	@Override
	public String serializeCrossRef(EObject semanticObject,
			CrossReference crossref, EObject target, INode node, Acceptor errors) {
		if (target instanceof EPackage) {
			return String.format("\"%s\"", ((EPackage)target).getNsURI()
					.toString());
		} else if (target instanceof ENamedElement) {
			return ((ENamedElement) target).getName();
		}
		return super.serializeCrossRef(semanticObject, crossref, target, node,
				errors);
	}

}
