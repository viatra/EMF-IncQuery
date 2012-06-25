package org.eclipse.viatra2.emf.incquery.tooling.generator.scoping;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.serializer.diagnostic.ISerializationDiagnostic.Acceptor;
import org.eclipse.xtext.serializer.tokens.CrossReferenceSerializer;

public class GeneratorModelCrossRefSerializer extends CrossReferenceSerializer {

	@Override
	public String serializeCrossRef(EObject semanticObject,
			CrossReference crossref, EObject target, INode node, Acceptor errors) {
		if (target instanceof GenModel && target.eResource() != null) {
			return String.format("\"%s\"", target.eResource().getURI()
					.toString());
		}
		return super.serializeCrossRef(semanticObject, crossref, target, node,
				errors);
	}

}
