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
package org.eclipse.incquery.tooling.generator.model.scoping;

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
