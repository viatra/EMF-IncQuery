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

package org.eclipse.viatra2.patternlanguage.ui.highlight;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.core.annotations.PatternAnnotationProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.AnnotationParameter;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ReferenceType;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XNumberLiteral;
import org.eclipse.xtext.xbase.annotations.xAnnotations.XAnnotation;
import org.eclipse.xtext.xbase.ui.highlighting.XbaseHighlightingCalculator;
import org.eclipse.xtext.xbase.ui.highlighting.XbaseHighlightingConfiguration;

import com.google.inject.Inject;

@SuppressWarnings("restriction")
public class EMFPatternLanguageHighlightingCalculator extends
		XbaseHighlightingCalculator {

	@Inject
	private PatternAnnotationProvider annotationProvider;

	@Override
	protected void searchAndHighlightElements(XtextResource resource, IHighlightedPositionAcceptor acceptor) {
		TreeIterator<EObject> iterator = resource.getAllContents();
		while (iterator.hasNext()) {
			EObject object = iterator.next();
			if (object instanceof XAbstractFeatureCall) {
				computeFeatureCallHighlighting((XAbstractFeatureCall) object, acceptor);
			} else if (object instanceof XNumberLiteral) {
			// Handle XAnnotation in a special way because we want the @ highlighted too
				highlightNumberLiterals((XNumberLiteral) object, acceptor);
			} else if (object instanceof XAnnotation) {
				highlightAnnotation((XAnnotation) object, acceptor);
			} else if (object instanceof ClassType  || object instanceof ReferenceType) {
				ICompositeNode node = NodeModelUtils.findActualNodeFor(object);
				highlightNode(node, EMFPatternLanguageHighlightingConfiguration.METAMODEL_REFERENCE, acceptor);
			} else if (object instanceof Annotation
					&& annotationProvider.isDeprecated((Annotation) object)) {
				Annotation annotation = (Annotation) object;
				ICompositeNode compositeNode = NodeModelUtils
						.findActualNodeFor(annotation);
				INode node = null;
				for (ILeafNode leafNode : compositeNode.getLeafNodes()) {
					if (leafNode.getText().equals(annotation.getName())) {
						node = leafNode;
						break;
					}
				}
				node = (node == null) ? compositeNode : node;
				highlightNode(node,
						XbaseHighlightingConfiguration.DEPRECATED_MEMBERS,
						acceptor);
			} else if (object instanceof AnnotationParameter
					&& annotationProvider
							.isDeprecated((AnnotationParameter) object)) {
				INode node = NodeModelUtils.getNode(object).getFirstChild();
				highlightNode(node,
						XbaseHighlightingConfiguration.DEPRECATED_MEMBERS,
						acceptor);
			} else {
				computeReferencedJvmTypeHighlighting(acceptor, object);
			}
		}
	}
	
}
